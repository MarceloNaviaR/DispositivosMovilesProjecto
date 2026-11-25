
package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SnowAnimation() {
    val progress = weatherCycle()

    val active = progress < 0.5f
    val phase = if (active) progress / 0.5f else 0f

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        RainStyleCloud(
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )

        Canvas(
            modifier = Modifier.size(120.dp)
        ) {

            if (active) {
                val factor = size.minDimension / 120f

                for (index in 0 until 6) {

                    val snowPhase = (
                            phase * (
                                    1.8f + index * 0.12f
                                    ) + index * 0.16f
                            ) % 1f

                    val x = (
                            23f + index * 15f +
                                    sin(
                                        snowPhase * 9f + index
                                    ) * 8f
                            ) * factor

                    val y = (
                            53f + snowPhase * 62f
                            ) * factor

                    val radius = (
                            4f + index % 3
                            ) * factor

                    val rotation =
                        snowPhase * PI.toFloat()

                    val opacity = (
                            1f - snowPhase * 0.55f
                            ).coerceIn(0f, 1f)

                    for (axis in 0 until 3) {

                        val angle = rotation +
                                axis * PI.toFloat() / 3f

                        val dx = cos(angle) * radius
                        val dy = sin(angle) * radius

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                x - dx,
                                y - dy
                            ),
                            end = Offset(
                                x + dx,
                                y + dy
                            ),
                            strokeWidth = 2f * factor,
                            cap = StrokeCap.Round,
                            alpha = opacity
                        )
                    }
                }
            }
        }
    }
}
