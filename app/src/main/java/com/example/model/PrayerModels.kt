package com.example.model

import com.example.service.icon.AppIconTheme
import com.example.ui.theme.AppThemeColor

enum class PrayerType(val arabicName: String, val englishName: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha")
}

enum class CalculationMethod(val titleArabic: String, val description: String) {
    UMM_AL_QURA("أم القرى - مكة المكرمة", "تقويم مكة المكرمة المعتمد"),
    EGYPTIAN("الهيئة المصرية العامة للمساحة", "مصر وإفريقيا والدول العربية"),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي", "أوروبا وأمريكا وأغلب الدول"),
    ISNA("الجمعية الإسلامية لأمريكا الشمالية", "ISNA - أمريكا وكندا"),
    KARACHI("جامعة العلوم الإسلامية بكراتشي", "شبه القارة الهندية وحنفي")
}

enum class MuezzinSound(val titleArabic: String, val tonePitch: Float) {
    MOHAMED_REFAAT("الشيخ محمد رفعت (صوت القاهرة وأذان الإذاعة المصرية)", 0.90f),
    ABDUL_BASIT("الشيخ عبد الباسط عبد الصمد (أذان مسجد الإمام الحسين)", 0.96f),
    MUSTAFA_ISMAIL("الشيخ مصطفى إسماعيل (أذان الجامع الأزهر الشريف)", 1.02f),
    MAHMOUD_AL_HUSSARY("الشيخ محمود خليل الحصري (أذان الديار المصرية)", 0.94f),
    NASREDDIN_TOBAR("الشيخ نصر الدين طوبار (أذان الفجر والابتهالات)", 0.98f),
    ALI_MULLA("الشيخ علي ملا (أذان الحرم المكي الشريف)", 1.0f),
    ESSAM_BUKHARI("الشيخ عصام بخاري (أذان المسجد النبوي الشريف)", 1.05f),
    ALAFASY("الشيخ مشاري راشد العفاسي", 1.08f)
}

enum class QuranReciter(val nameArabic: String, val speechRate: Float, val pitch: Float) {
    ABDUL_BASIT("الشيخ عبد الباسط عبد الصمد", 0.78f, 0.96f),
    MINSHAWI("الشيخ محمد صديق المنشاوي", 0.80f, 0.98f),
    AL_HUSSARY("الشيخ محمود خليل الحصري", 0.82f, 0.95f),
    MUSTAFA_ISMAIL("الشيخ مصطفى إسماعيل", 0.80f, 1.02f),
    ALAFASY("الشيخ مشاري راشد العفاسي", 0.85f, 1.08f)
}

enum class AlertMode(val titleArabic: String) {
    FULL_ADHAN("أذان كامل"),
    TAKBEER_ONLY("تكبيرتان فقط"),
    CHIME("نغمة تنبيه هادئة"),
    VIBRATE_ONLY("اهتزاز فقط"),
    SILENT("صامت")
}

data class SinglePrayerSetting(
    val prayerType: PrayerType,
    val alertMode: AlertMode = AlertMode.FULL_ADHAN,
    val muezzin: MuezzinSound = MuezzinSound.ALI_MULLA,
    val preAlertEnabled: Boolean = true,
    val preAlertMinutes: Int = 15,
    val postAthkarReminderEnabled: Boolean = true,
    val postAthkarDelayMinutes: Int = 10,
    val manualOffsetMinutes: Int = 0
)

data class PrayerTimeItem(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val iqamaFormattedTime: String = "",
    val isNext: Boolean = false,
    val isPast: Boolean = false,
    val remainingMillis: Long = 0L
)

data class WorshipDailyLog(
    val completedFard: Set<PrayerType> = setOf(PrayerType.FAJR, PrayerType.DHUHR),
    val sunnahDone: Boolean = true,
    val witrDone: Boolean = false,
    val morningAthkarDone: Boolean = true,
    val eveningAthkarDone: Boolean = false,
    val sleepAthkarDone: Boolean = false,
    val salawatCompletedCount: Int = 100,
    val istighfarCompletedCount: Int = 100,
    val isFastingToday: Boolean = false
)

