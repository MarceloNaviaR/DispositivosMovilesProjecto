package edu.ucb.project.feature.weather.presentation.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import edu.ucb.project.feature.weather.domain.model.Weather
import edu.ucb.project.feature.weather.domain.model.HourlyWeather
import edu.ucb.project.feature.weather.domain.model.DailyWeather

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val labNavy = Color(0xFF091B35)
private val labPanel = Color(0xFF173B64)
private val labPanelLight = Color(0xFF214D7B)
private val labAccent = Color(0xFF8BDFFF)
private val labMuted = Color(0xFFD4E9FA)
private val labYellow = Color(0xFFFFD166)

@Composable
fun WeatherAnimationLab(
    weather: Weather?,
    onBack: () -> Unit
) {
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var animationsEnabled by rememberSaveable { mutableStateOf(true) }

    val titles = listOf(
        "Por hora",
        "7 días",
        "Día / Noche"
    )

    val background = if (animationsEnabled) {
        Modifier.background(
            Brush.verticalGradient(listOf(labNavy, Color(0xFF133A63), Color(0xFF23638B)))
        )
    } else {
        Modifier.background(labNavy)
    }

    Column(Modifier.fillMaxSize().then(background)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    "← Volver al clima",
                    color = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                if (animationsEnabled) "Modo animado" else "Modo estático",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = animationsEnabled,
                onCheckedChange = { animationsEnabled = it },
                modifier = Modifier.semantics { contentDescription = "Modo animado" }
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Text(
                "LABORATORIO",
                color = labAccent,
                letterSpacing = 2.sp,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                "Explora el clima",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Pronóstico por hora, semanal y cielo de día o de noche",
                color = labMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            titles.forEachIndexed { index, label ->

                val selected = selectedTab == index

                val color by animateColorAsState(
                    targetValue = if (selected) {
                        Color(0xFF328BC0)
                    } else {
                        Color(0xFF183756)
                    },
                    animationSpec = if (animationsEnabled) tween(350) else snap(),
                    label = "TabBackground"
                )

                val scale by animateFloatAsState(
                    targetValue =
                        if (selected) 1f else 0.96f,
                    animationSpec = if (animationsEnabled) spring(
                        dampingRatio =
                            Spring.DampingRatioMediumBouncy
                    ) else snap(),
                    label = "TabScale"
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = if (animationsEnabled) scale else 1f
                            scaleY = if (animationsEnabled) scale else 1f
                        }
                        .clickable {
                            selectedTab = index
                        },
                    color = if (animationsEnabled) color else labPanel,
                    border = if (!animationsEnabled && selected) BorderStroke(1.dp, labAccent) else null,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontWeight =
                            if (selected) FontWeight.Bold
                            else FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            horizontal = 3.dp,
                            vertical = 15.dp
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                if (!animationsEnabled) {
                    (EnterTransition.None togetherWith ExitTransition.None).using(null)
                } else {
                    val forward = targetState > initialState

                    (
                            slideInHorizontally(
                                initialOffsetX = {
                                    if (forward) it / 3 else -it / 3
                                },
                                animationSpec = tween(430)
                            ) + fadeIn(tween(350))
                            ) togetherWith (
                            slideOutHorizontally(
                                targetOffsetX = {
                                    if (forward) -it / 3 else it / 3
                                },
                                animationSpec = tween(380)
                            ) + fadeOut(tween(280))
                            )
                }
            },
            label = "LabPageTransition"
        ) { page ->

            when (page) {
                0 -> HourlyLab(
                    points = weather?.hourlyForecast.orEmpty(),
                    animationsEnabled = animationsEnabled
                )

                1 -> WeeklyLab(
                    days = weather?.dailyForecast.orEmpty(),
                    animationsEnabled = animationsEnabled
                )

                else -> DayNightLab(
                    weather = weather,
                    animationsEnabled = animationsEnabled
                )
            }
        }
    }
}

// ======================================================
// 1. LÍNEA DE TIEMPO METEOROLÓGICA
// ======================================================

