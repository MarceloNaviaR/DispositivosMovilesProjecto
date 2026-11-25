package edu.ucb.project

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import edu.ucb.project.feature.weather.presentation.screen.WeatherScreen

import kotlinx.coroutines.delay

private val weatherGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF071A35),
        Color(0xFF174A7B),
        Color(0xFF347CB5)
    )
)

class WeatherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                WeatherAppContent()
            }
        }
    }
}

@Composable
fun WeatherAppContent() {

    var showSplash by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(3000)
        showSplash = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(weatherGradient)
    ) {

        AnimatedContent(
            targetState = showSplash,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(650)
                ) togetherWith fadeOut(
                    animationSpec = tween(550)
                )
            },
            label = "SplashTransition"
        ) { splash ->

            if (splash) {
                WeatherSplashScreen()
            } else {
                WeatherPage()
            }
        }
    }
}

@Composable
fun WeatherSplashScreen() {

    var showIcon by remember {
        mutableStateOf(false)
    }

    var showTitle by remember {
        mutableStateOf(false)
    }

    var showSubtitle by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(150)
        showIcon = true

        delay(450)
        showTitle = true

        delay(550)
        showSubtitle = true
    }

    val iconScale = animateFloatAsState(
        targetValue = if (showIcon) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SplashIconScale"
    )

    val iconAlpha = animateFloatAsState(
        targetValue = if (showIcon) 1f else 0f,
        animationSpec = tween(650),
        label = "SplashIconAlpha"
    )

    val titleAlpha = animateFloatAsState(
        targetValue = if (showTitle) 1f else 0f,
        animationSpec = tween(650),
        label = "SplashTitleAlpha"
    )

    val titleOffset = animateFloatAsState(
        targetValue = if (showTitle) 0f else 55f,
        animationSpec = tween(750),
        label = "SplashTitleOffset"
    )

    val subtitleAlpha = animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = tween(700),
        label = "SplashSubtitleAlpha"
    )

    val transition = rememberInfiniteTransition(
        label = "SplashFloating"
    )

    val floating = transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatingIcons"
    )

    val ringScale = transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .size(290.dp)
                .graphicsLayer {
                    scaleX = ringScale.value
                    scaleY = ringScale.value
                }
        ) {
            val radius = size.minDimension / 2f

            drawCircle(
                color = Color(0x557CCFFF),
                radius = radius * 0.95f,
                style = Stroke(
                    width = 2.dp.toPx()
                )
            )

            drawCircle(
                color = Color(0x337CCFFF),
                radius = radius * 0.72f,
                style = Stroke(
                    width = 3.dp.toPx()
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x335AD6FF),
                        Color.Transparent
                    )
                ),
                radius = radius * 0.68f
            )
        }

        Text(
            text = "☁️",
            fontSize = 42.sp,
            modifier = Modifier
                .offset(
                    x = (-115).dp,
                    y = (-145).dp
                )
                .graphicsLayer {
                    translationY = floating.value
                }
        )

        Text(
            text = "❄️",
            fontSize = 32.sp,
            modifier = Modifier
                .offset(
                    x = 118.dp,
                    y = (-110).dp
                )
                .graphicsLayer {
                    translationY = -floating.value
                    rotationZ = floating.value * 2f
                }
        )

        Text(
            text = "💧",
            fontSize = 31.sp,
            modifier = Modifier
                .offset(
                    x = 125.dp,
                    y = 120.dp
                )
                .graphicsLayer {
                    translationY = floating.value
                }
        )

        Text(
            text = "✨",
            fontSize = 29.sp,
            modifier = Modifier
                .offset(
                    x = (-125).dp,
                    y = 115.dp
                )
                .graphicsLayer {
                    alpha = 0.65f +
                            (floating.value + 8f) / 80f
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🌤️",
                fontSize = 100.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale.value
                    scaleY = iconScale.value
                    alpha = iconAlpha.value
                }
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "REPORTE\nDEL CLIMA",
                fontSize = 34.sp,
                lineHeight = 41.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = titleOffset.value
                }
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "COCHABAMBA · BOLIVIA",
                fontSize = 15.sp,
                letterSpacing = 2.sp,
                color = Color(0xFFC6E8FF),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha.value
                }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tu clima, tu día, tu mejor decisión",
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = subtitleAlpha.value
                }
            )
        }
    }
}

@Composable
fun WeatherPage() {

    Scaffold(
        containerColor = Color.Transparent,

        topBar = {
            Surface(
                color = Color(0x77102548)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Reporte del clima",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            WeatherScreen()
        }
    }
}
