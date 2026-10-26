package edu.ucb.project.feature.notification.presentation

import androidx.compose.runtime.Composable

@Composable
actual fun RequestNotificationPermission(
    onRequestPermissionResult: (Boolean) -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
) {
    val requestPermission: () -> Unit = {
        onRequestPermissionResult(true)
    }

    content(requestPermission)
}
