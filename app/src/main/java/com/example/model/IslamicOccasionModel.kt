package com.example.model

enum class OccasionCategory(val titleArabic: String) {
    MAJOR_EID("الأعياد الكبرى"),
    BLESSED_SEASON("مواسم الطاعات والبركات"),
    SUNNAH_FASTING("أيام الصيام المستحب"),
    BLESSED_NIGHT("الليالي المباركة"),
    HISTORICAL_EVENT("محطات إسلامية خالدة")
}

data class IslamicOccasion(
    val id: Int,
    val nameArabic: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val hijriMonthName: String,
    val hijriFormattedDate: String,
    val category: OccasionCategory,
    val summary: String,
    val virtue: String,
    val recommendedDeeds: List<String>,
    val supplicationText: String,
    val daysRemainingEstimate: Int = 0,
    val reminderEnabled: Boolean = true
)
