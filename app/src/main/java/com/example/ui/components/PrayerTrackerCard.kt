package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerType
import com.example.model.WorshipDailyLog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun PrayerTrackerCard(
    initialLog: WorshipDailyLog = WorshipDailyLog(),
    onLogChanged: (WorshipDailyLog) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var log by remember { mutableStateOf(initialLog) }

    fun update(newLog: WorshipDailyLog) {
        log = newLog
        onLogChanged(newLog)
    }

    // Calculate total score: 5 fard + sunnah + witr + morning + evening + sleep + salawat + istighfar + fasting = 12 items
    val totalItems = 12
    var completedCount = log.completedFard.size
    if (log.sunnahDone) completedCount++
    if (log.witrDone) completedCount++
    if (log.morningAthkarDone) completedCount++
    if (log.eveningAthkarDone) completedCount++
    if (log.sleepAthkarDone) completedCount++
    if (log.salawatCompletedCount >= 100) completedCount++
    if (log.istighfarCompletedCount >= 100) completedCount++
    if (log.isFastingToday) completedCount++

    val progress = (completedCount.toFloat() / totalItems.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "worshipProgress")

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shapeRadius = 24.dp,
        tintColor = Color(0xFF11261E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with percentage badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "متتبع العبادات الشامل واليومي ✦",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "سجل الصلوات، الأذكار، الصلاة على النبي، والصيام",
                            color = Color(0xFFA5BDB2),
                            fontSize = 11.sp
                        )
                    }
                }

                GlassPill(
                    backgroundColor = Color(0x3348D5A3),
                    borderColor = IslamicGreenAccent
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% إنجاز اليوم",
                        color = IslamicGreenAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = IslamicGold,
                trackColor = Color(0xFF1B3B2E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Five Daily Fard Prayers
            Text("١. الصلوات الخمس المفروضة:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val fardPrayers = listOf(
                    PrayerType.FAJR,
                    PrayerType.DHUHR,
                    PrayerType.ASR,
                    PrayerType.MAGHRIB,
                    PrayerType.ISHA
                )

                fardPrayers.forEach { prayer ->
                    val isDone = log.completedFard.contains(prayer)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            val nextSet = if (isDone) log.completedFard - prayer else log.completedFard + prayer
                            update(log.copy(completedFard = nextSet))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDone) IslamicGreenAccent else Color(0xFF162E24)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF09241B), modifier = Modifier.size(24.dp))
                            } else {
                                Text(prayer.arabicName.take(1), color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(prayer.arabicName, fontSize = 11.sp, color = if (isDone) IslamicGreenAccent else Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Sunnah & Fasting Row
            Text("٢. السنن وصيام اليوم:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = log.sunnahDone,
                    onClick = { update(log.copy(sunnahDone = !log.sunnahDone)) },
                    label = { Text("السنن: ${if (log.sunnahDone) "تمت ✓" else "لم تؤدَ"}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B4534),
                        selectedLabelColor = IslamicGold
                    )
                )

                FilterChip(
                    selected = log.witrDone,
                    onClick = { update(log.copy(witrDone = !log.witrDone)) },
                    label = { Text("الوتر والقيام: ${if (log.witrDone) "تمت ✓" else "لم تؤدَ"}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B4534),
                        selectedLabelColor = IslamicGold
                    )
                )

                FilterChip(
                    selected = log.isFastingToday,
                    onClick = { update(log.copy(isFastingToday = !log.isFastingToday)) },
                    label = { Text("صائم اليوم: ${if (log.isFastingToday) "نعم 🌙" else "لا"}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4A3512),
                        selectedLabelColor = IslamicGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Daily Athkar Checklist (Morning, Evening, Sleep)
            Text("٣. الأذكار اليومية:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = log.morningAthkarDone,
                    onClick = { update(log.copy(morningAthkarDone = !log.morningAthkarDone)) },
                    label = { Text("الصباح ${if (log.morningAthkarDone) "✓" else ""}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B4534),
                        selectedLabelColor = IslamicGreenAccent
                    )
                )
                FilterChip(
                    selected = log.eveningAthkarDone,
                    onClick = { update(log.copy(eveningAthkarDone = !log.eveningAthkarDone)) },
                    label = { Text("المساء ${if (log.eveningAthkarDone) "✓" else ""}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B4534),
                        selectedLabelColor = IslamicGreenAccent
                    )
                )
                FilterChip(
                    selected = log.sleepAthkarDone,
                    onClick = { update(log.copy(sleepAthkarDone = !log.sleepAthkarDone)) },
                    label = { Text("النوم ${if (log.sleepAthkarDone) "✓" else ""}", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1B4534),
                        selectedLabelColor = IslamicGreenAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Salawat on Prophet ﷺ & Istighfar Counters
            Text("٤. الصلاة على النبي ﷺ والاستغفار:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Salawat quick bead
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val next = log.salawatCompletedCount + 10
                            update(log.copy(salawatCompletedCount = next))
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF17382B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("الصلاة على النبي ﷺ", fontSize = 11.sp, color = IslamicGold, fontWeight = FontWeight.Bold)
                        Text("${log.salawatCompletedCount} / 100", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("+10 تسبيحات", fontSize = 9.sp, color = IslamicGreenAccent)
                    }
                }

                // Istighfar quick bead
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val next = log.istighfarCompletedCount + 10
                            update(log.copy(istighfarCompletedCount = next))
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF17382B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("الاستغفار اليومي", fontSize = 11.sp, color = IslamicGold, fontWeight = FontWeight.Bold)
                        Text("${log.istighfarCompletedCount} / 100", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("+10 استغفار", fontSize = 9.sp, color = IslamicGreenAccent)
                    }
                }
            }
        }
    }
}
