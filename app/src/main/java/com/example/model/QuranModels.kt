package com.example.model

data class QuranVerse(
    val verseNumber: Int,
    val arabicText: String
)

data class QuranSurah(
    val number: Int,
    val nameArabic: String,
    val englishName: String,
    val englishTranslation: String,
    val revelationType: String, // مكي or مدني
    val totalVerses: Int,
    val verses: List<QuranVerse>,
    val audioDurationSec: Int = 180
)

data class QuranBookmark(
    val surahNumber: Int,
    val surahName: String,
    val verseNumber: Int,
    val timestamp: Long
)
