package edu.ucb.project.feature.notification.domain

interface NotificationService {
    fun createNotificationChannel()
    fun showNotification(title: String, message: String)
    fun hasNotificationPermission(): Boolean
}
