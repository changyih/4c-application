package com.example.olderperson.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.olderperson.MainActivity
import com.example.olderperson.R
import com.example.olderperson.service.TextToSpeechService

/**
 * 日程提醒广播接收器
 * 接收闹钟管理器发送的广播，并显示通知
 */
class ScheduleAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ScheduleAlarmReceiver"
        private const val NOTIFICATION_ID = 1000
        private const val CHANNEL_ID = "schedule_notification_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "接收到日程提醒广播")
        
        // 获取日程信息
        val scheduleId = intent.getStringExtra("SCHEDULE_ID") ?: return
        val scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE") ?: "日程提醒"
        val scheduleDesc = intent.getStringExtra("SCHEDULE_DESC") ?: ""
        val scheduleTime = intent.getStringExtra("SCHEDULE_TIME") ?: ""
        
        // 创建通知
        showNotification(context, scheduleId, scheduleTitle, scheduleDesc, scheduleTime)
        
        // 使用语音播报提醒
        speakReminder(context, scheduleTitle, scheduleTime)
    }
    
    /**
     * 显示通知
     */
    private fun showNotification(
        context: Context,
        scheduleId: String,
        title: String,
        description: String,
        time: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 创建打开App的PendingIntent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SCHEDULE_ID", scheduleId)
        }
        
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            pendingIntentFlag
        )
        
        // 创建通知
        val notificationContent = "$time - $description"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 请确保有合适的图标
            .setContentTitle(title)
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        // 显示通知
        notificationManager.notify(NOTIFICATION_ID + scheduleId.hashCode(), notification)
        Log.d(TAG, "已显示通知: $title")
    }
    
    /**
     * 使用语音播报提醒
     */
    private fun speakReminder(context: Context, title: String, time: String) {
        try {
            val tts = TextToSpeechService(context)
            // 等待TTS初始化完成
            tts.setOnInitListener {
                // 播报信息
                val message = "提醒您，现在是$time，您有日程安排：$title"
                tts.speak(message)
                Log.d(TAG, "语音播报: $message")
            }
        } catch (e: Exception) {
            Log.e(TAG, "语音播报失败: ${e.message}")
        }
    }
} 