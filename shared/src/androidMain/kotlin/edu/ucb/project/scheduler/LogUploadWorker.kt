package edu.ucb.project.scheduler

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LogUploadWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        Log.i(
            "LogUploadWorker",
            "Tarea en segundo plano ejecutada correctamente"
        )

        // Aquí se agregará la operación real.

        return Result.success()
    }
}