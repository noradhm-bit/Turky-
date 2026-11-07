package com.example.model

enum class HadithCategory(val titleArabic: String) {
    NAWAWI("الأربعون النووية"),
    RIYAD_AS_SALIHIN("رياض الصالحين"),
    BUKHARI_PEARLS("صحيح البخاري"),
    CHARACTER_MANNERS("مكارم الأخلاق")
}

data class HadithItem(
    val id: Int,
    val category: HadithCategory,
    val title: String,
    val arabicText: String,
    val narrator: String,
    val explanation: String,
    val reference: String
)
