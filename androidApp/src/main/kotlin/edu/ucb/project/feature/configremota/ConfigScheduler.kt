package edu.ucb.project.feature.configremota

import android.content.Context
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
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_PERIODICO,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun sincronizarAhora(context: Context) {

        val request =
            OneTimeWorkRequestBuilder<ConfigSyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORK_UNICO,
                ExistingWorkPolicy.KEEP,
                request
            )
    }
}
