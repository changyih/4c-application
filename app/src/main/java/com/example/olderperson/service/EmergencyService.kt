package com.example.olderperson.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import com.example.olderperson.data.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmergencyService(private val context: Context) {
    private val _isEmergencyActive = MutableStateFlow(false)
    val isEmergencyActive: StateFlow<Boolean> = _isEmergencyActive

    private val emergencyContacts = listOf(
        "10086", // 示例紧急联系人号码
        "10010"  // 示例紧急联系人号码
    )

    fun activateEmergency(healthData: Map<String, HealthData>? = null) {
        _isEmergencyActive.value = true
        // 发送短信通知紧急联系人
        sendEmergencySMS(healthData)
        // 拨打急救电话
        callEmergencyNumber()
    }

    private fun sendEmergencySMS(healthData: Map<String, HealthData>?) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val smsManager = SmsManager.getDefault()
        val message = buildEmergencyMessage(healthData)
        
        emergencyContacts.forEach { contact ->
            smsManager.sendTextMessage(contact, null, message, null, null)
        }
    }

    private fun buildEmergencyMessage(healthData: Map<String, HealthData>?): String {
        val baseMessage = "紧急求助：老年人需要帮助！"
        val healthInfo = healthData?.let { data ->
            "\n健康数据：\n" + data.entries.joinToString("\n") { (type, value) ->
                "$type: ${value.value}"
            }
        } ?: ""
        return baseMessage + healthInfo
    }

    private fun callEmergencyNumber() {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:120")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun deactivateEmergency() {
        _isEmergencyActive.value = false
    }
} 