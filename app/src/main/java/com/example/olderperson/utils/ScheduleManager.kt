package com.example.olderperson.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日程安排管理类，用于管理老年人的日程安排
 */
class ScheduleManager(private val context: Context) {

    // 日程安排项数据类
    data class ScheduleItem(
        val id: String = UUID.randomUUID().toString(),
        var time: String,
        var title: String,
        var description: String,
        var reminderEnabled: Boolean = true, // 添加是否开启提醒的标志
        var notificationEnabled: Boolean = true, // 是否启用通知提醒
        var vibrationEnabled: Boolean = true, // 是否启用震动提醒
        var alarmSoundEnabled: Boolean = true, // 是否启用闹铃声音
        var voiceEnabled: Boolean = true // 是否启用语音播报
    )

    // SharedPreferences 键名
    private val PREFS_NAME = "schedule_prefs"
    private val SCHEDULE_KEY = "schedule_items"

    // 提醒相关常量
    private val NOTIFICATION_CHANNEL_ID = "schedule_notification_channel"
    private val NOTIFICATION_CHANNEL_NAME = "日程提醒"
    
    // 通知管理器
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    // 闹钟管理器
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    // 获取 SharedPreferences
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Gson 实例用于序列化和反序列化
    private val gson = Gson()
    
    init {
        // 创建通知渠道
        createNotificationChannel()
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "日程提醒通知"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500, 250, 1000) // 增强震动模式，更强烈且持续时间更长
                
                // 设置通知声音为系统闹钟声音
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                setSound(alarmSound, android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 获取所有日程安排项
     */
    fun getAllScheduleItems(): List<ScheduleItem> {
        val json = prefs.getString(SCHEDULE_KEY, null) ?: return getDefaultScheduleItems()
        val type = object : TypeToken<List<ScheduleItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            getDefaultScheduleItems()
        }
    }

    /**
     * 保存所有日程安排项
     */
    private fun saveAllScheduleItems(items: List<ScheduleItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(SCHEDULE_KEY, json).apply()
    }

    /**
     * 添加一个新的日程安排项
     */
    fun addScheduleItem(item: ScheduleItem) {
        val items = getAllScheduleItems().toMutableList()
        items.add(item)
        // 按时间排序
        items.sortBy { it.time }
        saveAllScheduleItems(items)
        
        // 如果启用了提醒，设置闹钟
        if (item.reminderEnabled) {
            setScheduleReminder(item)
        }
    }

