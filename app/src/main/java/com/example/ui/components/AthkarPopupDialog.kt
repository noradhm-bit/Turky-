package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AthkarCategory
import com.example.model.DhikrItem
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun AthkarPopupDialog(
    category: AthkarCategory,
    dhikrList: List<DhikrItem>,
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    if (dhikrList.isEmpty()) {
        onDismiss()
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    val currentDhikr = dhikrList.getOrNull(currentIndex) ?: dhikrList.first()
    var count by remember(currentDhikr.id) { mutableStateOf(0) }
    val isPlaying by soundManager.isPlaying.collectAsState()

    // Select Nature Image based on Athkar category (as requested: صور للطبيعة مناسبة للحالة)
    val imageRes = when (category) {
        AthkarCategory.MORNING -> R.drawable.img_nature_morning
        AthkarCategory.EVENING -> R.drawable.img_nature_evening
        AthkarCategory.SLEEP -> R.drawable.img_nature_night
        AthkarCategory.WAKING -> R.drawable.img_nature_morning
        AthkarCategory.POST_PRAYER -> R.drawable.img_mosque_adhan
        AthkarCategory.ISTIGHFAR_SALAWAT -> R.drawable.img_nature_evening
    }

    val progress = if (currentDhikr.targetCount > 0) {
        count.toFloat() / currentDhikr.targetCount.toFloat()
    } else 1f
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "athkarProgress")

    Dialog(
        onDismissRequest = {
            soundManager.stopAll()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.88f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10261E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Nature Photo Banner (شاشات وصور متحركة للشروق والغروب والنجوم)
                    val atmosphere = when (category) {
                        AthkarCategory.MORNING, AthkarCategory.WAKING -> BackdropAtmosphere.SUNRISE_MORNING
                        AthkarCategory.EVENING -> BackdropAtmosphere.SUNSET_EVENING
                        AthkarCategory.SLEEP -> BackdropAtmosphere.STARRY_NIGHT
                        AthkarCategory.POST_PRAYER -> BackdropAtmosphere.MOSQUE_ADHAN
                        else -> BackdropAtmosphere.SUNRISE_MORNING
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        AnimatedCinematicBackdrop(
                            imageRes = imageRes,
                            atmosphere = atmosphere,
                            modifier = Modifier.fillMaxSize()
                        )


                        IconButton(
                            onClick = {
                                soundManager.stopAll()
                                onDismiss()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = category.titleArabic,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "الذكر (${currentIndex + 1} من ${dhikrList.size})",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                            )
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Athkar Text Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18382C)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentDhikr.arabicText,
                                    fontSize = 18.sp,
                                    lineHeight = 32.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المصدر: ${currentDhikr.reference}",
                                        fontSize = 12.sp,
                                        color = IslamicGold
                                    )
                                    IconButton(
                                        onClick = {
                                            if (isPlaying) {
                                                soundManager.stopAll()
                                            } else {
                                                soundManager.playText(
                                                    "${category.titleArabic}: ذكر ${currentIndex + 1}",
                                                    currentDhikr.arabicText
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            if (isPlaying) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                            contentDescription = "استماع بالصوت",
                                            tint = IslamicGold
                                        )
                                    }
                                }
                                if (currentDhikr.virtue.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "★ الفضل: ${currentDhikr.virtue}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFC7E2D5),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Interactive Counter Button (Big Circular Tap Target)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF142E24))
                                .clickable {
                                    if (count < currentDhikr.targetCount) {
                                        count++
                                    } else {
                                        // Auto advance to next
                                        if (currentIndex < dhikrList.size - 1) {
                                            currentIndex++
                                            count = 0
                                        }
                                    }
                                }
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(104.dp),
                                color = IslamicGold,
                                trackColor = Color(0xFF234D3D),
                                strokeWidth = 6.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$count / ${currentDhikr.targetCount}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (count >= currentDhikr.targetCount) "اكتمل ✓" else "انقر للتسبيح",
                                    color = if (count >= currentDhikr.targetCount) Color(0xFF48D5A3) else IslamicGold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Navigation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (currentIndex > 0) {
                                        soundManager.stopAll()
                                        currentIndex--
                                        count = 0
                                    }
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "السابق")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("السابق")
                            }

                            Button(
                                onClick = {
                                    if (currentIndex < dhikrList.size - 1) {
                                        soundManager.stopAll()
                                        currentIndex++
                                        count = 0
                                    } else {
                                        soundManager.stopAll()
                                        onDismiss()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Text(if (currentIndex < dhikrList.size - 1) "التالي" else "إنهاء الأذكار")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowBack, contentDescription = "التالي")
                            }
                        }
                    }
                }
            }
        }
    }
}
