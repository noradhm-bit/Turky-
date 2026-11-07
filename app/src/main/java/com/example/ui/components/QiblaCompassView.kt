package com.example.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.location.LocationHelper
import com.example.service.prayer.PrayerCalculationEngine
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary
import kotlin.math.*

@Composable
fun QiblaCompassView(
    latitude: Double,
    longitude: Double,
    cityName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val qiblaAngle = remember(latitude, longitude) {
        PrayerCalculationEngine.calculateQiblaDirection(latitude, longitude)
    }
    val distanceToKaaba = remember(latitude, longitude) {
        LocationHelper.calculateDistanceToKaaba(latitude, longitude)
    }

    var deviceAzimuth by remember { mutableStateOf(0f) }

    // Compass sensor listener
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val degrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    deviceAzimuth = (degrees + 360f) % 360f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorManager != null && rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Needle relative rotation
    val needleRotation = (qiblaAngle - deviceAzimuth + 360f) % 360f
    val animatedNeedleRotation by animateFloatAsState(
        targetValue = needleRotation,
        animationSpec = tween(150),
        label = "qiblaNeedle"
    )

    val isFacingQibla = abs(needleRotation) < 5f || abs(needleRotation - 360f) < 5f

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shapeRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "بوصلة القبلة الدقيقة",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }

                GlassPill(
                    backgroundColor = if (isFacingQibla) Color(0x3348D5A3) else Color(0x22FFFFFF),
                    borderColor = if (isFacingQibla) IslamicGreenAccent else Color(0x33FFFFFF)
                ) {
                    Text(
                        text = if (isFacingQibla) "أنت باتجاه الكعبة ✓" else "زاوية القبلة: ${qiblaAngle.roundToInt()}°",
                        color = if (isFacingQibla) IslamicGreenAccent else IslamicGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Compass Dial (Apple Glass / Samsung One UI dial)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1B382D),
                                Color(0xFF0F221B),
                                Color(0xFF07120E)
                            )
                        )
                    )
            ) {
                // Dial ring canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 12.dp.toPx()

                    // Outer glass ring
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.White.copy(alpha = 0.2f), Color(0xFF48D5A3).copy(alpha = 0.3f), Color.White.copy(alpha = 0.2f))
                        ),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Tick marks
                    for (i in 0 until 360 step 30) {
                        val angleRad = Math.toRadians(i.toDouble() - 90.0)
                        val startLen = if (i % 90 == 0) 14.dp.toPx() else 7.dp.toPx()
                        val startX = (center.x + (radius - startLen) * cos(angleRad)).toFloat()
                        val startY = (center.y + (radius - startLen) * sin(angleRad)).toFloat()
                        val endX = (center.x + radius * cos(angleRad)).toFloat()
                        val endY = (center.y + radius * sin(angleRad)).toFloat()

                        drawLine(
                            color = if (i % 90 == 0) IslamicGold else Color.White.copy(alpha = 0.3f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (i % 90 == 0) 2.5.dp.toPx() else 1.2.dp.toPx()
                        )
                    }
                }

                // Rotating Qibla Needle pointing towards Kaaba
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(animatedNeedleRotation),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        // Kaaba Icon pointer at tip of arrow
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isFacingQibla) IslamicGreenAccent else IslamicGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = "اتجاه القبلة",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // North indicator opposite
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    }
                }

                // Center Hub
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(IslamicGold),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Distance to Kaaba info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المسافة إلى الكعبة المشرفة من $cityName:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "%,d كم".format(distanceToKaaba),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGold
                )
            }
        }
    }
}
