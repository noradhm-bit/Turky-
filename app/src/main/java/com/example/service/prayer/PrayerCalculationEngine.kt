package com.example.service.prayer

import com.example.model.CalculationMethod
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

object PrayerCalculationEngine {

    private const val KAABA_LAT = 21.4225
    private const val KAABA_LNG = 39.8262

    /**
     * Calculates prayer times for a given day, coordinates, and calculation method.
     */
    fun calculatePrayerTimes(
        calendar: Calendar = Calendar.getInstance(),
        latitude: Double,
        longitude: Double,
        method: CalculationMethod,
        isHanafi: Boolean = false,
        manualOffsets: Map<PrayerType, Int> = emptyMap(),
        iqamaDelays: Map<PrayerType, Int> = emptyMap()
    ): List<PrayerTimeItem> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val timeZoneOffset = calendar.timeZone.getOffset(calendar.timeInMillis) / 3600000.0

        // Julian date
        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)

        // Sun parameters
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0
        val eqT = q / 15.0 - ra
        val decl = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))

        // Dhuhr (solar noon)
        val noon = fixHour(12.0 + timeZoneOffset - longitude / 15.0 - eqT)

        // Method angles
        val fajrAngle = when (method) {
            CalculationMethod.UMM_AL_QURA -> 18.5
            CalculationMethod.EGYPTIAN -> 19.5
            CalculationMethod.MUSLIM_WORLD_LEAGUE -> 18.0
            CalculationMethod.ISNA -> 15.0
            CalculationMethod.KARACHI -> 18.0
        }

        // Sunrise & Sunset (approx 0.833 degrees refraction)
        val sunriseDiff = sunAngleTime(0.833, latitude, decl)
        val sunriseHour = noon - sunriseDiff
        val sunsetHour = noon + sunriseDiff

        // Fajr
        val fajrHour = noon - sunAngleTime(fajrAngle, latitude, decl)

        // Asr (shadow ratio: 1 for Shafi'i/standard, 2 for Hanafi)
        val asrFactor = if (isHanafi) 2 else 1
        val asrAngle = Math.toDegrees(atan(1.0 / (asrFactor + tan(Math.toRadians(abs(latitude - decl))))))
        val asrDiff = sunAngleTime(90.0 - asrAngle, latitude, decl)
        val asrHour = noon + asrDiff

        // Maghrib
        val maghribHour = sunsetHour

        // Isha
        val ishaHour = when (method) {
            CalculationMethod.UMM_AL_QURA -> maghribHour + 1.5 // 90 min after Maghrib
            CalculationMethod.EGYPTIAN -> noon + sunAngleTime(17.5, latitude, decl)
            CalculationMethod.MUSLIM_WORLD_LEAGUE -> noon + sunAngleTime(17.0, latitude, decl)
            CalculationMethod.ISNA -> noon + sunAngleTime(15.0, latitude, decl)
            CalculationMethod.KARACHI -> noon + sunAngleTime(18.0, latitude, decl)
        }

        val rawTimes = mapOf(
            PrayerType.FAJR to fajrHour,
            PrayerType.SUNRISE to sunriseHour,
            PrayerType.DHUHR to noon,
            PrayerType.ASR to asrHour,
            PrayerType.MAGHRIB to maghribHour,
            PrayerType.ISHA to ishaHour
        )

        val nowMillis = System.currentTimeMillis()
        var nextFound = false

        return rawTimes.map { (prayer, hourDecimal) ->
            val offsetMinutes = manualOffsets[prayer] ?: 0
            val prayerCalendar = (calendar.clone() as Calendar).apply {
                val totalMinutes = (hourDecimal * 60).roundToInt() + offsetMinutes
                val hours = (totalMinutes / 60) % 24
                val minutes = (totalMinutes % 60 + 60) % 60
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val prayerMillis = prayerCalendar.timeInMillis
            val isPast = nowMillis > prayerMillis
            val isNext = !isPast && !nextFound && prayer != PrayerType.SUNRISE
            if (isNext) nextFound = true

            val defaultIqamaDelay = when (prayer) {
                PrayerType.FAJR -> 25
                PrayerType.DHUHR -> 20
                PrayerType.ASR -> 20
                PrayerType.MAGHRIB -> 10
                PrayerType.ISHA -> 20
                PrayerType.SUNRISE -> 0
            }
            val iqamaDelay = iqamaDelays[prayer] ?: defaultIqamaDelay
            val iqamaCalendar = (prayerCalendar.clone() as Calendar).apply {
                add(Calendar.MINUTE, iqamaDelay)
            }
            val iqamaFormatted = if (prayer != PrayerType.SUNRISE) {
                String.format(
                    "%02d:%02d %s",
                    if (iqamaCalendar.get(Calendar.HOUR) == 0) 12 else iqamaCalendar.get(Calendar.HOUR),
                    iqamaCalendar.get(Calendar.MINUTE),
                    if (iqamaCalendar.get(Calendar.AM_PM) == Calendar.AM) "ص" else "م"
                )
            } else ""

            val formatted = String.format(
                "%02d:%02d %s",
                if (prayerCalendar.get(Calendar.HOUR) == 0) 12 else prayerCalendar.get(Calendar.HOUR),
                prayerCalendar.get(Calendar.MINUTE),
                if (prayerCalendar.get(Calendar.AM_PM) == Calendar.AM) "ص" else "م"
            )
            val remaining = if (isNext) maxOf(0L, prayerMillis - nowMillis) else 0L

            PrayerTimeItem(
                type = prayer,
                timeMillis = prayerMillis,
                formattedTime = formatted,
                iqamaFormattedTime = iqamaFormatted,
                isNext = isNext,
                isPast = isPast,
                remainingMillis = remaining
            )
        }
    }


    /**
     * Calculates Qibla direction in degrees clockwise from North.
     */
    fun calculateQiblaDirection(latitude: Double, longitude: Double): Float {
        val latRad = Math.toRadians(latitude)
        val lngRad = Math.toRadians(longitude)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLngRad = Math.toRadians(KAABA_LNG)

        val dLng = kaabaLngRad - lngRad
        val y = sin(dLng)
        val x = cos(latRad) * tan(kaabaLatRad) - sin(latRad) * cos(dLng)
        var qibla = Math.toDegrees(atan2(y, x))
        qibla = (qibla + 360) % 360
        return qibla.toFloat()
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunAngleTime(angle: Double, lat: Double, decl: Double): Double {
        val numerator = -sin(Math.toRadians(angle)) - sin(Math.toRadians(lat)) * sin(Math.toRadians(decl))
        val denominator = cos(Math.toRadians(lat)) * cos(Math.toRadians(decl))
        val cosT = numerator / denominator
        return if (cosT > 1.0) 0.0 else if (cosT < -1.0) 12.0 else Math.toDegrees(acos(cosT)) / 15.0
    }

    private fun fixAngle(a: Double): Double = (a % 360.0 + 360.0) % 360.0
    private fun fixHour(h: Double): Double = (h % 24.0 + 24.0) % 24.0
}
