package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun WeatherZoomAnimation(
    onAnimationFinished: () -> Unit,
    content: @Composable () -> Unit
) {
    val scale = remember {
        Animatable(0.65f)
    }

    val opacity = remember {
        Animatable(0.3f)
    }

    LaunchedEffect(Unit) {

        // ETAPA 1: Acercamiento hacia la pantalla
        scale.animateTo(
            targetValue = 1.35f,
            animationSpec = tween(
                durationMillis = 700
            )
        )

        // ETAPA 2: Regreso con efecto de rebote
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // ETAPA 3: Mostrar las recomendaciones
        onAnimationFinished()
    }

    LaunchedEffect(Unit) {
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 450
            )
        )
    }

    Box(
        modifier = Modifier.graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            alpha = opacity.value

            transformOrigin = TransformOrigin.Center
        }
    ) {
        content()
    }
}
