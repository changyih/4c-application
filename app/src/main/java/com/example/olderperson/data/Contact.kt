package com.example.olderperson.data

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 家庭联系人数据类
 */
data class FamilyContact(
    val id: String, // 唯一ID
    val name: String, // 联系人姓名
    val relation: String, // 与用户的关系，如"儿子"、"女儿"等
    val phoneNumber: String, // 电话号码
    val colorHex: String // 颜色值的十六进制表示
) {
    // 将十六进制颜色字符串转换为Color对象
    fun getColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFFFF9800) // 默认橙色
        }
    }
    
    // 将联系人转换为JSON字符串
    fun toJson(): String {
        return Gson().toJson(this)
    }
    
    companion object {
        // 从JSON字符串解析联系人对象
        fun fromJson(json: String): FamilyContact? {
            return try {
                Gson().fromJson(json, FamilyContact::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * 联系人存储相关工具类
 */
object ContactHelper {
    // DataStore联系人列表key
    val CONTACTS_KEY = stringPreferencesKey("family_contacts")
    
    // 默认联系人列表
    val defaultContacts = listOf(
        FamilyContact(
            id = "1",
            name = "李明远",
            relation = "儿子",
            phoneNumber = "15035594053",
            colorHex = "#FF9800"
        ),
        FamilyContact(
            id = "2",
            name = "李安和",
            relation = "女儿",
            phoneNumber = "13687654321",
            colorHex = "#9C27B0"
        ),
        FamilyContact(
            id = "3",
            name = "宋若宁",
            relation = "儿媳",
            phoneNumber = "13598765432",
            colorHex = "#2196F3"
        )
    )
    
    // 将联系人列表转换为JSON字符串
    fun contactsToJson(contacts: List<FamilyContact>): String {
        return Gson().toJson(contacts)
    }
    
    // 从JSON字符串解析联系人列表
    fun contactsFromJson(json: String): List<FamilyContact> {
        return try {
            val type = object : TypeToken<List<FamilyContact>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // 生成唯一ID
    fun generateId(): String {
        return System.currentTimeMillis().toString()
    }
    
    // 生成随机颜色
    fun getRandomColorHex(): String {
        val colors = listOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", 
            "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", 
            "#009688", "#4CAF50", "#8BC34A", "#CDDC39", 
            "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"
        )
        return colors.random()
    }
} 