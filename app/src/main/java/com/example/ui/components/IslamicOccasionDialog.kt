package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.IslamicOccasion
import com.example.model.OccasionCategory
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun IslamicOccasionDialog(
    occasion: IslamicOccasion,
    soundManager: SoundManager,
    onDismiss: () -> Unit
) {
    var isAudioPlaying by remember { mutableStateOf(false) }
    var reminderActive by remember { mutableStateOf(occasion.reminderEnabled) }
    var fastingLogged by remember { mutableStateOf(false) }

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
                .background(Color.Black.copy(alpha = 0.82f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(26.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(IslamicGold.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101B16)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                soundManager.stopAll()
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.LightGray)
                        }

                        // Category Badge
                        val (categoryText, badgeBg, badgeTextColor) = when (occasion.category) {
                            OccasionCategory.MAJOR_EID -> Triple("عيد مبارك 🎉", IslamicGold.copy(alpha = 0.2f), IslamicGold)
                            OccasionCategory.SUNNAH_FASTING -> Triple("صيام مستحب 🌙", Color(0xFF1E3A8A).copy(alpha = 0.35f), Color(0xFF93C5FD))
                            OccasionCategory.BLESSED_SEASON -> Triple("موسم طاعة 🌟", IslamicGreenAccent.copy(alpha = 0.2f), IslamicGreenAccent)
                            OccasionCategory.BLESSED_NIGHT -> Triple("ليلة مباركة ✨", Color(0xFF581C87).copy(alpha = 0.35f), Color(0xFFD8B4FE))
                            OccasionCategory.HISTORICAL_EVENT -> Triple("حدث إسلامي 📜", Color(0xFF78350F).copy(alpha = 0.35f), Color(0xFFFDE68A))
                        }

                        Surface(
                            color = badgeBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = categoryText,
                                color = badgeTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Emblem Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(IslamicGold.copy(alpha = 0.35f), Color(0xFF1B3B30))
                                )
                            )
                            .border(1.dp, IslamicGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (occasion.category) {
                                OccasionCategory.MAJOR_EID -> Icons.Default.Celebration
                                OccasionCategory.SUNNAH_FASTING -> Icons.Default.WbTwilight
                                OccasionCategory.BLESSED_SEASON -> Icons.Default.Stars
                                OccasionCategory.BLESSED_NIGHT -> Icons.Default.NightsStay
                                OccasionCategory.HISTORICAL_EVENT -> Icons.Default.AutoStories
                            },
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title & Date
                    Text(
                        text = occasion.nameArabic,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFF1C2C25),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الموعد: ${occasion.hijriFormattedDate}",
                                color = IslamicGreenAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary & Virtues Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162520))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "عن هذه المناسبة المباركة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = IslamicGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = occasion.summary,
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFF233830), thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "فضل اليوم وثوابه",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = IslamicGreenAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = occasion.virtue,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 19.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recommended Deeds Chips
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "الأعمال المستحبة في هذا اليوم:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            occasion.recommendedDeeds.take(3).forEach { deed ->
                                Surface(
                                    color = Color(0xFF1D352B),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "✓ $deed",
                                        color = IslamicGreenAccent,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Supplication & Audio Player
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2822)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenAccent.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "دعاء وذكر المناسبة المأثور",
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = occasion.supplicationText,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (isAudioPlaying) {
                                        soundManager.stopAll()
                                        isAudioPlaying = false
                                    } else {
                                        isAudioPlaying = true
                                        soundManager.playText(
                                            title = occasion.nameArabic,
                                            content = occasion.supplicationText,
                                            onComplete = { isAudioPlaying = false }
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAudioPlaying) Color(0xFFB91C1C) else IslamicGreenPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAudioPlaying) "إيقاف الصوت" else "استماع للدعاء بصوت شجي",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reminder Toggle & Fasting Tracker Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("تنبيه المناسبة مسبقاً", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("إشعار وصوت قبل المناسبة بيوم", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = reminderActive,
                            onCheckedChange = { reminderActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = IslamicGreenAccent,
                                checkedTrackColor = Color(0xFF1B3B30)
                            )
                        )
                    }

                    if (occasion.category == OccasionCategory.SUNNAH_FASTING) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { fastingLogged = !fastingLogged },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (fastingLogged) IslamicGreenAccent else Color.Gray
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (fastingLogged) IslamicGreenAccent.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = if (fastingLogged) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (fastingLogged) IslamicGreenAccent else Color.LightGray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (fastingLogged) "تم تسجيل نية صيام هذا اليوم في سجل العبادات ✓" else "تسجيل نية صيام هذا اليوم المبارك",
                                color = if (fastingLogged) IslamicGreenAccent else Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
