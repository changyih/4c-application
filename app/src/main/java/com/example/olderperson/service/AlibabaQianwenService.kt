package com.example.olderperson.service

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * 阿里云通义千问-VL-MAX服务类
 * 负责与阿里云通义千问API交互，实现多模态对话功能
 */
class AlibabaQianwenService(private val context: Context) {

    private val TAG = "AlibabaQianwenService"
    
    // API配置
    private val API_KEY = "sk-e3df1048a51e4a36af6d2d00f5ad7b86" // 需要在使用前配置
    
    // DashScope 文本生成API端点 (应用调用)
    private val TEXT_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
    
    // DashScope 多模态生成API端点
    private val VISION_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation"
    
    // 模型名称
    private val TEXT_MODEL_NAME = "qwen2-7b-instruct-ft-202504101651-dcd6"  // 微调后的模型
    private val VISION_MODEL_NAME = "qwen-vl-max" // 视觉大模型
    
    // 初始化OkHttpClient
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS) // 增加超时时间
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // 对话状态
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    
    /**
     * 发送文本消息到通义千问模型
     * @param prompt 用户文本消息
     * @return 模型回复的文本
     */
    suspend fun sendTextMessage(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                _isProcessing.value = true
                
                // 使用messages格式而非简化prompt格式
                val requestBody = """
                {
                    "model": "$TEXT_MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "system",
                                "content": "你是慧龄智慧助手，专门为老年人提供生活帮助和情感陪伴的智能助手"
                            },
                            {
                                "role": "user",
                                "content": "${prompt.replace("\"", "\\\"").replace("\n", "\\n")}"
                            }
                        ]
                    },
                    "parameters": {
                        "result_format": "message"
                    }
                }
                """.trimIndent()
                
                Log.d(TAG, "文本请求体: $requestBody")
                
                val response = sendRequest(TEXT_API_URL, requestBody)
                Log.d(TAG, "文本原始响应: $response")
                
                return@withContext parseResponse(response)
            } catch (e: Exception) {
                Log.e(TAG, "发送文本消息失败: ${e.message}")
                return@withContext "抱歉，我遇到了问题，无法处理您的请求。错误信息: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * 发送图像消息到通义千问视觉模型
     * @param prompt 用户文本消息
     * @param imageUri 图像的URI
     * @return 模型回复的文本
     */
    suspend fun sendImageMessage(prompt: String, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                _isProcessing.value = true
                
                // 将图像转换为Base64
                val imageStream = context.contentResolver.openInputStream(imageUri)
                
                // 检查文件大小
                val fileSize = imageStream?.available() ?: 0
                if (fileSize > 10 * 1024 * 1024) { // 10MB限制
                    return@withContext "抱歉，图片太大了，请选择小于10MB的图片。"
                }
                
                // 获取MIME类型
                val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                
                val base64Image = encodeImageToBase64WithPrefix(imageStream!!, mimeType)
                
                // 使用多模态格式构建请求
                val requestBody = """
                {
                    "model": "$VISION_MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "system",
                                "content": [
                                    {"text": "你是慧龄智慧助手，专门为老年人提供生活帮助和情感陪伴的智能助手。当解释图像时，请用简单易懂的语言，尽量详细描述图像内容。"}
                                ]
                            },
                            {
                                "role": "user",
                                "content": [
                                    {"text": "${prompt.replace("\"", "\\\"").replace("\n", "\\n")}"},
                                    {"image": "$base64Image"}
                                ]
                            }
                        ]
                    },
                    "parameters": {
                        "result_format": "message"
                    }
                }
                """.trimIndent()
                
                Log.d(TAG, "图像请求体: 包含Base64图像的请求")
                
                val response = sendRequest(VISION_API_URL, requestBody)
                Log.d(TAG, "图像原始响应: $response")
                
                return@withContext parseResponse(response)
            } catch (e: Exception) {
                Log.e(TAG, "发送图像消息失败: ${e.message}")
                return@withContext "抱歉，我无法处理这张图片。错误信息: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * 发送图像消息到通义千问视觉模型
     * @param prompt 用户文本消息
     * @param bitmap 图像的Bitmap对象
     * @return 模型回复的文本
     */
    suspend fun sendImageMessage(prompt: String, bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            try {
                _isProcessing.value = true
                
                // 检查图像大小
                if (bitmap.width * bitmap.height > 4096 * 4096) {
                    // 如果图像太大，进行压缩
                    val scaleFactor = 4096.0 / Math.max(bitmap.width, bitmap.height)
                    val resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scaleFactor).toInt(),
                        (bitmap.height * scaleFactor).toInt(),
                        true
                    )
                    return@withContext sendImageMessage(prompt, resizedBitmap)
                }
                
                // 将Bitmap转换为Base64（带前缀）
                val base64Image = encodeImageToBase64WithPrefix(bitmap, "image/jpeg")
                
                // 使用多模态格式构建请求
                val requestBody = """
                {
                    "model": "$VISION_MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "system",
                                "content": [
                                    {"text": "你是慧龄智慧助手，专门为老年人提供生活帮助和情感陪伴的智能助手。当解释图像时，请用简单易懂的语言，尽量详细描述图像内容。"}
                                ]
                            },
                            {
                                "role": "user",
                                "content": [
                                    {"text": "${prompt.replace("\"", "\\\"").replace("\n", "\\n")}"},
                                    {"image": "$base64Image"}
                                ]
                            }
                        ]
                    },
                    "parameters": {
                        "result_format": "message"
                    }
                }
                """.trimIndent()
                
                Log.d(TAG, "图像请求体: 包含Base64图像的请求")
                
                val response = sendRequest(VISION_API_URL, requestBody)
                Log.d(TAG, "图像原始响应: $response")
                
                return@withContext parseResponse(response)
            } catch (e: Exception) {
                Log.e(TAG, "发送图像消息失败: ${e.message}")
                return@withContext "抱歉，我无法处理这张图片。错误信息: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * 解析API响应
     * @param response 原始响应字符串
     * @return 处理后的文本
     */
    private fun parseResponse(response: String): String {
        try {
            // 使用原生的JSONObject解析，避免fastjson的问题
            val jsonObject = JSONObject(response)
            
            // 优先检查错误信息
            if (jsonObject.has("code") || jsonObject.has("message")) {
                val code = if (jsonObject.has("code")) jsonObject.getString("code") else "未知"
                val message = if (jsonObject.has("message")) jsonObject.getString("message") else "未知错误"
                throw Exception("API错误：$code, $message")
            }
            
            // 如果是旧版API格式(output.text)
            if (jsonObject.has("output") && jsonObject.getJSONObject("output").has("text")) {
                return jsonObject.getJSONObject("output").getString("text")
            }
            
            // 如果是新版API格式(output.choices[0].message.content)
            if (jsonObject.has("output") && jsonObject.getJSONObject("output").has("choices")) {
                val choices = jsonObject.getJSONObject("output").getJSONArray("choices")
                if (choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    
                    // 如果是基本的message.content格式
                    if (firstChoice.has("message") && firstChoice.getJSONObject("message").has("content")) {
                        val message = firstChoice.getJSONObject("message")
                        val content = message.opt("content")
                        
                        // 处理content是字符串的情况
                        if (content is String) {
                            return content
                        }
                        
                        // 处理content是JSONArray的情况
                        if (message.get("content") is JSONObject || content.toString().startsWith("[")) {
                            val contentArray = message.getJSONArray("content")
                            if (contentArray.length() > 0) {
                                // 处理数组中的文本类型
                                for (i in 0 until contentArray.length()) {
                                    val contentItem = contentArray.getJSONObject(i)
                                    if (contentItem.has("text")) {
                                        return contentItem.getString("text")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 尝试直接解析响应体中的简单文本
            if (response.contains("Hello") || response.contains("你好")) {
                return response.replace("\"", "").trim()
            }
            
            throw Exception("无法解析API响应")
        } catch (e: Exception) {
            Log.e(TAG, "解析JSON失败: ${e.message}")
            
            // 如果根本不是JSON格式(而是纯文本)，直接返回
            if (!response.trim().startsWith("{") && !response.trim().startsWith("[")) {
                return response.trim()
            }
            
            throw e
        }
    }
    
    /**
     * 发送请求到阿里云API
     * @param url API端点
     * @param requestBody 请求体
     * @return 响应体
     */
    private fun sendRequest(url: String, requestBody: String): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()
        
        try {
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "无响应内容"
                Log.e(TAG, "API请求失败: ${response.code} ${response.message}\n$errorBody")
                
                // 记录更详细的请求信息以便调试（但不记录完整Base64图像）
                val sanitizedRequestBody = if (url == VISION_API_URL) {
                    "多模态请求(图像数据已省略)" 
                } else {
                    requestBody
                }
                Log.e(TAG, "请求详情 - URL: $url\n请求体: $sanitizedRequestBody")
                
                // 尝试从错误响应中提取信息
                try {
                    val jsonError = JSONObject(errorBody)
                    if (jsonError.has("message")) {
                        val errorMessage = jsonError.getString("message")
                        Log.e(TAG, "API错误消息: $errorMessage")
                        
                        // 根据常见错误提供更具体的提示
                        if (errorMessage.contains("token") || errorMessage.contains("API")) {
                            throw Exception("API密钥错误或未授权: $errorMessage")
                        } else if (errorMessage.contains("quota") || errorMessage.contains("limit")) {
                            throw Exception("API配额超限: $errorMessage")
                        } else if (errorMessage.contains("image") || errorMessage.contains("format")) {
                            throw Exception("图像格式或大小不支持: $errorMessage")
                        } else {
                            throw Exception("API错误: $errorMessage")
                        }
                    }
                } catch (e: Exception) {
                    // 如果不是JSON格式的错误或解析失败，则提供HTTP状态码错误
                    if (response.code == 400) {
                        throw Exception("请求格式错误(400)，请检查图片格式和大小是否符合要求")
                    } else if (response.code == 401) {
                        throw Exception("API密钥未授权(401)，请检查密钥是否有效")
                    } else if (response.code == 403) {
                        throw Exception("无权限访问(403)，请确认模型权限")
                    } else if (response.code == 429) {
                        throw Exception("请求频率超限(429)，请稍后再试")
                    } else {
                        throw Exception("API请求失败: ${response.code} ${response.message}")
                    }
                }
            }
            
            return response.body?.string() ?: throw Exception("响应体为空")
        } catch (e: Exception) {
            Log.e(TAG, "网络请求异常: ${e.message}")
            throw e
        }
    }
    
    // 添加新方法 - 带格式前缀的Base64编码方法
    /**
     * 将InputStream编码为带前缀的Base64
     * @param inputStream 图像的InputStream
     * @param mimeType 图像的MIME类型
     * @return 带前缀的Base64编码字符串
     */
    private fun encodeImageToBase64WithPrefix(inputStream: InputStream, mimeType: String): String {
        val bytes = inputStream.readBytes()
        inputStream.close()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:$mimeType;base64,$base64"
    }
    
    /**
     * 将Bitmap编码为带前缀的Base64
     * @param bitmap 图像的Bitmap
     * @param mimeType 图像的MIME类型
     * @return 带前缀的Base64编码字符串
     */
    private fun encodeImageToBase64WithPrefix(bitmap: Bitmap, mimeType: String): String {
        val outputStream = ByteArrayOutputStream()
        // 使用较高质量的JPEG压缩以确保图像质量
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val bytes = outputStream.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return "data:$mimeType;base64,$base64"
    }
    
    // 保留原方法以向后兼容
    /**
     * 将InputStream编码为Base64
     * @param inputStream 图像的InputStream
     * @return Base64编码的字符串
     */
    private fun encodeImageToBase64(inputStream: InputStream): String {
        val bytes = inputStream.readBytes()
        inputStream.close()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * 将Bitmap编码为Base64
     * @param bitmap 图像的Bitmap
     * @return Base64编码的字符串
     */
    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
} 