package com.example.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.model.CalculationMethod
import java.util.Locale
import kotlin.math.*

data class WorldCity(
    val nameArabic: String,
    val nameEnglish: String,
    val countryArabic: String,
    val flagEmoji: String,
    val latitude: Double,
    val longitude: Double,
    val recommendedMethod: CalculationMethod
)

object LocationHelper {

    const val KAABA_LAT = 21.4225
    const val KAABA_LNG = 39.8262

    /**
     * Comprehensive database of world cities for prayer calculation.
     */
    val worldCitiesDatabase = listOf(
        // Saudi Arabia & Gulf
        WorldCity("مكة المكرمة", "Mecca", "المملكة العربية السعودية", "🇸🇦", 21.4225, 39.8262, CalculationMethod.UMM_AL_QURA),
        WorldCity("المدينة المنورة", "Medina", "المملكة العربية السعودية", "🇸🇦", 24.4672, 39.6111, CalculationMethod.UMM_AL_QURA),
        WorldCity("الرياض", "Riyadh", "المملكة العربية السعودية", "🇸🇦", 24.7136, 46.6753, CalculationMethod.UMM_AL_QURA),
        WorldCity("جدة", "Jeddah", "المملكة العربية السعودية", "🇸🇦", 21.5433, 39.1728, CalculationMethod.UMM_AL_QURA),
        WorldCity("الدمام", "Dammam", "المملكة العربية السعودية", "🇸🇦", 26.4207, 50.0888, CalculationMethod.UMM_AL_QURA),
        WorldCity("دبي", "Dubai", "الإمارات العربية المتحدة", "🇦🇪", 25.2048, 55.2708, CalculationMethod.UMM_AL_QURA),
        WorldCity("أبوظبي", "Abu Dhabi", "الإمارات العربية المتحدة", "🇦🇪", 24.4539, 54.3773, CalculationMethod.UMM_AL_QURA),
        WorldCity("الشارقة", "Sharjah", "الإمارات العربية المتحدة", "🇦🇪", 25.3463, 55.4209, CalculationMethod.UMM_AL_QURA),
        WorldCity("الدوحة", "Doha", "قطر", "🇶🇦", 25.2854, 51.5310, CalculationMethod.UMM_AL_QURA),
        WorldCity("الكويت", "Kuwait City", "الكويت", "🇰🇼", 29.3759, 47.9774, CalculationMethod.UMM_AL_QURA),
        WorldCity("المنامة", "Manama", "البحرين", "🇧🇭", 26.2285, 50.5860, CalculationMethod.UMM_AL_QURA),
        WorldCity("مسقط", "Muscat", "سلطنة عمان", "🇴🇲", 23.5880, 58.3829, CalculationMethod.UMM_AL_QURA),

        // Egypt & Levant
        WorldCity("القاهرة", "Cairo", "مصر", "🇪🇬", 30.0444, 31.2357, CalculationMethod.EGYPTIAN),
        WorldCity("الإسكندرية", "Alexandria", "مصر", "🇪🇬", 31.2001, 29.9187, CalculationMethod.EGYPTIAN),
        WorldCity("الجيزة", "Giza", "مصر", "🇪🇬", 30.0131, 31.2089, CalculationMethod.EGYPTIAN),
        WorldCity("القدس الشريف", "Jerusalem", "فلسطين", "🇵🇸", 31.7683, 35.2137, CalculationMethod.EGYPTIAN),
        WorldCity("غزة", "Gaza", "فلسطين", "🇵🇸", 31.5017, 34.4668, CalculationMethod.EGYPTIAN),
        WorldCity("عمان", "Amman", "الأردن", "🇯🇴", 31.9539, 35.9106, CalculationMethod.EGYPTIAN),
        WorldCity("دمشق", "Damascus", "سوريا", "🇸🇾", 33.5138, 36.2765, CalculationMethod.EGYPTIAN),
        WorldCity("حلب", "Aleppo", "سوريا", "🇸🇾", 36.2021, 37.1343, CalculationMethod.EGYPTIAN),
        WorldCity("بيروت", "Beirut", "لبنان", "🇱🇧", 33.8938, 35.5018, CalculationMethod.EGYPTIAN),
        WorldCity("بغداد", "Baghdad", "العراق", "🇮🇶", 33.3152, 44.3661, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("أربيل", "Erbil", "العراق", "🇮🇶", 36.1911, 44.0092, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("البصرة", "Basra", "العراق", "🇮🇶", 30.5085, 47.7804, CalculationMethod.MUSLIM_WORLD_LEAGUE),

        // North Africa & Sudan & Yemen
        WorldCity("الدار البيضاء", "Casablanca", "المغرب", "🇲🇦", 33.5731, -7.5898, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("الرباط", "Rabat", "المغرب", "🇲🇦", 34.0209, -6.8416, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("فاس", "Fez", "المغرب", "🇲🇦", 34.0181, -5.0078, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("مراكش", "Marrakech", "المغرب", "🇲🇦", 31.6295, -7.9811, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("الجزائر", "Algiers", "الجزائر", "🇩🇿", 36.7538, 3.0588, CalculationMethod.EGYPTIAN),
        WorldCity("وهران", "Oran", "الجزائر", "🇩🇿", 35.6987, -0.6349, CalculationMethod.EGYPTIAN),
        WorldCity("تونس", "Tunis", "تونس", "🇹🇳", 36.8065, 10.1815, CalculationMethod.EGYPTIAN),
        WorldCity("طرابلس", "Tripoli", "ليبيا", "🇱🇾", 32.8872, 13.1913, CalculationMethod.EGYPTIAN),
        WorldCity("بنغازي", "Benghazi", "ليبيا", "🇱🇾", 32.1167, 20.0667, CalculationMethod.EGYPTIAN),
        WorldCity("الخرطوم", "Khartoum", "السودان", "🇸🇩", 15.5007, 32.5599, CalculationMethod.EGYPTIAN),
        WorldCity("صنعاء", "Sanaa", "اليمن", "🇾🇪", 15.3694, 44.1910, CalculationMethod.UMM_AL_QURA),
        WorldCity("عدن", "Aden", "اليمن", "🇾🇪", 12.7855, 45.0187, CalculationMethod.UMM_AL_QURA),

        // Turkey, Europe & Americas
        WorldCity("إسطنبول", "Istanbul", "تركيا", "🇹🇷", 41.0082, 28.9784, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("أنقرة", "Ankara", "تركيا", "🇹🇷", 39.9334, 32.8597, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("لندن", "London", "المملكة المتحدة", "🇬🇧", 51.5074, -0.1278, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("باريس", "Paris", "فرنسا", "🇫🇷", 48.8566, 2.3522, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("برلين", "Berlin", "ألمانيا", "🇩🇪", 52.5200, 13.4050, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("فرانكفورت", "Frankfurt", "ألمانيا", "🇩🇪", 50.1109, 8.6821, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("مدريد", "Madrid", "إسبانيا", "🇪🇸", 40.4168, -3.7038, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("روما", "Rome", "إيطاليا", "🇮🇹", 41.9028, 12.4964, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("أمستردام", "Amsterdam", "هولندا", "🇳🇱", 52.3676, 4.9041, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("نيويورك", "New York", "الولايات المتحدة", "🇺🇸", 40.7128, -74.0060, CalculationMethod.ISNA),
        WorldCity("شيكاغو", "Chicago", "الولايات المتحدة", "🇺🇸", 41.8781, -87.6298, CalculationMethod.ISNA),
        WorldCity("لوس أنجلوس", "Los Angeles", "الولايات المتحدة", "🇺🇸", 34.0522, -118.2437, CalculationMethod.ISNA),
        WorldCity("تورونتو", "Toronto", "كندا", "🇨🇦", 43.6532, -79.3832, CalculationMethod.ISNA),

        // Asia & Australia
        WorldCity("كوالالمبور", "Kuala Lumpur", "ماليزيا", "🇲🇾", 3.1390, 101.6869, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("جاكرتا", "Jakarta", "إندونيسيا", "🇮🇩", -6.2088, 106.8456, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        WorldCity("إسلام أباد", "Islamabad", "باكستان", "🇵🇰", 33.6844, 73.0479, CalculationMethod.KARACHI),
        WorldCity("كراتشي", "Karachi", "باكستان", "🇵🇰", 24.8607, 67.0011, CalculationMethod.KARACHI),
        WorldCity("دلهي", "Delhi", "الهند", "🇮🇳", 28.6139, 77.2090, CalculationMethod.KARACHI),
        WorldCity("سيدني", "Sydney", "أستراليا", "🇦🇺", -33.8688, 151.2093, CalculationMethod.MUSLIM_WORLD_LEAGUE)
    )

    /**
     * Calculates great circle distance to Kaaba in kilometers.
     */
    fun calculateDistanceToKaaba(lat: Double, lng: Double): Int {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(KAABA_LAT - lat)
        val dLng = Math.toRadians(KAABA_LNG - lng)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat)) * cos(Math.toRadians(KAABA_LAT)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadiusKm * c).roundToInt()
    }

    /**
     * Requests accurate location via GPS and Network provider.
     */
    fun detectDeviceGpsLocation(
        context: Context,
        onLocationFound: (latitude: Double, longitude: Double, cityName: String, countryName: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            onError("يرجى منح إذن الوصول إلى الموقع (GPS) لتحديد مواقيت الصلاة بدقة.")
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onError("خدمة تحديد الموقع غير متوفرة على هذا الجهاز.")
            return
        }

        // Try last known location first for immediate response
        var bestLocation: Location? = null
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (bestLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        if (bestLocation != null) {
            resolveLocation(context, bestLocation.latitude, bestLocation.longitude, onLocationFound)
            return
        }

        // Request single fresh update
        val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationManager.GPS_PROVIDER
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            LocationManager.NETWORK_PROVIDER
        } else {
            onError("خدمات الموقع (GPS) مغلقة. يرجى تفعيل الموقع من إعدادات الهاتف.")
            return
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                locationManager.removeUpdates(this)
                resolveLocation(context, loc.latitude, loc.longitude, onLocationFound)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            locationManager.requestSingleUpdate(provider, listener, null)
        } catch (e: Exception) {
            onError("تعذر الاتصال بمستشعر GPS: ${e.localizedMessage}")
        }
    }

    private fun resolveLocation(
        context: Context,
        lat: Double,
        lng: Double,
        callback: (latitude: Double, longitude: Double, cityName: String, countryName: String) -> Unit
    ) {
        try {
            val geocoder = Geocoder(context, Locale("ar"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "موقعي الحالي"
                val country = addr.countryName ?: "العالم"
                callback(lat, lng, city, country)
                return
            }
        } catch (e: Exception) {
            // Geocoder lookup failed or offline, find nearest city in local world database
        }

        // Match nearest world city
        val nearest = worldCitiesDatabase.minByOrNull { city ->
            val dLat = city.latitude - lat
            val dLng = city.longitude - lng
            dLat * dLat + dLng * dLng
        }

        if (nearest != null) {
            callback(lat, lng, nearest.nameArabic, nearest.countryArabic)
        } else {
            callback(lat, lng, "موقعي الحالي (GPS)", "إحداثيات جغرافية")
        }
    }
}
