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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.IslamicRepository
import com.example.model.DhikrItem
import com.example.model.PrayerType
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun PostPrayerAthkarDialog(
    prayerType: PrayerType,
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    val athkarList = IslamicRepository.postPrayerAthkar
    var currentIndex by remember { mutableStateOf(0) }
    val currentItem = athkarList.getOrNull(currentIndex) ?: athkarList.first()
    var count by remember(currentItem.id) { mutableStateOf(0) }
    val isPlaying by soundManager.isPlaying.collectAsState()

    val progress = if (currentItem.targetCount > 0) {
        count.toFloat() / currentItem.targetCount.toFloat()
    } else 1f
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "postAthkarProgress")

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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Mosque Imagery
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_mosque_adhan),
                            contentDescription = "أذكار بعد الصلاة",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xFF0F261E)),
                                        startY = 70f
                                    )
                                )
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
                                text = "أذكار ما بعد صلاة ${prayerType.arabicName} المفروضة 🕌",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "الذكر (${currentIndex + 1} من ${athkarList.size})",
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF163B2E)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentItem.arabicText,
                                    fontSize = 17.sp,
                                    lineHeight = 30.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentItem.reference,
                                        fontSize = 11.sp,
                                        color = IslamicGold
                                    )
                                    IconButton(
                                        onClick = {
                                            if (isPlaying) {
                                                soundManager.stopAll()
                                            } else {
                                                soundManager.playText(
                                                    "أذكار بعد صلاة ${prayerType.arabicName}",
                                                    currentItem.arabicText
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                            contentDescription = "استماع",
                                            tint = IslamicGold
                                        )
                                    }
                                }
                                if (currentItem.virtue.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "★ ${currentItem.virtue}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFC7E2D5),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Circular Interactive Bead Tap Target
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF133327))
                                .clickable {
                                    if (count < currentItem.targetCount) {
                                        count++
                                    } else {
                                        if (currentIndex < athkarList.size - 1) {
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
                                    text = "$count / ${currentItem.targetCount}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (count >= currentItem.targetCount) "تم ✓" else "انقر للتسبيح",
                                    color = if (count >= currentItem.targetCount) IslamicGreenAccent else IslamicGold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Navigation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                Text("السابق")
                            }

                            Button(
                                onClick = {
                                    if (currentIndex < athkarList.size - 1) {
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
                                Text(if (currentIndex < athkarList.size - 1) "التالي" else "إنهاء الأذكار")
                            }
                        }
                    }
                }
            }
        }
    }
}
