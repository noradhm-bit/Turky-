package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslamicRepository
import com.example.model.HadithCategory
import com.example.model.HadithItem
import com.example.model.SeerahChapter
import com.example.service.audio.SoundManager
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@Composable
fun HadithAndSeerahScreen(
    soundManager: SoundManager
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Hadith, 1: Seerah
    var selectedHadithCategory by remember { mutableStateOf(HadithCategory.NAWAWI) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = IslamicGreenPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("كتب الأحاديث النبوية", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("السيرة النبوية العطرة", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.AutoStories, contentDescription = null) }
            )
        }

        if (selectedTab == 0) {
            // HADITH SECTION
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(HadithCategory.values()) { category ->
                    FilterChip(
                        selected = selectedHadithCategory == category,
                        onClick = { selectedHadithCategory = category },
                        label = { Text(category.titleArabic, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            val hadiths = remember(selectedHadithCategory) {
                IslamicRepository.hadithCollection.filter { it.category == selectedHadithCategory }
                    .ifEmpty { IslamicRepository.hadithCollection }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(hadiths, key = { it.id }) { hadith ->
                    HadithCardItem(
                        hadith = hadith,
                        soundManager = soundManager,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("حديث نبوي", "${hadith.title}\n\n${hadith.arabicText}\n\n[${hadith.reference}]"))
                            Toast.makeText(context, "تم نسخ الحديث الشريف", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        } else {
            // SEERAH NABAWIYYAH SECTION
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = IslamicGreenDark)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = IslamicGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("السيرة النبوية الشريفة", color = IslamicGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "محطات مضيئة من حياة النبي المصطفى ﷺ لتعزيز الأجواء الروحانية والاقتداء بأخلاقه الكريمة.",
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                items(IslamicRepository.seerahChapters, key = { it.id }) { chapter ->
                    SeerahChapterCard(chapter = chapter, soundManager = soundManager)
                }
            }
        }
    }
}

@Composable
fun HadithCardItem(
    hadith: HadithItem,
    soundManager: SoundManager,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hadith.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = IslamicGreenPrimary
                )
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = Color.Gray)
                    }
                    IconButton(onClick = {
                        soundManager.playText(hadith.title, hadith.arabicText)
                    }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "قراءة الحديث", tint = IslamicGreenPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = hadith.arabicText,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 الفوائد والعبر: ${hadith.explanation}",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "الراوي: ${hadith.narrator}", fontSize = 11.sp, color = Color.Gray)
                Text(text = hadith.reference, fontSize = 11.sp, color = IslamicGoldDark, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SeerahChapterCard(
    chapter: SeerahChapter,
    soundManager: SoundManager
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IslamicGreenPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chapter.order}",
                            color = IslamicGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = chapter.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${chapter.era} • ${chapter.keyYear}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = {
                    soundManager.playText(chapter.title, chapter.fullText)
                }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "استماع للقصة", tint = IslamicGreenPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (expanded) chapter.fullText else chapter.summary,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Right
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("الدروس والعبر المستفادة:", fontWeight = FontWeight.Bold, color = IslamicGreenPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        chapter.moralLessons.forEach { lesson ->
                            Text("• $lesson", fontSize = 12.sp, color = Color(0xFF1E3A2F), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (expanded) "طي القصة ▲" else "قراءة القصة كاملة والدروس ▼", color = IslamicGreenPrimary)
            }
        }
    }
}