data class AppSettings(
    val cityName: String = "مكة المكرمة",
    val countryName: String = "المملكة العربية السعودية",
    val latitude: Double = 21.4225,
    val longitude: Double = 39.8262,
    val calculationMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA,
    val asrMethodHanafi: Boolean = false,
    
    // Override phone silent mode using Alarm stream
    val overrideSilentMode: Boolean = true,
    
    // Auto-Silent mode during prayer (يبدأ بعد كام دقيقة وينتهي بعد كام دقيقة)
    val autoSilentEnabled: Boolean = true,
    val autoSilentStartDelayMinutes: Int = 0, // 0: فوراً مع الأذان، 5، 10، 15
    val autoSilentDurationMinutes: Int = 25, // يستمر 15، 20، 25، 30، 45 د
    
    // Iqama for each prayer (الإقامة لكل صلاة كما في المؤذن الإلكتروني)
    val iqamaEnabled: Boolean = true,
    val iqamaDelayFajr: Int = 25,
    val iqamaDelayDhuhr: Int = 20,
    val iqamaDelayAsr: Int = 20,
    val iqamaDelayMaghrib: Int = 10,
    val iqamaDelayIsha: Int = 20,
    
    // Pre-prayer Approaching screen with Hadiths & Previous prayer check question ("هل صليت الصلاة الفائتة؟")
    val prePrayerCheckEnabled: Boolean = true,
    val prePrayerMinutesBefore: Int = 15,
    
    // Post-prayer dedicated popup dialog
    val postPrayerPopupEnabled: Boolean = true,
    val postPrayerPopupDelayMinutes: Int = 15,
    
    // Salawat on Prophet ﷺ (حرية اختيار الأيام والأوقات)
    val salawatEnabled: Boolean = true,
    val salawatSelectedDays: Set<Int> = setOf(5, 6), // 5: Thursday night, 6: Friday, etc.
    val salawatIntervalMinutes: Int = 30, // Every 30 minutes or user chosen
    val salawatCustomTimes: List<String> = listOf("09:00", "14:00", "18:00", "21:00"),
    val salawatSoundVoice: String = "صوت عذب شجي",
    
    // Separate Istighfar Reminder (فصل الاستغفار في إعداد منفصل)
    val istighfarEnabled: Boolean = true,
    val istighfarIntervalMinutes: Int = 60,
    val istighfarDailyTarget: Int = 100,
    val istighfarSoundVoice: String = "أستغفر الله وأتوب إليه",
    
    // Fasting Reminders (تذكير بالصيام في الأيام القمرية والإثنين والخميس ورجب وشعبان وعاشوراء)
    val fastingMondayThursdayReminder: Boolean = true,
    val fastingWhiteDaysReminder: Boolean = true, // 13, 14, 15 هجري
    val fastingRajabShaabanReminder: Boolean = true, // أول 3 أيام ونصف شعبان
    val fastingAshuraArafahReminder: Boolean = true,
    
    // Friday specific
    val fridayFirstAdhanEnabled: Boolean = true,
    val fridayFirstAdhanMinutesBefore: Int = 45,
    val fridaySecondAdhanEnabled: Boolean = true,
    val fridaySurahKahfReminder: Boolean = true,
    val fridaySurahKahfTime: String = "09:30",
    
    // Fajr special alarms
    val fajrWakeupCountdownEnabled: Boolean = true,
    val fajrWakeupMinutesBefore: Int = 30,
    val fajrIbtihalatEnabled: Boolean = true,
    val fajrIbtihalatMinutesBefore: Int = 20,
    val fajrIbtihalatVoice: String = "الشيخ نصر الدين طوبار",
    
    // Ramadan & Eid
    val ramadanMesaharatyEnabled: Boolean = true,
    val ramadanMesaharatyMinutesBeforeFajr: Int = 60,
    val ramadanIftarCannonEnabled: Boolean = true,
    val ramadanPreIftarQuranEnabled: Boolean = true,
    val ramadanPreIftarQuranMinutes: Int = 20,
    val ramadanTaraweehSilentMinutes: Int = 90,
    val eidTakbeeratAlarmEnabled: Boolean = true,
    val eidTakbeeratSound: String = "تكبيرات الحرم المكي جماعية",
    
    // Seerah & Hadith
    val seerahDailyReminderEnabled: Boolean = true,
    val seerahReminderTime: String = "20:00",
    val hadithDailyReminderEnabled: Boolean = true,
    val hadithReminderTime: String = "17:00",
    val hadithFontSize: Int = 16,

    // Quran font and reciter settings
    val quranFontSize: Int = 22,
    val quranFontFamily: String = "المصحف العثماني (نسخ)",
    val quranDefaultReciter: QuranReciter = QuranReciter.ABDUL_BASIT,

    // Visual popups
    val showAdhanPopupDialog: Boolean = true,
    val showAthkarPopupDialog: Boolean = true,

    // App Appearance: Dynamic Theme Color Palettes & Launcher Icons
    val appThemeColor: AppThemeColor = AppThemeColor.EMERALD,
    val appIconTheme: AppIconTheme = AppIconTheme.EMERALD
)


