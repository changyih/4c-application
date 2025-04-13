package com.example.olderperson.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.olderperson.utils.ScheduleManager

/**
 * 系统启动完成广播接收器
 * 用于在设备重启后重置所有闹钟提醒
 */
class BootCompleteReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootCompleteReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "设备启动完成，重置所有日程提醒")
            
            // 获取ScheduleManager实例并重置所有提醒
            try {
                val scheduleManager = ScheduleManager.getInstance(context)
                scheduleManager.resetAllReminders()
                Log.d(TAG, "重置日程提醒成功")
            } catch (e: Exception) {
                Log.e(TAG, "重置日程提醒失败: ${e.message}")
            }
        }
    }
} 