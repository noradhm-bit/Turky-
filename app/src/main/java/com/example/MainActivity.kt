package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.IslamicRepository
import com.example.model.*
import com.example.service.alarm.AlarmScheduler
import com.example.service.audio.SoundManager
import com.example.service.prayer.PrayerCalculationEngine
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundManager = SoundManager(this)

        // Schedule exact lightweight prayer alarm on app start
        AlarmScheduler.scheduleNextPrayer(this)

        val incomingAdhanPrayer = intent?.getStringExtra("OPEN_ADHAN_PRAYER")
        val incomingTab = intent?.getIntExtra("OPEN_TAB", 0) ?: 0

        setContent {
            IslamicApp(
                soundManager = soundManager,
                initialAdhanPrayer = incomingAdhanPrayer,
                initialTab = incomingTab
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.stopAll()
    }
}

@Composable
fun IslamicApp(
    soundManager: SoundManager,
    initialAdhanPrayer: String?,
    initialTab: Int
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }
    var appSettings by remember { mutableStateOf(AppSettings()) }

    // Request notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Gracefully handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Live prayer times calculation
    val prayerTimes = remember(appSettings, Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
        val iqamaDelays = mapOf(
            PrayerType.FAJR to appSettings.iqamaDelayFajr,
            PrayerType.DHUHR to appSettings.iqamaDelayDhuhr,
            PrayerType.ASR to appSettings.iqamaDelayAsr,
            PrayerType.MAGHRIB to appSettings.iqamaDelayMaghrib,
            PrayerType.ISHA to appSettings.iqamaDelayIsha
        )
        PrayerCalculationEngine.calculatePrayerTimes(
            calendar = Calendar.getInstance(),
            latitude = appSettings.latitude,
            longitude = appSettings.longitude,
            method = appSettings.calculationMethod,
            isHanafi = appSettings.asrMethodHanafi,
            iqamaDelays = iqamaDelays
        )
    }

    // Dialog state controllers
    var activeAdhanPrayer by remember {
        mutableStateOf(
            if (initialAdhanPrayer != null) {
                try { PrayerType.valueOf(initialAdhanPrayer) } catch (e: Exception) { null }
            } else null
        )
    }

    var activeAthkarCategory by remember { mutableStateOf<AthkarCategory?>(null) }
    var activePrePrayerType by remember { mutableStateOf<PrayerType?>(null) }
    var activePostPrayerType by remember { mutableStateOf<PrayerType?>(null) }
    var showFridayDialog by remember { mutableStateOf(false) }
    var showFajrDialog by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var openQuranSurahNumber by remember { mutableStateOf<Int?>(null) }

    // BackHandler to handle custom state navigation smoothly
    BackHandler(
        enabled = selectedTab != 0 || activeAdhanPrayer != null || activeAthkarCategory != null ||
                activePrePrayerType != null || activePostPrayerType != null ||
                showFridayDialog || showFajrDialog || showLocationPicker
    ) {
        when {
            showLocationPicker -> showLocationPicker = false
            activePrePrayerType != null -> activePrePrayerType = null
            activePostPrayerType != null -> activePostPrayerType = null
            activeAdhanPrayer != null -> {
                soundManager.stopAll()
                activeAdhanPrayer = null
            }
            activeAthkarCategory != null -> {
                soundManager.stopAll()
                activeAthkarCategory = null
            }
            showFridayDialog -> showFridayDialog = false
            showFajrDialog -> {
                soundManager.stopAll()
                showFajrDialog = false
            }
            selectedTab != 0 -> selectedTab = 0
        }
    }

    MyApplicationTheme(
        darkTheme = true,
        themeColor = appSettings.appThemeColor
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val navItems = listOf(
                        Triple(0, "الرئيسية", Icons.Default.Home),
                        Triple(1, "الأذكار", Icons.Default.TouchApp),
                        Triple(2, "القرآن", Icons.Default.MenuBook),
                        Triple(3, "السيرة", Icons.Default.AutoStories),
                        Triple(4, "رمضان والمناسبات", Icons.Default.NightsStay),
                        Triple(5, "الإعدادات", Icons.Default.Settings)
                    )

                    navItems.forEach { (index, label, icon) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedTab = index
                                openQuranSurahNumber = null
                            },
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        appSettings = appSettings,
                        prayerTimes = prayerTimes,
                        soundManager = soundManager,
                        onOpenLocationPicker = { showLocationPicker = true },
                        onOpenPrayerDetails = { prayerType -> activeAdhanPrayer = prayerType },
                        onOpenAthkarCategory = { category -> activeAthkarCategory = category },
                        onOpenRamadanHub = { selectedTab = 4 },
                        onOpenFridayDialog = { showFridayDialog = true },
                        onOpenFajrDialog = { showFajrDialog = true },
                        onOpenQuranSurah = { surahNum ->
                            openQuranSurahNumber = surahNum
                            selectedTab = 2
                        }
                    )
                    1 -> AthkarScreen(
                        soundManager = soundManager,
                        onOpenVisualPopup = { category -> activeAthkarCategory = category }
                    )
                    2 -> QuranScreen(
                        soundManager = soundManager,
                        initialSurahNumber = openQuranSurahNumber
                    )
                    3 -> HadithAndSeerahScreen(soundManager = soundManager)
                    4 -> RamadanAndEidScreen(
                        soundManager = soundManager,
                        prayerTimes = prayerTimes
                    )
                    5 -> SettingsScreen(
                        currentSettings = appSettings,
                        onSettingsChanged = { newSettings ->
                            appSettings = newSettings
                            AlarmScheduler.scheduleNextPrayer(context, newSettings)
                        },
                        soundManager = soundManager,
                        onOpenLocationPicker = { showLocationPicker = true },
                        onTestAdhanPopup = { prayerType -> activeAdhanPrayer = prayerType },
                        onTestAthkarPopup = { activeAthkarCategory = AthkarCategory.MORNING },
                        onTestPrePrayerReminder = { activePrePrayerType = PrayerType.ASR },
                        onTestPostPrayerAthkar = { activePostPrayerType = PrayerType.DHUHR },
                        onTestFridayDialog = { showFridayDialog = true },
                        onTestFajrDialog = { showFajrDialog = true }
                    )
                }

                // POPUPS SYSTEM
                // 1. Adhan Popup with animated mosque visual
                activeAdhanPrayer?.let { prayerType ->
                    AdhanPopupDialog(
                        prayerType = prayerType,
                        muezzin = MuezzinSound.ALI_MULLA,
                        silentModeActive = appSettings.autoSilentEnabled,
                        silentMinutesRemaining = appSettings.autoSilentDurationMinutes,
                        soundManager = soundManager,
                        onDismiss = {
                            soundManager.stopAll()
                            activeAdhanPrayer = null
                        }
                    )
                }

                // 2. Nature Athkar Popup with animated sunrise/sunset/starfield
                activeAthkarCategory?.let { category ->
                    val list = when (category) {
                        AthkarCategory.MORNING -> IslamicRepository.morningAthkar
                        AthkarCategory.EVENING -> IslamicRepository.eveningAthkar
                        AthkarCategory.SLEEP -> IslamicRepository.sleepAthkar
                        AthkarCategory.POST_PRAYER -> IslamicRepository.postPrayerAthkar
                        else -> IslamicRepository.morningAthkar
                    }
                    AthkarPopupDialog(
                        category = category,
                        dhikrList = list,
                        soundManager = soundManager,
                        onDismiss = {
                            soundManager.stopAll()
                            activeAthkarCategory = null
                        }
                    )
                }

                // 3. Pre-Prayer Approaching Reminder Dialog ("اقتربت الصلاة وسؤال هل صليت الفائتة؟")
                activePrePrayerType?.let { prayerType ->
                    val previous = when (prayerType) {
                        PrayerType.FAJR -> PrayerType.ISHA
                        PrayerType.DHUHR -> PrayerType.FAJR
                        PrayerType.ASR -> PrayerType.DHUHR
                        PrayerType.MAGHRIB -> PrayerType.ASR
                        PrayerType.ISHA -> PrayerType.MAGHRIB
                        PrayerType.SUNRISE -> PrayerType.FAJR
                    }
                    PrePrayerReminderDialog(
                        upcomingPrayer = prayerType,
                        previousPrayer = previous,
                        minutesRemaining = appSettings.prePrayerMinutesBefore,
                        soundManager = soundManager,
                        onAnswerPreviousPrayer = { _ -> },
                        onDismiss = { activePrePrayerType = null }
                    )
                }

                // 4. Dedicated Post-Prayer Athkar Popup Dialog
                activePostPrayerType?.let { prayerType ->
                    PostPrayerAthkarDialog(
                        prayerType = prayerType,
                        soundManager = soundManager,
                        onDismiss = { activePostPrayerType = null }
                    )
                }

                // 5. Friday Special Dialog
                if (showFridayDialog) {
                    FridaySpecialDialog(
                        soundManager = soundManager,
                        onOpenSurahKahf = {
                            showFridayDialog = false
                            openQuranSurahNumber = 18 // Surah Al-Kahf
                            selectedTab = 2
                        },
                        onDismiss = { showFridayDialog = false }
                    )
                }

                // 6. Fajr Special Dialog
                if (showFajrDialog) {
                    val fajr = prayerTimes.find { it.type == PrayerType.FAJR }
                    val minutesToFajr = fajr?.let {
                        if (it.timeMillis > System.currentTimeMillis()) ((it.timeMillis - System.currentTimeMillis()) / (1000 * 60)).toInt() else 30
                    } ?: 30
                    FajrSpecialDialog(
                        soundManager = soundManager,
                        minutesUntilFajr = minutesToFajr,
                        onDismiss = {
                            soundManager.stopAll()
                            showFajrDialog = false
                        }
                    )
                }

                // 7. Location Picker Modal (GPS & Global World Cities)
                if (showLocationPicker) {
                    LocationPickerModal(
                        currentSettings = appSettings,
                        onLocationSelected = { city, country, lat, lon, method ->
                            val updated = appSettings.copy(
                                cityName = city,
                                countryName = country,
                                latitude = lat,
                                longitude = lon,
                                calculationMethod = method
                            )
                            appSettings = updated
                            AlarmScheduler.scheduleNextPrayer(context, updated)
                            showLocationPicker = false
                        },
                        onDismiss = { showLocationPicker = false }
                    )
                }
            }
        }
    }
}
