package edu.ucb.project.feature.configremota

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException

class ConfigSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return try {

            val repository = ConfigRemoteRepository(
                applicationContext
            )

            val cambios = repository.sincronizar()

            Log.d(
                "CONFIG_SYNC",
                "Sincronizacion completa. Cambios: $cambios"
            )

            Result.success()

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {

            Log.e(
                "CONFIG_SYNC",
                "Error al sincronizar configuracion",
                e
            )

            Result.retry()
        }
    }
}
