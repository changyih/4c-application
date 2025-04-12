package com.example.olderperson.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * 电话服务类，提供拨打电话功能
 */
class PhoneCallService(private val context: Context) {
    
    /**
     * 检查是否有拨打电话的权限
     */
    fun hasCallPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 拨打电话
     * @param phoneNumber 电话号码
     * @return 是否成功发起拨号
     */
    fun makePhoneCall(phoneNumber: String): Boolean {
        return try {
            if (hasCallPhonePermission()) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                Log.d("PhoneCallService", "正在拨打电话: $phoneNumber")
                true
            } else {
                Log.e("PhoneCallService", "没有拨打电话的权限")
                false
            }
        } catch (e: Exception) {
            Log.e("PhoneCallService", "拨打电话失败: ${e.message}")
            false
        }
    }
} 