package com.example.olderperson.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
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
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * 阿里云语音合成服务
 * 基于阿里云语音合成CosyVoice实现更自然高质量的语音
 */
class AlibabaTextToSpeechService(private val context: Context) {
    private val TAG = "AlibabaTextToSpeechService"
    
    // API配置 - 使用与AlibabaQianwenService相同的密钥
    private val API_KEY = "sk-e3df1048a51e4a36af6d2d00f5ad7b86" 
    
    // 阿里云语音合成API端点
    private val TTS_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2audio/generation"
    
    // 音色和模型
    private val MODEL_NAME = "cosyvoice-v2" // 使用更新的CosyVoice 2.0模型
    private val VOICE_NAME = "longxiaoxia_v2" // 女声：龙小霞 - 适合老年人的清晰自然女声
    
    // 语音合成参数
    private var speechRate = 0.8f // 语速 0.5-1.5
    private var volume = 1.0f // 音量 0-1.0
    private var isEnabled = true // 是否启用
    
    // 媒体播放器
    private var mediaPlayer: MediaPlayer? = null
    
    // 状态管理
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking
    
    // 初始化OkHttpClient
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    init {
        // 初始化MediaPlayer
        setupMediaPlayer()
    }
    
    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            
            setOnCompletionListener {
                _isSpeaking.value = false
                Log.d(TAG, "播放完成")
            }
            
            setOnErrorListener { _, what, extra ->
                _isSpeaking.value = false
                Log.e(TAG, "播放错误: $what, $extra")
                true
            }
        }
    }
    
    /**
     * 使用阿里云语音合成API合成语音并播放
     * @param text 要合成的文本
     */
    suspend fun speak(text: String) {
        if (!isEnabled || text.isBlank()) {
            Log.d(TAG, "语音功能已禁用或文本为空")
            return
        }
        
        withContext(Dispatchers.IO) {
            try {
                // 停止当前播放
                stop()
                
                // 构建请求体
                val requestBody = """
                {
                    "model": "$MODEL_NAME",
                    "input": {
                        "text": "${text.replace("\"", "\\\"").replace("\n", "\\n")}"
                    },
                    "parameters": {
                        "voice": "$VOICE_NAME",
                        "sample_rate": 24000,
                        "format": "mp3",
                        "speed_ratio": $speechRate,
                        "volume_ratio": $volume
                    }
                }
                """.trimIndent()
                
                Log.d(TAG, "发送语音合成请求: 文本长度=${text.length}, 语速=$speechRate, 音量=$volume")
                
                // 发送请求
                val audioData = sendRequest(TTS_API_URL, requestBody)
                if (audioData != null) {
                    // 保存到临时文件
                    val tempFile = saveTempAudioFile(audioData)
                    if (tempFile != null) {
                        // 播放文件
                        playAudioFile(tempFile.absolutePath)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "语音合成失败: ${e.message}")
                _isSpeaking.value = false
            }
        }
    }
    
    /**
     * 发送请求到阿里云API
     * @param url API端点
     * @param requestBody 请求体
     * @return 音频数据字节数组
     */
    private fun sendRequest(url: String, requestBody: String): ByteArray? {
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
            
            if (response.isSuccessful) {
                return response.body?.bytes()
            } else {
                // 处理错误
                val errorBody = response.body?.string() ?: "无响应内容"
                Log.e(TAG, "API请求失败: ${response.code} ${response.message}\n$errorBody")
                
                try {
                    val jsonError = JSONObject(errorBody)
                    if (jsonError.has("message")) {
                        throw Exception("API错误: ${jsonError.getString("message")}")
                    }
                } catch (e: Exception) {
                    throw Exception("API请求失败: ${response.code} ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "网络请求异常: ${e.message}")
            throw e
        }
        return null
    }
    
    /**
     * 保存临时音频文件
     * @param audioData 音频数据
     * @return 临时文件
     */
    private fun saveTempAudioFile(audioData: ByteArray): File? {
        try {
            val cacheDir = context.cacheDir
            val tempFile = File(cacheDir, "tts_${UUID.randomUUID()}.mp3")
            
            FileOutputStream(tempFile).use { fos ->
                fos.write(audioData)
            }
            
            Log.d(TAG, "音频文件已保存: ${tempFile.absolutePath}")
            return tempFile
        } catch (e: Exception) {
            Log.e(TAG, "保存音频文件失败: ${e.message}")
        }
        return null
    }
    
    /**
     * 播放音频文件
     * @param filePath 文件路径
     */
    private fun playAudioFile(filePath: String) {
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(filePath)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            _isSpeaking.value = true
            Log.d(TAG, "开始播放音频文件")
        } catch (e: Exception) {
            Log.e(TAG, "播放音频文件失败: ${e.message}")
            _isSpeaking.value = false
        }
    }
    
    /**
     * 设置语速
     * @param rate 语速比例 (0.5-1.5)
     */
    fun setSpeechRate(rate: Float) {
        this.speechRate = rate.coerceIn(0.5f, 1.5f)
        Log.d(TAG, "设置语速: $speechRate")
    }
    
    /**
     * 设置音量
     * @param volume 音量比例 (0-1)
     */
    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f)
        Log.d(TAG, "设置音量: $volume")
        
        // 如果当前正在播放，实时调整音量
        mediaPlayer?.let {
            if (_isSpeaking.value) {
                it.setVolume(this.volume, this.volume)
            }
        }
    }
    
    /**
     * 停止当前语音
     */
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                Log.d(TAG, "停止播放")
            }
            _isSpeaking.value = false
        }
    }
    
    /**
     * 暂停当前语音
     */
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                Log.d(TAG, "暂停播放")
            }
        }
    }
    
    /**
     * 恢复播放
     */
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying && _isSpeaking.value) {
                it.start()
                Log.d(TAG, "恢复播放")
            }
        }
    }
    
    /**
     * 设置语音功能开关
     * @param enabled 是否启用
     */
    fun setEnabled(enabled: Boolean) {
        this.isEnabled = enabled
        if (!enabled) {
            stop()
        }
        Log.d(TAG, "语音功能${if (enabled) "已启用" else "已禁用"}")
    }
    
    /**
     * 检查是否正在播放
     * @return 是否正在播放
     */
    fun isSpeaking(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
    
    /**
     * 释放资源
     */
    fun shutdown() {
        stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d(TAG, "资源已释放")
    }
} 