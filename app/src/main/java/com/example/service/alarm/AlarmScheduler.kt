package com.example.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.model.AppSettings
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import com.example.service.prayer.PrayerCalculationEngine
import java.util.Calendar

object AlarmScheduler {

    const val EXTRA_PRAYER_TYPE = "EXTRA_PRAYER_TYPE"
    const val EXTRA_IS_PRE_ALERT = "EXTRA_IS_PRE_ALERT"

    /**
     * Efficiently schedules only the next upcoming prayer alarm using setExactAndAllowWhileIdle
     * to ensure absolute zero battery consumption when idle.
     */
    fun scheduleNextPrayer(context: Context, settings: AppSettings = AppSettings()) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = Calendar.getInstance()
        val prayersToday = PrayerCalculationEngine.calculatePrayerTimes(
            calendar = now,
            latitude = settings.latitude,
            longitude = settings.longitude,
            method = settings.calculationMethod,
            isHanafi = settings.asrMethodHanafi
        )

        // Find next prayer today or Fajr tomorrow
        val nextPrayer = prayersToday.firstOrNull { it.timeMillis > System.currentTimeMillis() && it.type != PrayerType.SUNRISE }
            ?: run {
                val tomorrow = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
                val prayersTomorrow = PrayerCalculationEngine.calculatePrayerTimes(
                    calendar = tomorrow,
                    latitude = settings.latitude,
                    longitude = settings.longitude,
                    method = settings.calculationMethod,
                    isHanafi = settings.asrMethodHanafi
                )
                prayersTomorrow.firstOrNull { it.type == PrayerType.FAJR }
            }

        if (nextPrayer != null) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra(EXTRA_PRAYER_TYPE, nextPrayer.type.name)
                putExtra(EXTRA_IS_PRE_ALERT, false)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextPrayer.timeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        nextPrayer.timeMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                // If exact alarm permission is restricted on Android 12+, use windowed alarm
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextPrayer.timeMillis,
                    pendingIntent
                )
            }
        }
    }
}
