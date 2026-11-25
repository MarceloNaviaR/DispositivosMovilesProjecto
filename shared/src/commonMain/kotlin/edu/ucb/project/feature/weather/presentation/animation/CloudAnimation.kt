
package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun CloudAnimation() {
    val progress = weatherCycle()

    val active = progress < 0.5f
    val phase = if (active) progress / 0.5f else 0f

    val movement = if (active) {
        sin(phase * PI.toFloat())
    } else {
        0f
    }

    val verticalMovement = if (active) {
        sin(phase * PI.toFloat() * 2f)
    } else {
        0f
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {

        // Nube secundaria
        RainStyleCloud(
            fontSize = 61,
            modifier = Modifier.graphicsLayer {
                translationX =
                    27f - movement * 30f

                translationY =
                    -20f + verticalMovement * 5f

                alpha = 0.75f
            }
        )

        // Nube principal
        RainStyleCloud(
            fontSize = 76,
            modifier = Modifier.graphicsLayer {
                translationX =
                    -17f + movement * 25f

                translationY =
                    15f - verticalMovement * 3f
            }
        )
    }
}
