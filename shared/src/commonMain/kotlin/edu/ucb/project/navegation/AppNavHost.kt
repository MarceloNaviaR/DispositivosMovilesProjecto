
package edu.ucb.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import org.koin.compose.viewmodel.koinViewModel

import edu.ucb.project.feature.auth.login.presentation.viewmodel.LoginViewModel
import edu.ucb.project.feature.auth.login.presentation.screen.LoginScreen
import edu.ucb.project.feature.weather.presentation.screen.WeatherScreen
import edu.ucb.project.feature.notification.presentation.screen.NotificationScreen
import edu.ucb.project.roomastruth.presentation.screen.ConfigScreen

@Composable
fun AppNavHost(
    weatherNavigationRequest: Int = 0
) {
    val navController = rememberNavController()

    // Detecta cuando el botón del clima es presionado.
    LaunchedEffect(weatherNavigationRequest) {
        if (weatherNavigationRequest > 0) {
            navController.navigate(NavRoute.Weather) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoute.Login,

        // Animación de entrada a una pantalla.
        enterTransition = {
            fadeIn(
                animationSpec = tween(500)
            ) + slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(500)
            )
        },

        // Animación de salida.
        exitTransition = {
            fadeOut(
                animationSpec = tween(250)
            )
        },

        // Animación al regresar.
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(400)
            )
        },

        popExitTransition = {
            fadeOut(
                animationSpec = tween(250)
            ) + slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(400)
            )
        }
    ) {

        // LOGIN
        composable<NavRoute.Login> {
            val viewModel = koinViewModel<LoginViewModel>()

            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // WEATHER
        composable<NavRoute.Weather> {
            WeatherScreen()
        }

        // NOTIFICACIONES
        composable<NavRoute.Notification> {
            NotificationScreen(
                navController = navController
            )
        }

        // CONFIGURACIÓN
        composable<NavRoute.Configuration> {
            ConfigScreen(
                navController = navController
            )
        }
    }
}
