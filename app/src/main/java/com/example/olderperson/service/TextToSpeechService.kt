package com.example.olderperson.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class TextToSpeechService(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var isEnabled = true // 是否启用语音
    private var volume = 1.0f // 音量 0.0f-1.0f

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 如果中文不可用，使用默认语言
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                isInitialized = true
            }
        }

        // 设置语音参数
        textToSpeech?.apply {
            setPitch(0.8f) // 音调
            setSpeechRate(0.8f) // 语速
        }
    }

    fun speak(text: String) {
        if (isInitialized && isEnabled) {
            // 创建Bundle参数对象，设置音量
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
            }
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "utteranceId")
        }
    }

    // 设置语音开关
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            stop() // 如果关闭，停止当前播报
        }
    }

    // 设置音量
    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f) // 限制在0-1范围内
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
} 