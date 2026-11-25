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
fun SunAnimation() {
    val progress = weatherCycle()

    val active = progress < 0.5f
    val phase = if (active) progress / 0.5f else 0f

    val pulse = if (active) {
        sin(phase * PI.toFloat() * 2f)
    } else {
        0f
    }

    val growth = 1f + pulse * pulse * 0.22f

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "☀️",
            fontSize = 72.sp,
            modifier = Modifier.graphicsLayer {
                scaleX = growth
                scaleY = growth

                rotationZ = if (active) {
                    sin(
                        phase * PI.toFloat()
                    ) * 24f
                } else {
                    0f
                }
            }
        )
    }
}
