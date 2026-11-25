package edu.ucb.project.feature.weather.presentation.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.presentation.getWeatherRecommendation
import edu.ucb.project.feature.weather.presentation.animation.AnimatedWeatherEmoji
import edu.ucb.project.feature.weather.presentation.animation.WeatherAnimation
import edu.ucb.project.feature.weather.presentation.animation.WeatherZoomAnimation
import edu.ucb.project.feature.weather.presentation.animation.WeatherAnimationsGallery

import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val panelColor = Color(0xFF173B64)
private val panelLight = Color(0xFF214D7B)
private val panelText = Color(0xFFDAEDFF)
private val accentBlue = Color(0xFF8DDFFF)

@Composable
fun WeatherContent(
    weather: Weather?,
    loading: Boolean,
    onOpenLab: () -> Unit = {},
    onRefresh: (() -> Unit)? = null
) {
    var animationsEnabled by remember {
        mutableStateOf(true)
    }

    var showGallery by remember {
        mutableStateOf(false)
    }

    var showRecommendation by remember {
        mutableStateOf(false)
    }

    var detailsExpanded by remember {
        mutableStateOf(false)
    }

    var animationKey by remember {
        mutableIntStateOf(0)
    }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    var scrollTopInRoot by remember {
        mutableFloatStateOf(0f)
    }

    var thermometerTopInRoot by remember {
        mutableFloatStateOf(Float.NaN)
    }

    // Se desplaza al comienzo de los indicadores,
    // no directamente al final de toda la página.
    LaunchedEffect(
        detailsExpanded,
        animationsEnabled,
        showGallery
    ) {
        if (detailsExpanded && !showGallery) {

            delay(
                if (animationsEnabled) 520L else 60L
            )

            if (thermometerTopInRoot.isFinite()) {

                val marginPx = with(density) {
                    12.dp.toPx()
                }

                val distance =
                    thermometerTopInRoot -
                            scrollTopInRoot -
                            marginPx

                val destination = (
                        scrollState.value +
                                distance.roundToInt()
                        ).coerceIn(0, scrollState.maxValue)

                if (animationsEnabled) {
                    scrollState.animateScrollTo(
                        value = destination,
                        animationSpec = tween(
                            durationMillis = 850,
                            easing = FastOutSlowInEasing
                        )
                    )
                } else {
                    scrollState.scrollTo(destination)
                }
            }
        }
    }

    AnimatedContent(
        targetState = showGallery,
        transitionSpec = {
            if (animationsEnabled) {
                (
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(550)
                        ) + fadeIn()
                        ) togetherWith (
                        slideOutVertically(
                            targetOffsetY = { -it / 3 },
                            animationSpec = tween(450)
                        ) + fadeOut()
                        )
            } else {
                EnterTransition.None togetherWith
                        ExitTransition.None
            }
        },
        label = "WeatherGalleryTransition"
    ) { galleryVisible ->

        if (galleryVisible) {

            WeatherAnimationsGallery(
                onBack = {
                    showGallery = false
                }
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        scrollTopInRoot =
                            it.positionInRoot().y
                    }
                    .verticalScroll(scrollState)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 30.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "COCHABAMBA · BOLIVIA",
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = accentBlue
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Reporte interactivo del clima",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = panelColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Modo animado",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (animationsEnabled) {
                                    "Visualización interactiva"
                                } else {
                                    "Visualización estática"
                                },
                                color = panelText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = animationsEnabled,
                            onCheckedChange = {
                                animationsEnabled = it
                                showRecommendation = false
                                detailsExpanded = false
                                animationKey++
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                AnimatedVisibility(
                    visible = loading && weather != null,
                    enter = if (animationsEnabled) fadeIn() + expandVertically()
                    else EnterTransition.None,
                    exit = if (animationsEnabled) fadeOut() + shrinkVertically()
                    else ExitTransition.None
                ) {
                    Column(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Text("Actualizando el clima…", color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = accentBlue
                        )
                    }
                }

                if (onRefresh != null && (!loading || weather != null)) {
                    Button(
                        onClick = onRefresh,
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF287AB9)
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("↻ Actualizar clima")
                    }
                    Spacer(Modifier.height(12.dp))
                }

                WeatherAnimation(
                    weather = weather,
                    loading = loading && weather == null,

                    loadingContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = accentBlue
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "Consultando el clima...",
                                color = Color.White
                            )
                        }
                    },

                    weatherContent = { currentWeather ->

                        val recommendation =
                            remember(currentWeather) {
                                getWeatherRecommendation(
                                    currentWeather
                                )
                            }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            if (animationsEnabled) {
                                key(animationKey) {
                                    WeatherZoomAnimation(
                                        onAnimationFinished = {
                                            showRecommendation = true
                                        }
                                    ) {
                                        WeatherMainCard(
                                            weather = currentWeather,
                                            animated = true
                                        )
                                    }
                                }
                            } else {
                                WeatherMainCard(
                                    weather = currentWeather,
                                    animated = false
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            AnimatedVisibility(
                                visible = !animationsEnabled ||
                                        showRecommendation,
                                enter = if (animationsEnabled) {
                                    fadeIn(tween(500)) +
                                            slideInVertically(
                                                initialOffsetY = { it / 4 },
                                                animationSpec = tween(500)
                                            )
                                } else {
                                    EnterTransition.None
                                },
                                exit = fadeOut(tween(200))
                            ) {
                                Column {

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = panelColor
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(20.dp)
                                        ) {
                                            Text(
                                                text =
                                                    recommendation.icon +
                                                            " " +
                                                            recommendation.title,
                                                color = Color.White,
                                                style =
                                                    MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(Modifier.height(12.dp))

                                            HorizontalDivider(
                                                color = Color(0x445FD0FF)
                                            )

                                            Spacer(Modifier.height(12.dp))

                                            WeatherTypewriterText(
                                                text = recommendation.message,
                                                animated = animationsEnabled,
                                                restartKey = animationKey
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    AnimatedWeatherAlert(
                                        weather = currentWeather,
                                        animated = animationsEnabled
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    val arrowRotation by animateFloatAsState(
                                        targetValue =
                                            if (detailsExpanded) 180f else 0f,
                                        animationSpec = tween(
                                            if (animationsEnabled) 300 else 0
                                        ),
                                        label = "DetailsArrow"
                                    )

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                detailsExpanded =
                                                    !detailsExpanded

                                                if (!detailsExpanded) {
                                                    thermometerTopInRoot =
                                                        Float.NaN
                                                }
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = panelColor
                                        )
                                    ) {

                                        Column(
                                            modifier = Modifier.padding(18.dp)
                                        ) {

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment =
                                                    Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "Detalles del clima",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier =
                                                        Modifier.weight(1f)
                                                )

                                                Text(
                                                    "⌄",
                                                    color = Color.White,
                                                    fontSize = 28.sp,
                                                    modifier =
                                                        Modifier.graphicsLayer {
                                                            rotationZ =
                                                                arrowRotation
                                                        }
                                                )
                                            }

                                            Text(
                                                "Toca para explorar los indicadores",
                                                color = panelText,
                                                style =
                                                    MaterialTheme.typography.bodySmall
                                            )

                                            AnimatedVisibility(
                                                visible = detailsExpanded,
                                                enter = if (animationsEnabled) {
                                                    expandVertically(
                                                        tween(450)
                                                    ) + fadeIn()
                                                } else {
                                                    EnterTransition.None
                                                },
                                                exit = if (animationsEnabled) {
                                                    shrinkVertically(
                                                        tween(300)
                                                    ) + fadeOut()
                                                } else {
                                                    ExitTransition.None
                                                }
                                            ) {
                                                key(animationKey) {

                                                    Column {

                                                        Spacer(
                                                            Modifier.height(18.dp)
                                                        )

                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .onGloballyPositioned {
                                                                    thermometerTopInRoot =
                                                                        it.positionInRoot().y
                                                                }
                                                        ) {
                                                            WeatherIndicatorCard(
                                                                title =
                                                                    "🌡️ Temperatura animada"
                                                            ) {
                                                                AnimatedThermometer(
                                                                    temperature =
                                                                        currentWeather.temperature,
                                                                    animated =
                                                                        animationsEnabled
                                                                )
                                                            }
                                                        }

                                                        Spacer(
                                                            Modifier.height(12.dp)
                                                        )

                                                        WeatherIndicatorCard(
                                                            title =
                                                                "🧭 Brújula del viento"
                                                        ) {
                                                            AnimatedWindCompass(
                                                                degrees =
                                                                    currentWeather.windDirection,
                                                                animated =
                                                                    animationsEnabled
                                                            )
                                                        }

                                                        Spacer(
                                                            Modifier.height(12.dp)
                                                        )

                                                        WeatherIndicatorCard(
                                                            title =
                                                                "📈 Temperatura por hora"
                                                        ) {
                                                            AnimatedTemperatureChart(
                                                                points =
                                                                    currentWeather.hourlyForecast,
                                                                animated =
                                                                    animationsEnabled
                                                            )
                                                        }

                                                        Spacer(
                                                            Modifier.height(12.dp)
                                                        )

                                                        WeatherIndicatorCard(
                                                            title =
                                                                "🌧️ Precipitación actual"
                                                        ) {
                                                            AnimatedPrecipitation(
                                                                millimeters =
                                                                    currentWeather.precipitationMm,
                                                                animated =
                                                                    animationsEnabled
                                                            )
                                                        }

                                                        Spacer(
                                                            Modifier.height(12.dp)
                                                        )

                                                        WeatherIndicatorCard(
                                                            title = "📋 Otros datos"
                                                        ) {
                                                            Text(
                                                                "Viento: ${currentWeather.windSpeed} km/h",
                                                                color = Color.White
                                                            )

                                                            Spacer(
                                                                Modifier.height(6.dp)
                                                            )

                                                            Text(
                                                                "Código meteorológico: ${currentWeather.weatherCode}",
                                                                color = panelText
                                                            )

                                                            Spacer(
                                                                Modifier.height(6.dp)
                                                            )

                                                            Text(
                                                                "Actualizado: ${currentWeather.time}",
                                                                color = panelText
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // NUEVO ORDEN:
                            // EL LABORATORIO VA PRIMERO.
                            Button(
                                onClick = onOpenLab,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3A91BD)
                                ),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(
                                    "✨ Laboratorio de animaciones",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    showGallery = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF287AB9)
                                )
                            ) {
                                Text("🎨 Ver todas las animaciones")
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    showRecommendation = false
                                    detailsExpanded = false
                                    thermometerTopInRoot = Float.NaN
                                    animationKey++
                                },
                                enabled = animationsEnabled,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "↻ Repetir animación",
                                    color = Color.White
                                )
                            }
                        }
                    },

                    emptyContent = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = panelColor
                                )
                            ) {
                                Text(
                                    "No hay información meteorológica disponible.",
                                    color = Color.White,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = onOpenLab,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✨ Laboratorio de animaciones")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun WeatherMainCard(
    weather: Weather,
    animated: Boolean
) {
    val counter = remember {
        Animatable(
            if (animated) 0f
            else weather.temperature.toFloat()
        )
    }

    LaunchedEffect(weather.temperature, animated) {
        if (animated) {
            counter.animateTo(
                weather.temperature.toFloat(),
                animationSpec = tween(1800)
            )
        } else {
            counter.snapTo(weather.temperature.toFloat())
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = panelLight
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (animated) {
                AnimatedWeatherEmoji(
                    weatherCode = weather.weatherCode,
                    windSpeed = weather.windSpeed
                )
            } else {
                Text(
                    staticWeatherSymbol(
                        weather.weatherCode,
                        weather.windSpeed
                    ),
                    fontSize = 74.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                "TEMPERATURA ACTUAL",
                color = panelText,
                letterSpacing = 1.sp,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${(if (animated) counter.value else weather.temperature.toFloat()).roundToInt()} °C",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Actualizado: ${weather.time}",
                color = panelText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AnimatedThermometer(
    temperature: Double,
    animated: Boolean
) {
    val normalized = (
            (temperature.toFloat() + 10f) / 55f
            ).coerceIn(0f, 1f)

    val liquid = remember {
        Animatable(if (animated) 0f else normalized)
    }

    LaunchedEffect(temperature, animated) {
        if (animated) {
            liquid.animateTo(
                normalized,
                animationSpec = tween(
                    2500,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            liquid.snapTo(normalized)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Canvas(
            modifier = Modifier
                .width(75.dp)
                .height(165.dp)
        ) {
            val centerX = size.width / 2f
            val top = 12.dp.toPx()
            val bottom = size.height - 28.dp.toPx()
            val tubeHeight = bottom - top
            val tubeWidth = 19.dp.toPx()
            val innerWidth = 11.dp.toPx()

            drawRoundRect(
                color = Color(0xFFD6F2FF),
                topLeft = Offset(
                    centerX - tubeWidth / 2f, top
                ),
                size = Size(tubeWidth, tubeHeight),
                cornerRadius = CornerRadius(tubeWidth / 2f)
            )

            drawRoundRect(
                color = Color(0xFF315A7C),
                topLeft = Offset(
                    centerX - innerWidth / 2f,
                    top + 5.dp.toPx()
                ),
                size = Size(
                    innerWidth,
                    tubeHeight - 9.dp.toPx()
                ),
                cornerRadius = CornerRadius(innerWidth / 2f)
            )

            val fillHeight =
                (tubeHeight - 13.dp.toPx()) * liquid.value

            if (fillHeight > 0f) {
                drawRoundRect(
                    color = Color(0xFFFF6B59),
                    topLeft = Offset(
                        centerX - innerWidth / 2f,
                        bottom - 4.dp.toPx() - fillHeight
                    ),
                    size = Size(innerWidth, fillHeight),
                    cornerRadius = CornerRadius(innerWidth / 2f)
                )
            }

            drawCircle(
                Color(0xFFD6F2FF),
                21.dp.toPx(),
                Offset(centerX, bottom)
            )

            drawCircle(
                Color(0xFFFF6B59),
                15.dp.toPx(),
                Offset(centerX, bottom)
            )

            for (index in 0..5) {
                val y = top + tubeHeight * index / 5f
                drawLine(
                    Color(0xFFB9E8FF),
                    Offset(centerX + 13.dp.toPx(), y),
                    Offset(centerX + 22.dp.toPx(), y),
                    2.dp.toPx()
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "$temperature °C",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(9.dp))

            Text(
                if (animated) {
                    "El líquido asciende hasta representar la temperatura registrada."
                } else {
                    "Temperatura representada sin animación."
                },
                color = panelText
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Escala gráfica: -10 °C a 45 °C",
                color = accentBlue,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun WeatherTypewriterText(
    text: String,
    animated: Boolean,
    restartKey: Int
) {
    var visible by remember(text, animated, restartKey) {
        mutableIntStateOf(
            if (animated) 0 else text.length
        )
    }

    LaunchedEffect(text, animated, restartKey) {
        if (animated) {
            visible = 0
            for (index in 1..text.length) {
                delay(35)
                visible = index
            }
        } else {
            visible = text.length
        }
    }

    Text(
        text.take(visible) +
                if (animated && visible < text.length) "▍"
                else "",
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
        minLines = 3
    )
}

private fun staticWeatherSymbol(
    code: Int,
    windSpeed: Double
): String = when {
    code in 95..99 -> "⛈️"
    code in 71..77 || code in 85..86 -> "❄️"
    code in 51..67 || code in 80..82 -> "🌧️"
    code in 45..48 -> "🌫️"
    windSpeed >= 40.0 -> "💨"
    code == 0 -> "☀️"
    else -> "☁️"
}