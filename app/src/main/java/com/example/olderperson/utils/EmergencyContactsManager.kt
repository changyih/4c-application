package com.example.olderperson.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * 紧急联系人管理类
 * 用于存储和获取紧急联系人信息
 */
object EmergencyContactsManager {
    private const val PREFS_NAME = "emergency_contacts"
    private const val KEY_NAME = "emergency_contact_name"
    private const val KEY_PHONE = "emergency_contact_phone"
    private const val KEY_RELATIONSHIP = "emergency_contact_relationship"
    
    /**
     * 紧急联系人数据类
     */
    data class EmergencyContact(
        val name: String,
        val phone: String,
        val relationship: String
    )
    
    /**
     * 获取紧急联系人
     * @return 紧急联系人信息，如果未设置则返回null
     */
    fun getEmergencyContact(context: Context): EmergencyContact? {
        val prefs = getPrefs(context)
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val phone = prefs.getString(KEY_PHONE, null) ?: return null
        val relationship = prefs.getString(KEY_RELATIONSHIP, "") ?: ""
        
        return EmergencyContact(name, phone, relationship)
    }
    
    /**
     * 保存紧急联系人
     * @param contact 要保存的紧急联系人信息
     */
    fun saveEmergencyContact(context: Context, contact: EmergencyContact) {
        getPrefs(context).edit {
            putString(KEY_NAME, contact.name)
            putString(KEY_PHONE, contact.phone)
            putString(KEY_RELATIONSHIP, contact.relationship)
        }
    }
    
    /**
     * 清除紧急联系人
     */
    fun clearEmergencyContact(context: Context) {
        getPrefs(context).edit {
            remove(KEY_NAME)
            remove(KEY_PHONE)
            remove(KEY_RELATIONSHIP)
        }
    }
    
    /**
     * 获取SharedPreferences实例
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
} 