package com.example.model

enum class AthkarCategory(val titleArabic: String) {
    MORNING("أذكار الصباح"),
    EVENING("أذكار المساء"),
    SLEEP("أذكار النوم"),
    WAKING("أذكار الاستيقاظ"),
    POST_PRAYER("أذكار بعد الصلاة المفروضة"),
    ISTIGHFAR_SALAWAT("استغفار وتسبيح وصلاة على النبي")
}

enum class NatureThemeType {
    MORNING_NATURE, // Serene sunrise valley
    EVENING_NATURE, // Tranquil sunset lake
    NIGHT_NATURE,   // Starry quiet desert night
    MOSQUE          // Majestic illuminated mosque
}

data class DhikrItem(
    val id: Int,
    val category: AthkarCategory,
    val arabicText: String,
    val virtue: String,
    val targetCount: Int,
    val reference: String,
    var currentCount: Int = 0
)

data class PeriodicDhikrAlert(
    val id: Int,
    val title: String,
    val text: String,
    val audioTone: String
)
