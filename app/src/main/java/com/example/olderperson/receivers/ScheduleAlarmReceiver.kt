package com.example.olderperson.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.olderperson.CareActivity
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
        private var mediaPlayer: MediaPlayer? = null // 用于播放闹铃声音
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "接收到日程提醒广播")
        
        // 获取日程信息
        val scheduleId = intent.getStringExtra("SCHEDULE_ID") ?: return
        val scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE") ?: "日程提醒"
        val scheduleDesc = intent.getStringExtra("SCHEDULE_DESC") ?: ""
        val scheduleTime = intent.getStringExtra("SCHEDULE_TIME") ?: ""
        
        // 获取提醒选项
        val notificationEnabled = intent.getBooleanExtra("NOTIFICATION_ENABLED", true)
        val vibrationEnabled = intent.getBooleanExtra("VIBRATION_ENABLED", true)
        val alarmSoundEnabled = intent.getBooleanExtra("ALARM_SOUND_ENABLED", true)
        val voiceEnabled = intent.getBooleanExtra("VOICE_ENABLED", true)
        
        Log.d(TAG, "接收到提醒选项: 通知=$notificationEnabled, 震动=$vibrationEnabled, 闹铃=$alarmSoundEnabled, 语音=$voiceEnabled")
        
        // 根据选项显示通知
        if (notificationEnabled) {
            showNotification(context, scheduleId, scheduleTitle, scheduleDesc, scheduleTime, vibrationEnabled)
        }
        
        // 启动前台服务进行语音播报和闹铃提醒
        startReminderSoundService(
            context, 
            scheduleId, 
            scheduleTitle, 
            scheduleDesc, 
            scheduleTime,
            notificationEnabled,
            vibrationEnabled,
            alarmSoundEnabled,
            voiceEnabled
        )
    }
    
    /**
     * 显示通知
     */
    private fun showNotification(
        context: Context,
        scheduleId: String,
        title: String,
        description: String,
        time: String,
        vibrationEnabled: Boolean
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 创建打开App的PendingIntent
        val intent = Intent(context, CareActivity::class.java).apply {
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
        
        // 创建通知构建器
        val notificationContent = "$time - $description"
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 请确保有合适的图标
            .setContentTitle(title)
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        
        // 根据设置决定是否震动
        if (vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 250, 500, 250, 500, 250, 1000)) // 增强震动模式，更强烈、持续时间更长
        }
        
        // 显示通知
        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID + scheduleId.hashCode(), notification)
        Log.d(TAG, "已显示通知: $title, 震动: $vibrationEnabled")
    }
    
    /**
     * 使用语音播报提醒
     */
    private fun speakReminder(context: Context, title: String, time: String) {
        try {
            val tts = TextToSpeechService(context)
            // 确保TTS初始化后才播报
            var initialized = false
            // 设置超时，避免无限等待
            val startTime = System.currentTimeMillis()
            val timeoutMs = 5000 // 5秒超时
            
            // 等待TTS初始化完成
            tts.setOnInitListener {
                // 标记初始化完成
                initialized = true
                // 播报信息
                try {
                    val message = "提醒您，现在是$time，您有日程安排：$title"
                    tts.speak(message)
                    Log.d(TAG, "语音播报: $message")
                } catch (e: Exception) {
                    Log.e(TAG, "语音播报消息时出错: ${e.message}", e)
                }
            }
            
            // 使用循环等待初始化，但设置超时避免无限等待
            while (!initialized && System.currentTimeMillis() - startTime < timeoutMs) {
                Thread.sleep(100)
            }
            
            // 如果超时仍未初始化，则记录日志
            if (!initialized) {
                Log.e(TAG, "TTS初始化超时，无法播报消息")
            }
            
            // 延迟关闭TTS资源，确保有足够时间播报完成
            Thread {
                try {
                    // 等待足够时间让TTS播报完成
                    Thread.sleep(10000) // 10秒
                    tts.shutdown()
                    Log.d(TAG, "TTS资源已释放")
                } catch (e: Exception) {
                    Log.e(TAG, "关闭TTS资源时出错: ${e.message}", e)
                }
            }.start()
        } catch (e: Exception) {
            Log.e(TAG, "语音播报失败: ${e.message}", e)
        }
    }
    
    /**
     * 播放闹铃声音
     */
    private fun playAlarmSound(context: Context) {
        try {
            // 停止之前可能正在播放的闹铃
            stopAlarmSound()
            
            // 获取系统默认闹钟铃声
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            
            // 如果没有闹钟铃声，使用通知铃声
            val finalAlarmUri = if (alarmUri == null) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                alarmUri
            }
            
            // 创建MediaPlayer并设置音频属性
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, finalAlarmUri)
                
                // 设置音频属性（适用于高版本Android）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                } else {
                    // 低版本Android使用旧的设置方法
                    @Suppress("DEPRECATION")
                    setAudioStreamType(android.media.AudioManager.STREAM_ALARM)
                }
                
                // 设置循环播放
                isLooping = true
                
                prepare()
                start()
            }
            
            Log.d(TAG, "开始播放闹铃声音")
            
            // 设置闹铃持续时间（这里设置为30秒后自动停止）
            Handler(Looper.getMainLooper()).postDelayed({
                stopAlarmSound()
                Log.d(TAG, "闹铃声音已自动停止")
            }, 30000) // 30秒
            
        } catch (e: Exception) {
            Log.e(TAG, "播放闹铃声音失败: ${e.message}", e)
        }
    }
    
    /**
     * 停止闹铃声音
     */
    private fun stopAlarmSound() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                Log.d(TAG, "闹铃声音已停止")
            } catch (e: Exception) {
                Log.e(TAG, "停止闹铃声音时出错: ${e.message}", e)
            }
        }
        mediaPlayer = null
    }

    private fun startReminderSoundService(
        context: Context,
        scheduleId: String,
        title: String,
        description: String,
        time: String,
        notificationEnabled: Boolean,
        vibrationEnabled: Boolean,
        alarmSoundEnabled: Boolean,
        voiceEnabled: Boolean
    ) {
        try {
            Log.d(TAG, "准备启动ReminderSoundService: title=$title, time=$time")
            
            // 创建服务Intent
            val serviceIntent = Intent(context, com.example.olderperson.service.ReminderSoundService::class.java).apply {
                action = com.example.olderperson.service.ReminderSoundService.ACTION_PLAY_REMINDER
                
                // 添加额外数据
                putExtra("schedule_id", scheduleId)
                putExtra("schedule_title", title)
                putExtra("schedule_desc", description)
                putExtra("schedule_time", time)
                
                // 添加提醒选项
                putExtra("notification_enabled", notificationEnabled)
                putExtra("vibration_enabled", vibrationEnabled)
                putExtra("alarm_sound_enabled", alarmSoundEnabled)
                putExtra("voice_enabled", voiceEnabled)
                
                // 设置包名确保找到正确的服务
                setPackage(context.packageName)
            }
            
            // 以前台服务方式启动
            Log.d(TAG, "调用startForegroundService/startService")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            
            Log.d(TAG, "服务启动请求发送成功")
        } catch (e: Exception) {
            Log.e(TAG, "启动提醒声音服务失败: ${e.message}", e)
            
            // 如果服务启动失败，回退到旧的方式
            Log.d(TAG, "回退到本地提醒方式")
            if (alarmSoundEnabled) {
                playAlarmSound(context)
            }
            if (voiceEnabled) {
                speakReminder(context, title, time)
            }
        }
    }
} 