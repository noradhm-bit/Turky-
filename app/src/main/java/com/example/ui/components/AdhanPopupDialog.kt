package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.model.MuezzinSound
import com.example.model.PrayerType
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun AdhanPopupDialog(
    prayerType: PrayerType,
    muezzin: MuezzinSound,
    silentModeActive: Boolean,
    silentStartDelayMinutes: Int = 0,
    silentMinutesRemaining: Int = 25,
    iqamaFormattedTime: String = "",
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    val isPlaying by soundManager.isPlaying.collectAsState()
    var isSilentModeForced by remember { mutableStateOf(silentModeActive) }

    LaunchedEffect(Unit) {
        soundManager.playAdhan(prayerType.arabicName, muezzin)
    }

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
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Mosque Photo Header (لقطات متحركة للمساجد وأنوار المآذن)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        AnimatedCinematicBackdrop(
                            imageRes = R.drawable.img_mosque_adhan,
                            atmosphere = BackdropAtmosphere.MOSQUE_ADHAN,
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
                            SuggestionChip(
                                onClick = {},
                                label = { Text("حان الآن موعد الأذان", color = IslamicGold, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.Black.copy(alpha = 0.6f))
                            )
                            Text(
                                text = "أذان صلاة ${prayerType.arabicName}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "بصوت: ${muezzin.titleArabic}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE0E0E0))
                            )
                        }
                    }

                    // Content Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Silent mode indicator
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSilentModeForced) Color(0xFF1E3A2F) else Color(0xFF2A2E2C)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isSilentModeForced) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = if (isSilentModeForced) IslamicGold else Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isSilentModeForced) "الوضع الصامت أثناء الصلاة مفعّل" else "الوضع الصامت للصلاة معطل",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (isSilentModeForced) "يبدأ بعد $silentStartDelayMinutes د، ويستمر $silentMinutesRemaining دقيقة ثم يعود الهاتف للوضع العام تلقائياً" else "يمكنك تفعيله لتجنب الإزعاج في المسجد",
                                            color = Color(0xFFB0C4B8),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Switch(
                                    checked = isSilentModeForced,
                                    onCheckedChange = { isSilentModeForced = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = IslamicGold)
                                )
                            }
                        }

                        if (iqamaFormattedTime.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF132D23)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = IslamicGold)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("موعد إقامة الصلاة ⏱️", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(iqamaFormattedTime, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                    }
                                    FilledTonalButton(
                                        onClick = { soundManager.playIqama(prayerType.arabicName) },
                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = IslamicGold)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGreenDark, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("سماع الإقامة", color = IslamicGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Duaa After Adhan
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF142E25)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "دعاء ما بعد الأذان",
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "«اللَّهُمَّ رَبَّ هَذِهِ الدَّعْوَةِ التَّامَّةِ، وَالصَّلَاةِ القَائِمَةِ، آتِ مُحَمَّداً الوَسِيلَةَ وَالفَضِيلَةَ، وَابْعَثْهُ مَقَاماً مَحْمُوداً الَّذِي وَعَدْتَهُ، حَلَّتْ لَهُ شَفَاعَتِي يَوْمَ القِيَامَةِ»",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 24.sp,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        soundManager.stopAll()
                                    } else {
                                        soundManager.playAdhan(prayerType.arabicName, muezzin)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPlaying) Color(0xFFB71C1C) else IslamicGreenPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isPlaying) "إيقاف الصوت" else "تشغيل الأذان")
                            }

                            OutlinedButton(
                                onClick = {
                                    soundManager.stopAll()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("إغلاق التنبيه")
                            }
                        }
                    }
                }
            }
        }
    }
}
