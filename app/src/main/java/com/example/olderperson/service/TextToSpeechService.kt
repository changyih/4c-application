package com.example.olderperson.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class TextToSpeechService(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

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
        if (isInitialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        }
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