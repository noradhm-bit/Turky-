package com.example.service.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.model.AppSettings
import com.example.model.PrayerType
import com.example.service.prayer.PrayerCalculationEngine
import java.util.Calendar

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val settings = AppSettings()
            val calendar = Calendar.getInstance()
            val prayers = PrayerCalculationEngine.calculatePrayerTimes(
                calendar = calendar,
                latitude = settings.latitude,
                longitude = settings.longitude,
                method = settings.calculationMethod,
                isHanafi = settings.asrMethodHanafi
            )

            val nextPrayer = prayers.firstOrNull { it.isNext } ?: prayers.firstOrNull()

            val views = RemoteViews(context.packageName, R.layout.widget_prayer_times).apply {
                setTextViewText(R.id.widget_city_name, settings.cityName)
                setTextViewText(R.id.widget_next_prayer_title, "الصلاة القادمة: صلاة ${nextPrayer?.type?.arabicName ?: "الفجر"}")
                setTextViewText(R.id.widget_next_prayer_time, nextPrayer?.formattedTime ?: "--:--")

                val remainingMin = nextPrayer?.let {
                    if (it.timeMillis > System.currentTimeMillis()) ((it.timeMillis - System.currentTimeMillis()) / (1000 * 60)).toInt() else 0
                } ?: 0
                val hours = remainingMin / 60
                val mins = remainingMin % 60
                val remainingStr = if (hours > 0) "(متبقي $hours س و $mins د)" else "(متبقي $mins دقيقة)"
                setTextViewText(R.id.widget_remaining_time, remainingStr)

                prayers.forEach { p ->
                    val viewId = when (p.type) {
                        PrayerType.FAJR -> R.id.widget_time_fajr
                        PrayerType.DHUHR -> R.id.widget_time_dhuhr
                        PrayerType.ASR -> R.id.widget_time_asr
                        PrayerType.MAGHRIB -> R.id.widget_time_maghrib
                        PrayerType.ISHA -> R.id.widget_time_isha
                        PrayerType.SUNRISE -> null
                    }
                    if (viewId != null) {
                        setTextViewText(viewId, p.formattedTime.substringBefore(" "))
                    }
                }

                // Tap launches MainActivity
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
