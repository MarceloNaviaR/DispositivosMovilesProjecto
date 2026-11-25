package edu.ucb.project.feature.weather.presentation.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.delay

private val galleryBlue = Color(0xFF173B64)
private val galleryLight = Color(0xFFDAEDFF)

private data class GalleryClimate(
    val name: String,
    val description: String,
    val code: Int,
    val wind: Double
)

private val climates = listOf(
    GalleryClimate(
        "Soleado",
        "El sol gira y aumenta su tamaño.",
        0, 5.0
    ),
    GalleryClimate(
        "Lluvia",
        "Las gotas caen desde la nube.",
        61, 8.0
    ),
    GalleryClimate(
        "Tormenta",
        "Relámpagos y destellos luminosos.",
        95, 12.0
    ),
    GalleryClimate(
        "Nublado",
        "Dos nubes flotan a distinta velocidad.",
        3, 5.0
    ),
    GalleryClimate(
        "Nieve",
        "Copos vectoriales giran mientras caen.",
        71, 4.0
    ),
    GalleryClimate(
        "Ventoso",
        "Ráfagas de viento dibujadas progresivamente.",
        0, 45.0
    ),
    GalleryClimate(
        "Niebla",
        "Capas de niebla cambian su opacidad.",
        45, 5.0
    )
)

