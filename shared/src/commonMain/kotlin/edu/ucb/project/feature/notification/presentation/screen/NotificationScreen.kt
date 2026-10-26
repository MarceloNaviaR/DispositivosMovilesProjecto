package edu.ucb.project.feature.notification.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import edu.ucb.project.feature.notification.presentation.RequestNotificationPermission
import edu.ucb.project.feature.notification.presentation.composable.NotificationContent
import edu.ucb.project.feature.notification.presentation.viewmodel.NotificationViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    RequestNotificationPermission(
        onRequestPermissionResult = { isGranted ->
            viewModel.updatePermissionState(isGranted)
            if (isGranted) {
                viewModel.sendNotification(
                    title = "Notificación Local",
                    message = "Hola desde la app"
                )
            }
        }
    ) { requestPermission ->
        NotificationContent(
            hasPermission = state.hasPermission,
            lastNotificationMessage = state.lastNotificationMessage,
            onSendNotificationClick = {
                if (viewModel.hasPermission()) {
                    viewModel.sendNotification(
                        title = "Notificación Local",
                        message = "Hola desde la app"
                    )
                } else {
                    requestPermission()
                }
            },
            onRequestPermissionClick = {
                requestPermission()
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}
