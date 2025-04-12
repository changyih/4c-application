package com.example.olderperson.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

class TextToSpeechService(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var isEnabled = true // 是否启用语音
    private var volume = 1.0f // 音量 0.0f-1.0f
    private var speechRate = 0.8f // 语速 0.5f-1.5f
    private var currentUtteranceId = ""
    private val TAG = "TextToSpeechService"

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 如果中文不可用，使用默认语言
                    textToSpeech?.setLanguage(Locale.getDefault())
                    Log.d(TAG, "中文不可用，使用默认语言")
                } else {
                    Log.d(TAG, "成功设置中文语言")
                }
                isInitialized = true
                
                // 设置语音参数
                textToSpeech?.apply {
                    setPitch(1.0f) // 音调默认
                    setSpeechRate(speechRate) // 语速
                }
                
                // 设置播放监听器
                setTtsListener()
            } else {
                Log.e(TAG, "TTS初始化失败，状态码: $status")
            }
        }
    }
    
    // 设置TTS监听器
    private fun setTtsListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.d(TAG, "开始播放: $utteranceId")
                currentUtteranceId = utteranceId
            }

            override fun onDone(utteranceId: String) {
                Log.d(TAG, "播放完成: $utteranceId")
                currentUtteranceId = ""
            }

            override fun onError(utteranceId: String) {
                Log.e(TAG, "播放错误: $utteranceId")
                currentUtteranceId = ""
            }
        })
    }

    fun speak(text: String) {
        if (isInitialized && isEnabled) {
            // 创建Bundle参数对象，设置音量
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
            }
            val utteranceId = UUID.randomUUID().toString()
            
            // 如果有正在播放的内容，先停止
            if (currentUtteranceId.isNotEmpty()) {
                stop()
            }
            
            // 开始播放新内容
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            Log.d(TAG, "请求播放文本，音量: $volume, 语速: $speechRate")
        } else if (!isInitialized) {
            Log.w(TAG, "TTS服务未初始化，无法播放")
        } else if (!isEnabled) {
            Log.d(TAG, "TTS服务已禁用，不会播放")
        }
    }

    // 设置语音开关
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            stop() // 如果关闭，停止当前播报
        }
        Log.d(TAG, "TTS服务${if (enabled) "启用" else "禁用"}")
    }

    // 设置语速
    fun setSpeechRate(rate: Float) {
        this.speechRate = rate.coerceIn(0.5f, 1.5f) // 限制在0.5-1.5范围内
        textToSpeech?.setSpeechRate(this.speechRate)
        Log.d(TAG, "设置语速: $speechRate")
    }

    // 设置音量
    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f) // 限制在0-1范围内
        Log.d(TAG, "设置音量: $volume")
    }

    // 停止朗读
    fun stop() {
        textToSpeech?.stop()
        Log.d(TAG, "停止朗读")
    }

    // 暂停朗读（部分设备可能不支持）
    fun pause() {
        if (isInitialized) {
            if (textToSpeech?.isSpeaking == true) {
                stop()
                Log.d(TAG, "尝试暂停朗读（通过停止实现）")
            }
        }
    }
    
    // 检查是否正在朗读
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    // 检查是否初始化完成
    fun isInitialized(): Boolean {
        return isInitialized
    }

    // 关闭并释放资源
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
        Log.d(TAG, "TTS服务已关闭")
    }
} 