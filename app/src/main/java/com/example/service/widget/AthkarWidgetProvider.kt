package com.example.service.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.IslamicRepository
import java.util.Calendar

class AthkarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val dhikrList = when {
                hour in 5..11 -> IslamicRepository.morningAthkar
                hour in 15..20 -> IslamicRepository.eveningAthkar
                else -> IslamicRepository.sleepAthkar
            }

            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val selectedDhikr = dhikrList[dayOfYear % dhikrList.size]

            val views = RemoteViews(context.packageName, R.layout.widget_athkar).apply {
                val badgeText = when {
                    hour in 5..11 -> "أذكار الصباح"
                    hour in 15..20 -> "أذكار المساء"
                    else -> "أذكار النوم والسكينة"
                }
                setTextViewText(R.id.widget_athkar_badge, badgeText)
                setTextViewText(R.id.widget_athkar_text, selectedDhikr.arabicText)
                setTextViewText(R.id.widget_athkar_count, "كرر ${selectedDhikr.targetCount} مرات")

                // Tap launches MainActivity straight to Athkar & Sebha tab
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("OPEN_TAB", 1) // Athkar tab
                }
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    1,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_athkar_root, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
