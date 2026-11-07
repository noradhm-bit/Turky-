package com.example.service.icon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

enum class AppIconTheme(
    val aliasSuffix: String,
    val titleArabic: String,
    val subtitleArabic: String,
    val hexColor: Long
) {
    EMERALD(
        aliasSuffix = "MainActivity",
        titleArabic = "الزمردي الملكي (الأصلي)",
        subtitleArabic = "أخضر زمردي مع الهلال والمنارة الذهبية",
        hexColor = 0xFF0D382E
    ),
    MIDNIGHT(
        aliasSuffix = "MainActivityMidnight",
        titleArabic = "اللازوردي الليلي (أزرق ملكي)",
        subtitleArabic = "سماء الليل الكحلية مع الفضي والذهبي",
        hexColor = 0xFF0B192C
    ),
    GOLD(
        aliasSuffix = "MainActivityGold",
        titleArabic = "الذهب الأندلسي (كهرمان دافئ)",
        subtitleArabic = "درجات الكهرمان والذهب الخالص",
        hexColor = 0xFF2A1705
    ),
    AMETHYST(
        aliasSuffix = "MainActivityAmethyst",
        titleArabic = "الأرجواني الإمبراطوري (بنفسجي فاخر)",
        subtitleArabic = "أرجواني ملكي ولمسات لافندر ساحرة",
        hexColor = 0xFF1E112A
    ),
    TURQUOISE(
        aliasSuffix = "MainActivityTurquoise",
        titleArabic = "الفيروزي القاهري (أوشن تيل)",
        subtitleArabic = "أزرق فيروزي إسلامي نقي ومنعش",
        hexColor = 0xFF082422
    ),
    ONYX(
        aliasSuffix = "MainActivityOnyx",
        titleArabic = "الأسود الماسي (أونيكس تيتانيوم)",
        subtitleArabic = "أسود فاحم AMOLED فائق الأناقة والغموض",
        hexColor = 0xFF121212
    )
}

object AppIconManager {

    private val allAliases = listOf(
        "com.example.MainActivity",
        "com.example.MainActivityMidnight",
        "com.example.MainActivityGold",
        "com.example.MainActivityAmethyst",
        "com.example.MainActivityTurquoise",
        "com.example.MainActivityOnyx"
    )

    fun applyIcon(context: Context, theme: AppIconTheme) {
        val pm = context.packageManager
        val targetAlias = "com.example.${theme.aliasSuffix}"

        for (alias in allAliases) {
            val newState = if (alias == targetAlias) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            try {
                pm.setComponentEnabledSetting(
                    ComponentName(context, alias),
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                // Graceful ignore
            }
        }
    }
}
