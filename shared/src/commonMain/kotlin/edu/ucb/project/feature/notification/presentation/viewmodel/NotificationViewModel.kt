package edu.ucb.project.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import edu.ucb.project.feature.notification.domain.NotificationService
import edu.ucb.project.feature.notification.presentation.state.NotificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationViewModel(
    private val notificationService: NotificationService
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    init {
        checkPermission()
    }

    fun checkPermission() {
        val granted = notificationService.hasNotificationPermission()
        _state.update { it.copy(hasPermission = granted) }
    }

    fun updatePermissionState(isGranted: Boolean) {
        _state.update { it.copy(hasPermission = isGranted) }
    }

    fun sendNotification(
        title: String = "Notificación Local",
        message: String = "Hola desde la app"
    ) {
        notificationService.showNotification(title, message)
        _state.update { it.copy(lastNotificationMessage = message) }
    }

    fun hasPermission(): Boolean {
        val granted = notificationService.hasNotificationPermission()
        _state.update { it.copy(hasPermission = granted) }
        return granted
    }
}
