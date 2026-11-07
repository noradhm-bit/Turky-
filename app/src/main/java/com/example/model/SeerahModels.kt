package com.example.model

data class SeerahChapter(
    val id: Int,
    val order: Int,
    val title: String,
    val era: String, // العهد المكي / العهد المدني
    val keyYear: String,
    val summary: String,
    val fullText: String,
    val moralLessons: List<String>
)
