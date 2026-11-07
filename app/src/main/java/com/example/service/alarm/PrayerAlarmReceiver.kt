package com.example.service.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.PrayerType

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "manarat_prayer_alerts"
        const val CHANNEL_NAME = "أذان ومواقيت الصلاة"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerTypeName = intent.getStringExtra(AlarmScheduler.EXTRA_PRAYER_TYPE) ?: PrayerType.FAJR.name
        val prayerType = try { PrayerType.valueOf(prayerTypeName) } catch (e: Exception) { PrayerType.FAJR }
        val isPreAlert = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_PRE_ALERT, false)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create high-priority notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات مواقيت الصلاة والأذان المبارك"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tap opens MainActivity directly to the Adhan visual popup
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_ADHAN_PRAYER", prayerType.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isPreAlert) "اقتربت صلاة ${prayerType.arabicName}" else "حان الآن موعد صلاة ${prayerType.arabicName}"
        val content = if (isPreAlert) "باقي 15 دقيقة على الأذان. تهيأ للوضوء والصلاة." else "الله أكبر.. اضغط هنا لفتح شاشة الأذان ودعاء ما بعد الصلاة والوضع الصامت."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(prayerType.ordinal + 100, notification)

        // Reschedule for next prayer
        AlarmScheduler.scheduleNextPrayer(context)
    }
}
