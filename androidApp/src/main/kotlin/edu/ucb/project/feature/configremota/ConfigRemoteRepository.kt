package edu.ucb.project.feature.configremota

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.UUID

class ConfigRemoteRepository(
    private val context: Context
) {

    private val dao =
        ConfigDatabase.getDatabase(context).configDao()

    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    private val database = FirebaseDatabase.getInstance()

    private val prefs = context.getSharedPreferences(
        "config_remota_preferences",
        Context.MODE_PRIVATE
    )

    private val deviceId: String
        get() {
            val existing = prefs.getString("device_id", null)
            if (existing != null) return existing

            val generated = UUID.randomUUID().toString()
            prefs.edit()
                .putString("device_id", generated)
                .apply()

            return generated
        }

    suspend fun sincronizar(): Int {

        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        ).awaitTask()

        remoteConfig.setDefaultsAsync(
            mapOf(
                "mensaje" to "Bienvenido al dashboard",
                "limite" to 10L,
                "feature_enabled" to true
            )
        ).awaitTask()

        remoteConfig.fetchAndActivate().awaitTask()

        val mensaje = remoteConfig.getString("mensaje")
        val limite = remoteConfig.getLong("limite").toInt()
        val enabled = remoteConfig.getBoolean("feature_enabled")

        val ultima = dao.obtenerUltima()

        val hayCambio = ultima == null ||
                ultima.mensaje != mensaje ||
                ultima.limite != limite ||
                ultima.featureEnabled != enabled

        if (hayCambio) {
            dao.insertar(
                ConfigVersionEntity(
                    mensaje = mensaje,
                    limite = limite,
                    featureEnabled = enabled
                )
            )
        }

        val pendientes = dao.obtenerPendientes()

        var cambios = 0

        for (config in pendientes) {

            val eventId = "${deviceId}_${config.id}"

            val version = incrementarVersion(eventId)

            val datos = hashMapOf<String, Any>(
                "mensaje" to config.mensaje,
                "limite" to config.limite,
                "feature_enabled" to config.featureEnabled,
                "version" to version,
                "timestamp" to config.timestamp,
                "actualizadoEn" to
                        com.google.firebase.Timestamp.now(),
                "eventId" to eventId
            )

            firestore.collection("configRemota")
                .document("actual")
                .set(datos)
                .awaitTask()

            dao.marcarSincronizada(config.id, version)

            if (!config.notified) {
                ConfigNotification.mostrar(
                    context,
                    version
                )
                dao.marcarNotificada(config.id)
            }

            cambios++
        }

        return cambios
    }

    private suspend fun incrementarVersion(
        eventId: String
    ): Long = suspendCancellableCoroutine { continuation ->

        val referencia: DatabaseReference =
            database.getReference("config")

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
                    snapshot: com.google.firebase.database.DataSnapshot?
                ) {
                    if (!continuation.isActive) return

                    if (error != null) {
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
                                "No se obtuvo la version"
                            )
                        )
                    }
                }
            }
        )
    }
}

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
                            "Error en Firebase"
                        )
                )
            }
        }
    }
