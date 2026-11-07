package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.theme.IslamicGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class BackdropAtmosphere {
    SUNRISE_MORNING, // اذكار الصباح مع الشروق المتحرك
    SUNSET_EVENING,  // اذكار المساء مع غروب الشمس المتحرك
    STARRY_NIGHT,    // اذكار النوم مع سماء الليل والنجوم المتلألئة
    MOSQUE_ADHAN,    // الاذان مع لقطات المساجد وانوار المآذن المتحركة
    FRIDAY_EMERALD   // الجمعة المباركة مع اشعة النور الزمردية
}

@Composable
fun AnimatedCinematicBackdrop(
    imageRes: Int,
    atmosphere: BackdropAtmosphere,
    modifier: Modifier = Modifier,
    overlayDarkGradient: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Ken Burns slow cinematic camera motion
    val infiniteTransition = rememberInfiniteTransition(label = "cinematicCamera")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cameraScale"
    )

    val translationX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cameraPanX"
    )

    val translationY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cameraPanY"
    )

    // Atmospheric dynamic motion variables
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val particlesPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particlesPhase"
    )

    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(50000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rayRotation"
    )

    // Fixed random seeds for stars / particles
    val starPositions = remember {
        List(32) {
            Triple(
                (Math.random()).toFloat(),
                (Math.random() * 0.75f).toFloat(),
                (Math.random() * 2.8f + 1.2f).toFloat()
            )
        }
    }

    Box(modifier = modifier) {
        // 1. Moving Ken-Burns Camera Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.translationX = translationX
                    this.translationY = translationY
                },
            contentScale = contentScale
        )

        // 2. Animated Atmosphere Canvas Layer (Sunrise, Sunset, Stars, Mosque Light)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            when (atmosphere) {
                BackdropAtmosphere.SUNRISE_MORNING -> {
                    // Sunrise Solar Flare & Ascending Dew Motes
                    val sunCenterX = width * 0.65f
                    val sunCenterY = height * 0.45f

                    // Soft pulsing morning sun flare
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFF7ED).copy(alpha = 0.45f * pulseGlow),
                                Color(0xFFFDE68A).copy(alpha = 0.25f * pulseGlow),
                                Color.Transparent
                            ),
                            center = Offset(sunCenterX, sunCenterY),
                            radius = width * 0.7f
                        ),
                        center = Offset(sunCenterX, sunCenterY),
                        radius = width * 0.7f
                    )

                    // Ascending morning particles (motes of light)
                    starPositions.take(18).forEachIndexed { index, (relX, relY, radius) ->
                        val currentY = (relY - particlesPhase + 1f) % 1f * height
                        val currentX = relX * width + sin((particlesPhase + index) * 2 * PI).toFloat() * 12f
                        val alpha = (sin(currentY / height * PI) * 0.7f).coerceIn(0.0, 1.0).toFloat()

                        drawCircle(
                            color = Color(0xFFFEF3C7).copy(alpha = alpha),
                            radius = radius * 1.2f,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                BackdropAtmosphere.SUNSET_EVENING -> {
                    // Sunset Twilight Horizon & Evening Fireflies
                    val sunsetHorizonY = height * 0.6f

                    // Warm twilight gradient shift
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF991B1B).copy(alpha = 0.15f * pulseGlow),
                                Color(0xFFD97706).copy(alpha = 0.25f * pulseGlow),
                                Color(0xFF451A03).copy(alpha = 0.2f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Floating evening fireflies
                    starPositions.take(15).forEachIndexed { index, (relX, relY, radius) ->
                        val floatOffset = sin((particlesPhase * 2 * PI + index).toDouble()).toFloat() * 18f
                        val currentX = (relX * width + floatOffset).coerceIn(0f, width)
                        val currentY = (relY * height + cos((particlesPhase * 2 * PI + index).toDouble()).toFloat() * 12f).coerceIn(0f, height)
                        val alpha = (0.3f + 0.5f * sin((particlesPhase * 4 * PI + index).toDouble()).toFloat()).coerceIn(0.1f, 0.85f)

                        drawCircle(
                            color = Color(0xFFFBBF24).copy(alpha = alpha),
                            radius = radius * 1.5f,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                BackdropAtmosphere.STARRY_NIGHT -> {
                    // Twinkling Starfield & Gentle Moonlight
                    starPositions.forEachIndexed { index, (relX, relY, radius) ->
                        val twinklePhase = (particlesPhase * 2 * PI + index * 0.7).toDouble()
                        val twinkleAlpha = ((sin(twinklePhase) + 1.0) * 0.45 + 0.1).coerceIn(0.1, 0.95).toFloat()

                        drawCircle(
                            color = Color(0xFFF8FAFC).copy(alpha = twinkleAlpha),
                            radius = radius,
                            center = Offset(relX * width, relY * height)
                        )
                    }

                    // Soft Moon Glow in top corner
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE2E8F0).copy(alpha = 0.35f * pulseGlow),
                                Color(0xFF38BDF8).copy(alpha = 0.15f * pulseGlow),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.82f, height * 0.22f),
                            radius = width * 0.4f
                        ),
                        center = Offset(width * 0.82f, height * 0.22f),
                        radius = width * 0.4f
                    )
                }

                BackdropAtmosphere.MOSQUE_ADHAN -> {
                    // Divine Golden Rays & Holy Ambient Light around Minarets
                    val minaretPeakX = width * 0.5f
                    val minaretPeakY = height * 0.35f

                    // Sacred radial aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                IslamicGold.copy(alpha = 0.4f * pulseGlow),
                                Color(0xFF10B981).copy(alpha = 0.2f * pulseGlow),
                                Color.Transparent
                            ),
                            center = Offset(minaretPeakX, minaretPeakY),
                            radius = width * 0.65f
                        ),
                        center = Offset(minaretPeakX, minaretPeakY),
                        radius = width * 0.65f
                    )

                    // Ascending light particles (موتس النور)
                    starPositions.take(20).forEachIndexed { index, (relX, relY, radius) ->
                        val currentY = (relY - particlesPhase + 1f) % 1f * height
                        val currentX = relX * width + cos((particlesPhase + index) * PI).toFloat() * 10f
                        val alpha = (sin(currentY / height * PI) * 0.65f).coerceIn(0.0, 1.0).toFloat()

                        drawCircle(
                            color = IslamicGold.copy(alpha = alpha),
                            radius = radius * 1.3f,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                BackdropAtmosphere.FRIDAY_EMERALD -> {
                    // Emerald & Gold Shimmering Light
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF34D399).copy(alpha = 0.35f * pulseGlow),
                                Color(0xFFD4AF37).copy(alpha = 0.2f * pulseGlow),
                                Color.Transparent
                            ),
                            center = Offset(width * 0.5f, height * 0.3f),
                            radius = width * 0.8f
                        )
                    )
                }
            }
        }

        // 3. Dark Bottom Vignette Gradient for pristine text readability
        if (overlayDarkGradient) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Transparent,
                                Color(0xFF0C1410).copy(alpha = 0.85f),
                                Color(0xFF0C1410)
                            ),
                            startY = 0f
                        )
                    )
            )
        }
    }
}
