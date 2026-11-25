package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import edu.ucb.project.feature.weather.domain.model.Weather

private enum class WeatherDisplayState {
    LOADING,
    SUCCESS,
    EMPTY
}

@Composable
fun WeatherAnimation(
    weather: Weather?,
    loading: Boolean,
    loadingContent: @Composable () -> Unit,
    weatherContent: @Composable (Weather) -> Unit,
    emptyContent: @Composable () -> Unit
) {
    val displayState = when {
        loading -> WeatherDisplayState.LOADING
        weather != null -> WeatherDisplayState.SUCCESS
        else -> WeatherDisplayState.EMPTY
    }

    AnimatedContent(
        targetState = displayState,
        transitionSpec = {
            (
                    fadeIn(
                        animationSpec = tween(500)
                    ) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(500)
                    )
                    ) togetherWith fadeOut(
                animationSpec = tween(250)
            )
        },
        label = "WeatherStateAnimation"
    ) { targetState ->

        when (targetState) {
            WeatherDisplayState.LOADING -> {
                loadingContent()
            }

            WeatherDisplayState.SUCCESS -> {
                weather?.let {
                    weatherContent(it)
                }
            }

            WeatherDisplayState.EMPTY -> {
                emptyContent()
            }
        }
    }
}
