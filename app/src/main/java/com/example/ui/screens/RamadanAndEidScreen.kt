package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.IslamicRepository
import com.example.model.IslamicOccasion
import com.example.model.OccasionCategory
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import com.example.service.audio.SoundManager
import com.example.ui.components.EidTakbeeratDialog
import com.example.ui.components.IftarCannonDialog
import com.example.ui.components.IslamicOccasionDialog
import com.example.ui.components.MesaharatyDialog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun RamadanAndEidScreen(
    soundManager: SoundManager,
    prayerTimes: List<PrayerTimeItem>
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Ramadan & Eids, 1: Islamic Occasions
    var selectedOccasionCategory by remember { mutableStateOf<OccasionCategory?>(null) }
    var activeOccasionForDialog by remember { mutableStateOf<IslamicOccasion?>(null) }

    var showMesaharatyDialog by remember { mutableStateOf(false) }
    var showCannonDialog by remember { mutableStateOf(false) }
    var showEidDialog by remember { mutableStateOf(false) }

    var taraweehSilentActive by remember { mutableStateOf(false) }
    var taraweehMinutes by remember { mutableStateOf(90) }

    val fajrPrayer = prayerTimes.find { it.type == PrayerType.FAJR }
    val maghribPrayer = prayerTimes.find { it.type == PrayerType.MAGHRIB }

    val currentTime = remember { System.currentTimeMillis() }
    val minutesToFajr = fajrPrayer?.let {
        if (it.timeMillis > currentTime) ((it.timeMillis - currentTime) / (1000 * 60)).toInt() else 45
    } ?: 45
    val minutesToMaghrib = maghribPrayer?.let {
        if (it.timeMillis > currentTime) ((it.timeMillis - currentTime) / (1000 * 60)).toInt() else 120
    } ?: 120

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top TabRow Switcher (رمضان والأعياد / المناسبات والمواسم الإسلامية)
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFF13221C),
            contentColor = IslamicGreenAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = IslamicGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        "أجواء رمضان والأعياد 🌙",
                        fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        color = if (selectedSubTab == 0) IslamicGold else Color.LightGray
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        "المناسبات الإسلامية 🗓️",
                        fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp,
                        color = if (selectedSubTab == 1) IslamicGold else Color.LightGray
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedSubTab == 0) {
                // ==========================================
                // SUB-TAB 0: RAMADAN & EID SPECIAL SUITE
                // ==========================================

                // Hero Ramadan Mubarak Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "رمضان كريم والأعياد المباركة 🌙",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "أجواء رمضانية متكاملة: المسحراتي بأصوات المنشدين، مدفع الإفطار، قرآن المغرب، وصامت التراويح.",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Section 1: Al-Mesaharaty (المسحراتي وتنبيه السحور)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFD97706))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("المسحراتي وتنبيه السحور 🥁", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("أصوات: سيد مكاوي ومنشدين", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Button(
                                    onClick = { showMesaharatyDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تشغيل وفتح")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2822)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("العداد التنازلي لموعد الإمساك والفجر:", fontSize = 12.sp, color = Color.LightGray)
                                    Text(
                                        "$minutesToFajr دقيقة",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFBBF24),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: Iftar Cannon & Pre-Maghrib Quran (مدفع الإفطار وقرآن المغرب)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEE2E2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFDC2626))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("مدفع الإفطار وقرآن المغرب 💥", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("صوت المدفع + دعاء الإفطار", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Button(
                                    onClick = { showCannonDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تجربة المدفع")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    soundManager.playText(
                                        "قرآن قبل المغرب",
                                        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ.. شَهْرُ رَمَضَانَ الَّذِي أُنْزِلَ فِيهِ الْقُرْآنُ هُدًى لِلنَّاسِ وَبَيِّنَاتٍ مِنَ الْهُدَىٰ وَالْفُرْقَانِ فَمَنْ شَهِدَ مِنْكُمُ الشَّهْرَ فَلْيَصُمْهُ"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استماع لقرآن ما قبل المغرب", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Section 3: Special Taraweeh Silent Mode (صامت خاص لصلاة التراويح)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0E7FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.VolumeOff, contentDescription = null, tint = Color(0xFF4338CA))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("صامت خاص لصلاة التراويح", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("كتم الهاتف تلقائياً أثناء صلاة القيام", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }

                                Switch(
                                    checked = taraweehSilentActive,
                                    onCheckedChange = { taraweehSilentActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = IslamicGreenPrimary)
                                )
                            }

                            if (taraweehSilentActive) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("مدة الوضع الصامت للتراويح:", fontSize = 12.sp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val durations = listOf(60, 90, 120)
                                    durations.forEach { d ->
                                        FilterChip(
                                            selected = taraweehMinutes == d,
                                            onClick = { taraweehMinutes = d },
                                            label = { Text("$d دقيقة") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Eid Takbeerat (تكبيرات العيد والتهليل)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(IslamicGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Celebration, contentDescription = null, tint = IslamicGoldDark)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("تكبيرات العيد والتهليل 🎉", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("أصوات الحرم المكي ومصر والمدينة", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Button(
                                onClick = { showEidDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("فتح التكبيرات", color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

            } else {
                // ==========================================
                // SUB-TAB 1: ISLAMIC OCCASIONS (المناسبات الإسلامية)
                // ==========================================

                // Banner: Islamic Calendar & Occasions
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF142921)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "المناسبات والمواسم الإسلامية 🗓️",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = IslamicGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تذكير تلقائي بالمناسبات الإسلامية العظيمة، وأيام الصيام المستحب (الأيام البيض، عاشوراء، عرفة، الإثنين والخميس)، وفضائل كل موسم.",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Filter Chips by Occasion Category
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedOccasionCategory == null,
                                onClick = { selectedOccasionCategory = null },
                                label = { Text("جميع المناسبات") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslamicGreenPrimary
                                )
                            )
                        }
                        OccasionCategory.values().forEach { category ->
                            item {
                                FilterChip(
                                    selected = selectedOccasionCategory == category,
                                    onClick = { selectedOccasionCategory = category },
                                    label = { Text(category.titleArabic) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IslamicGreenPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // Occasions List Items
                val filteredOccasions = IslamicRepository.islamicOccasions.filter {
                    selectedOccasionCategory == null || it.category == selectedOccasionCategory
                }

                items(filteredOccasions) { occasion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeOccasionForDialog = occasion },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F382E))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (occasion.category) {
                                        OccasionCategory.MAJOR_EID -> Icons.Default.Celebration
                                        OccasionCategory.SUNNAH_FASTING -> Icons.Default.WbTwilight
                                        OccasionCategory.BLESSED_SEASON -> Icons.Default.Stars
                                        OccasionCategory.BLESSED_NIGHT -> Icons.Default.NightsStay
                                        OccasionCategory.HISTORICAL_EVENT -> Icons.Default.AutoStories
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(IslamicGreenAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = occasion.nameArabic,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = occasion.hijriFormattedDate,
                                            fontSize = 11.sp,
                                            color = IslamicGold
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0xFF1B3B30),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "تفاصيل ودعاء",
                                        color = IslamicGreenAccent,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = occasion.summary,
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                maxLines = 2,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    occasion.recommendedDeeds.take(2).forEach { deed ->
                                        Surface(
                                            color = Color(0xFF14241E),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "✓ $deed",
                                                color = Color.LightGray,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        soundManager.playText(occasion.nameArabic, occasion.supplicationText)
                                    }
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("سماع الدعاء", fontSize = 11.sp, color = IslamicGold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Occasion Popup
    activeOccasionForDialog?.let { occasion ->
        IslamicOccasionDialog(
            occasion = occasion,
            soundManager = soundManager,
            onDismiss = {
                soundManager.stopAll()
                activeOccasionForDialog = null
            }
        )
    }

    if (showMesaharatyDialog) {
        MesaharatyDialog(
            soundManager = soundManager,
            minutesUntilFajr = minutesToFajr,
            onDismiss = { showMesaharatyDialog = false }
        )
    }

    if (showCannonDialog) {
        IftarCannonDialog(
            soundManager = soundManager,
            minutesUntilMaghrib = minutesToMaghrib,
            onDismiss = { showCannonDialog = false }
        )
    }

    if (showEidDialog) {
        EidTakbeeratDialog(
            soundManager = soundManager,
            onDismiss = { showEidDialog = false }
        )
    }
}
