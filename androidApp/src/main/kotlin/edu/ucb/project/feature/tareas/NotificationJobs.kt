package edu.ucb.project.feature.tareas

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

class NotificationJobs(context: Context) {

    private val appContext = context.applicationContext

    private val manager = appContext.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    private val preferencias =
        NotificationPreferences(appContext)

    init {
        crearCanal()
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Nuevas tareas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    "Avisos cuando se agrega una tarea en Firestore"
            }

            manager.createNotificationChannel(canal)
        }
    }

    fun mostrarNuevaTarea(tarea: jobs) {
        if (preferencias.estanSilenciadas()) {
            return
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            appContext.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (!manager.areNotificationsEnabled()) {
            return
        }

        val intent = Intent(
            appContext,
            ActivityJobs::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra("tarea_id", tarea.id)

            // Diferencia el PendingIntent de cada tarea.
            data = Uri.Builder()
                .scheme("ucbtareas")
                .authority("tarea")
                .appendPath(tarea.id)
                .build()
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        @Suppress("DEPRECATION")
        val builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(appContext, CANAL_ID)
            } else {
                Notification.Builder(appContext)
            }

        val notificacion = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva tarea: ${tarea.titulo}")
            .setContentText("Toca para abrir la pantalla de tareas")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                appContext.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        try {
            manager.notify(tarea.id, 0, notificacion)
        } catch (_: SecurityException) {
            // El permiso pudo revocarse después de comprobarlo.
        }
    }

    companion object {
        private const val CANAL_ID = "nuevas_tareas_e505"
    }
}