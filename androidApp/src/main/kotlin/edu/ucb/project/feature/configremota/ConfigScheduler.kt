
package edu.ucb.project.feature.configremota

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ConfigScheduler {

    private const val TAG = "CONFIG_SYNC"

    const val WORK_PERIODICO = "config_sync_periodico"

    // Nombre del trabajo manual de las versiones anteriores.
    private const val WORK_MANUAL_ANTIGUO =
        "config_sync_manual"

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

    // Limpiar los trabajos manuales antiguos.
    fun cancelarManualAnterior(context: Context) {

        WorkManager.getInstance(
            context.applicationContext
        ).cancelUniqueWork(WORK_MANUAL_ANTIGUO)

        Log.d(
            TAG,
            "Trabajo manual anterior cancelado"
        )
    }
}
