package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun FridaySpecialDialog(
    soundManager: SoundManager,
    onOpenSurahKahf: () -> Unit,
    onDismiss: () -> Unit
) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "يوم الجمعة المبارك 🕌",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = IslamicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF132A22)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = IslamicGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الأذان الأول للجمعة: مفعّل (قبل الصلاة بـ 45 دقيقة)", color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeOff, contentDescription = null, tint = Color(0xFF48D5A3))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الوضع الصامت لخطبة الجمعة: 60 دقيقة", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "قال النبي ﷺ: «مَنْ قَرَأَ سُورَةَ الْكَهْفِ فِي يَوْمِ الْجُمُعَةِ أَضَاءَ لَهُ مِنَ النُّورِ مَا بَيْنَ الْجُمُعَتَيْنِ»",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onOpenSurahKahf()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = IslamicGreenDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قراءة سورة الكهف الآن", color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FajrSpecialDialog(
    soundManager: SoundManager,
    minutesUntilFajr: Int,
    onDismiss: () -> Unit
) {
    val isPlaying by soundManager.isPlaying.collectAsState()
    var selectedSheikh by remember { mutableStateOf("الشيخ نصر الدين طوبار") }

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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261E))
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
                        text = "منبه الفجر والابتهالات 🌌",
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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF193B2F)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("الاستيقاظ لصلاة الفجر (عداد تنازلي)", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "باقي $minutesUntilFajr دقيقة على الفجر",
                            color = IslamicGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("«الصلاة خير من النوم.. بورك لأمتي في بكورها»", color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("ابتهالات ما قبل الفجر المبارك:", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                val sheikhs = listOf("الشيخ نصر الدين طوبار", "الشيخ سيد النقشبندي")
                sheikhs.forEach { sheikh ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSheikh == sheikh,
                            onClick = { selectedSheikh = sheikh },
                            colors = RadioButtonDefaults.colors(selectedColor = IslamicGold)
                        )
                        Text(sheikh, color = Color.White, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isPlaying) {
                            soundManager.stopAll()
                        } else {
                            soundManager.playFajrIbtihalat(selectedSheikh)
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
                    Text(if (isPlaying) "إيقاف الابتهالات" else "استماع لابتهالات الفجر")
                }
            }
        }
    }
}
