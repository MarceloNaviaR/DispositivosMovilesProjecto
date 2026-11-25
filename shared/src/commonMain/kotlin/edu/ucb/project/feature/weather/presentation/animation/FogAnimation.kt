package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FogAnimation() {

    // Ciclo lento e independiente de 8 segundos
    val transition = rememberInfiniteTransition(
        label = "FogTransition"
    )

    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = LinearEasing
            )
        ),
        label = "FogProgress"
    )

    Canvas(
        modifier = Modifier.size(120.dp)
    ) {
        val factor = size.minDimension / 120f
        val phase = progress.value * 2f * PI.toFloat()

        // Halo ambiental, suave y semitransparente
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x447FD5FF),
                    Color(0x2265B9E5),
                    Color.Transparent
                ),
                center = Offset(
                    60f * factor,
                    65f * factor
                ),
                radius = 67f * factor
            ),
            radius = 67f * factor,
            center = Offset(
                60f * factor,
                65f * factor
            )
        )

        // Siete capas anchas de niebla
        for (index in 0 until 7) {

            val direction =
                if (index % 2 == 0) 1f else -1f

            val movement = sin(
                phase + index * 0.63f
            ) * (7f + index * 1.15f) * direction

            val verticalMovement = sin(
                phase + index * 0.45f
            ) * 2.2f

            val widths = listOf(
                101f, 86f, 112f, 94f,
                106f, 81f, 98f
            )

            val width = widths[index]
            val height = 13f + (index % 3) * 3.5f

            val x = (
                    (120f - width) / 2f + movement
                    ) * factor

            val y = (
                    9f + index * 14.5f +
                            verticalMovement
                    ) * factor

            val baseOpacity = (
                    0.46f +
                            sin(phase + index * 0.78f) * 0.15f
                    ).coerceIn(0.22f, 0.68f)

            val fogColor = when (index % 3) {
                0 -> Color(0xFFF5FBFF)
                1 -> Color(0xFFCBEFFF)
                else -> Color(0xFFE1F5FF)
            }

            // Halo exterior para suavizar los bordes
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        fogColor.copy(
                            alpha = baseOpacity * 0.18f
                        ),
                        fogColor.copy(
                            alpha = baseOpacity * 0.34f
                        ),
                        fogColor.copy(
                            alpha = baseOpacity * 0.18f
                        ),
                        Color.Transparent
                    ),
                    startX = x - 5f * factor,
                    endX = x + (width + 5f) * factor
                ),
                topLeft = Offset(
                    x - 5f * factor,
                    y - 5f * factor
                ),
                size = Size(
                    (width + 10f) * factor,
                    (height + 10f) * factor
                ),
                cornerRadius = CornerRadius(
                    (height + 10f) * factor / 2f
                )
            )

            // Franja central de la bruma
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        fogColor.copy(
                            alpha = baseOpacity * 0.58f
                        ),
                        fogColor.copy(
                            alpha = baseOpacity
                        ),
                        fogColor.copy(
                            alpha = baseOpacity * 0.58f
                        ),
                        Color.Transparent
                    ),
                    startX = x,
                    endX = x + width * factor
                ),
                topLeft = Offset(x, y),
                size = Size(
                    width * factor,
                    height * factor
                ),
                cornerRadius = CornerRadius(
                    height * factor / 2f
                )
            )
        }

        // Partículas de humedad que flotan
        for (index in 0 until 10) {

            val wave = sin(
                phase + index * 0.8f
            )

            val x = (
                    14f +
                            (index * 23f) % 93f +
                            wave * 5f
                    ) * factor

            val y = (
                    17f +
                            (index * 19f) % 91f +
                            sin(phase + index) * 3f
                    ) * factor

            drawCircle(
                color = Color(0xFFE7F8FF),
                radius = (
                        1.2f + index % 3
                        ) * factor,
                center = Offset(x, y),
                alpha = 0.15f +
                        (wave + 1f) * 0.13f
            )
        }
    }
}
