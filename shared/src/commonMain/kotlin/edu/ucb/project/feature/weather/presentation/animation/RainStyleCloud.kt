package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun RainStyleCloud(
    modifier: Modifier = Modifier,
    fontSize: Int = 76
) {
    Text(
        text = "☁️",
        fontSize = fontSize.sp,
        modifier = modifier
    )
}
