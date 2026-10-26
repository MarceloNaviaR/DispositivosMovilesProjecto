package edu.ucb.project.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

actual class LogScheduler actual constructor(context: Any?) {

    private val appContext = (context as? Context)
        ?.applicationContext
        ?: throw IllegalArgumentException(
            "Se requiere un Context de Android"
        )

    actual fun schedulePeriodicalUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<LogUploadWorker>(
            15L,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(appContext)
            .enqueueUniquePeriodicWork(
                "logUploadWork",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}