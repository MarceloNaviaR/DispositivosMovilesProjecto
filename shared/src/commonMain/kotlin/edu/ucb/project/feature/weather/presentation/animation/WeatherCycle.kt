package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

@Composable
fun weatherCycle(): Float {
    val transition = rememberInfiniteTransition(
        label = "WeatherCycle"
    )

    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8_000,
                easing = LinearEasing
            )
        ),
        label = "WeatherProgress"
    )

    return progress.value
}
