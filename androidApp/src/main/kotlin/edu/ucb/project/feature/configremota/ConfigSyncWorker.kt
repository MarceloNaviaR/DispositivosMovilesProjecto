
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
            "Worker periodico iniciado. " +
                    "Intento ${runAttemptCount + 1}"
        )

        return try {

            val repository = ConfigRemoteRepository(
                applicationContext
            )

            val cambios = repository.sincronizar()

            Log.d(
                TAG,
                "Worker finalizado. Versiones: $cambios"
            )

            Result.success(
                Data.Builder()
                    .putInt("cambios", cambios)
                    .build()
            )

        } catch (e: CancellationException) {

            throw e

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error en sincronizacion periodica",
                e
            )

            val permisosFirestore =
                e is FirebaseFirestoreException &&
                        e.code ==
                        FirebaseFirestoreException.Code.PERMISSION_DENIED

            val errorConfiguracion =
                e is IllegalArgumentException ||
                        e is IllegalStateException

            when {

                permisosFirestore || errorConfiguracion -> {
                    Result.failure(
                        Data.Builder()
                            .putString(
                                "error",
                                e.message ?: "Error de configuracion"
                            )
                            .build()
                    )
                }

                e is FirebaseRemoteConfigFetchThrottledException -> {
                    if (runAttemptCount < MAX_INTENTOS - 1) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }

                runAttemptCount < MAX_INTENTOS - 1 -> {
                    Result.retry()
                }

                else -> {
                    Result.failure(
                        Data.Builder()
                            .putString(
                                "error",
                                e.message ?: "Error desconocido"
                            )
                            .build()
                    )
                }
            }
        }
    }
}
