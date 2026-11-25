package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun RainAnimation() {
    val progress = weatherCycle()

    val active = progress < 0.5f
    val phase = if (active) progress / 0.5f else 0f

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        RainStyleCloud(
            fontSize = 68,
            modifier = Modifier.graphicsLayer {
                translationY = -22f

                translationX = if (active) {
                    sin(
                        phase * PI.toFloat() * 2f
                    ) * 3f
                } else {
                    0f
                }
            }
        )

        repeat(4) { index ->

            val dropProgress = (
                    phase * 3f +
                            index * 0.25f
                    ) % 1f

            Text(
                text = "💧",
                fontSize = 19.sp,
                modifier = Modifier.graphicsLayer {

                    translationX =
                        (index - 1.5f) * 28f

                    translationY =
                        12f + dropProgress * 72f

                    alpha = if (active) {
                        (
                                1f - dropProgress * 0.75f
                                ).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                }
            )
        }
    }
}
