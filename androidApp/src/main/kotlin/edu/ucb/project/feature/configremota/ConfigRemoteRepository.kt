
package edu.ucb.project.feature.configremota

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConfigRemoteRepository(context: Context) {

    companion object {
        private const val TAG = "CONFIG_SYNC"
        private val mutex = Mutex()

        private const val RTDB_URL =
            "https://dispositivos-moviles-23f66-default-rtdb.firebaseio.com"
    }

    private val appContext = context.applicationContext

    private val dao = ConfigDatabase
        .getDatabase(appContext)
        .configDao()

    private val remoteConfig =
        FirebaseRemoteConfig.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    private val database =
        FirebaseDatabase.getInstance(RTDB_URL)

    // Sincronizacion inicial y periodica de WorkManager.
    suspend fun sincronizar(): Int {

        val solicitud = SystemClock.elapsedRealtime()

        Log.d(
            TAG,
            "SYNC_INICIAL solicitada; mutex=${mutex.isLocked}"
        )

        return mutex.withLock {

            val inicio = SystemClock.elapsedRealtime()

            Log.d(
                TAG,
                "TIEMPO_ESPERA_MUTEX_INICIAL=" +
                        "${inicio - solicitud} ms"
            )

            val inicioConfig = SystemClock.elapsedRealtime()

            configurarRemoteConfig()

            Log.d(
                TAG,
                "TIEMPO_CONFIG_RC=" +
                        "${SystemClock.elapsedRealtime() - inicioConfig} ms"
            )

            val inicioFetch = SystemClock.elapsedRealtime()

            remoteConfig.fetchAndActivate().awaitTask()

            Log.d(
                TAG,
                "TIEMPO_FETCH_RC=" +
                        "${SystemClock.elapsedRealtime() - inicioFetch} ms"
            )

            val resultado = procesarValoresActivos()

            Log.d(
                TAG,
                "TIEMPO_TOTAL_SYNC_INICIAL=" +
                        "${SystemClock.elapsedRealtime() - inicio} ms"
            )

            resultado
        }
    }

    // Se utiliza al recibir un cambio del listener.
    suspend fun procesarActualizacionRecibida(): Int {

        val solicitud = SystemClock.elapsedRealtime()

        Log.d(
            TAG,
            "SYNC_LISTENER solicitada; mutex=${mutex.isLocked}"
        )

        return mutex.withLock {

            val inicio = SystemClock.elapsedRealtime()

            Log.d(
                TAG,
                "TIEMPO_ESPERA_MUTEX_LISTENER=" +
                        "${inicio - solicitud} ms"
            )

            val inicioActivate = SystemClock.elapsedRealtime()

            remoteConfig.activate().awaitTask()

            Log.d(
                TAG,
                "TIEMPO_ACTIVATE_RC=" +
                        "${SystemClock.elapsedRealtime() - inicioActivate} ms"
            )

            val resultado = procesarValoresActivos()

            Log.d(
                TAG,
                "TIEMPO_TOTAL_SYNC_LISTENER=" +
                        "${SystemClock.elapsedRealtime() - inicio} ms"
            )

            resultado
        }
    }

    private suspend fun configurarRemoteConfig() {

        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                // Solo para pruebas.
                .setMinimumFetchIntervalInSeconds(0L)
                .build()
        ).awaitTask()

        remoteConfig.setDefaultsAsync(
            mapOf(
                "mensaje" to "Bienvenido al dashboard",
                "limite" to 10L,
                "feature_enabled" to false
            )
        ).awaitTask()
    }

    private suspend fun procesarValoresActivos(): Int {

        val inicio = SystemClock.elapsedRealtime()

        val valorMensaje =
            remoteConfig.getValue("mensaje")

        val valorLimite =
            remoteConfig.getValue("limite")

        val valorBooleano =
            remoteConfig.getValue("feature_enabled")

        val origenRemoto =
            FirebaseRemoteConfig.VALUE_SOURCE_REMOTE

        val ultima = dao.obtenerUltima()

        Log.d(TAG, "========== REMOTE CONFIG ==========")

        Log.d(
            TAG,
            "Mensaje RC=${valorMensaje.asString()}, " +
                    "origen=${valorMensaje.source}"
        )

        Log.d(
            TAG,
            "Limite RC=${valorLimite.asString()}, " +
                    "origen=${valorLimite.source}"
        )

        Log.d(
            TAG,
            "Booleano RC=${valorBooleano.asString()}, " +
                    "origen=${valorBooleano.source}"
        )

        val mensajeRemoto =
            valorMensaje.source == origenRemoto

        val limiteRemoto =
            valorLimite.source == origenRemoto

        val booleanoRemoto =
            valorBooleano.source == origenRemoto

        // En la primera sincronizacion se requieren
        // los tres parametros publicados.
        if (
            ultima == null &&
            !(mensajeRemoto && limiteRemoto && booleanoRemoto)
        ) {
            Log.w(
                TAG,
                "Primera configuracion remota incompleta"
            )

            return sincronizarPendientes()
        }

        // Conservar el ultimo valor de Room cuando
        // alguno de los parametros no sea remoto.
        val mensaje = if (mensajeRemoto) {
            valorMensaje.asString()
        } else {
            ultima?.mensaje ?: valorMensaje.asString()
        }

        val limiteLong = if (limiteRemoto) {
            valorLimite.asLong()
        } else {
            ultima?.limite?.toLong() ?: valorLimite.asLong()
        }

        require(
            limiteLong in Int.MIN_VALUE.toLong()..
                    Int.MAX_VALUE.toLong()
        ) {
            "Limite fuera del rango permitido"
        }

        val limite = limiteLong.toInt()

        val enabled = if (booleanoRemoto) {
            valorBooleano.asBoolean()
        } else {
            ultima?.featureEnabled ?: valorBooleano.asBoolean()
        }

        val cambioMensaje =
            ultima == null || ultima.mensaje != mensaje

        val cambioLimite =
            ultima == null || ultima.limite != limite

        val cambioBooleano =
            ultima == null || ultima.featureEnabled != enabled

        val hayCambio =
            cambioMensaje || cambioLimite || cambioBooleano

        Log.d(TAG, "========== COMPARACION ==========")

        Log.d(TAG, "Cambio mensaje=$cambioMensaje")
        Log.d(TAG, "Cambio limite=$cambioLimite")
        Log.d(TAG, "Cambio booleano=$cambioBooleano")

        Log.d(
            TAG,
            "Booleano anterior=${ultima?.featureEnabled}, " +
                    "nuevo=$enabled"
        )

        if (hayCambio) {

            val nueva = ConfigVersionEntity(
                mensaje = mensaje,
                limite = limite,
                featureEnabled = enabled
            )

            val inicioRoom = SystemClock.elapsedRealtime()

            val id = dao.insertar(nueva)

            Log.d(
                TAG,
                "TIEMPO_ROOM_INSERT=" +
                        "${SystemClock.elapsedRealtime() - inicioRoom} ms"
            )

            Log.d(
                TAG,
                "Nueva configuracion Room ID=$id"
            )

        } else {

            Log.d(TAG, "Sin cambios nuevos")
        }

        val resultado = sincronizarPendientes()

        Log.d(
            TAG,
            "TIEMPO_PROCESAR_VALORES=" +
                    "${SystemClock.elapsedRealtime() - inicio} ms"
        )

        return resultado
    }

    // Identifica una combinacion de valores.
    private fun huellaConfiguracion(
        config: ConfigVersionEntity
    ): String {

        val contenido = listOf(
            config.mensaje,
            config.limite.toString(),
            config.featureEnabled.toString()
        ).joinToString("\u0000")

        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(contenido.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }
    }

    private suspend fun sincronizarPendientes(): Int {

        val pendientes = dao.obtenerPendientes()

        Log.d(
            TAG,
            "Versiones pendientes=${pendientes.size}"
        )

        var procesadas = 0

        for (config in pendientes) {

            val inicioPendiente =
                SystemClock.elapsedRealtime()

            val huella = huellaConfiguracion(config)

            Log.d(
                TAG,
                "Procesando Room ID=${config.id}"
            )

            val inicioRTDB =
                SystemClock.elapsedRealtime()

            val version = obtenerVersionGlobal(huella)

            Log.d(
                TAG,
                "TIEMPO_RTDB=" +
                        "${SystemClock.elapsedRealtime() - inicioRTDB} ms"
            )

            Log.d(
                TAG,
                "Version global RTDB=$version"
            )

            val datos = hashMapOf<String, Any>(
                "mensaje" to config.mensaje,
                "limite" to config.limite,
                "feature_enabled" to config.featureEnabled,
                "version" to version,
                "timestamp" to config.timestamp,
                "actualizadoEn" to Timestamp.now(),
                "eventId" to huella,
                "huella" to huella
            )

            val referencia = firestore
                .collection("configRemota")
                .document("actual")

            val inicioFirestore =
                SystemClock.elapsedRealtime()

            val resultado = firestore.runTransaction {
                    transaction ->

                val documento = transaction.get(referencia)

                val versionActual =
                    documento.getLong("version") ?: 0L

                val huellaActual =
                    documento.getString("huella")
                        ?: documento.getString("eventId")

                Log.d(
                    TAG,
                    "Comparacion versiones: " +
                            "RTDB=$version, Firestore=$versionActual"
                )

                when {

                    version > versionActual -> {

                        transaction.set(referencia, datos)

                        "ACTUALIZADO"
                    }

                    version == versionActual &&
                            huellaActual == huella -> {

                        "EXISTENTE"
                    }

                    version < versionActual -> {

                        "ANTIGUO"
                    }

                    else -> {

                        throw IllegalStateException(
                            "Conflicto de version $version"
                        )
                    }
                }
            }.awaitTask()

            Log.d(
                TAG,
                "TIEMPO_FIRESTORE=" +
                        "${SystemClock.elapsedRealtime() - inicioFirestore} ms"
            )

            Log.d(
                TAG,
                "Resultado Firestore=$resultado"
            )

            dao.marcarSincronizada(
                config.id,
                version
            )

            if (!config.notified) {

                if (
                    resultado == "ACTUALIZADO" ||
                    resultado == "EXISTENTE"
                ) {
                    ConfigNotification.mostrar(
                        appContext,
                        version
                    )

                    Log.d(
                        TAG,
                        "Notificacion version $version solicitada"
                    )
                }

                dao.marcarNotificada(config.id)
            }

            procesadas++

            Log.d(
                TAG,
                "TIEMPO_TOTAL_PENDIENTE=" +
                        "${SystemClock.elapsedRealtime() - inicioPendiente} ms"
            )
        }

        Log.d(
            TAG,
            "Sincronizacion finalizada. Procesadas=$procesadas"
        )

        return procesadas
    }

    // Asigna una misma version global a configuraciones
    // con los mismos valores, incluso entre dispositivos.
    private suspend fun obtenerVersionGlobal(
        huella: String
    ): Long = suspendCancellableCoroutine { continuation ->

        val referencia = database.getReference("config")

        referencia.runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val huellas =
                        currentData.child("huellas")

                    val existente = (
                            huellas.child(huella).value
                                    as? Number
                            )?.toLong()

                    if (existente != null) {
                        return Transaction.success(currentData)
                    }

                    val actual = (
                            currentData.child("versiones").value
                                    as? Number
                            )?.toLong() ?: 0L

                    val siguiente = actual + 1L

                    currentData.child("versiones").value =
                        siguiente

                    huellas.child(huella).value =
                        siguiente

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    snapshot: DataSnapshot?
                ) {

                    if (!continuation.isActive) {
                        return
                    }

                    if (error != null) {

                        continuation.resumeWithException(
                            error.toException()
                        )

                        return
                    }

                    if (!committed || snapshot == null) {

                        continuation.resumeWithException(
                            IllegalStateException(
                                "Transaccion RTDB no confirmada"
                            )
                        )

                        return
                    }

                    val version = (
                            snapshot
                                .child("huellas")
                                .child(huella)
                                .value as? Number
                            )?.toLong()

                    if (version != null) {

                        continuation.resume(version)

                    } else {

                        continuation.resumeWithException(
                            IllegalStateException(
                                "No se obtuvo version global"
                            )
                        )
                    }
                }
            }
        )
    }
}

// Convierte Task de Firebase a una funcion suspend.
private suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { continuation ->

        addOnCompleteListener { task ->

            if (!continuation.isActive) {
                return@addOnCompleteListener
            }

            if (task.isSuccessful) {

                @Suppress("UNCHECKED_CAST")
                continuation.resume(task.result as T)

            } else {

                continuation.resumeWithException(
                    task.exception
                        ?: IllegalStateException(
                            "Error desconocido de Firebase"
                        )
                )
            }
        }
    }
