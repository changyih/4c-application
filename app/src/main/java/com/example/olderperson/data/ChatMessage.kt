package com.example.olderperson.data

import android.graphics.Bitmap
import android.net.Uri

/**
 * 聊天消息数据类
 * 支持文本和图像消息
 */
data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val messageType: MessageType = MessageType.TEXT,
    val imageUri: Uri? = null,
    val imageBitmap: Bitmap? = null
)

/**
 * 消息类型枚举
 */
enum class MessageType {
    TEXT,       // 纯文本消息
    IMAGE,      // 图像消息（带可选文本）
    VOICE       // 语音消息（将来可能支持）
} 