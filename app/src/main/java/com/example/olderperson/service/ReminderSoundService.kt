package com.example.olderperson.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.olderperson.CareActivity
import com.example.olderperson.R
import java.util.Locale
import java.util.HashMap

/**
 * 提醒声音服务 - 修复版
 * 使用应用Context初始化TTS，避免Activity生命周期问题
 */
class ReminderSoundService : Service() {
    companion object {
        private const val TAG = "ReminderSoundService"
        private const val NOTIFICATION_ID = 4321
        private const val CHANNEL_ID = "reminder_sound_service_channel"
        
        // 启动服务的意图动作
        const val ACTION_PLAY_REMINDER = "com.example.olderperson.action.PLAY_REMINDER"
        
        // 静态TTS实例，防止被销毁
        private var staticTextToSpeech: TextToSpeech? = null
        private var isTTSInitialized = false
    }
    
    // 用于音频播放的MediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    
    // 唤醒锁
    private var wakeLock: PowerManager.WakeLock? = null
    
    // 当前提醒信息
    private var currentTitle = ""
    private var currentTime = ""
    
    // 提醒选项
    private var notificationEnabled = true
    private var vibrationEnabled = true
    private var alarmSoundEnabled = true
    private var voiceEnabled = true
    
    // Handler用于延迟操作
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "提醒声音服务创建")
        
        // 获取唤醒锁，防止设备休眠
        acquireWakeLock()
        
        // 创建并启动前台服务
        startForeground(NOTIFICATION_ID, createNotification("正在准备日程提醒..."))
        
        // 初始化TTS引擎（如果还没有初始化）
        initTTSEngineIfNeeded()
        
        // 设置最大音量
        setMaxVolume()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "接收到服务启动命令: action=${intent?.action}")
        
        if (intent?.action == ACTION_PLAY_REMINDER) {
            // 获取提醒信息
            val scheduleId = intent.getStringExtra("schedule_id") ?: ""
            currentTitle = intent.getStringExtra("schedule_title") ?: "日程提醒"
            val description = intent.getStringExtra("schedule_desc") ?: ""
            currentTime = intent.getStringExtra("schedule_time") ?: ""
            
            // 获取提醒选项
            notificationEnabled = intent.getBooleanExtra("notification_enabled", true)
            vibrationEnabled = intent.getBooleanExtra("vibration_enabled", true)
            alarmSoundEnabled = intent.getBooleanExtra("alarm_sound_enabled", true)
            voiceEnabled = intent.getBooleanExtra("voice_enabled", true)
            
            Log.d(TAG, "收到提醒数据: title=$currentTitle, time=$currentTime")
            Log.d(TAG, "提醒选项: 通知=$notificationEnabled, 震动=$vibrationEnabled, 闹铃=$alarmSoundEnabled, 语音=$voiceEnabled")
            
            // 更新通知内容
            updateNotification("正在提醒: $currentTitle")
            
            // 根据选项播放闹铃声音
            if (alarmSoundEnabled) {
                playAlarmSound()
            }
            
            // 根据选项播放语音提醒
            if (voiceEnabled) {
                if (isTTSInitialized) {
                    Log.d(TAG, "TTS已准备好，立即开始语音提醒")
                    speakReminder(currentTitle, currentTime)
                } else {
                    Log.d(TAG, "TTS尚未准备好，等待初始化")
                    // 定期检查TTS初始化状态
                    checkTTSAndSpeak(10)
                }
            }
            
            // 60秒后关闭服务
            scheduleServiceStop(60000)
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "提醒声音服务销毁")
        
        // 服务结束时释放MediaPlayer资源
        releaseMediaPlayer()
        
        // 释放唤醒锁
        releaseWakeLock()
        
        // 注意：不要在这里关闭staticTextToSpeech，否则会导致语音播报中断
    }
    
    /**
     * 初始化TTS引擎（如果需要）
     */
    private fun initTTSEngineIfNeeded() {
        if (staticTextToSpeech == null) {
            Log.d(TAG, "开始初始化全局TTS引擎")
            
            try {
                // 使用应用Context初始化TTS
                staticTextToSpeech = TextToSpeech(applicationContext.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        Log.d(TAG, "TTS引擎初始化成功")
                        
                        // 配置TTS
                        staticTextToSpeech?.let { tts ->
                            // 设置语言为中文
                            val result = tts.setLanguage(Locale.CHINESE)
                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                tts.setLanguage(Locale.getDefault())
                                Log.d(TAG, "中文不可用，使用默认语言")
                            } else {
                                Log.d(TAG, "成功设置中文语言")
                            }
                            
                            // 设置语音参数
                            tts.setPitch(1.0f)
                            tts.setSpeechRate(0.8f) // 语速稍慢，适合老年人
                            
                            // 设置进度监听器
                            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                                override fun onStart(utteranceId: String) {
                                    Log.d(TAG, "语音播报开始: $utteranceId")
                                }
                                
                                override fun onDone(utteranceId: String) {
                                    Log.d(TAG, "语音播报完成: $utteranceId")
                                    
                                    // 第一次播报完成后，安排第二次播报
                                    if (utteranceId == "initial_msg") {
                                        handler.postDelayed({
                                            Log.d(TAG, "开始第二次语音播报")
                                            val message = "提醒您，现在是$currentTime，您有日程安排：$currentTitle"
                                            
                                            val params = Bundle()
                                            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                                            
                                            staticTextToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, params, "repeat_msg")
                                        }, 5000) // 5秒后重复播报
                                    }
                                }
                                
                                override fun onError(utteranceId: String) {
                                    Log.e(TAG, "语音播报错误: $utteranceId")
                                }
                            })
                            
                            isTTSInitialized = true
                            
                            // 如果有等待播放的提醒，立即播放
                            if (currentTitle.isNotEmpty() && currentTime.isNotEmpty()) {
                                Log.d(TAG, "TTS初始化完成，播放等待中的提醒")
                                speakReminder(currentTitle, currentTime)
                            }
                        }
                    } else {
                        Log.e(TAG, "TTS初始化失败，状态码: $status")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "TTS引擎初始化异常: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "TTS引擎已存在，不需要重新初始化")
            isTTSInitialized = true
        }
    }
    
    /**
     * 定期检查TTS初始化状态并播放语音
     */
    private fun checkTTSAndSpeak(remainingAttempts: Int) {
        if (remainingAttempts <= 0) {
            Log.e(TAG, "TTS初始化超时，无法播放语音提醒")
            return
        }
        
        handler.postDelayed({
            if (isTTSInitialized) {
                Log.d(TAG, "TTS已初始化，现在开始播放语音提醒")
                speakReminder(currentTitle, currentTime)
            } else {
                Log.d(TAG, "TTS仍未初始化，继续等待... (剩余尝试: $remainingAttempts)")
                checkTTSAndSpeak(remainingAttempts - 1)
            }
        }, 1000) // 每秒检查一次
    }
    
    /**
     * 播放语音提醒
     */
    private fun speakReminder(title: String, time: String) {
        try {
            if (!isTTSInitialized || staticTextToSpeech == null) {
                Log.e(TAG, "TTS引擎未初始化，无法播放语音提醒")
                return
            }
            
            // 强制设置最大音量
            setMaxVolume()
            
            val message = "提醒您，现在是$time，您有日程安排：$title"
            Log.d(TAG, "准备语音播报: $message")
            
            // 设置参数
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            
            // 播放语音
            val result = staticTextToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, params, "initial_msg")
            Log.d(TAG, "语音播报请求已发送，结果: $result, TTS实例: $staticTextToSpeech")
            
            // 检查是否正在播放
            val isSpeaking = staticTextToSpeech?.isSpeaking ?: false
            Log.d(TAG, "TTS是否正在播放: $isSpeaking")
            
        } catch (e: Exception) {
            Log.e(TAG, "语音播报失败: ${e.message}", e)
        }
    }
    
    /**
     * 设置最大音量
     */
    private fun setMaxVolume() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // 设置多媒体音量
            val maxMediaVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            
            if (currentMediaVolume < maxMediaVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMediaVolume, 0)
                Log.d(TAG, "已将媒体音量从 $currentMediaVolume 调整为最大: $maxMediaVolume")
            }
            
            // 设置闹钟音量
            val maxAlarmVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            val currentAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            
            if (currentAlarmVolume < maxAlarmVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarmVolume, 0)
                Log.d(TAG, "已将闹钟音量从 $currentAlarmVolume 调整为最大: $maxAlarmVolume")
            }
        } catch (e: Exception) {
            Log.e(TAG, "设置音量失败: ${e.message}", e)
        }
    }
    
    /**
     * 获取唤醒锁
     */
    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "OlderPerson:ReminderWakeLock"
            )
            wakeLock?.acquire(60000) // 持有60秒
            Log.d(TAG, "已获取唤醒锁")
        } catch (e: Exception) {
            Log.e(TAG, "获取唤醒锁失败: ${e.message}", e)
        }
    }
    
    /**
     * 释放唤醒锁
     */
    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "已释放唤醒锁")
                }
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "释放唤醒锁失败: ${e.message}", e)
        }
    }
    
    /**
     * 播放闹铃声音
     */
    private fun playAlarmSound() {
        try {
            // 释放已有资源
            releaseMediaPlayer()
            
            // 获取系统默认闹钟铃声
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val finalAlarmUri = alarmUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            Log.d(TAG, "准备播放闹铃声音: $finalAlarmUri")
            
            // 创建MediaPlayer实例
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, finalAlarmUri)
                
                // 设置音频属性
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                
                // 设置音量
                setVolume(1.0f, 1.0f)
                
                // 设置为循环播放
                isLooping = true
                
                // 准备并播放
                prepare()
                start()
                
                // 设置播放完成监听
                setOnCompletionListener {
                    Log.d(TAG, "闹铃声音播放完成")
                }
                
                // 设置错误监听
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "闹铃播放错误: $what, $extra")
                    true
                }
            }
            
            Log.d(TAG, "闹铃声音播放开始")
            
            // 设置15秒后停止循环
            handler.postDelayed({
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.isLooping = false
                        Log.d(TAG, "已关闭闹铃循环播放")
                    }
                }
            }, 15000)
            
        } catch (e: Exception) {
            Log.e(TAG, "播放闹铃声音失败: ${e.message}", e)
        }
    }
    
    /**
     * 释放MediaPlayer资源
     */
    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
                Log.d(TAG, "已释放MediaPlayer资源")
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "释放MediaPlayer资源失败: ${e.message}", e)
        }
    }
    
    /**
     * 创建前台服务通知
     */
    private fun createNotification(message: String): Notification {
        // 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "提醒服务",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "用于确保提醒声音和语音能可靠播放"
                // 根据选项启用震动
                enableVibration(vibrationEnabled)
                if (vibrationEnabled) {
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                }
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        
        // 创建点击通知时打开应用的Intent
        val intent = Intent(this, CareActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        // 创建通知
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("日程提醒")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("日程提醒")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(true)
        }
        
        // 根据选项启用震动
        if (vibrationEnabled && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            builder.setVibrate(longArrayOf(0, 500, 250, 500))
        }
        
        return builder.build()
    }
    
    /**
     * 更新通知内容
     */
    private fun updateNotification(message: String) {
        val notification = createNotification(message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * 计划延迟停止服务
     */
    private fun scheduleServiceStop(delayMs: Long) {
        handler.postDelayed({
            Log.d(TAG, "执行计划任务：停止服务")
            stopSelf()
        }, delayMs)
        Log.d(TAG, "已安排${delayMs/1000}秒后停止服务")
    }
    
    /**
     * 静态方法用于关闭TTS资源（应用退出时调用）
     */
    fun shutdownTTS() {
        try {
            staticTextToSpeech?.let {
                it.stop()
                it.shutdown()
                Log.d(TAG, "静态TTS资源已关闭")
                staticTextToSpeech = null
                isTTSInitialized = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "关闭TTS资源时出错: ${e.message}", e)
        }
    }
} 