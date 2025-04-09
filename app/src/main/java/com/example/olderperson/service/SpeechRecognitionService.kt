package com.example.olderperson.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

/**
 * 语音识别服务，用于将语音转换为文字
 */
class SpeechRecognitionService(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val TAG = "SpeechRecognition"
    
    // 识别状态
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening
    
    // 识别结果
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText
    
    init {
        // 初始化语音识别器
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.d(TAG, "语音识别服务可用")
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            setupRecognitionListener()
        } else {
            Log.e(TAG, "语音识别服务不可用")
        }
    }
    
    /**
     * 开始语音识别
     */
    fun startListening() {
        Log.d(TAG, "开始语音识别")
        // 取消之前的识别（如果有）
        stopListening()
        
        // 创建语音识别意图
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINESE.toString())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // 获取部分结果
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)
        }
        
        // 开始识别
        _isListening.value = true
        _recognizedText.value = ""
        try {
            speechRecognizer?.startListening(intent)
            Log.d(TAG, "开始监听语音输入")
        } catch (e: Exception) {
            Log.e(TAG, "启动语音识别失败: ${e.message}")
            _isListening.value = false
        }
    }
    
    /**
     * 停止语音识别
     */
    fun stopListening() {
        if (_isListening.value) {
            Log.d(TAG, "停止语音识别")
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e(TAG, "停止语音识别失败: ${e.message}")
            }
            _isListening.value = false
        }
    }
    
    /**
     * 设置识别监听器
     */
    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "准备好开始语音识别")
            }
            
            override fun onBeginningOfSpeech() {
                Log.d(TAG, "开始语音输入")
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // 音量变化，可以用来显示音量大小
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "接收到音频缓冲区")
            }
            
            override fun onEndOfSpeech() {
                Log.d(TAG, "语音输入结束")
                _isListening.value = false
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                    SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                    SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                    SpeechRecognizer.ERROR_NO_MATCH -> "没有匹配的结果"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别服务忙"
                    SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                    else -> "未知错误"
                }
                Log.e(TAG, "识别错误: $errorMessage (错误码: $error)")
                _isListening.value = false
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    Log.d(TAG, "识别结果: $text")
                    _recognizedText.value = text
                } else {
                    Log.d(TAG, "没有识别结果")
                }
                _isListening.value = false
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    Log.d(TAG, "部分识别结果: $text")
                    _recognizedText.value = text
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "识别事件: $eventType")
            }
        })
    }
    
    /**
     * 释放资源
     */
    fun shutdown() {
        Log.d(TAG, "关闭语音识别服务")
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
} 