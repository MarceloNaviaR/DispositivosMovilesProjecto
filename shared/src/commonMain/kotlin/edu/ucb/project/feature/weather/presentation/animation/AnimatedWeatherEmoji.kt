package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedWeatherEmoji(
    weatherCode: Int,
    windSpeed: Double,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(28.dp)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF347CB5),
            Color(0xFF174A7B),
            Color(0xFF071A35)
        )
    )

    val glowGradient = Brush.radialGradient(
        colors = listOf(
            Color(0x5579D9FF),
            Color(0x223AA8E8),
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .size(176.dp)
            .shadow(
                elevation = 14.dp,
                shape = containerShape
            )
            .background(
                brush = backgroundGradient,
                shape = containerShape
            )
            .border(
                width = 2.dp,
                color = Color(0xFF76D5FF),
                shape = containerShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(146.dp)
                .background(
                    brush = glowGradient,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                weatherCode in 95..99 ->
                    StormAnimation()

                weatherCode in 71..77 ||
                        weatherCode in 85..86 ->
                    SnowAnimation()

                weatherCode in 51..67 ||
                        weatherCode in 80..82 ->
                    RainAnimation()

                weatherCode in 45..48 ->
                    FogAnimation()

                windSpeed >= 40.0 ->
                    WindAnimation()

                weatherCode == 0 ->
                    SunAnimation()

                else ->
                    CloudAnimation()
            }
        }
    }
}
