package com.example.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.service.audio.SoundManager
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    appSettings: AppSettings,
    prayerTimes: List<PrayerTimeItem>,
    soundManager: SoundManager,
    onOpenLocationPicker: () -> Unit,
    onOpenPrayerDetails: (PrayerType) -> Unit,
    onOpenAthkarCategory: (AthkarCategory) -> Unit,
    onOpenRamadanHub: () -> Unit,
    onOpenFridayDialog: () -> Unit,
    onOpenFajrDialog: () -> Unit,
    onOpenQuranSurah: (Int) -> Unit
) {
    val nextPrayer = prayerTimes.firstOrNull { it.isNext } ?: prayerTimes.firstOrNull()
    val isAudioPlaying by soundManager.isPlaying.collectAsState()
    val currentAudioTitle by soundManager.currentPlayingTitle.collectAsState()

    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val remainingMillis = nextPrayer?.let {
        if (it.timeMillis > currentTime) it.timeMillis - currentTime else 0L
    } ?: 0L

    val hours = (remainingMillis / (1000 * 60 * 60)).toInt()
    val minutes = ((remainingMillis / (1000 * 60)) % 60).toInt()
    val seconds = ((remainingMillis / 1000) % 60).toInt()

    val dateFormatted = remember(currentTime) {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        sdf.format(Date(currentTime))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Frosted Glass Location & GPS Header Bar
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 20.dp,
                onClick = onOpenLocationPicker
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = IslamicGreenAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${appSettings.cityName}، ${appSettings.countryName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.EditLocationAlt,
                                contentDescription = "تغيير الموقع أو GPS",
                                tint = IslamicGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$dateFormatted • ${appSettings.calculationMethod.titleArabic}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFA0B5AC))
                        )
                    }

                    GlassPill(
                        backgroundColor = Color(0x3348D5A3),
                        borderColor = IslamicGreenAccent
                    ) {
                        Icon(Icons.Default.GpsFixed, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GPS / مدن العالم", color = IslamicGreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Audio Playback Bar
        if (isAudioPlaying && currentAudioTitle != null) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 16.dp,
                    tintColor = Color(0xFF6B4E10)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = IslamicGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentAudioTitle ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { soundManager.stopAll() }) {
                            Icon(Icons.Default.StopCircle, contentDescription = "إيقاف", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Hero Next Prayer Countdown Glass Card with Mosque Imagery
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 26.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_mosque_adhan),
                        contentDescription = "مسجد مواقيت الصلاة",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color(0xFF0C1914).copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassPill(
                                backgroundColor = Color(0x66000000),
                                borderColor = IslamicGold
                            ) {
                                Text("الصلاة القادمة", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (appSettings.autoSilentEnabled) {
                                GlassPill(
                                    backgroundColor = Color(0x40133B2E),
                                    borderColor = IslamicGreenAccent
                                ) {
                                    Icon(Icons.Default.VolumeOff, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("صامت: ${appSettings.autoSilentDurationMinutes} د", color = IslamicGreenAccent, fontSize = 11.sp)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "صلاة ${nextPrayer?.type?.arabicName ?: "الفجر"}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nextPrayer?.formattedTime ?: "--:--",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Countdown Timer Glass Capsule
                        GlassPill(
                            backgroundColor = Color(0x80000000),
                            borderColor = Color(0x4DFFFFFF)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "متبقي: %02d:%02d:%02d".format(hours, minutes, seconds),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Daily Prayer Tracker (Fard & Sunnah adherence)
        item {
            PrayerTrackerCard()
        }

        // Qibla Compass with Live Sensor Dial & Kaaba Distance
        item {
            QiblaCompassView(
                latitude = appSettings.latitude,
                longitude = appSettings.longitude,
                cityName = appSettings.cityName
            )
        }

        // Quick Spiritual Shortcuts Row
        item {
            Column {
                Text(
                    text = "الأجواء والمناسبات الروحانية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        ActionPill(
                            title = "طبلة المسحراتي",
                            subtitle = "سيد مكاوي",
                            icon = Icons.Default.MusicNote,
                            accentColor = Color(0xFFD97706),
                            onClick = onOpenRamadanHub
                        )
                    }
                    item {
                        ActionPill(
                            title = "مدفع الإفطار",
                            subtitle = "اضرب والدعاء",
                            icon = Icons.Default.FlashOn,
                            accentColor = Color(0xFFDC2626),
                            onClick = onOpenRamadanHub
                        )
                    }
                    item {
                        ActionPill(
                            title = "الجمعة وسورة الكهف",
                            subtitle = "أذان أول وثانٍ",
                            icon = Icons.Default.MenuBook,
                            accentColor = IslamicGreenAccent,
                            onClick = onOpenFridayDialog
                        )
                    }
                    item {
                        ActionPill(
                            title = "منبه الفجر والابتهالات",
                            subtitle = "طوبار والنقشبندي",
                            icon = Icons.Default.WbTwilight,
                            accentColor = Color(0xFF3B82F6),
                            onClick = onOpenFajrDialog
                        )
                    }
                }
            }
        }

        // Today's Prayer Times Grid (Glassmorphic cards)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مواقيت صلوات اليوم",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "انقر للاستماع وتجربة الأذان",
                        style = MaterialTheme.typography.bodySmall.copy(color = IslamicGreenAccent)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    prayerTimes.forEach { prayer ->
                        PrayerRowCard(
                            prayer = prayer,
                            onClick = { onOpenPrayerDetails(prayer.type) }
                        )
                    }
                }
            }
        }

        // Daily Athkar with Scenic Nature Previews
        item {
            Column {
                Text(
                    text = "الأذكار اليومية بصوت وصور الطبيعة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AthkarNatureCard(
                        title = "أذكار الصباح",
                        imageRes = R.drawable.img_nature_morning,
                        badge = "مع الشروق",
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenAthkarCategory(AthkarCategory.MORNING) }
                    )
                    AthkarNatureCard(
                        title = "أذكار المساء",
                        imageRes = R.drawable.img_nature_evening,
                        badge = "مع الغروب",
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenAthkarCategory(AthkarCategory.EVENING) }
                    )
                    AthkarNatureCard(
                        title = "أذكار النوم",
                        imageRes = R.drawable.img_nature_night,
                        badge = "سكينة الليل",
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenAthkarCategory(AthkarCategory.SLEEP) }
                    )
                }
            }
        }

        // Periodic Dhikr & Salawat banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 20.dp,
                tintColor = Color(0xFF16382C)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "منبه الصلاة على النبي والاستغفار",
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "«اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ»",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = {
                            soundManager.playText(
                                "الصلاة على النبي ﷺ",
                                "اللهم صل وسلم وبارك على نبينا وحبيبنا محمد وعلى آله وصحبه أجمعين"
                            )
                        },
                        modifier = Modifier
                            .background(IslamicGold, CircleShape)
                            .size(42.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "استماع", tint = IslamicGreenDark)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionPill(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(148.dp)
            .clickable(onClick = onClick),
        shapeRadius = 18.dp,
        tintColor = Color(0xFF13261F)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, color = Color.White)
            Text(subtitle, color = Color.LightGray, fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
fun PrayerRowCard(
    prayer: PrayerTimeItem,
    onClick: () -> Unit
) {
    val isNext = prayer.isNext

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shapeRadius = 18.dp,
        tintColor = if (isNext) Color(0xFF1E4A3B) else Color(0xFF14241E),
        strokeAlpha = if (isNext) 0.5f else 0.2f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isNext) IslamicGold.copy(alpha = 0.2f) else IslamicGreenAccent.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (prayer.type) {
                            PrayerType.FAJR -> Icons.Default.WbTwilight
                            PrayerType.SUNRISE -> Icons.Default.WbSunny
                            PrayerType.DHUHR -> Icons.Default.LightMode
                            PrayerType.ASR -> Icons.Default.WbCloudy
                            PrayerType.MAGHRIB -> Icons.Default.NightsStay
                            PrayerType.ISHA -> Icons.Default.Bedtime
                        },
                        contentDescription = null,
                        tint = if (isNext) IslamicGold else IslamicGreenAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "صلاة ${prayer.type.arabicName}",
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        if (isNext) {
                            Spacer(modifier = Modifier.width(8.dp))
                            GlassPill(
                                backgroundColor = Color(0x40D4AF37),
                                borderColor = IslamicGold
                            ) {
                                Text("القادمة", color = IslamicGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (prayer.iqamaFormattedTime.isNotEmpty()) {
                        Text(
                            text = "الأذان: ${prayer.formattedTime}  •  الإقامة: ${prayer.iqamaFormattedTime}",
                            fontSize = 12.sp,
                            color = if (isNext) IslamicGoldLight else Color(0xFFA0B5AC)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = prayer.formattedTime,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isNext) IslamicGold else Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "أذان",
                    tint = if (isNext) IslamicGold else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AthkarNatureCard(
    title: String,
    imageRes: Int,
    badge: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 50f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = badge,
                    fontSize = 9.sp,
                    color = IslamicGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