@Composable
private fun HourlyLab(
    points: List<HourlyWeather>,
    animationsEnabled: Boolean
) {
    if (points.isEmpty()) {
        LabEmptyState(
            "No hay pronóstico horario disponible.",
            "Comprueba que Open-Meteo esté devolviendo " +
                    "temperature_2m, weather_code e is_day."
        )
        return
    }

    var selectedIndex by remember(points) {
        mutableIntStateOf(0)
    }

    val selected = points[
        selectedIndex.coerceIn(0, points.lastIndex)
    ]

    val temperatures = remember(points) { points.map { it.temperature.toFloat() } }
    val low = remember(temperatures) { temperatures.min() }
    val high = remember(temperatures) { temperatures.max() }
    val timelineState = rememberLazyListState()

    LaunchedEffect(selectedIndex, points, animationsEnabled) {
        if (timelineState.layoutInfo.visibleItemsInfo.none { it.index == selectedIndex }) {
            if (animationsEnabled) timelineState.animateScrollToItem(selectedIndex)
            else timelineState.scrollToItem(selectedIndex)
        }
    }

    val temperature by animateIntAsState(
        targetValue = selected.temperature.roundToInt(),
        animationSpec = if (animationsEnabled) tween(
            durationMillis = 850,
            easing = FastOutSlowInEasing
        ) else snap(),
        label = "HourlyTemperature"
    )

    val skyTransition = updateTransition(
        targetState = selected.isDay ?: true,
        label = "HourlySkyTransition"
    )

    val skyTop by skyTransition.animateColor(
        transitionSpec = { if (animationsEnabled) tween(900) else snap() },
        label = "HourlySkyTop"
    ) { isDay ->
        if (isDay) Color(0xFF348FC8)
        else Color(0xFF101D47)
    }

    val skyBottom by skyTransition.animateColor(
        transitionSpec = { if (animationsEnabled) tween(900) else snap() },
        label = "HourlySkyBottom"
    ) { isDay ->
        if (isDay) Color(0xFF85C9E7)
        else Color(0xFF374B86)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        LabSectionTitle(
            title = "Pronóstico por hora",
            subtitle = "Elegí una hora para consultar su pronóstico."
        )

        Spacer(Modifier.height(16.dp))

        // Tarjeta principal con cambio gradual de colores
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (animationsEnabled) {
                            Modifier.background(Brush.verticalGradient(listOf(skyTop, skyBottom)))
                        } else {
                            Modifier.background(labPanelLight)
                        }
                    )
                    .padding(if (animationsEnabled) 22.dp else 16.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        selected.time.replace("T", " · "),
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = labWeatherIcon(
                            selected.weatherCode,
                            selected.isDay
                        ),
                        transitionSpec = {
                            if (!animationsEnabled) {
                                (EnterTransition.None togetherWith ExitTransition.None).using(null)
                            } else {
                                (
                                        scaleIn(
                                            initialScale = 0.65f,
                                            animationSpec = tween(500)
                                        ) + fadeIn()
                                        ) togetherWith (
                                        scaleOut(
                                            targetScale = 1.25f,
                                            animationSpec = tween(350)
                                        ) + fadeOut()
                                        )
                            }
                        },
                        label = "HourlyIconTransition"
                    ) { icon ->
                        Text(
                            text = icon,
                            fontSize = if (animationsEnabled) 85.sp else 58.sp
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "${if (animationsEnabled) temperature else selected.temperature.roundToInt()} °C",
                        color = Color.White,
                        fontSize = if (animationsEnabled) 54.sp else 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        labWeatherName(selected.weatherCode),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(18.dp))

                    HorizontalDivider(
                        color = Color(0x66FFFFFF)
                    )

                    Spacer(Modifier.height(13.dp))

                    Text(
                        selected.precipitationMm?.let {
                            "💧 Precipitación prevista: $it mm"
                        } ?: "💧 Precipitación no disponible",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        HourlyTemperatureTrend(
            points = points,
            temperatures = temperatures,
            low = low,
            high = high,
            animationsEnabled = animationsEnabled,
            selectedIndex = selectedIndex,
            onSelectHour = { selectedIndex = it }
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "LÍNEA DE TIEMPO",
            color = labAccent,
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        if (animationsEnabled && low < high) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(thermalBlue, thermalSky, thermalAmber)
                        ),
                        RoundedCornerShape(3.dp)
                    )
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Mín. ${low.roundToInt()} °C", color = labMuted, fontSize = 12.sp)
                Text("Máx. ${high.roundToInt()} °C", color = labMuted, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(11.dp))

        LazyRow(
            state = timelineState,
            contentPadding = PaddingValues(
                horizontal = 3.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            itemsIndexed(
                items = points,
                key = { _, item -> item.time }
            ) { index, point ->

                val active = selectedIndex == index

                val cardHeight by animateDpAsState(
                    targetValue =
                        if (active) 132.dp else 109.dp,
                    animationSpec = if (animationsEnabled) spring(
                        dampingRatio =
                            Spring.DampingRatioMediumBouncy
                    ) else snap(),
                    label = "HourCardHeight"
                )

                val thermalColor = hourlyThermalColor(temperatures[index], low, high)
                val background = lerp(labNavy, thermalColor, 0.36f)

                Card(
                    modifier = Modifier
                        .width(91.dp)
                        .height(if (animationsEnabled) cardHeight else 109.dp)
                        .semantics { this.selected = active }
                        .clickable {
                            selectedIndex = index
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (animationsEnabled) background else labPanel
                    ),
                    border = if (active) BorderStroke(
                        width = if (animationsEnabled) 2.dp else 1.dp,
                        color = if (animationsEnabled) Color.White else labAccent
                    ) else null,
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.SpaceEvenly
                    ) {

                        Text(
                            point.time.takeLast(5),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            labWeatherIcon(
                                point.weatherCode,
                                point.isDay
                            ),
                            fontSize = 28.sp
                        )

                        Text(
                            "${point.temperature.roundToInt()}°",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        if (active && animationsEnabled) {
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(4.dp)
                                    .background(
                                        labYellow,
                                        RoundedCornerShape(20.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private val thermalBlue = Color(0xFF6C9AFF)
private val thermalSky = Color(0xFF76DFFF)
private val thermalAmber = Color(0xFFFFC45C)

private fun hourlyThermalColor(temperature: Float, low: Float, high: Float): Color {
    if (high == low) return thermalSky
    val fraction = ((temperature - low) / (high - low)).coerceIn(0f, 1f)
    return if (fraction <= 0.5f) {
        lerp(thermalBlue, thermalSky, fraction * 2f)
    } else {
        lerp(thermalSky, thermalAmber, (fraction - 0.5f) * 2f)
    }
}

@Composable
private fun HourlyTemperatureTrend(
    points: List<HourlyWeather>,
    temperatures: List<Float>,
    low: Float,
    high: Float,
    animationsEnabled: Boolean,
    selectedIndex: Int,
    onSelectHour: (Int) -> Unit
) {
    val currentOnSelectHour by rememberUpdatedState(onSelectHour)
    val markerColor by animateColorAsState(
        targetValue = hourlyThermalColor(temperatures[selectedIndex], low, high),
        animationSpec = if (animationsEnabled) tween(220) else snap(),
        label = "HourlyChartMarkerColor"
    )
    val markerIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = if (animationsEnabled) tween(220) else snap(),
        label = "HourlyChartMarker"
    )
    val selected = points[selectedIndex]
    val plotPadding = 12.dp
    val lastIndex = points.lastIndex

    fun selectHour(x: Float, canvasWidth: Int, padding: Float) {
        val plotWidth = (canvasWidth - 2 * padding).coerceAtLeast(1f)
        val index = ((x - padding) / plotWidth * lastIndex).roundToInt()
        currentOnSelectHour(index.coerceIn(0, lastIndex))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = labPanel)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Tendencia de temperatura", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "${selected.time.takeLast(5)} · ${selected.temperature.roundToInt()} °C",
                color = labAccent,
                style = MaterialTheme.typography.bodyMedium
            )
            if (points.size > 1) {
                Text(
                    "Tocá o deslizá sobre el gráfico para elegir una hora.",
                    color = labMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .pointerInput(points.size) {
                        detectTapGestures { position ->
                            selectHour(position.x, size.width, plotPadding.toPx())
                        }
                    }
                    .pointerInput(points.size) {
                        detectHorizontalDragGestures { change, _ ->
                            selectHour(change.position.x, size.width, plotPadding.toPx())
                        }
                    }
            ) {
                val padding = plotPadding.toPx()
                val plotWidth = (size.width - 2 * padding).coerceAtLeast(0f)
                val plotHeight = (size.height - 2 * padding).coerceAtLeast(0f)

                fun point(index: Float, temperature: Float): Offset {
                    val x = if (points.size == 1) 0.5f else index / points.lastIndex
                    val y = if (high == low) 0.5f else (high - temperature) / (high - low)
                    return Offset(padding + x * plotWidth, padding + y * plotHeight)
                }

                val line = Path().apply {
                    temperatures.forEachIndexed { index, temperature ->
                        val position = point(index.toFloat(), temperature)
                        if (index == 0) moveTo(position.x, position.y)
                        else lineTo(position.x, position.y)
                    }
                }
                drawPath(line, labAccent, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

                val index = if (animationsEnabled) {
                    markerIndex.coerceIn(0f, points.lastIndex.toFloat())
                } else {
                    selectedIndex.toFloat()
                }
                val left = index.toInt()
                val right = (left + 1).coerceAtMost(points.lastIndex)
                val markerTemperature = temperatures[left] +
                        (temperatures[right] - temperatures[left]) * (index - left)
                val marker = point(index, markerTemperature)
                drawLine(
                    labAccent.copy(alpha = 0.4f),
                    Offset(marker.x, padding),
                    Offset(marker.x, size.height - padding),
                    strokeWidth = 1.dp.toPx()
                )
                drawCircle(Color.White, radius = 7.dp.toPx(), center = marker)
                drawCircle(
                    if (animationsEnabled) markerColor else labAccent,
                    radius = 5.dp.toPx(),
                    center = marker
                )
            }
        }
    }
}

// ======================================================
// 2. PRONÓSTICO SEMANAL CON TARJETAS REORDENABLES
// ======================================================

@Composable
private fun WeeklyLab(
    days: List<DailyWeather>,
    animationsEnabled: Boolean
) {
    if (days.isEmpty()) {
        LabEmptyState(
            "No hay pronóstico semanal disponible.",
            "La consulta debe solicitar forecast_days=7 " +
                    "y los campos daily de Open-Meteo."
        )
        return
    }

    var sortByTemperature by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableStateOf<String?>(null)
    }

    val sortedDays = remember(days, sortByTemperature) {
        if (sortByTemperature) {
            days.sortedByDescending { it.maximum }
        } else {
            days.sortedBy { it.date }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        LabSectionTitle(
            "Pronóstico de siete días",
            "Expande una tarjeta o cambia el orden de los días."
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            LabMiniStat(
                label = "Días",
                value = "${days.size}",
                modifier = Modifier.weight(1f)
            )

            LabMiniStat(
                label = "Máxima prevista",
                value = "${days.maxOf { it.maximum }.roundToInt()}°",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = {
                sortByTemperature = !sortByTemperature
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (sortByTemperature) {
                    "↕ Ordenado por temperatura"
                } else {
                    "↕ Ordenar de mayor a menor temperatura"
                },
                color = Color.White
            )
        }

        Spacer(Modifier.height(13.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(
                top = 4.dp,
                bottom = 30.dp
            )
        ) {

            itemsIndexed(
                items = sortedDays,
                key = { _, day -> day.date }
            ) { _, day ->

                val expanded = selectedDate == day.date

                val cardColor by animateColorAsState(
                    targetValue = if (expanded) {
                        Color(0xFF286A9A)
                    } else {
                        labPanel
                    },
                    animationSpec = if (animationsEnabled) tween(400) else snap(),
                    label = "WeeklyCardColor"
                )

                val iconScale by animateFloatAsState(
                    targetValue =
                        if (expanded) 1.20f else 1f,
                    animationSpec = if (animationsEnabled) spring(
                        dampingRatio =
                            Spring.DampingRatioMediumBouncy
                    ) else snap(),
                    label = "WeeklyIconScale"
                )

                val itemMotion = if (animationsEnabled) {
                    Modifier.animateItem().animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                } else {
                    Modifier
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(itemMotion)
                        .clickable {
                            selectedDate =
                                if (expanded) null else day.date
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (animationsEnabled) cardColor else labPanel
                    ),
                    border = if (!animationsEnabled && expanded) BorderStroke(1.dp, labAccent) else null,
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(66.dp)
                                    .background(
                                        Color(0xFF315F83),
                                        RoundedCornerShape(19.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = labWeatherIcon(
                                        day.weatherCode,
                                        true
                                    ),
                                    fontSize = 35.sp,
                                    modifier =
                                        Modifier.graphicsLayer {
                                            scaleX = if (animationsEnabled) iconScale else 1f
                                            scaleY = if (animationsEnabled) iconScale else 1f
                                        }
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    day.date,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    labWeatherName(day.weatherCode),
                                    color = labMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(
                                    "${day.maximum.roundToInt()}°",
                                    color = labYellow,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "mín. ${day.minimum.roundToInt()}°",
                                    color = labMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (expanded) {

                            Spacer(Modifier.height(16.dp))

                            HorizontalDivider(
                                color = Color(0x557FD8FF)
                            )

                            Spacer(Modifier.height(15.dp))

                            Text(
                                "DETALLES DEL DÍA",
                                color = labAccent,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                LabMiniStat(
                                    "Máxima",
                                    "${day.maximum.roundToInt()} °C",
                                    Modifier.weight(1f)
                                )

                                LabMiniStat(
                                    "Mínima",
                                    "${day.minimum.roundToInt()} °C",
                                    Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {

                                LabMiniStat(
                                    "Amanecer",
                                    day.sunrise
                                        ?.takeLast(5) ?: "—",
                                    Modifier.weight(1f)
                                )

                                LabMiniStat(
                                    "Atardecer",
                                    day.sunset
                                        ?.takeLast(5) ?: "—",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================
// 3. CIELO VECTORIAL CON TRANSICIÓN DÍA / NOCHE
// ======================================================

@Composable
private fun DayNightLab(
    weather: Weather?,
    animationsEnabled: Boolean
) {
    val actualIsDay = weather?.isDay

    var night by remember(actualIsDay) {
        mutableStateOf(actualIsDay == false)
    }

    val transition = updateTransition(
        targetState = night,
        label = "DayNightSky"
    )

    val upperColor by transition.animateColor(
        transitionSpec = { if (animationsEnabled) tween(1500) else snap() },
        label = "SkyUpperColor"
    ) {
        if (it) Color(0xFF0B1536)
        else Color(0xFF3B9DDB)
    }

    val lowerColor by transition.animateColor(
        transitionSpec = { if (animationsEnabled) tween(1500) else snap() },
        label = "SkyLowerColor"
    ) {
        if (it) Color(0xFF364A7B)
        else Color(0xFFB4E4F7)
    }

    val starsAlpha by transition.animateFloat(
        transitionSpec = { if (animationsEnabled) tween(1400) else snap() },
        label = "StarsAlpha"
    ) {
        if (it) 1f else 0f
    }

    val sunAlpha by transition.animateFloat(
        transitionSpec = { if (animationsEnabled) tween(1200) else snap() },
        label = "SunAlpha"
    ) {
        if (it) 0f else 1f
    }

    val moonAlpha by transition.animateFloat(
        transitionSpec = { if (animationsEnabled) tween(1200) else snap() },
        label = "MoonAlpha"
    ) {
        if (it) 1f else 0f
    }

    val sunHeight by transition.animateFloat(
        transitionSpec = {
            if (animationsEnabled) spring(
                dampingRatio =
                    Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ) else snap()
        },
        label = "SunHeight"
    ) {
        if (it) 1.20f else 0.34f
    }

    val moonHeight by transition.animateFloat(
        transitionSpec = {
            if (animationsEnabled) spring(
                dampingRatio =
                    Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ) else snap()
        },
        label = "MoonHeight"
    ) {
        if (it) 0.30f else 1.20f
    }

    val infinite = if (animationsEnabled) {
        rememberInfiniteTransition(label = "SkyAmbientMotion")
    } else {
        null
    }

    val twinkle = infinite?.animateFloat(
        initialValue = 0.50f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarTwinkle"
    )

    val cloudDrift = infinite?.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudDrift"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LabSectionTitle(
            "Del día a la noche",
            "Elegí día o noche para ver cada estado del cielo."
        )

        Spacer(Modifier.height(18.dp))

        if (animationsEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(27.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(upperColor, lowerColor)
                            )
                        )
                ) {

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val w = size.width
                        val h = size.height
                        val s = size.minDimension

                        // ESTRELLAS
                        val starPositions = listOf(
                            Offset(.13f, .14f),
                            Offset(.32f, .08f),
                            Offset(.67f, .13f),
                            Offset(.83f, .24f),
                            Offset(.18f, .38f),
                            Offset(.48f, .26f),
                            Offset(.72f, .43f),
                            Offset(.91f, .09f),
                            Offset(.39f, .47f),
                            Offset(.59f, .06f),
                            Offset(.08f, .55f),
                            Offset(.87f, .55f)
                        )

                        starPositions.forEachIndexed { index, point ->
                            drawCircle(
                                color = Color.White,
                                radius =
                                    if (index % 3 == 0) 2.8f
                                    else 1.8f,
                                center = Offset(
                                    point.x * w,
                                    point.y * h
                                ),
                                alpha = starsAlpha *
                                        if (index % 2 == 0) (twinkle?.value ?: 0.8f)
                                        else 0.8f
                            )
                        }

                        // LUZ DEL SOL
                        val sunCenter = Offset(
                            w * 0.70f,
                            h * sunHeight
                        )

                        drawCircle(
                            color = Color(0xFFFFE89D),
                            radius = s * 0.21f,
                            center = sunCenter,
                            alpha = sunAlpha * 0.15f
                        )

                        // RAYOS SOLARES
                        for (index in 0 until 12) {
                            val angle = index *
                                    PI.toFloat() / 6f

                            drawLine(
                                color = Color(0xFFFFF0B0),
                                start = Offset(
                                    sunCenter.x +
                                            cos(angle) * s * .135f,
                                    sunCenter.y +
                                            sin(angle) * s * .135f
                                ),
                                end = Offset(
                                    sunCenter.x +
                                            cos(angle) * s * .175f,
                                    sunCenter.y +
                                            sin(angle) * s * .175f
                                ),
                                strokeWidth = 3.5f,
                                cap = StrokeCap.Round,
                                alpha = sunAlpha
                            )
                        }

                        drawCircle(
                            color = Color(0xFFFFD369),
                            radius = s * .105f,
                            center = sunCenter,
                            alpha = sunAlpha
                        )

                        // LUNA CON FORMA DE CRECIENTE
                        val moonCenter = Offset(
                            w * .69f,
                            h * moonHeight
                        )

                        drawCircle(
                            color = Color(0xFFFFF0CA),
                            radius = s * .105f,
                            center = moonCenter,
                            alpha = moonAlpha
                        )

                        drawCircle(
                            color = upperColor,
                            radius = s * .100f,
                            center = Offset(
                                moonCenter.x + s * .042f,
                                moonCenter.y - s * .045f
                            ),
                            alpha = moonAlpha
                        )

                        // NUBES SUAVES
                        val cloudX = cloudDrift?.value ?: 0f
                        val cloudColor = Color.White

                        for (index in 0..1) {
                            val x = w *
                                    (0.18f + index * 0.46f) +
                                    cloudX * (index + 1)

                            val y = h *
                                    (0.45f + index * 0.09f)

                            drawCircle(
                                color = cloudColor,
                                radius = s * .068f,
                                center = Offset(x, y),
                                alpha = (1f - starsAlpha) * .60f
                            )

                            drawCircle(
                                color = cloudColor,
                                radius = s * .052f,
                                center = Offset(
                                    x + s * .075f,
                                    y + s * .012f
                                ),
                                alpha = (1f - starsAlpha) * .60f
                            )

                            drawCircle(
                                color = cloudColor,
                                radius = s * .047f,
                                center = Offset(
                                    x - s * .067f,
                                    y + s * .016f
                                ),
                                alpha = (1f - starsAlpha) * .60f
                            )
                        }

                        // MONTAÑAS DE FONDO
                        val farMountains = Path().apply {
                            moveTo(0f, h * .83f)
                            lineTo(w * .21f, h * .59f)
                            lineTo(w * .43f, h * .80f)
                            lineTo(w * .67f, h * .55f)
                            lineTo(w, h * .85f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = farMountains,
                            color = if (night) {
                                Color(0xFF233E64)
                            } else {
                                Color(0xFF5E9CB7)
                            }
                        )

                        // MONTAÑAS DELANTERAS
                        val nearMountains = Path().apply {
                            moveTo(0f, h * .92f)
                            lineTo(w * .28f, h * .72f)
                            lineTo(w * .46f, h * .89f)
                            lineTo(w * .77f, h * .68f)
                            lineTo(w, h * .89f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }

                        drawPath(
                            path = nearMountains,
                            color = if (night) {
                                Color(0xFF102842)
                            } else {
                                Color(0xFF317A91)
                            }
                        )
                    }

                    Text(
                        text = if (night) "BUENAS NOCHES"
                        else "BUENOS DÍAS",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 22.dp),
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = labPanelLight),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (night) "🌙" else "☀️", fontSize = 56.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (night) "Noche" else "Día",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { night = false },
                modifier = Modifier.weight(1f),
                enabled = night
            ) {
                Text("☀️ Día")
            }

            Button(
                onClick = { night = true },
                modifier = Modifier.weight(1f),
                enabled = !night
            ) {
                Text("🌙 Noche")
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = labPanel
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(17.dp)
            ) {
                Text(
                    "Estado actual de Open-Meteo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(9.dp))

                Text(
                    when (actualIsDay) {
                        true -> "☀️ Actualmente es de día."
                        false -> "🌙 Actualmente es de noche."
                        null -> "No hay datos de día/noche disponibles."
                    },
                    color = labMuted
                )

                Spacer(Modifier.height(9.dp))

                Text(
                    "Los botones de arriba simulan " +
                            "el cambio visual; no alteran los datos reales.",
                    color = labMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ======================================================
// ELEMENTOS VISUALES COMPARTIDOS
// ======================================================

@Composable
private fun LabSectionTitle(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = labMuted
        )
    }
}

@Composable
private fun LabMiniStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = labPanelLight,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                label,
                color = labMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun LabEmptyState(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = labPanel
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "☁️",
                    fontSize = 52.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    subtitle,
                    color = labMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun labWeatherIcon(
    code: Int,
    isDay: Boolean?
): String = when {
    code in 95..99 -> "⛈️"
    code in 71..77 || code in 85..86 -> "❄️"
    code in 51..67 || code in 80..82 -> "🌧️"
    code in 45..48 -> "🌫️"
    code == 0 && isDay == false -> "🌙"
    code == 0 -> "☀️"
    code in 1..3 -> "☁️"
    else -> "🌤️"
}

private fun labWeatherName(
    code: Int
): String = when {
    code in 95..99 -> "Tormenta"
    code in 71..77 || code in 85..86 -> "Nieve"
    code in 51..67 || code in 80..82 -> "Lluvia"
    code in 45..48 -> "Niebla"
    code == 0 -> "Cielo despejado"
    code in 1..3 -> "Nubosidad"
    else -> "Condiciones variables"
}