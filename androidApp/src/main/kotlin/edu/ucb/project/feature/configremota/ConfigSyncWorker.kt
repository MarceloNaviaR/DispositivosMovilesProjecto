
package edu.ucb.project.feature.configremota

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException
import kotlinx.coroutines.CancellationException

class ConfigSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "CONFIG_SYNC"
        private const val MAX_INTENTOS = 4
    }

    override suspend fun doWork(): Result {

        Log.d(
            TAG,
            "Worker iniciado. Intento ${runAttemptCount + 1}"
        )

        return try {

            val repository = ConfigRemoteRepository(
                applicationContext
            )

            val cambios = repository.sincronizar()

            Log.d(
                TAG,
                "Worker completado. Cambios: $cambios"
            )

            Result.success(
                Data.Builder()
                    .putInt("cambios", cambios)
                    .putString(
                        "mensaje",
                        "Sincronizacion correcta"
                    )
                    .build()
            )

        } catch (e: CancellationException) {

            Log.w(TAG, "Worker cancelado")
            throw e

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error en sincronizacion automatica",
                e
            )

            val permisosFirestore =
                e is FirebaseFirestoreException &&
                        e.code ==
                        FirebaseFirestoreException.Code.PERMISSION_DENIED

            val configuracionInvalida =
                e is IllegalArgumentException ||
                        e is IllegalStateException

            val limitado =
                e is FirebaseRemoteConfigFetchThrottledException

            when {

                permisosFirestore || configuracionInvalida -> {
                    fallar(e.message ?: "Error de configuracion")
                }

                limitado -> {
                    Log.w(
                        TAG,
                        "Remote Config limito las descargas"
                    )

                    if (runAttemptCount < MAX_INTENTOS - 1) {
                        Result.retry()
                    } else {
                        fallar("Descargas limitadas por Firebase")
                    }
                }

                runAttemptCount < MAX_INTENTOS - 1 -> {
                    Log.w(
                        TAG,
                        "Error temporal: reintento programado"
                    )
                    Result.retry()
                }

                else -> {
                    fallar(
                        e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    private fun fallar(mensaje: String): Result {

        Log.e(TAG, mensaje)

        return Result.failure(
            Data.Builder()
                .putString("error", mensaje)
                .build()
        )
    }
}
