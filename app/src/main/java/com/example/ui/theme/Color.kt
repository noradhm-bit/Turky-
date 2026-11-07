package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Default Islamic Luxury Palette: Emerald Green & Desert Gold
val IslamicGreenPrimary = Color(0xFF0F5A47)
val IslamicGreenDark = Color(0xFF093B2E)
val IslamicGreenLight = Color(0xFF1B7D63)
val IslamicGreenAccent = Color(0xFF48D5A3)

val IslamicGold = Color(0xFFD4AF37)
val IslamicGoldLight = Color(0xFFE8C862)
val IslamicGoldDark = Color(0xFFA07B18)

val IslamicSand = Color(0xFFF9F7F2)
val IslamicSurfaceLight = Color(0xFFFFFFFF)
val IslamicSurfaceContainer = Color(0xFFF0F4F1)

val IslamicNightDark = Color(0xFF0A1512)
val IslamicNightSurface = Color(0xFF12221D)
val IslamicNightSurfaceVariant = Color(0xFF19322B)

val PrayerActiveCard = Color(0xFF104E3E)
val SkyTwilight = Color(0xFF1B2A4A)
val AccentAmber = Color(0xFFF59E0B)

enum class AppThemeColor(
    val titleArabic: String,
    val description: String,
    val primaryColor: Color,
    val accentColor: Color,
    val secondaryColor: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkSurfaceVariant: Color
) {
    EMERALD(
        titleArabic = "الزمرد الملكي",
        description = "أخضر زمردي فخم مع لمسات الذهب",
        primaryColor = Color(0xFF0F5A47),
        accentColor = Color(0xFF48D5A3),
        secondaryColor = Color(0xFFD4AF37),
        darkBackground = Color(0xFF0A1512),
        darkSurface = Color(0xFF12221D),
        darkSurfaceVariant = Color(0xFF19322B)
    ),
    MIDNIGHT(
        titleArabic = "اللازوردي الليلي",
        description = "أزرق كحلي عميق مع الفضي والذهبي",
        primaryColor = Color(0xFF1D4ED8),
        accentColor = Color(0xFF60A5FA),
        secondaryColor = Color(0xFFFBBF24),
        darkBackground = Color(0xFF080E1A),
        darkSurface = Color(0xFF0F1A2E),
        darkSurfaceVariant = Color(0xFF182744)
    ),
    GOLD(
        titleArabic = "الذهب الأندلسي",
        description = "كهرمان وعسل دافئ مع الذهب الخالص",
        primaryColor = Color(0xFFB45309),
        accentColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFDE68A),
        darkBackground = Color(0xFF140D05),
        darkSurface = Color(0xFF22170B),
        darkSurfaceVariant = Color(0xFF332311)
    ),
    AMETHYST(
        titleArabic = "الأرجواني الإمبراطوري",
        description = "بنفسجي ملكي ساحر مع لافندر ناعم",
        primaryColor = Color(0xFF7E22CE),
        accentColor = Color(0xFFC084FC),
        secondaryColor = Color(0xFFF472B6),
        darkBackground = Color(0xFF100918),
        darkSurface = Color(0xFF1C1129),
        darkSurfaceVariant = Color(0xFF2C1A40)
    ),
    TURQUOISE(
        titleArabic = "الفيروزي القاهري",
        description = "تيل مائي منعش وأنيق مع لمسات بلورية",
        primaryColor = Color(0xFF0D9488),
        accentColor = Color(0xFF2DD4BF),
        secondaryColor = Color(0xFF99F6E4),
        darkBackground = Color(0xFF061413),
        darkSurface = Color(0xFF0D2422),
        darkSurfaceVariant = Color(0xFF143835)
    ),
    ONYX(
        titleArabic = "الأسود الماسي",
        description = "أسود فاحم AMOLED مع التيتانيوم والذهب",
        primaryColor = Color(0xFF2B2B2B),
        accentColor = Color(0xFFD4AF37),
        secondaryColor = Color(0xFFE5E5E5),
        darkBackground = Color(0xFF080808),
        darkSurface = Color(0xFF121212),
        darkSurfaceVariant = Color(0xFF1E1E1E)
    ),
    RUBY(
        titleArabic = "الياقوت الوردي",
        description = "وردي مخملي دافئ مع الذهبي الروحي",
        primaryColor = Color(0xFFBE185D),
        accentColor = Color(0xFFF472B6),
        secondaryColor = Color(0xFFFDE047),
        darkBackground = Color(0xFF14080F),
        darkSurface = Color(0xFF21101A),
        darkSurfaceVariant = Color(0xFF331929)
    )
}
