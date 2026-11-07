package com.example.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PrayerType
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun PrePrayerReminderDialog(
    upcomingPrayer: PrayerType,
    previousPrayer: PrayerType,
    minutesRemaining: Int,
    soundManager: SoundManager,
    onAnswerPreviousPrayer: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var answeredState by remember { mutableStateOf<Boolean?>(null) }
    val isPlaying by soundManager.isPlaying.collectAsState()

    val hadithPreparation = "قال رسول الله ﷺ: «مَنْ تَوَضَّأَ فَأَحْسَنَ الْوُضُوءَ خَرَجَتْ خَطَايَاهُ مِنْ جَسَدِهِ حَتَّى تَخْرُجَ مِنْ تَحْتِ أَظْفَارِهِ»"
    val duaaWudu = "«أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّداً عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ المُتَطَهِّرِينَ»"

    Dialog(
        onDismissRequest = {
            soundManager.stopAll()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(IslamicGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "اقتربت صلاة ${upcomingPrayer.arabicName} ⏳",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "متبقي $minutesRemaining دقيقة على الأذان",
                                color = IslamicGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = {
                        soundManager.stopAll()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // CRITICAL QUESTION: هل صليت الصلاة الفائتة؟
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF193B2F))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "سؤال للمؤمن: هل أديت صلاة ${previousPrayer.arabicName} الفائتة؟",
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    answeredState = true
                                    onAnswerPreviousPrayer(true)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (answeredState == true) Color(0xFF2E7D32) else Color(0xFF1B4D3C)
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نعم، صليتها ✓", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    answeredState = false
                                    onAnswerPreviousPrayer(false)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (answeredState == false) Color(0xFFC62828) else Color(0xFF3B2525)
                                )
                            ) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("لا، سأقضيها الآن", fontSize = 12.sp)
                            }
                        }

                        if (answeredState == false) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "💡 بادر بالوضوء الآن وأداء صلاة ${previousPrayer.arabicName} قضاءً قبل دخول وقت صلاة ${upcomingPrayer.arabicName}.",
                                color = Color(0xFFFFB4A9),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        } else if (answeredState == true) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "تقبل الله طاعتك وثبتك! تهيأ الآن للوضوء لصلاة ${upcomingPrayer.arabicName}.",
                                color = Color(0xFF48D5A3),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hadith Preparation & Duaa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132820))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("فضل الوضوء والاستعداد:", color = IslamicGreenAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            IconButton(
                                onClick = {
                                    if (isPlaying) {
                                        soundManager.stopAll()
                                    } else {
                                        soundManager.playText("فضل الاستعداد للصلاة", "$hadithPreparation. $duaaWudu")
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = hadithPreparation,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "دعاء ما بعد الوضوء: $duaaWudu",
                            color = Color(0xFFC7E2D5),
                            fontSize = 11.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        soundManager.stopAll()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                ) {
                    Text("الاستعداد للوضوء والصلاة")
                }
            }
        }
    }
}
