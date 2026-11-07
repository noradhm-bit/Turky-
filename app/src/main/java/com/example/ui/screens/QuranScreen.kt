package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslamicRepository
import com.example.model.QuranSurah
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    soundManager: SoundManager,
    initialSurahNumber: Int? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSurah by remember {
        mutableStateOf(
            if (initialSurahNumber != null) {
                IslamicRepository.quranSurahs.find { it.number == initialSurahNumber }
            } else null
        )
    }

    // Font size control for Large Font requirement (القرآن الكريم بخط كبير)
    var quranFontSize by remember { mutableStateOf(26.sp) }
    var selectedReciter by remember { mutableStateOf(com.example.model.QuranReciter.ABDUL_BASIT) }
    val isAudioPlaying by soundManager.isPlaying.collectAsState()

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            IslamicRepository.quranSurahs
        } else {
            IslamicRepository.quranSurahs.filter {
                it.nameArabic.contains(searchQuery.trim()) ||
                it.englishName.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    if (selectedSurah != null) {
        val surah = selectedSurah!!
        // SURAH DETAIL READER (Large font & Audio)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Reader Top Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "سورة ${surah.nameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${surah.revelationType} • ${surah.totalVerses} آيات",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        soundManager.stopAll()
                        selectedSurah = null
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    // Font Size Zoom Controls (+ / -)
                    IconButton(onClick = {
                        if (quranFontSize.value > 18f) quranFontSize = (quranFontSize.value - 2).sp
                    }) {
                        Icon(Icons.Default.TextDecrease, contentDescription = "تصغير الخط")
                    }
                    Text(
                        "${quranFontSize.value.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreenPrimary
                    )
                    IconButton(onClick = {
                        if (quranFontSize.value < 44f) quranFontSize = (quranFontSize.value + 2).sp
                    }) {
                        Icon(Icons.Default.TextIncrease, contentDescription = "تكبير الخط")
                    }

                    // Audio Playback
                    IconButton(
                        onClick = {
                            if (isAudioPlaying) {
                                soundManager.stopAll()
                            } else {
                                val fullSurahText = surah.verses.joinToString(" ") { it.arabicText }
                                soundManager.playSurahAudio(
                                    surahNumber = surah.number,
                                    reciter = selectedReciter,
                                    surahArabicName = surah.nameArabic,
                                    fallbackVerses = fullSurahText
                                )
                            }
                        }
                    ) {
                        Icon(
                            if (isAudioPlaying) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                            contentDescription = "استماع للتلاوة",
                            tint = if (isAudioPlaying) Color.Red else IslamicGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            // Reciter Selection Chips Row
            Surface(
                color = Color(0xFF14241E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("القارئ:", fontSize = 11.sp, color = IslamicGold, fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(com.example.model.QuranReciter.values()) { reciter ->
                            val isSelected = selectedReciter == reciter
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedReciter = reciter
                                    if (isAudioPlaying) {
                                        val fullSurahText = surah.verses.joinToString(" ") { it.arabicText }
                                        soundManager.playSurahAudio(
                                            surahNumber = surah.number,
                                            reciter = reciter,
                                            surahArabicName = surah.nameArabic,
                                            fallbackVerses = fullSurahText
                                        )
                                    }
                                },
                                label = { Text(reciter.nameArabic, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslamicGreenPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Surah Reading Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Surah Header Ornament
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "سُورَةُ ${surah.nameArabic}",
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                color = Color.White,
                                fontSize = (quranFontSize.value * 0.95f).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Continuous Flow Quranic Verses with Verse End Symbols ۝
                items(surah.verses) { verse ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGreenPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${verse.verseNumber}",
                                    color = IslamicGreenPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${verse.arabicText}  ۝",
                                fontSize = quranFontSize,
                                lineHeight = (quranFontSize.value * 1.8f).sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    } else {
        // SURAH INDEX LIST
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Surah Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث عن سورة بالاسم (الكهف، يس، الملك...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGreenPrimary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGreenPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Surahs List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSurah = surah },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(IslamicGreenPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${surah.number}",
                                        color = IslamicGreenPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "سورة ${surah.nameArabic}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${surah.englishName} • ${surah.totalVerses} آية",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SuggestionChip(
                                    onClick = { selectedSurah = surah },
                                    label = { Text(surah.revelationType, fontSize = 11.sp) }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
