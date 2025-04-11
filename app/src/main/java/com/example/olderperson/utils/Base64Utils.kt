package com.example.olderperson.utils

import android.util.Base64
import android.util.Log

/**
 * Base64处理工具类
 */
object Base64Utils {
    private const val TAG = "Base64Utils"
    
    /**
     * 清理Base64字符串，移除可能存在的data URL前缀和填充字符
     */
    fun cleanBase64(base64String: String): String {
        return try {
            // 移除data:image/xxx;base64,前缀
            val cleanedString = if (base64String.contains(",")) {
                base64String.substring(base64String.indexOf(",") + 1)
            } else {
                base64String
            }
            
            // 移除可能的换行符、空格等
            val noWhitespace = cleanedString.replace("\\s".toRegex(), "")
            
            Log.d(TAG, "Base64 string cleaned, original size: ${base64String.length}, new size: ${noWhitespace.length}")
            
            noWhitespace
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning Base64 string", e)
            base64String // 出错时返回原字符串
        }
    }
    
    /**
     * 验证Base64字符串是否有效
     */
    fun isValidBase64(base64String: String): Boolean {
        return try {
            val cleaned = cleanBase64(base64String)
            // 尝试解码看是否会出错
            Base64.decode(cleaned, Base64.DEFAULT)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Invalid Base64 string", e)
            false
        }
    }
    
    /**
     * 获取Base64字符串的大致大小（字节）
     */
    fun getApproximateSize(base64String: String): Int {
        val cleaned = cleanBase64(base64String)
        // Base64编码会将3字节数据编码为4个字符，所以解码后大小约为原始字符串长度的3/4
        return (cleaned.length * 0.75).toInt()
    }
} 