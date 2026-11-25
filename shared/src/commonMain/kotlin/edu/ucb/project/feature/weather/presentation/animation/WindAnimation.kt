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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WindAnimation() {

    val transition = rememberInfiniteTransition(
        label = "WindTransition"
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
        label = "WindProgress"
    )

    Canvas(
        modifier = Modifier.size(120.dp)
    ) {
        val factor = size.minDimension / 120f
        val phase = progress.value * 2f * PI.toFloat()

        // 1. ÁRBOL PEQUEÑO FLEXIONADO POR EL VIENTO

        val treeSway = (
                6f + sin(phase) * 4f
                ) * factor

        val trunk = Path().apply {
            moveTo(22f * factor, 111f * factor)

            cubicTo(
                20f * factor,
                89f * factor,
                23f * factor,
                67f * factor,
                26f * factor + treeSway,
                42f * factor
            )
        }

        drawPath(
            path = trunk,
            color = Color(0xFFB88955),
            style = Stroke(
                width = 5f * factor,
                cap = StrokeCap.Round
            )
        )

        // Ramas y follaje balanceándose
        val crownX = 29f * factor + treeSway
        val crownY = 37f * factor

        val foliage = listOf(
            Offset(-12f, 3f),
            Offset(1f, -9f),
            Offset(13f, -2f),
            Offset(7f, 11f),
            Offset(-7f, 12f)
        )

        foliage.forEachIndexed { index, position ->

            drawCircle(
                color = if (index % 2 == 0) {
                    Color(0xFF67B778)
                } else {
                    Color(0xFF8DCE83)
                },
                radius = (11f + index % 2 * 2f) * factor,
                center = Offset(
                    crownX + position.x * factor,
                    crownY + position.y * factor
                ),
                alpha = 0.88f
            )
        }

        // 2. RÁFAGAS QUE RECORREN TODO EL CUADRO

        for (index in 0 until 4) {

            val t = (
                    progress.value * 1.25f +
                            index * 0.27f
                    ) % 1f

            val x = -40f + t * 185f

            val baseY = 23f + index * 25f

            val wave = sin(
                phase + index * 0.8f
            ) * 5f

            val gust = Path().apply {

                moveTo(
                    x * factor,
                    (baseY + wave) * factor
                )

                cubicTo(
                    (x + 15f) * factor,
                    (baseY - 8f + wave) * factor,

                    (x + 31f) * factor,
                    (baseY + 8f + wave) * factor,

                    (x + 48f) * factor,
                    (baseY + wave) * factor
                )
            }

            val opacity = (
                    sin(t * PI.toFloat())
                        .coerceAtLeast(0f) * 0.75f
                    )

            drawPath(
                path = gust,
                color = Color(0xFFE9F8FF),
                alpha = opacity,
                style = Stroke(
                    width = (2.4f + index % 2) * factor,
                    cap = StrokeCap.Round
                )
            )
        }

        // 3. HOJAS ARRASTRADAS POR EL VIENTO

        for (index in 0 until 9) {

            val t = (
                    progress.value +
                            index / 9f
                    ) % 1f

            // Las hojas cruzan de izquierda a derecha
            val leafX = -12f + t * 145f

            val leafY =
                18f +
                        (index * 17f) % 85f +
                        sin(
                            t * PI.toFloat() * 4f +
                                    index
                        ) * 8f

            // Desvanecimiento al entrar y salir
            val opacity = (
                    sin(t * PI.toFloat())
                    ).coerceIn(0f, 1f)

            val rotationDegrees =
                t * 390f + index * 42f

            val leafColor = when (index % 3) {
                0 -> Color(0xFF8BCF75)
                1 -> Color(0xFFFFD16B)
                else -> Color(0xFF5FBF8F)
            }

            val center = Offset(
                leafX * factor,
                leafY * factor
            )

            rotate(
                degrees = rotationDegrees,
                pivot = center
            ) {

                val leaf = Path().apply {

                    moveTo(
                        center.x - 6f * factor,
                        center.y
                    )

                    quadraticTo(
                        center.x,
                        center.y - 7f * factor,
                        center.x + 7f * factor,
                        center.y
                    )

                    quadraticTo(
                        center.x,
                        center.y + 6f * factor,
                        center.x - 6f * factor,
                        center.y
                    )

                    close()
                }

                drawPath(
                    path = leaf,
                    color = leafColor,
                    alpha = opacity
                )

                // Nervadura de cada hoja
                drawLine(
                    color = Color(0xFF286B43),
                    start = Offset(
                        center.x - 5f * factor,
                        center.y
                    ),
                    end = Offset(
                        center.x + 5f * factor,
                        center.y
                    ),
                    strokeWidth = 1f * factor,
                    alpha = opacity
                )
            }
        }
    }
}
