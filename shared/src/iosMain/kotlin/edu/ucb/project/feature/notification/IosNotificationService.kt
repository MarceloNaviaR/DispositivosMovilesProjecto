package edu.ucb.project.feature.notification

import edu.ucb.project.feature.notification.domain.NotificationService

class IosNotificationService : NotificationService {
    override fun createNotificationChannel() {}
    override fun showNotification(title: String, message: String) {}
    override fun hasNotificationPermission(): Boolean = true
}
