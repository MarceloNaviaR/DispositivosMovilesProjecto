package edu.ucb.project.feature.notification.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun RequestNotificationPermission(
    onRequestPermissionResult: (Boolean) -> Unit,
    content: @Composable (requestPermission: () -> Unit) -> Unit
)
