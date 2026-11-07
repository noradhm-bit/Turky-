package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun MesaharatyDialog(
    soundManager: SoundManager,
    minutesUntilFajr: Int,
    onDismiss: () -> Unit
) {
    var selectedVoice by remember { mutableStateOf("سيد مكاوي") }
    val isPlaying by soundManager.isPlaying.collectAsState()

    // Drum pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "drumTransition")
    val drumScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPlaying) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drumScale"
    )

    Dialog(
        onDismissRequest = {
            soundManager.stopAll()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "طَبْلَةُ الْمُسَحِّرَاتِي 🥁",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = {
                        soundManager.stopAll()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fajr Countdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132A22)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "باقي على أذان الفجر (موعد الإمساك)",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$minutesUntilFajr دقيقة",
                            color = IslamicGold,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Pulsing Drum Graphic
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(drumScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(IslamicGold, Color(0xFF8B6B18))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = "طبلة المسحراتي",
                        tint = IslamicGreenDark,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "«إصْحَى يَا نَايِمْ.. وَحِّدِ الدَّايِمْ! رَمَضَان كَرِيمْ»",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "قال النبي ﷺ: «تَسَحَّرُوا فَإِنَّ فِي السَّحُورِ بَرَكَة»",
                    color = Color(0xFFB5D4C7),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Voice selection
                Text("اختر صوت المسحراتي:", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val voices = listOf("سيد مكاوي", "محمود البنا", "أهازيج شعبية")
                    voices.forEach { voice ->
                        FilterChip(
                            selected = selectedVoice == voice,
                            onClick = { selectedVoice = voice },
                            label = { Text(voice, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = IslamicGreenDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button
                Button(
                    onClick = {
                        if (isPlaying) {
                            soundManager.stopAll()
                        } else {
                            soundManager.playMesaharaty(selectedVoice)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color(0xFFC62828) else IslamicGreenPrimary
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPlaying) "إيقاف صوت المسحراتي" else "تشغيل نداء المسحراتي والإيقاع")
                }
            }
        }
    }
}

@Composable
fun IftarCannonDialog(
    soundManager: SoundManager,
    minutesUntilMaghrib: Int,
    onDismiss: () -> Unit
) {
    val isPlaying by soundManager.isPlaying.collectAsState()

    Dialog(
        onDismissRequest = {
            soundManager.stopAll()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162520))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مَدْفَعُ الإِفْطَارِ 💥",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = {
                        soundManager.stopAll()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "«مَدْفَعُ الإِفْطَارِ.. اِضْرِبْ!»",
                    fontSize = 20.sp,
                    color = Color(0xFFFFB300),
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF22362F)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "دعاء الإفطار المأثور",
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "«ذَهَبَ الظَّمَأُ وَابْتَلَّتِ العُرُوقُ، وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللهُ»",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isPlaying) {
                            soundManager.stopAll()
                        } else {
                            soundManager.playIftarCannon()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPlaying) "إيقاف الصوت" else "إطلاق صوت المدفع والدعاء")
                }
            }
        }
    }
}

@Composable
fun EidTakbeeratDialog(
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    var selectedStyle by remember { mutableStateOf("تكبيرات الحرم المكي جماعية") }
    val isPlaying by soundManager.isPlaying.collectAsState()

    Dialog(
        onDismissRequest = {
            soundManager.stopAll()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3227))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تكبيرات العيد والتهليل 🌙",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = {
                        soundManager.stopAll()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "«اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، لَا إِلَهَ إِلَّا اللهُ، اللهُ أَكْبَرُ، اللهُ أَكْبَرُ، وَللهِ الحَمْدُ»",
                    fontSize = 17.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                val styles = listOf("تكبيرات الحرم المكي جماعية", "تكبيرات مصرية بأصوات عذبة", "تكبيرات الحرم النبوي")
                styles.forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
                        )
                        Text(style, color = Color.White, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isPlaying) {
                            soundManager.stopAll()
                        } else {
                            soundManager.playEidTakbeerat(selectedStyle)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color(0xFFB71C1C) else IslamicGreenPrimary
                    )
                ) {
                    Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPlaying) "إيقاف التكبيرات" else "تشغيل تكبيرات العيد")
                }
            }
        }
    }
}
