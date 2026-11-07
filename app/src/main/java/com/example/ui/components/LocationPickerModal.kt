package com.example.ui.components

import android.widget.Toast
import kotlin.math.roundToInt
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppSettings
import com.example.model.CalculationMethod
import com.example.service.location.LocationHelper
import com.example.service.location.WorldCity
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGreenAccent
import com.example.ui.theme.IslamicGreenDark
import com.example.ui.theme.IslamicGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerModal(
    currentSettings: AppSettings,
    onLocationSelected: (cityName: String, countryName: String, latitude: Double, longitude: Double, method: CalculationMethod) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isDetectingGps by remember { mutableStateOf(false) }

    var customLat by remember { mutableStateOf(currentSettings.latitude.toString()) }
    var customLng by remember { mutableStateOf(currentSettings.longitude.toString()) }
    var showCoordinatesInput by remember { mutableStateOf(false) }

    val filteredCities = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            LocationHelper.worldCitiesDatabase
        } else {
            val q = searchQuery.trim().lowercase()
            LocationHelper.worldCitiesDatabase.filter {
                it.nameArabic.contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.countryArabic.contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E18))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "تحديد الموقع والمدينة 🌍",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "عبر الأقمار الصناعية (GPS) أو قاعدة مدن العالم",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GPS Auto Detect Button (Apple Glass style)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 16.dp,
                    tintColor = Color(0xFF193D2F),
                    onClick = {
                        isDetectingGps = true
                        LocationHelper.detectDeviceGpsLocation(
                            context = context,
                            onLocationFound = { lat, lng, city, country ->
                                isDetectingGps = false
                                Toast.makeText(context, "تم تحديد موقعك بدقة: $city", Toast.LENGTH_SHORT).show()
                                onLocationSelected(city, country, lat, lng, currentSettings.calculationMethod)
                                onDismiss()
                            },
                            onError = { err ->
                                isDetectingGps = false
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGreenAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDetectingGps) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = IslamicGreenAccent,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.MyLocation,
                                        contentDescription = "GPS",
                                        tint = IslamicGreenAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isDetectingGps) "جارٍ الاتصال بالأقمار الصناعية..." else "تحديد موقعي الحالي تلقائياً (GPS)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "حساب دقيق للمواقيت بناءً على إحداثياتك الحالية",
                                    color = Color(0xFFA0C4B4),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = IslamicGold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ابحث عن أي مدينة أو دولة في العالم...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGreenAccent) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGreenAccent,
                        unfocusedBorderColor = Color(0xFF234437),
                        focusedContainerColor = Color(0xFF142921),
                        unfocusedContainerColor = Color(0xFF142921),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Custom Coordinates Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مدن العالم المعتمدة (${filteredCities.size}):",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )

                    TextButton(onClick = { showCoordinatesInput = !showCoordinatesInput }) {
                        Text(
                            text = if (showCoordinatesInput) "إخفاء الإحداثيات اليدوية" else "إدخال إحداثيات خط الطول والعرض",
                            fontSize = 11.sp,
                            color = IslamicGold
                        )
                    }
                }

                if (showCoordinatesInput) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF173026)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customLat,
                                    onValueChange = { customLat = it },
                                    label = { Text("خط العرض (Lat)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = customLng,
                                    onValueChange = { customLng = it },
                                    label = { Text("خط الطول (Lng)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val lat = customLat.toDoubleOrNull()
                                    val lng = customLng.toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        onLocationSelected("إحداثيات مخصصة", "الموقع المختار", lat, lng, currentSettings.calculationMethod)
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "يرجى إدخال إحداثيات صحيحة", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreenPrimary)
                            ) {
                                Text("تطبيق الإحداثيات")
                            }
                        }
                    }
                }

                // Global Cities List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCities) { city ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLocationSelected(
                                        city.nameArabic,
                                        city.countryArabic,
                                        city.latitude,
                                        city.longitude,
                                        city.recommendedMethod
                                    )
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (city.nameArabic == currentSettings.cityName) Color(0xFF1E4636) else Color(0xFF14241E)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(city.flagEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = city.nameArabic,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${city.nameEnglish} • ${city.countryArabic}",
                                            color = Color(0xFFA5B8AF),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (city.nameArabic == currentSettings.cityName) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "المحدد", tint = IslamicGreenAccent)
                                } else {
                                    Text(
                                        text = "${city.latitude.roundToInt()}°, ${city.longitude.roundToInt()}°",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