    /**
     * 更新现有的日程安排项
     */
    fun updateScheduleItem(item: ScheduleItem) {
        val items = getAllScheduleItems().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            // 先取消旧的提醒
            cancelScheduleReminder(items[index])
            
            items[index] = item
            // 按时间排序
            items.sortBy { it.time }
            saveAllScheduleItems(items)
            
            // 设置新的提醒
            if (item.reminderEnabled) {
                setScheduleReminder(item)
            }
        }
    }

    /**
     * 删除日程安排项
     */
    fun deleteScheduleItem(id: String) {
        val items = getAllScheduleItems().toMutableList()
        val itemToRemove = items.find { it.id == id }
        
        // 如果找到了要删除的项目，先取消它的提醒
        itemToRemove?.let { cancelScheduleReminder(it) }
        
        items.removeIf { it.id == id }
        saveAllScheduleItems(items)
    }

    /**
     * 清空所有日程安排项
     */
    fun clearAllScheduleItems() {
        // 取消所有提醒
        val items = getAllScheduleItems()
        items.forEach { cancelScheduleReminder(it) }
        
        prefs.edit().remove(SCHEDULE_KEY).apply()
    }

    /**
     * 设置日程提醒
     */
    fun setScheduleReminder(item: ScheduleItem) {
        try {
            // 检查Android 12及以上版本的精确闹钟权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("ScheduleManager", "没有精确闹钟权限，无法设置提醒")
                    return
                }
            }

            // 解析时间字符串（格式为 HH:mm）
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val scheduledTime = try {
                val parsedTime = timeFormat.parse(item.time)
                val calendar = Calendar.getInstance()
                val now = Calendar.getInstance()
                
                // 设置小时和分钟
                calendar.time = parsedTime ?: return
                
                // 使用今天的日期
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
                calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
                calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                
                // 如果时间已经过去，设置为明天
                if (calendar.before(now)) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                
                calendar.timeInMillis
            } catch (e: Exception) {
                Log.e("ScheduleManager", "解析时间失败: ${e.message}")
                return
            }
            
            // 创建提醒意图
            try {
                val receiverClass = Class.forName("com.example.olderperson.receivers.ScheduleAlarmReceiver")
                val intent = Intent(context, receiverClass).apply {
                    putExtra("SCHEDULE_ID", item.id)
                    putExtra("SCHEDULE_TITLE", item.title)
                    putExtra("SCHEDULE_DESC", item.description)
                    putExtra("SCHEDULE_TIME", item.time)
                    
                    // 添加提醒选项
                    putExtra("NOTIFICATION_ENABLED", item.notificationEnabled)
                    putExtra("VIBRATION_ENABLED", item.vibrationEnabled)
                    putExtra("ALARM_SOUND_ENABLED", item.alarmSoundEnabled)
                    putExtra("VOICE_ENABLED", item.voiceEnabled)
                    
                    // 添加一个备用操作
                    action = "com.example.olderperson.ACTION_SCHEDULE_ALARM"
                }
                
                val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                
                // 需要使用唯一的requestCode，这里使用item.id的hashCode
                val requestCode = item.id.hashCode()
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    pendingIntentFlag
                )
                
                // 设置精确闹钟
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            scheduledTime,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            scheduledTime,
                            pendingIntent
                        )
                    }
                    
                    Log.d("ScheduleManager", "设置提醒成功: ${item.title}, 时间: ${item.time}, ID: ${item.id}")
                    Log.d("ScheduleManager", "提醒选项: 通知=${item.notificationEnabled}, 震动=${item.vibrationEnabled}, 闹铃=${item.alarmSoundEnabled}, 语音=${item.voiceEnabled}")
                } catch (e: Exception) {
                    Log.e("ScheduleManager", "设置闹钟失败: ${e.message}", e)
                }
            } catch (e: Exception) {
                Log.e("ScheduleManager", "创建提醒意图失败: ${e.message}", e)
            }
        } catch (e: Exception) {
            Log.e("ScheduleManager", "设置提醒过程中发生错误: ${e.message}", e)
        }
    }
    
    /**
     * 取消日程提醒
     */
    fun cancelScheduleReminder(item: ScheduleItem) {
        try {
            val receiverClass = Class.forName("com.example.olderperson.receivers.ScheduleAlarmReceiver")
            val intent = Intent(context, receiverClass)
            
            val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_NO_CREATE
            }
            
            // 需要使用相同的requestCode
            val requestCode = item.id.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                pendingIntentFlag
            )
            
            // 如果pendingIntent存在，取消它
            pendingIntent?.let {
                try {
                    alarmManager.cancel(it)
                    it.cancel()
                    Log.d("ScheduleManager", "取消提醒成功: ${item.title}, ID: ${item.id}")
                } catch (e: Exception) {
                    Log.e("ScheduleManager", "取消提醒失败: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            Log.e("ScheduleManager", "取消提醒过程中发生错误: ${e.message}", e)
        }
    }
    
    /**
     * 重新设置所有提醒（例如在应用启动时或设备重启后）
     */
    fun resetAllReminders() {
        val items = getAllScheduleItems()
        items.forEach { item ->
            if (item.reminderEnabled) {
                // 先取消已有的提醒
                cancelScheduleReminder(item)
                // 重新设置提醒
                setScheduleReminder(item)
            }
        }
        Log.d("ScheduleManager", "重置所有提醒: ${items.size}个日程")
    }

    /**
     * 获取默认的日程安排项（如果没有保存的数据）
     */
    private fun getDefaultScheduleItems(): List<ScheduleItem> {
        return listOf(
            ScheduleItem(
                id = "1",
                time = "08:00",
                title = "晨间服药",
                description = "降压药 1片，维生素 1片",
                reminderEnabled = true,
                notificationEnabled = true,
                vibrationEnabled = true,
                alarmSoundEnabled = true,
                voiceEnabled = true
            ),
            ScheduleItem(
                id = "2",
                time = "10:30",
                title = "心脏科复诊",
                description = "市第一人民医院",
                reminderEnabled = true,
                notificationEnabled = true,
                vibrationEnabled = true,
                alarmSoundEnabled = true,
                voiceEnabled = true
            )
        )
    }

    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: ScheduleManager? = null

        fun getInstance(context: Context): ScheduleManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ScheduleManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
} 