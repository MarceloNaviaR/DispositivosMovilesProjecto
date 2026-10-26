package edu.ucb.project.feature.tareas

import android.content.Context

class NotificationPreferences(context: Context) {

    private val preferencias =
        context.applicationContext.getSharedPreferences(
            "tareas_notificaciones",
            Context.MODE_PRIVATE
        )

    fun estanSilenciadas(): Boolean {
        return preferencias.getBoolean(
            "silenciadas",
            false
        )
    }

    fun establecerSilenciadas(valor: Boolean) {
        preferencias.edit()
            .putBoolean("silenciadas", valor)
            .apply()
    }
}