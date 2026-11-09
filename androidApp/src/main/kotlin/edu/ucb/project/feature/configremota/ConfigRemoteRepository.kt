
package edu.ucb.project.feature.configremota

import android.content.Context
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConfigRemoteRepository(
    context: Context
) {

    companion object {
        private const val TAG = "CONFIG_SYNC"
        private val mutex = Mutex()
    }

    private val appContext = context.applicationContext

    private val dao =
        ConfigDatabase.getDatabase(appContext).configDao()

    private val remoteConfig =
        FirebaseRemoteConfig.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    private val database = FirebaseDatabase.getInstance(
        "https://dispositivos-moviles-23f66-default-rtdb.firebaseio.com"
    )

    private val prefs = appContext.getSharedPreferences(
        "config_remota_preferences",
        Context.MODE_PRIVATE
    )

    private val deviceId: String
        get() {
            val existing = prefs.getString("device_id", null)

            if (existing != null) {
                return existing
            }

            val nuevo = UUID.randomUUID().toString()

            prefs.edit()
                .putString("device_id", nuevo)
                .apply()

            return nuevo
        }

    suspend fun sincronizar(): Int = mutex.withLock {

        Log.d(TAG, "================================")
        Log.d(TAG, "INICIO SINCRONIZACION E100-2")
        Log.d(TAG, "================================")

        // 1. Configuracion para pruebas.
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0L)
                .build()
        ).awaitTask()

        // 2. Valores predeterminados de respaldo.
        remoteConfig.setDefaultsAsync(
            mapOf(
                "mensaje" to "Bienvenido al dashboard",
                "limite" to 10L,
                "feature_enabled" to false
            )
        ).awaitTask()

        // 3. Descargar los ultimos valores disponibles.
        // Un intervalo de cero NO elimina los limites
        // de solicitudes impuestos por Firebase.
        try {

            remoteConfig.fetch(0L).awaitTask()

            Log.d(
                TAG,
                "Descarga Remote Config completada"
            )

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {

            Log.e(
                TAG,
                "No se pudo descargar Remote Config",
                e
            )

            // No presentar una descarga fallida
            // como una sincronizacion nueva.
            throw e
        }

        // 4. Activar la configuracion descargada.
        val activado =
            remoteConfig.activate().awaitTask()

        Log.d(
            TAG,
            "Valores nuevos activados: $activado"
        )

        // 5. Obtener los parametros.
        val valorMensaje =
            remoteConfig.getValue("mensaje")

        val valorLimite =
            remoteConfig.getValue("limite")

        val valorBooleano =
            remoteConfig.getValue("feature_enabled")

        val origenRemoto =
            FirebaseRemoteConfig.VALUE_SOURCE_REMOTE

        // 6. Mostrar informacion real de Firebase.
        val info = remoteConfig.info

        Log.d(TAG, "========== REMOTE CONFIG ==========")

        Log.d(
            TAG,
            "RC mensaje=${valorMensaje.asString()} " +
                    "origen=${valorMensaje.source}"
        )

        Log.d(
            TAG,
            "RC limite=${valorLimite.asString()} " +
                    "origen=${valorLimite.source}"
        )

        Log.d(
            TAG,
            "RC feature_enabled=${valorBooleano.asString()} " +
                    "origen=${valorBooleano.source}"
        )

        Log.d(
            TAG,
            "Estado fetch=${info.lastFetchStatus}"
        )

        Log.d(
            TAG,
            "Fecha fetch=${info.fetchTimeMillis}"
        )

        Log.d(
            TAG,
            "Parametros disponibles=${remoteConfig.all.keys}"
        )

        // 7. Leer la ultima version guardada en Room.
        val ultima = dao.obtenerUltima()

        // 8. Utilizar el valor remoto cuando existe.
        // Si un parametro no llega remotamente,
        // conservar su ultimo valor local conocido.
        val mensaje = if (
            valorMensaje.source == origenRemoto
        ) {
            valorMensaje.asString()
        } else {
            Log.w(
                TAG,
                "mensaje no tiene origen remoto"
            )
            ultima?.mensaje ?: valorMensaje.asString()
        }

        val limiteLong = if (
            valorLimite.source == origenRemoto
        ) {
            valorLimite.asLong()
        } else {
            Log.w(
                TAG,
                "limite no tiene origen remoto"
            )
            ultima?.limite?.toLong()
                ?: valorLimite.asLong()
        }

        require(
            limiteLong in Int.MIN_VALUE.toLong()..
                    Int.MAX_VALUE.toLong()
        ) {
            "El limite excede el rango Int"
        }

        val limite = limiteLong.toInt()

        // Este valor reconoce true y false.
        // No se obliga artificialmente a ninguno.
        val enabled = if (
            valorBooleano.source == origenRemoto
        ) {
            valorBooleano.asBoolean()
        } else {
            Log.w(
                TAG,
                "feature_enabled no tiene origen remoto"
            )
            ultima?.featureEnabled
                ?: valorBooleano.asBoolean()
        }

        Log.d(TAG, "========== VALORES ELEGIDOS ==========")

        Log.d(TAG, "Mensaje: $mensaje")
        Log.d(TAG, "Limite: $limite")
        Log.d(TAG, "Booleano: $enabled")

        Log.d(
            TAG,
            "Booleano anterior: ${ultima?.featureEnabled}"
        )

        // 9. Detectar si cada parametro cambio.
        val cambioMensaje =
            ultima == null ||
                    ultima.mensaje != mensaje

        val cambioLimite =
            ultima == null ||
                    ultima.limite != limite

        val cambioBooleano =
            ultima == null ||
                    ultima.featureEnabled != enabled

        val hayCambio =
            cambioMensaje ||
                    cambioLimite ||
                    cambioBooleano

        Log.d(TAG, "========== COMPARACION ==========")

        Log.d(
            TAG,
            "Cambio mensaje: $cambioMensaje"
        )

        Log.d(
            TAG,
            "Cambio limite: $cambioLimite"
        )

        Log.d(
            TAG,
            "Cambio booleano: $cambioBooleano"
        )

        // 10. Guardar nueva version solamente
        // cuando alguno de los valores cambio.
        if (hayCambio) {

            val nuevaConfig = ConfigVersionEntity(
                mensaje = mensaje,
                limite = limite,
                featureEnabled = enabled
            )

            val id = dao.insertar(nuevaConfig)

            Log.d(
                TAG,
                "Nueva version Room: $id"
            )

        } else {

            Log.d(
                TAG,
                "No existen cambios nuevos"
            )
        }

        // 11. Procesar los pendientes.
        sincronizarPendientes()
    }

    private suspend fun sincronizarPendientes(): Int {

        val pendientes = dao.obtenerPendientes()

        Log.d(
            TAG,
            "Versiones pendientes: ${pendientes.size}"
        )

        var cambios = 0

        for (config in pendientes) {

            val eventId = "${deviceId}_${config.id}"

            Log.d(
                TAG,
                "Procesando configuracion ID ${config.id}"
            )

            // 12. Generar version en RTDB.
            // El mismo evento no suma dos veces.
            val version = incrementarVersion(eventId)

            Log.d(
                TAG,
                "Version obtenida RTDB: $version"
            )

            // 13. Construir datos de Firestore.
            val datos = hashMapOf<String, Any>(
                "mensaje" to config.mensaje,
                "limite" to config.limite,
                "feature_enabled" to config.featureEnabled,
                "version" to version,
                "timestamp" to config.timestamp,
                "actualizadoEn" to Timestamp.now(),
                "eventId" to eventId
            )

            val referencia = firestore
                .collection("configRemota")
                .document("actual")

            // 14. Escribir solamente si la version
            // es superior a la almacenada en Firestore.
            val actualizado = firestore.runTransaction {
                    transaction ->

                val documento = transaction.get(referencia)

                val versionActual =
                    documento.getLong("version") ?: 0L

                Log.d(
                    TAG,
                    "Versiones: RTDB=$version, " +
                            "Firestore=$versionActual"
                )

                if (version > versionActual) {

                    transaction.set(referencia, datos)
                    true

                } else {

                    false
                }
            }.awaitTask()

            if (actualizado) {

                Log.d(
                    TAG,
                    "Firestore actualizado a version $version"
                )

                Log.d(
                    TAG,
                    "Mensaje Firestore: ${config.mensaje}"
                )

                Log.d(
                    TAG,
                    "Limite Firestore: ${config.limite}"
                )

                Log.d(
                    TAG,
                    "Booleano Firestore: ${config.featureEnabled}"
                )

            } else {

                Log.w(
                    TAG,
                    "Version $version omitida porque " +
                            "Firestore posee una version igual o mayor"
                )

                // No marcar como sincronizado cuando
                // Firestore indica una inconsistencia
                // de versiones. Asi podremos revisarla.
                throw IllegalStateException(
                    "Conflicto de versiones: " +
                            "RTDB=$version. " +
                            "Revisa el contador de Firestore."
                )
            }

            // 15. Marcar la version como sincronizada.
            dao.marcarSincronizada(
                config.id,
                version
            )

            // 16. Generar notificacion.
            if (!config.notified) {

                ConfigNotification.mostrar(
                    appContext,
                    version
                )

                dao.marcarNotificada(config.id)

                Log.d(
                    TAG,
                    "Notificacion procesada para version $version"
                )
            }

            cambios++
        }

        Log.d(TAG, "================================")
        Log.d(TAG, "SINCRONIZACION COMPLETADA")
        Log.d(TAG, "Cambios procesados: $cambios")
        Log.d(TAG, "================================")

        return cambios
    }

    // Incrementar contador RTDB sin duplicar eventos.
    private suspend fun incrementarVersion(
        eventId: String
    ): Long = suspendCancellableCoroutine { continuation ->

        val referencia = database.getReference("config")

        referencia.runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val procesado = currentData
                        .child("procesadas")
                        .child(eventId)

                    if (procesado.value != null) {
                        return Transaction.success(currentData)
                    }

                    val actual = (
                            currentData.child("versiones").value
                                    as? Number
                            )?.toLong() ?: 0L

                    val siguiente = actual + 1L

                    currentData.child("versiones").value =
                        siguiente

                    procesado.value = siguiente

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

                        Log.e(
                            TAG,
                            "Error RTDB",
                            error.toException()
                        )

                        continuation.resumeWithException(
                            error.toException()
                        )

                        return
                    }

                    if (!committed || snapshot == null) {

                        continuation.resumeWithException(
                            IllegalStateException(
                                "No se pudo actualizar RTDB"
                            )
                        )

                        return
                    }

                    val version = snapshot
                        .child("procesadas")
                        .child(eventId)
                        .getValue(Long::class.java)

                    if (version != null) {

                        continuation.resume(version)

                    } else {

                        continuation.resumeWithException(
                            IllegalStateException(
                                "No se obtuvo la version RTDB"
                            )
                        )
                    }
                }
            }
        )
    }
}

// Adaptador Task Firebase a corrutinas.
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
