package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.model.*
import com.example.service.audio.SoundManager
import com.example.service.icon.AppIconManager
import com.example.service.icon.AppIconTheme
import com.example.ui.components.GlassCard
import com.example.ui.theme.AppThemeColor
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

enum class SettingsSection(val titleArabic: String, val icon: ImageVector, val accentColor: Color) {
    ALL("الكل", Icons.Default.AllInclusive, IslamicGold),
    APPEARANCE("الألوان والأيقونة", Icons.Default.Palette, Color(0xFFEC4899)),
    PRAYER_AND_ADHAN("الصلاة والأذان", Icons.Default.AccessTime, Color(0xFF10B981)),
    ATHKAR_AND_SALAWAT("الأذكار والتسبيح", Icons.Default.TouchApp, Color(0xFF06B6D4)),
    FRIDAY("الجمعة المباركة", Icons.Default.Mosque, IslamicGold),
    FAJR("صلاة الفجر والابتهالات", Icons.Default.WbTwilight, Color(0xFF6366F1)),
    RAMADAN("شهر رمضان", Icons.Default.NightsStay, Color(0xFFF59E0B)),
    EID_AND_OCCASIONS("الأعياد والمناسبات", Icons.Default.Celebration, Color(0xFFF43F5E)),
    QURAN("القرآن الكريم", Icons.Default.MenuBook, Color(0xFF14B8A6)),
    HADITH_AND_SEERAH("الأحاديث والسيرة", Icons.Default.AutoStories, Color(0xFF8B5CF6))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    soundManager: SoundManager,
    onOpenLocationPicker: () -> Unit = {},
    onTestAdhanPopup: (PrayerType) -> Unit = {},
    onTestAthkarPopup: () -> Unit = {},
    onTestPrePrayerReminder: () -> Unit = {},
    onTestPostPrayerAthkar: () -> Unit = {},
    onTestFridayDialog: () -> Unit = {},
    onTestFajrDialog: () -> Unit = {}
) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(currentSettings) }
    var selectedSection by remember { mutableStateOf(SettingsSection.ALL) }
    var selectedPrayerForConfig by remember { mutableStateOf(PrayerType.FAJR) }
    var iconChangeFeedback by remember { mutableStateOf<String?>(null) }

    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
        soundManager.overrideSilentMode = newSettings.overrideSilentMode
        onSettingsChanged(newSettings)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Apple/Samsung Style Header
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 24.dp,
                tintColor = Color(0xFF132B22)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إعدادات التطبيق والتخصيص ⚙️",
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "مقسمة حسب المواضيع بأسلوب أنيق ومنظم مع تحكم كامل في كل تفصيلة.",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(IslamicGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = IslamicGold)
                        }
                    }
                }
            }
        }

        // Section Selector Chips (Apple/Samsung Categorized View)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SettingsSection.values()) { section ->
                    val isSelected = selectedSection == section
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSection = section },
                        leadingIcon = {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else section.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = section.titleArabic,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = section.accentColor,
                            selectedLabelColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // =========================================================================
        // SECTION 0: APPEARANCE, THEME COLORS & APP ICON (مظهر وألوان وأيقونة التطبيق)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.APPEARANCE) {
            item {
                SectionHeaderCard(
                    title = "مظهر وألوان وأيقونة التطبيق 🎨",
                    subtitle = "تخصيص ثيم الألوان العصري وتغيير أيقونة التطبيق لشاشة الهاتف",
                    icon = Icons.Default.Palette,
                    accentColor = Color(0xFFEC4899)
                )
            }

            // Theme Color Palettes Studio
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 20.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = Icons.Default.ColorLens, bg = Color(0xFFBE185D))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("اختر لوحة ألوان التطبيق العصرية 🌈", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text("تغيير كامل لألوان التطبيق (الزمرد، اللازوردي، الذهب، البنفسجي، الفيروزي، الأسود)", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid of 7 Luxury Palettes
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppThemeColor.values().forEach { palette ->
                                val isSelected = settings.appThemeColor == palette
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            updateSettings(settings.copy(appThemeColor = palette))
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E2E28) else Color(0xFF131E19)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) palette.accentColor else Color(0xFF24362E)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Palette Color Samples Preview Dots
                                            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(palette.primaryColor)
                                                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(palette.accentColor)
                                                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(palette.secondaryColor)
                                                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = palette.titleArabic,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (isSelected) palette.accentColor else Color.White
                                                )
                                                Text(
                                                    text = palette.description,
                                                    fontSize = 11.sp,
                                                    color = Color.LightGray
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Surface(
                                                color = palette.accentColor.copy(alpha = 0.2f),
                                                shape = CircleShape
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = palette.accentColor,
                                                    modifier = Modifier.padding(6.dp).size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // App Launcher Icon Studio
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 20.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = Icons.Default.Apps, bg = Color(0xFF7C3AED))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("أيقونة التطبيق لشاشة الهاتف (App Icon) 📱", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                Text("اختر شكل ولون أيقونة التطبيق التي تظهر على شاشة جوالك الرئيسية", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        if (iconChangeFeedback != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = IslamicGreenAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGreenAccent.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IslamicGreenAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = iconChangeFeedback ?: "",
                                        color = IslamicGreenAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppIconTheme.values().forEach { iconTheme ->
                                val isSelected = settings.appIconTheme == iconTheme
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            AppIconManager.applyIcon(context, iconTheme)
                                            updateSettings(settings.copy(appIconTheme = iconTheme))
                                            iconChangeFeedback = "تم تفعيل أيقونة «${iconTheme.titleArabic}» لشاشة هاتفك بنجاح ✓"
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E2835) else Color(0xFF131B24)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) IslamicGold else Color(0xFF23303D)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Simulated Adaptive App Icon Preview
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(iconTheme.hexColor))
                                                    .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Mosque,
                                                    contentDescription = null,
                                                    tint = IslamicGold,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = iconTheme.titleArabic,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (isSelected) IslamicGold else Color.White
                                                )
                                                Text(
                                                    text = iconTheme.subtitleArabic,
                                                    fontSize = 11.sp,
                                                    color = Color.LightGray
                                                )
                                            }
                                        }

                                        if (isSelected) {
                                            Surface(
                                                color = IslamicGold.copy(alpha = 0.2f),
                                                shape = CircleShape
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = IslamicGold,
                                                    modifier = Modifier.padding(6.dp).size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        // =========================================================================
        // SECTION 1: PRAYER, ADHAN, SILENT & LOCATION (قسم الصلاة والآذان والصامت)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.PRAYER_AND_ADHAN) {
            item {
                SectionHeaderCard(
                    title = "الصلاة والأذان والصامت والإقامة",
                    subtitle = "تحديد الموقع، تجاوز الصامت، أوقات الصامت، والإقامة والمؤذنين",
                    icon = Icons.Default.AccessTime,
                    accentColor = Color(0xFF10B981)
                )
            }

            // Location & GPS Row
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp,
                    onClick = onOpenLocationPicker
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(icon = Icons.Default.Place, bg = Color(0xFF047857))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("الموقع الجغرافي والمدينة 🗺️", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                Text("${settings.cityName} - ${settings.countryName}", fontSize = 12.sp, color = IslamicGold)
                            }
                        }
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }

            // Override Silent Mode Toggle (Alarm Stream)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconBadge(icon = Icons.Default.AlarmOn, bg = Color(0xFFD97706))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("تجاوز الوضع الصامت للجهاز (تشغيل كمنبه) 🔔", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("يرن الأذان والمنبهات حتى لو كان الهاتف صامتاً تماماً عبر قناة المنبه", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = settings.overrideSilentMode,
                                onCheckedChange = { updateSettings(settings.copy(overrideSilentMode = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IslamicGold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { soundManager.playPrePrayerChime("تجربة المنبه", 0) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختبار صوت المنبه لتجاوز الوضع الصامت", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Auto-Silent Mode Timing
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.VolumeOff, bg = Color(0xFF3B82F6))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("الوضع الصامت التلقائي للصلاة بالمسجد 📴", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("كتم رنين الهاتف بالكامل أثناء الصلاة لعدم الإزعاج", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = settings.autoSilentEnabled,
                                onCheckedChange = { updateSettings(settings.copy(autoSilentEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IslamicGreenAccent)
                            )
                        }

                        if (settings.autoSilentEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("١. يبدأ الصامت بعد الأذان بـ: (${settings.autoSilentStartDelayMinutes} دقيقة)", fontSize = 12.sp, color = IslamicGreenAccent, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(0 to "فوراً مع الأذان", 5 to "بعد 5 د (مع الإقامة)", 10 to "بعد 10 د", 15 to "بعد 15 د").forEach { (m, l) ->
                                    FilterChip(
                                        selected = settings.autoSilentStartDelayMinutes == m,
                                        onClick = { updateSettings(settings.copy(autoSilentStartDelayMinutes = m)) },
                                        label = { Text(l, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("٢. يستمر الصامت لمدة: (${settings.autoSilentDurationMinutes} دقيقة)", fontSize = 12.sp, color = IslamicGold, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(15, 20, 25, 30, 45, 60).forEach { d ->
                                    FilterChip(
                                        selected = settings.autoSilentDurationMinutes == d,
                                        onClick = { updateSettings(settings.copy(autoSilentDurationMinutes = d)) },
                                        label = { Text("$d د") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Iqama per Prayer
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.HourglassBottom, bg = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("تنبيه إقامة الصلاة لكل فريضة ⏱️", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("نداء «قد قامت الصلاة» وعداد تنازلي للركوع", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = settings.iqamaEnabled,
                                onCheckedChange = { updateSettings(settings.copy(iqamaEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IslamicGold)
                            )
                        }

                        if (settings.iqamaEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            val prayerIqama = listOf(
                                Triple("الفجر", settings.iqamaDelayFajr) { m: Int -> updateSettings(settings.copy(iqamaDelayFajr = m)) },
                                Triple("الظهر", settings.iqamaDelayDhuhr) { m: Int -> updateSettings(settings.copy(iqamaDelayDhuhr = m)) },
                                Triple("العصر", settings.iqamaDelayAsr) { m: Int -> updateSettings(settings.copy(iqamaDelayAsr = m)) },
                                Triple("المغرب", settings.iqamaDelayMaghrib) { m: Int -> updateSettings(settings.copy(iqamaDelayMaghrib = m)) },
                                Triple("العشاء", settings.iqamaDelayIsha) { m: Int -> updateSettings(settings.copy(iqamaDelayIsha = m)) }
                            )

                            prayerIqama.forEach { (name, delayMins, onUpdate) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("صلاة $name", fontSize = 13.sp, color = Color.White)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { if (delayMins > 5) onUpdate(delayMins - 5) }, modifier = Modifier.size(26.dp)) {
                                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.LightGray)
                                        }
                                        Text("$delayMins دقيقة", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                        IconButton(onClick = { if (delayMins < 60) onUpdate(delayMins + 5) }, modifier = Modifier.size(26.dp)) {
                                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.LightGray)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { soundManager.playIqama(selectedPrayerForConfig.arabicName) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استماع لتنبيه الإقامة («قد قامت الصلاة»)")
                            }
                        }
                    }
                }
            }

            // Per-Prayer Muezzin Customization & Mosque Popups
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("أصوات الأذان المصرية والحرم المكي والنوافذ المنبثقة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IslamicGreenAccent)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(PrayerType.values()) { prayer ->
                                FilterChip(
                                    selected = selectedPrayerForConfig == prayer,
                                    onClick = { selectedPrayerForConfig = prayer },
                                    label = { Text(prayer.arabicName, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        MuezzinSound.values().take(4).forEach { muezzin ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(muezzin.titleArabic, fontSize = 12.sp, color = Color.LightGray, modifier = Modifier.weight(1f))
                                IconButton(onClick = { soundManager.playAdhan(selectedPrayerForConfig.arabicName, muezzin) }) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onTestAdhanPopup(selectedPrayerForConfig) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختبار شاشة الأذان المنبثقة بصور المساجد")
                        }
                    }
                }
            }

            // Pre-prayer Check & Post-prayer Athkar Popups
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("نوافذ اقتربت الصلاة وأذكار ما بعد الصلاة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IslamicGold)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("شاشة «اقتربت الصلاة» وسؤال هل صليت الفائتة؟", fontSize = 12.sp, color = Color.White)
                            Switch(
                                checked = settings.prePrayerCheckEnabled,
                                onCheckedChange = { updateSettings(settings.copy(prePrayerCheckEnabled = it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onTestPrePrayerReminder,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("اختبار شاشة اقتربت الصلاة وسؤال هل صليت الفائتة؟", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("النافذة المنبثقة لأذكار ما بعد الصلاة", fontSize = 12.sp, color = Color.White)
                            Switch(
                                checked = settings.postPrayerPopupEnabled,
                                onCheckedChange = { updateSettings(settings.copy(postPrayerPopupEnabled = it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onTestPostPrayerAthkar,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("اختبار نافذة أذكار ما بعد الصلاة المنبثقة", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 2: ATHKAR, SALAWAT & SEBHA (قسم الأذكار والصلاة على النبي والاستغفار)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.ATHKAR_AND_SALAWAT) {
            item {
                SectionHeaderCard(
                    title = "الأذكار والصلاة على النبي والاستغفار",
                    subtitle = "تخصيص أيام وأوقات الصلاة على النبي، الاستغفار المنفصل، والمسبحة",
                    icon = Icons.Default.TouchApp,
                    accentColor = Color(0xFF06B6D4)
                )
            }

            // Salawat on Prophet ﷺ (Full Freedom of Days & Times)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.Favorite, bg = Color(0xFF0284C7))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("منبه الصلاة على النبي ﷺ 🤍", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("حرية تامة في اختيار الأيام والأوقات والتردد", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = settings.salawatEnabled,
                                onCheckedChange = { updateSettings(settings.copy(salawatEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        if (settings.salawatEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("اختر أيام التذكير بالصلاة على النبي:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            val days = listOf(
                                1 to "الأحد", 2 to "الإثنين", 3 to "الثلاثاء", 4 to "الأربعاء",
                                5 to "الخميس", 6 to "الجمعة", 7 to "السبت"
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(days) { (dayNum, dayName) ->
                                    val isSelected = settings.salawatSelectedDays.contains(dayNum)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            val newSet = if (isSelected) settings.salawatSelectedDays - dayNum else settings.salawatSelectedDays + dayNum
                                            updateSettings(settings.copy(salawatSelectedDays = newSet))
                                        },
                                        label = { Text(dayName, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("تردد التنبيه: كل ${settings.salawatIntervalMinutes} دقيقة", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(15, 30, 45, 60, 120).forEach { mins ->
                                    FilterChip(
                                        selected = settings.salawatIntervalMinutes == mins,
                                        onClick = { updateSettings(settings.copy(salawatIntervalMinutes = mins)) },
                                        label = { Text("$mins د") }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { soundManager.playSalawat() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استماع لصوت التذكير بالصلاة على النبي ﷺ")
                            }
                        }
                    }
                }
            }

            // Separate Istighfar Reminder
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBadge(icon = Icons.Default.FilterVintage, bg = Color(0xFF059669))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("منبه الاستغفار والتوبة 🌿", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("إعداد منفصل تماماً مع هدف يومي وصوت مخصص", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = settings.istighfarEnabled,
                                onCheckedChange = { updateSettings(settings.copy(istighfarEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IslamicGreenAccent)
                            )
                        }

                        if (settings.istighfarEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("الهدف اليومي للاستغفار: ${settings.istighfarDailyTarget} استغفار", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(100, 300, 500, 1000).forEach { target ->
                                    FilterChip(
                                        selected = settings.istighfarDailyTarget == target,
                                        onClick = { updateSettings(settings.copy(istighfarDailyTarget = target)) },
                                        label = { Text("$target") }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { soundManager.playIstighfar() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استماع لصوت تذكير الاستغفار")
                            }
                        }
                    }
                }
            }

            // Athkar Nature Popups Test
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("النوافذ المنبثقة للأذكار بصور الطبيعة الخلابة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IslamicGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("شروق الصباح، بحيرة الغروب، وسماء الليل المرصعة بالنجوم مع الأذكار والصوت.", fontSize = 11.sp, color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onTestAthkarPopup,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = IslamicGreenDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختبار شاشة أذكار الصباح بصور الطبيعة", color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 3: FRIDAY SPECIALS (قسم يوم الجمعة المبارك)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.FRIDAY) {
            item {
                SectionHeaderCard(
                    title = "يوم الجمعة المبارك",
                    subtitle = "الأذان الأول والثاني، منبه سورة الكهف، وساعة الإجابة",
                    icon = Icons.Default.Mosque,
                    accentColor = IslamicGold
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الأذان الأول للجمعة (تنبيه السعي للمسجد)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("تنبيه مبكر قبل صلاة الجمعة للاغتسال والتبكير", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.fridayFirstAdhanEnabled,
                                onCheckedChange = { updateSettings(settings.copy(fridayFirstAdhanEnabled = it)) }
                            )
                        }

                        if (settings.fridayFirstAdhanEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("موعد الأذان الأول قبل الجمعة بـ: (${settings.fridayFirstAdhanMinutesBefore} دقيقة)", fontSize = 12.sp, color = IslamicGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(30, 45, 60).forEach { m ->
                                    FilterChip(
                                        selected = settings.fridayFirstAdhanMinutesBefore == m,
                                        onClick = { updateSettings(settings.copy(fridayFirstAdhanMinutesBefore = m)) },
                                        label = { Text("$m دقيقة") }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تذكير قراءة سورة الكهف وساعة الإجابة 📖", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("تنبيه صباح الجمعة مع فتح السورة مباشرة", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.fridaySurahKahfReminder,
                                onCheckedChange = { updateSettings(settings.copy(fridaySurahKahfReminder = it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTestFridayDialog,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = IslamicGreenDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح شاشة الجمعة المباركة والكهف", color = IslamicGreenDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 4: FAJR & IBTIHALAT (قسم صلاة الفجر والابتهالات ومنبه الفجر)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.FAJR) {
            item {
                SectionHeaderCard(
                    title = "صلاة الفجر والابتهالات ومنبه الفجر",
                    subtitle = "الاستيقاظ التنازلي، وابتهالات طوبار والنقشبندي وعمران",
                    icon = Icons.Default.WbTwilight,
                    accentColor = Color(0xFF6366F1)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("منبه الاستيقاظ للفجر بعداد تنازلي ⏰", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("تنبيه تدريجي هادئ قبل أذان الفجر", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.fajrWakeupCountdownEnabled,
                                onCheckedChange = { updateSettings(settings.copy(fajrWakeupCountdownEnabled = it)) }
                            )
                        }

                        if (settings.fajrWakeupCountdownEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("موعد المنبه قبل الفجر بـ: (${settings.fajrWakeupMinutesBefore} دقيقة)", fontSize = 12.sp, color = Color(0xFFA5B4FC))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(15, 30, 45, 60).forEach { m ->
                                    FilterChip(
                                        selected = settings.fajrWakeupMinutesBefore == m,
                                        onClick = { updateSettings(settings.copy(fajrWakeupMinutesBefore = m)) },
                                        label = { Text("$m دقيقة") }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ابتهالات الفجر قبل الأذان 🎙️", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("الشيخ نصر الدين طوبار وسيد النقشبندي ومحمد عمران", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.fajrIbtihalatEnabled,
                                onCheckedChange = { updateSettings(settings.copy(fajrIbtihalatEnabled = it)) }
                            )
                        }

                        if (settings.fajrIbtihalatEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val ibtihalatVoices = listOf("الشيخ نصر الدين طوبار", "الشيخ سيد النقشبندي", "الشيخ محمد عمران")
                            ibtihalatVoices.forEach { voice ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(voice, fontSize = 12.sp, color = Color.LightGray)
                                    RadioButton(
                                        selected = settings.fajrIbtihalatVoice == voice,
                                        onClick = { updateSettings(settings.copy(fajrIbtihalatVoice = voice)) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onTestFajrDialog,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح شاشة الفجر والاستماع للابتهالات")
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 5: RAMADAN SUITE (قسم شهر رمضان المبارك)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.RAMADAN) {
            item {
                SectionHeaderCard(
                    title = "شهر رمضان المبارك",
                    subtitle = "المسحراتي وسيد مكاوي، مدفع الإفطار، وصامت التراويح",
                    icon = Icons.Default.NightsStay,
                    accentColor = Color(0xFFF59E0B)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تنبيه المسحراتي بنغمات الطبلة والسيد مكاوي 🥁", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("إيقاظ للسحور بعداد تنازلي لموعد الإمساك", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.ramadanMesaharatyEnabled,
                                onCheckedChange = { updateSettings(settings.copy(ramadanMesaharatyEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("مدفع الإفطار وقرآن المغرب 💥", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("صوت المدفع الشهير وتلاوة آيات الصيام ودعاء الإفطار", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.ramadanIftarCannonEnabled,
                                onCheckedChange = { updateSettings(settings.copy(ramadanIftarCannonEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEF4444))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("صامت خاص لصلاة التراويح والتهجد 📴", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("كتم الهاتف تلقائياً لمدة صلاة القيام بالمسجد", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text("${settings.ramadanTaraweehSilentMinutes} د", color = IslamicGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 6: EIDS & ISLAMIC OCCASIONS (قسم الأعياد والمناسبات الإسلامية)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.EID_AND_OCCASIONS) {
            item {
                SectionHeaderCard(
                    title = "الأعياد والمناسبات الإسلامية",
                    subtitle = "تكبيرات العيد، تلبية الحج، وتذكير صيام النوافل والمواسم",
                    icon = Icons.Default.Celebration,
                    accentColor = Color(0xFFEC4899)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("تكبيرات وتلبية الأعياد المباركة 🎉", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IslamicGold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تكبيرات عيد الفطر وعيد الأضحى", fontSize = 12.sp, color = Color.White)
                            Button(
                                onClick = { soundManager.playEidTakbeerat() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                            ) {
                                Text("استماع للتكبيرات", color = IslamicGreenDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("تلبية الحج («لبيك اللهم لبيك»)", fontSize = 12.sp, color = Color.White)
                            Button(
                                onClick = { soundManager.playTalbiyah() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Text("استماع للتلبية", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("تنبيهات صيام النوافل والمناسبات الإسلامية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))

                        val occasionToggles = listOf(
                            Triple("صيام الأيام البيض (13 و 14 و 15 من كل شهر)", settings.fastingWhiteDaysReminder) { b: Boolean ->
                                updateSettings(settings.copy(fastingWhiteDaysReminder = b))
                            },
                            Triple("صيام الإثنين والخميس أسبوعياً", settings.fastingMondayThursdayReminder) { b: Boolean ->
                                updateSettings(settings.copy(fastingMondayThursdayReminder = b))
                            },
                            Triple("عاشوراء وتاسوعاء ويوم عرفة", settings.fastingAshuraArafahReminder) { b: Boolean ->
                                updateSettings(settings.copy(fastingAshuraArafahReminder = b))
                            },
                            Triple("أول 3 أيام من رجب وشعبان ونصف شعبان", settings.fastingRajabShaabanReminder) { b: Boolean ->
                                updateSettings(settings.copy(fastingRajabShaabanReminder = b))
                            }
                        )

                        occasionToggles.forEach { (label, isChecked, onToggle) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, fontSize = 12.sp, color = Color.LightGray)
                                Switch(
                                    checked = isChecked,
                                    onCheckedChange = onToggle
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 7: HOLY QURAN (قسم القرآن الكريم وتلاواته وخطوطه)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.QURAN) {
            item {
                SectionHeaderCard(
                    title = "القرآن الكريم وتلاواته وخطوطه",
                    subtitle = "أحجام الخطوط، نوع الخط، والقراء المصريين العظماء",
                    icon = Icons.Default.MenuBook,
                    accentColor = Color(0xFF14B8A6)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("حجم خط المصحف الشريف: (${settings.quranFontSize} نقطة)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("أصغر", fontSize = 11.sp, color = Color.Gray)
                            Slider(
                                value = settings.quranFontSize.toFloat(),
                                onValueChange = { updateSettings(settings.copy(quranFontSize = it.toInt())) },
                                valueRange = 16f..36f,
                                steps = 10,
                                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = IslamicGold,
                                    activeTrackColor = IslamicGreenAccent
                                )
                            )
                            Text("أكبر", fontSize = 11.sp, color = Color.Gray)
                        }

                        // Sample Preview Text
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14241E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝",
                                fontSize = settings.quranFontSize.sp,
                                color = IslamicGold,
                                modifier = Modifier.padding(12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("القارئ المفضل لتلاوة السور والأذكار:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))

                        QuranReciter.values().forEach { reciter ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(reciter.nameArabic, fontSize = 12.sp, color = Color.LightGray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        soundManager.playText(
                                            title = reciter.nameArabic,
                                            content = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ.. الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ الرَّحْمَٰنِ الرَّحِيمِ مَالِكِ يَوْمِ الدِّينِ"
                                        )
                                    }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IslamicGreenAccent)
                                    }
                                    RadioButton(
                                        selected = settings.quranDefaultReciter == reciter,
                                        onClick = { updateSettings(settings.copy(quranDefaultReciter = reciter)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 8: HADITHS & SEERAH (قسم كتب الأحاديث والسيرة النبوية)
        // =========================================================================
        if (selectedSection == SettingsSection.ALL || selectedSection == SettingsSection.HADITH_AND_SEERAH) {
            item {
                SectionHeaderCard(
                    title = "كتب الأحاديث والسيرة النبوية",
                    subtitle = "حجم الخطوط، التذكير اليومي بالحديث الشريف ومجالس السيرة",
                    icon = Icons.Default.AutoStories,
                    accentColor = Color(0xFF8B5CF6)
                )
            }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("حجم خط الأحاديث النبوية والشروح: (${settings.hadithFontSize} نقطة)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("A-", fontSize = 12.sp, color = Color.Gray)
                            Slider(
                                value = settings.hadithFontSize.toFloat(),
                                onValueChange = { updateSettings(settings.copy(hadithFontSize = it.toInt())) },
                                valueRange = 13f..24f,
                                steps = 11,
                                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFA78BFA),
                                    activeTrackColor = Color(0xFF8B5CF6)
                                )
                            )
                            Text("A+", fontSize = 14.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("إشعار بحديث نبوي شريف يومياً 📜", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("من الأربعين النووية ورياض الصالحين", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.hadithDailyReminderEnabled,
                                onCheckedChange = { updateSettings(settings.copy(hadithDailyReminderEnabled = it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تذكير بقراءة السيرة النبوية العطرة 🕊️", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("لتعزيز الأجواء الروحانية ومعرفة حياة المصطفى ﷺ", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = settings.seerahDailyReminderEnabled,
                                onCheckedChange = { updateSettings(settings.copy(seerahDailyReminderEnabled = it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector, bg: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}
