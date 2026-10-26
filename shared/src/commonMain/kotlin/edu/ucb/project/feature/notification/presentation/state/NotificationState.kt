package edu.ucb.project.feature.notification.presentation.state

data class NotificationState(
    val hasPermission: Boolean = false,
    val lastNotificationMessage: String? = null
)