@Composable
fun WeatherAnimationsGallery(
    onBack: () -> Unit
) {
    var showWindmill by remember {
        mutableStateOf(true)
    }

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    var direction by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(Unit) {
        delay(2100)
        showWindmill = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "← Volver al clima",
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Galería meteorológica",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Explora las siete animaciones",
            style = MaterialTheme.typography.bodyMedium,
            color = galleryLight
        )

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = showWindmill,

            transitionSpec = {
                if (targetState) {

                    (
                            scaleIn(
                                initialScale = 0.65f,
                                animationSpec = tween(500)
                            ) + fadeIn(
                                animationSpec = tween(500)
                            )
                            ) togetherWith fadeOut()

                } else {

                    (
                            scaleIn(
                                initialScale = 0.70f,
                                animationSpec = tween(650)
                            ) + fadeIn(
                                animationSpec = tween(650)
                            )
                            ) togetherWith (
                            scaleOut(
                                targetScale = 1.45f,
                                animationSpec = tween(550)
                            ) + fadeOut(
                                animationSpec = tween(550)
                            )
                            )
                }
            },

            label = "WindmillToGallery"
        ) { windmillVisible ->

            if (windmillVisible) {

                WindmillVectorIntro()

            } else {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AnimatedContent(
                        targetState = selectedIndex,

                        transitionSpec = {
                            val forward = direction > 0

                            (
                                    slideInHorizontally(
                                        initialOffsetX = {
                                            if (forward) it else -it
                                        },
                                        animationSpec = tween(430)
                                    ) + fadeIn(
                                        animationSpec = tween(350)
                                    )
                                    ) togetherWith (
                                    slideOutHorizontally(
                                        targetOffsetX = {
                                            if (forward) -it else it
                                        },
                                        animationSpec = tween(430)
                                    ) + fadeOut(
                                        animationSpec = tween(300)
                                    )
                                    )
                        },

                        label = "WeatherGalleryCarousel"
                    ) { index ->

                        val climate = climates[index]

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(330.dp),

                            colors = CardDefaults.cardColors(
                                containerColor = galleryBlue
                            ),

                            shape = MaterialTheme.shapes.extraLarge
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.Center
                            ) {

                                AnimatedWeatherEmoji(
                                    weatherCode = climate.code,
                                    windSpeed = climate.wind
                                )

                                Spacer(Modifier.height(18.dp))

                                Text(
                                    text = climate.name,
                                    style =
                                        MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = climate.description,
                                    color = galleryLight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(
                            onClick = {
                                direction = -1

                                selectedIndex =
                                    if (selectedIndex == 0) {
                                        climates.lastIndex
                                    } else {
                                        selectedIndex - 1
                                    }
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF287AB9)
                            )
                        ) {
                            Text("←")
                        }

                        Text(
                            text = "${selectedIndex + 1} / ${climates.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                direction = 1

                                selectedIndex =
                                    if (
                                        selectedIndex ==
                                        climates.lastIndex
                                    ) {
                                        0
                                    } else {
                                        selectedIndex + 1
                                    }
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF287AB9)
                            )
                        ) {
                            Text("→")
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Cada efecto se repite " +
                                "automáticamente cada 10 segundos.",
                        color = galleryLight,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// ===============================================
// INTRODUCCIÓN VECTORIAL: MOLINO DE VIENTO
// ===============================================

@Composable
private fun WindmillVectorIntro() {

    val rotation = remember {
        Animatable(0f)
    }

    val scale = remember {
        Animatable(0.55f)
    }

    val opacity = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 540f,
            animationSpec = tween(
                durationMillis = 1750,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp),

        colors = CardDefaults.cardColors(
            containerColor = galleryBlue
        ),

        shape = MaterialTheme.shapes.extraLarge
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Canvas(
                modifier = Modifier.size(185.dp)
            ) {

                val center = Offset(
                    size.width / 2f,
                    size.height * 0.43f
                )

                val factor = size.minDimension / 200f

                val animatedScale = scale.value

                // TORRE DEL MOLINO
                val tower = Path().apply {
                    moveTo(
                        center.x - 14f * factor,
                        center.y
                    )

                    lineTo(
                        center.x - 25f * factor,
                        size.height * 0.97f
                    )

                    lineTo(
                        center.x + 25f * factor,
                        size.height * 0.97f
                    )

                    lineTo(
                        center.x + 14f * factor,
                        center.y
                    )

                    close()
                }

                drawPath(
                    path = tower,
                    color = Color(0xFF426B94),
                    alpha = opacity.value
                )

                drawPath(
                    path = tower,
                    color = Color(0xFFBDEAFF),
                    style = Stroke(
                        width = 3.dp.toPx()
                    ),
                    alpha = opacity.value
                )

                // VENTANA DEL MOLINO
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 5f * factor,
                    center = Offset(
                        center.x,
                        center.y + 65f * factor
                    ),
                    alpha = opacity.value
                )

                // ASPAS GIRATORIAS
                rotate(
                    degrees = rotation.value,
                    pivot = center
                ) {

                    for (index in 0 until 4) {

                        rotate(
                            degrees = index * 90f,
                            pivot = center
                        ) {

                            val blade = Path().apply {
                                moveTo(
                                    center.x - 7f * factor,
                                    center.y
                                )

                                lineTo(
                                    center.x - 13f *
                                            factor * animatedScale,
                                    center.y - 69f *
                                            factor * animatedScale
                                )

                                quadraticTo(
                                    center.x,
                                    center.y - 87f *
                                            factor * animatedScale,
                                    center.x + 13f *
                                            factor * animatedScale,
                                    center.y - 69f *
                                            factor * animatedScale
                                )

                                lineTo(
                                    center.x + 7f * factor,
                                    center.y
                                )

                                close()
                            }

                            drawPath(
                                path = blade,
                                color = Color(0xFFBEEAFF),
                                alpha = opacity.value
                            )

                            drawPath(
                                path = blade,
                                color = Color(0xFF69B9E8),
                                style = Stroke(
                                    width = 2.dp.toPx()
                                ),
                                alpha = opacity.value
                            )
                        }
                    }
                }

                // CENTRO DEL ROTOR
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 10f * factor,
                    center = center,
                    alpha = opacity.value
                )

                drawCircle(
                    color = Color.White,
                    radius = 4f * factor,
                    center = center,
                    alpha = opacity.value
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "EXPLORANDO EL CLIMA",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Molino vectorial animado",
                color = galleryLight,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
