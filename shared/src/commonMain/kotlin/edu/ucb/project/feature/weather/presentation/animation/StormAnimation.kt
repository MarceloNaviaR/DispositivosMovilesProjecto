package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun StormAnimation() {
    val progress = weatherCycle()

    val active = progress < 0.5f
    val phase = if (active) progress / 0.5f else 0f

    val shake = if (active) {
        sin(phase * PI.toFloat() * 18f) * 3.5f
    } else {
        0f
    }

    val flash = if (active) {
        val brightness = sin(
            phase * PI.toFloat() * 8f
        )

        (
                brightness * brightness *
                        sin(
                            phase * PI.toFloat()
                        ).coerceAtLeast(0f)
                ).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        RainStyleCloud(
            modifier = Modifier.graphicsLayer {
                translationX = shake
                translationY = -18f
            }
        )

        Canvas(
            modifier = Modifier.size(120.dp)
        ) {
            val factor = size.minDimension / 120f

            val lightning = Path().apply {
                moveTo(
                    66f * factor,
                    65f * factor
                )

                lineTo(
                    48f * factor,
                    88f * factor
                )

                lineTo(
                    63f * factor,
                    86f * factor
                )

                lineTo(
                    51f * factor,
                    113f * factor
                )
            }

            // Resplandor del relámpago
            drawCircle(
                color = Color(0xFFFFE69B),
                radius = 30f * factor,
                center = Offset(
                    58f * factor,
                    90f * factor
                ),
                alpha = flash * 0.18f
            )

            // Trazo luminoso exterior
            drawPath(
                path = lightning,
                color = Color(0xFFFFD166),
                alpha = flash * 0.45f,
                style = Stroke(
                    width = 13f * factor,
                    cap = StrokeCap.Round
                )
            )

            // Trazo central
            drawPath(
                path = lightning,
                color = Color(0xFFFFEC91),
                alpha = flash,
                style = Stroke(
                    width = 4.5f * factor,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}
