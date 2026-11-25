package edu.ucb.project.feature.weather.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import org.koin.compose.viewmodel.koinViewModel

import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.presentation.viewmodel.WeatherViewModel
import edu.ucb.project.feature.weather.presentation.composable.WeatherContent
import edu.ucb.project.feature.weather.presentation.composable.WeatherAnimationLab

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showAnimationLab by remember {
        mutableStateOf(false)
    }

    var lastWeather by remember(viewModel) { mutableStateOf<Weather?>(null) }
    val latestWeather = state.weather
    val displayedWeather = latestWeather ?: lastWeather

    SideEffect {
        if (latestWeather != null) lastWeather = latestWeather
    }

    val refreshWeather = {
        viewModel.loadWeather(
            latitude = -17.3935,
            longitude = -66.1570
        )
        Unit
    }

    LaunchedEffect(viewModel) {
        refreshWeather()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = showAnimationLab,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                if (targetState) {
                    (
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(550)
                            ) + fadeIn(tween(400))
                            ) togetherWith (
                            slideOutHorizontally(
                                targetOffsetX = { -it / 4 },
                                animationSpec = tween(450)
                            ) + fadeOut(tween(300))
                            )
                } else {
                    (
                            slideInHorizontally(
                                initialOffsetX = { -it / 4 },
                                animationSpec = tween(500)
                            ) + fadeIn(tween(400))
                            ) togetherWith (
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(450)
                            ) + fadeOut(tween(300))
                            )
                }
            },
            label = "WeatherLabNavigation"
        ) { labVisible ->

            if (labVisible) {
                WeatherAnimationLab(
                    weather = displayedWeather,
                    onBack = {
                        showAnimationLab = false
                    }
                )
            } else {
                WeatherContent(
                    weather = displayedWeather,
                    loading = state.loading,
                    onOpenLab = {
                        showAnimationLab = true
                    },
                    onRefresh = refreshWeather
                )
            }
        }

        state.error?.let { error ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFF713448)
            ) {
                Text(
                    text = "Error al consultar el clima: $error",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}