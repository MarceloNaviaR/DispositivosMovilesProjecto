package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal val VectorBlue = Color(0xFF8DDFFF)
internal val VectorWhite = Color(0xFFF1FAFF)
internal val VectorYellow = Color(0xFFFFD166)

internal fun DrawScope.drawWeatherCloud(
    x: Float,
    y: Float,
    width: Float,
    fill: Color = Color(0xFF245781),
    outline: Color = VectorWhite,
    alpha: Float = 1f
) {
    val h = width * 0.61f

    val path = Path().apply {
        moveTo(x + width * 0.21f, y + h * 0.84f)

        cubicTo(
            x - width * 0.03f, y + h * 0.81f,
            x - width * 0.03f, y + h * 0.41f,
            x + width * 0.20f, y + h * 0.37f
        )

        cubicTo(
            x + width * 0.26f, y + h * 0.05f,
            x + width * 0.59f, y - h * 0.03f,
            x + width * 0.72f, y + h * 0.26f
        )

        cubicTo(
            x + width * 1.08f, y + h * 0.22f,
            x + width * 1.12f, y + h * 0.79f,
            x + width * 0.84f, y + h * 0.84f
        )

        close()
    }

    drawPath(
        path = path,
        color = fill,
        alpha = alpha
    )

    drawPath(
        path = path,
        color = outline,
        alpha = alpha,
        style = Stroke(
            width = 3.8f,
            cap = StrokeCap.Round
        )
    )
}

internal fun DrawScope.drawWeatherWave(
    startX: Float,
    y: Float,
    length: Float,
    progress: Float,
    amplitude: Float = 8f,
    color: Color = VectorBlue,
    alpha: Float = 1f
) {
    val visibleLength = length * progress.coerceIn(0f, 1f)

    if (visibleLength <= 0f) return

    val path = Path()

    var x = 0f

    path.moveTo(startX, y)

    while (x <= visibleLength) {
        val positionY = y +
                sin(x / length * PI.toFloat() * 2f) * amplitude

        path.lineTo(
            startX + x,
            positionY
        )

        x += 3f
    }

    drawPath(
        path = path,
        color = color,
        alpha = alpha,
        style = Stroke(
            width = 4.5f,
            cap = StrokeCap.Round
        )
    )
}

internal fun DrawScope.drawSnowflake(
    center: Offset,
    radius: Float,
    rotationDegrees: Float,
    color: Color = VectorWhite,
    alpha: Float = 1f
) {
    val rotation = rotationDegrees *
            PI.toFloat() / 180f

    for (axis in 0 until 3) {
        val angle = rotation +
                axis * PI.toFloat() / 3f

        val dx = cos(angle) * radius
        val dy = sin(angle) * radius

        drawLine(
            color = color,
            start = Offset(
                center.x - dx,
                center.y - dy
            ),
            end = Offset(
                center.x + dx,
                center.y + dy
            ),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round,
            alpha = alpha
        )
    }
}
