package edu.ucb.project.feature.weather.presentation.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import edu.ucb.project.feature.weather.domain.model.HourlyWeather
import edu.ucb.project.feature.weather.domain.model.Weather

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

private val indicatorBlue = Color(0xFF214D7B)
private val indicatorText = Color(0xFFD9EEFF)
private val indicatorAccent = Color(0xFF8DDFFF)

// ================================================
// 1. BRÚJULA VECTORIAL ANIMADA
// ================================================

@Composable
fun AnimatedWindCompass(
    degrees: Double,
    animated: Boolean
) {
    val rotation = remember {
        Animatable(0f)
    }

    LaunchedEffect(degrees, animated) {
        val target = degrees.toFloat()
            .takeIf { it.isFinite() }
            ?: 0f

        if (animated) {
            rotation.animateTo(
                targetValue = target,
                animationSpec = tween(1600)
            )
        } else {
            rotation.snapTo(target)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.size(90.dp)
            ) {
                val center = Offset(
                    size.width / 2f,
                    size.height / 2f
                )

                val radius = size.minDimension * 0.40f

                drawCircle(
                    color = indicatorAccent,
                    radius = radius,
                    center = center,
                    style = Stroke(2.dp.toPx())
                )

                for (i in 0 until 4) {
                    val angle = i * PI.toFloat() / 2f

                    val dx = cos(angle)
                    val dy = sin(angle)

                    drawLine(
                        color = indicatorText,
                        start = Offset(
                            center.x + dx * radius * 0.85f,
                            center.y + dy * radius * 0.85f
                        ),
                        end = Offset(
                            center.x + dx * radius,
                            center.y + dy * radius
                        ),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // La dirección meteorológica indica
                // de dónde viene el viento.
                val angle = (
                        rotation.value - 90f
                        ) * PI.toFloat() / 180f

                val dx = cos(angle)
                val dy = sin(angle)

                val tip = Offset(
                    center.x + dx * radius * 0.74f,
                    center.y + dy * radius * 0.74f
                )

                val base = Offset(
                    center.x - dx * radius * 0.37f,
                    center.y - dy * radius * 0.37f
                )

                drawLine(
                    color = Color(0xFFFFD166),
                    start = base,
                    end = tip,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Punta triangular del indicador
                val arrow = Path().apply {
                    moveTo(tip.x, tip.y)
                    lineTo(
                        tip.x - dx * 14f - dy * 7f,
                        tip.y - dy * 14f + dx * 7f
                    )
                    lineTo(
                        tip.x - dx * 14f + dy * 7f,
                        tip.y - dy * 14f - dx * 7f
                    )
                    close()
                }

                drawPath(
                    path = arrow,
                    color = Color(0xFFFFD166)
                )

                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = center
                )
            }

            Text(
                text = "N",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${degrees}°",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "La aguja señala de dónde procede el viento.",
                color = indicatorText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ================================================
// 2. GRÁFICO HORARIO QUE SE DIBUJA
// ================================================

@Composable
fun AnimatedTemperatureChart(
    points: List<HourlyWeather>,
    animated: Boolean
) {
    if (points.size < 2) {
        Text(
            text = "No hay suficientes datos horarios para el gráfico.",
            color = indicatorText
        )
        return
    }

    val progress = remember {
        Animatable(0f)
    }

    LaunchedEffect(points, animated) {
        progress.snapTo(if (animated) 0f else 1f)

        if (animated) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 2800,
                    easing = LinearEasing
                )
            )
        }
    }

    val temperatures = points.map {
        it.temperature.toFloat()
    }

    val minimum = temperatures.minOrNull() ?: 0f
    val maximum = temperatures.maxOrNull() ?: 1f

    val range = (maximum - minimum).coerceAtLeast(1f)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Pronóstico por hora · °C",
            color = indicatorText,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
        ) {
            val left = 12.dp.toPx()
            val right = size.width - 12.dp.toPx()
            val top = 14.dp.toPx()
            val bottom = size.height - 14.dp.toPx()

            val width = right - left
            val height = bottom - top

            // Líneas horizontales de referencia
            for (index in 0..3) {
                val y = top + height * index / 3f

                drawLine(
                    color = Color(0x335FCFFF),
                    start = Offset(left, y),
                    end = Offset(right, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            fun position(index: Int): Offset {
                val x = left + width *
                        index / (points.size - 1f)

                val relative = (
                        temperatures[index] - minimum
                        ) / range

                val y = bottom - relative * height

                return Offset(x, y)
            }

            val lastVisible = progress.value *
                    (points.size - 1f)

            for (index in 0 until points.lastIndex) {

                if (lastVisible <= index) break

                val from = position(index)
                val to = position(index + 1)

                val portion = (
                        lastVisible - index
                        ).coerceIn(0f, 1f)

                val currentEnd = Offset(
                    x = from.x + (to.x - from.x) * portion,
                    y = from.y + (to.y - from.y) * portion
                )

                drawLine(
                    color = Color(0xFF8DDFFF),
                    start = from,
                    end = currentEnd,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            for (index in points.indices) {
                if (index <= lastVisible) {
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = position(index)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = points.first().time.takeLast(5),
                color = indicatorText,
                fontSize = 12.sp
            )

            Text(
                text = "Mín. $minimum °C · Máx. $maximum °C",
                color = indicatorText,
                fontSize = 11.sp
            )

            Text(
                text = points.last().time.takeLast(5),
                color = indicatorText,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Valores horarios proporcionados por Open-Meteo.",
            color = indicatorText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ================================================
// 3. ALERTA METEOROLÓGICA ANIMADA
// ================================================

private data class WeatherNotice(
    val title: String,
    val message: String
)

private fun weatherNotice(
    weather: Weather
): WeatherNotice? {
    return when {

        weather.weatherCode in 95..99 ->
            WeatherNotice(
                "⛈️ Precaución por tormenta",
                "Evita espacios abiertos y busca refugio si hay actividad eléctrica."
            )

        weather.weatherCode in 65..67 ||
                weather.weatherCode in 81..82 ->
            WeatherNotice(
                "🌧️ Precaución por precipitación",
                "Presta atención a posibles acumulaciones de agua."
            )

        weather.windSpeed >= 40.0 ->
            WeatherNotice(
                "💨 Viento fuerte",
                "Evita permanecer cerca de objetos que puedan caer."
            )

        else -> null
    }
}

@Composable
fun AnimatedWeatherAlert(
    weather: Weather,
    animated: Boolean
) {
    val notice = weatherNotice(weather) ?: return

    val pulse = if (animated) {
        val transition = rememberInfiniteTransition(
            label = "WeatherAlertPulse"
        )

        val state = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600),
                repeatMode =
                    androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "AlertPulse"
        )

        state.value
    } else {
        0f
    }

    val alertColor = Color(
        red = 0.46f + 0.10f * pulse,
        green = 0.20f + 0.04f * pulse,
        blue = 0.24f + 0.04f * pulse
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = alertColor
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = notice.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = notice.message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Aviso orientativo de la aplicación. No es una alerta oficial.",
                color = Color(0xFFFFE0E0),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ================================================
// 4. PRECIPITACIÓN CON GOTAS VECTORIALES
// ================================================

@Composable
fun AnimatedPrecipitation(
    millimeters: Double?,
    animated: Boolean
) {
    if (millimeters == null) {
        Text(
            text = "Precipitación actual no disponible.",
            color = indicatorText
        )
        return
    }

    val validMm = millimeters.coerceAtLeast(0.0)

    val progress = if (animated && validMm > 0.0) {

        val transition = rememberInfiniteTransition(
            label = "PrecipitationTransition"
        )

        val state = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000,
                    easing = LinearEasing
                )
            ),
            label = "PrecipitationProgress"
        )

        state.value
    } else {
        0f
    }

    // Número de gotas de la ilustración.
    // No es una escala meteorológica oficial.
    val dropCount = if (validMm <= 0.0) {
        0
    } else {
        (5 + (validMm * 4).toInt())
            .coerceIn(5, 30)
    }

    Column {

        Text(
            text = "$validMm mm",
            color = Color.White,
            fontSize = 24.sp
        )

        Spacer(Modifier.height(10.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    color = Color(0xFF102F53),
                    shape = RoundedCornerShape(14.dp)
                )
        ) {

            for (index in 0 until dropCount) {

                val phase = if (animated) {
                    (progress * 2f +
                            index.toFloat() / dropCount) % 1f
                } else {
                    index.toFloat() / dropCount
                }

                val xFraction = (
                        (index * 37) % 97
                        ) / 97f

                val x = size.width *
                        (0.06f + 0.88f * xFraction)

                val y = size.height * phase

                drawLine(
                    color = Color(0xFF8DDFFF),
                    start = Offset(x, y),
                    end = Offset(x - 5f, y + 13f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    alpha = 0.5f + phase * 0.5f
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (validMm == 0.0) {
                "No se registra precipitación en el intervalo actual."
            } else {
                "Las gotas representan visualmente la precipitación medida."
            },
            color = indicatorText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ================================================
// CONTENEDOR COMPARTIDO DE LOS INDICADORES
// ================================================

@Composable
fun WeatherIndicatorCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = indicatorBlue,
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}
