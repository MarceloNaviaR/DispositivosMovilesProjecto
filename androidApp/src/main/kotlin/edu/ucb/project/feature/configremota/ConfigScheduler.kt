
package edu.ucb.project.feature.configremota

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ConfigScheduler {

    private const val TAG = "CONFIG_SYNC"

    const val WORK_UNICO = "config_sync_manual"
    const val WORK_PERIODICO = "config_sync_periodico"

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun programarPeriodico(context: Context) {

        val request =
            PeriodicWorkRequestBuilder<ConfigSyncWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .addTag("CONFIG_PERIODICO")
                .build()

        WorkManager.getInstance(
            context.applicationContext
        ).enqueueUniquePeriodicWork(
            WORK_PERIODICO,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        Log.d(
            TAG,
            "Sincronizacion periodica registrada"
        )
    }

    // Metodo disponible para sincronizaciones
    // manuales desde otros componentes.
    // El Dashboard actual llama directamente
    // al repositorio y no necesita este metodo.
    fun sincronizarAhora(context: Context) {

        val request =
            OneTimeWorkRequestBuilder<ConfigSyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .addTag("CONFIG_MANUAL")
                .build()

        WorkManager.getInstance(
            context.applicationContext
        ).enqueueUniqueWork(
            WORK_UNICO,
            ExistingWorkPolicy.KEEP,
            request
        )

        Log.d(
            TAG,
            "Trabajo manual solicitado mediante WorkManager"
        )
    }

    // Cancela solicitudes manuales antiguas
    // que pudieron quedar de versiones previas.
    fun cancelarManualAnterior(context: Context) {

        WorkManager.getInstance(
            context.applicationContext
        ).cancelUniqueWork(WORK_UNICO)
    }
}
