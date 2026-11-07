package com.example.ui.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.R
import com.example.data.IslamicRepository
import com.example.model.AthkarCategory
import com.example.model.DhikrItem
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun AthkarScreen(
    soundManager: SoundManager,
    initialCategory: AthkarCategory = AthkarCategory.MORNING,
    onOpenVisualPopup: (AthkarCategory) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Athkar List, 1: Digital Sebha
    val isPlaying by soundManager.isPlaying.collectAsState()

    val currentList = when (selectedCategory) {
        AthkarCategory.MORNING -> IslamicRepository.morningAthkar
        AthkarCategory.EVENING -> IslamicRepository.eveningAthkar
        AthkarCategory.SLEEP -> IslamicRepository.sleepAthkar
        AthkarCategory.POST_PRAYER -> IslamicRepository.postPrayerAthkar
        else -> IslamicRepository.morningAthkar
    }

    val context = LocalContext.current
    val vibrator = remember {
        context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Tab Switcher: Athkar vs Electronic Sebha
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = IslamicGreenPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("الأذكار المأثورة", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("المسبحة الإلكترونية", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.TouchApp, contentDescription = null) }
            )
        }

        if (selectedTab == 0) {
            // Category Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                val categories = listOf(
                    AthkarCategory.MORNING,
                    AthkarCategory.EVENING,
                    AthkarCategory.SLEEP,
                    AthkarCategory.POST_PRAYER
                )
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.titleArabic, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Nature Banner & Full Popup Trigger Button
            val bannerImage = when (selectedCategory) {
                AthkarCategory.MORNING -> R.drawable.img_nature_morning
                AthkarCategory.EVENING -> R.drawable.img_nature_evening
                AthkarCategory.SLEEP -> R.drawable.img_nature_night
                AthkarCategory.POST_PRAYER -> R.drawable.img_mosque_adhan
                else -> R.drawable.img_nature_morning
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(130.dp)
                    .clickable { onOpenVisualPopup(selectedCategory) },
                shape = RoundedCornerShape(18.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = bannerImage),
                        contentDescription = "طبيعة الأذكار",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                    startY = 20f
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = selectedCategory.titleArabic,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "انقر لفتح النافذة المنبثقة التفاعلية بصور الطبيعة",
                                color = IslamicGold,
                                fontSize = 12.sp
                            )
                        }
                        FilledTonalButton(
                            onClick = { onOpenVisualPopup(selectedCategory) },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = IslamicGold)
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = null, tint = IslamicGreenDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("عرض منبثق", color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Athkar Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList, key = { it.id }) { item ->
                    DhikrCardItem(
                        item = item,
                        soundManager = soundManager,
                        onVibrate = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator?.vibrate(35)
                            }
                        }
                    )
                }
            }
        } else {
            // DIGITAL SEBHA (المسبحة الإلكترونية الذكية)
            DigitalSebhaView(soundManager = soundManager, vibrator = vibrator)
        }
    }
}

@Composable
fun DhikrCardItem(
    item: DhikrItem,
    soundManager: SoundManager,
    onVibrate: () -> Unit
) {
    var count by remember { mutableStateOf(0) }
    val isComplete = count >= item.targetCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = item.arabicText,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (item.virtue.isNotEmpty()) {
                Text(
                    text = "★ ${item.virtue}",
                    fontSize = 12.sp,
                    color = IslamicGreenPrimary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            soundManager.playText("قراءة الذكر", item.arabicText)
                        }
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "قراءة", tint = IslamicGreenPrimary)
                    }
                    Text(
                        text = item.reference,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Interactive Counter Pill
                Button(
                    onClick = {
                        if (count < item.targetCount) {
                            count++
                            onVibrate()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isComplete) Color(0xFF2E7D32) else IslamicGreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isComplete) "تم ✓" else "$count / ${item.targetCount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DigitalSebhaView(
    soundManager: SoundManager,
    vibrator: Vibrator?
) {
    var totalCount by remember { mutableStateOf(0) }
    var currentCycleCount by remember { mutableStateOf(0) }
    var targetCount by remember { mutableStateOf(33) }
    var selectedZikr by remember { mutableStateOf("سُبْحَانَ اللهِ") }

    val zikrOptions = listOf(
        "سُبْحَانَ اللهِ",
        "الحَمْدُ للهِ",
        "اللهُ أَكْبَرُ",
        "لَا إِلَهَ إِلَّا اللهُ",
        "أَسْتَغْفِرُ اللهَ وَأَتُوبُ إِلَيْهِ",
        "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            // Selected Zikr dropdown/row
            Text(
                text = "اختر صيغة التسبيح أو الذكر:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(zikrOptions) { zikr ->
                    FilterChip(
                        selected = selectedZikr == zikr,
                        onClick = {
                            selectedZikr = zikr
                            currentCycleCount = 0
                        },
                        label = { Text(zikr, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target selector: 33, 99, 100, Free
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val targets = listOf(33, 99, 100, 1000)
                targets.forEach { t ->
                    FilterChip(
                        selected = targetCount == t,
                        onClick = {
                            targetCount = t
                            currentCycleCount = 0
                        },
                        label = { Text(if (t == 1000) "مفتوح" else "$t") },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = IslamicGreenDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Large Sebha Bead Clickable
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(IslamicGreenPrimary, IslamicGreenDark)
                        )
                    )
                    .clickable {
                        currentCycleCount++
                        totalCount++
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(45)
                        }

                        if (currentCycleCount >= targetCount && targetCount != 1000) {
                            soundManager.playText("اكتمل التسبيح", "ما شاء الله، اكتملت الدورة")
                        }
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedZikr,
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$currentCycleCount",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "الهدف: $targetCount",
                        color = Color(0xFFC7E2D5),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط للتسبيح ✦",
                        color = IslamicGold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Counter Stats and Reset
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إجمالي التسبيحات اليومية", fontSize = 12.sp, color = Color.Gray)
                        Text("$totalCount تسبيحة", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = IslamicGreenPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            currentCycleCount = 0
                            totalCount = 0
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفير العداد", color = Color.Red)
                    }
                }
            }
        }
    }
}
