package com.example.olderperson.service

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.alibaba.fastjson2.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
    private val API_SECRET = "" // 需要在使用前配置
    private val BASE_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation"
    
    // 模型名称
    private val MODEL_NAME = "qwen-vl-max"
    
    // 初始化OkHttpClient
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
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
                
                val requestBody = """
                {
                    "model": "$MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "user",
                                "content": [
                                    {
                                        "text": "$prompt"
                                    }
                                ]
                            }
                        ]
                    }
                }
                """.trimIndent()
                
                val response = sendRequest(requestBody)
                
                // 解析响应
                val jsonObject = JSON.parseObject(response)
                val output = jsonObject.getJSONObject("output")
                val choices = output.getJSONArray("choices")
                val message = choices.getJSONObject(0).getJSONObject("message")
                val content = message.getJSONArray("content")
                val text = content.getJSONObject(0).getString("text")
                
                return@withContext text
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
                val base64Image = encodeImageToBase64(imageStream!!)
                
                val requestBody = """
                {
                    "model": "$MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "user",
                                "content": [
                                    {
                                        "text": "$prompt"
                                    },
                                    {
                                        "image": "$base64Image"
                                    }
                                ]
                            }
                        ]
                    }
                }
                """.trimIndent()
                
                val response = sendRequest(requestBody)
                
                // 解析响应
                val jsonObject = JSON.parseObject(response)
                val output = jsonObject.getJSONObject("output")
                val choices = output.getJSONArray("choices")
                val message = choices.getJSONObject(0).getJSONObject("message")
                val content = message.getJSONArray("content")
                val text = content.getJSONObject(0).getString("text")
                
                return@withContext text
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
                
                // 将Bitmap转换为Base64
                val base64Image = encodeImageToBase64(bitmap)
                
                val requestBody = """
                {
                    "model": "$MODEL_NAME",
                    "input": {
                        "messages": [
                            {
                                "role": "user",
                                "content": [
                                    {
                                        "text": "$prompt"
                                    },
                                    {
                                        "image": "$base64Image"
                                    }
                                ]
                            }
                        ]
                    }
                }
                """.trimIndent()
                
                val response = sendRequest(requestBody)
                
                // 解析响应
                val jsonObject = JSON.parseObject(response)
                val output = jsonObject.getJSONObject("output")
                val choices = output.getJSONArray("choices")
                val message = choices.getJSONObject(0).getJSONObject("message")
                val content = message.getJSONArray("content")
                val text = content.getJSONObject(0).getString("text")
                
                return@withContext text
            } catch (e: Exception) {
                Log.e(TAG, "发送图像消息失败: ${e.message}")
                return@withContext "抱歉，我无法处理这张图片。错误信息: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    /**
     * 发送请求到阿里云API
     * @param requestBody 请求体
     * @return 响应体
     */
    private fun sendRequest(requestBody: String): String {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBody.toRequestBody(mediaType)
        
        val request = Request.Builder()
            .url(BASE_URL)
            .post(body)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()
        
        val response = client.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw Exception("API请求失败: ${response.code} ${response.message}")
        }
        
        return response.body?.string() ?: throw Exception("响应体为空")
    }
    
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