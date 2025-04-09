package com.example.olderperson

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.screens.CareHomeScreen
import com.example.olderperson.ui.screens.ProfileScreen
import com.example.olderperson.ui.theme.OlderPersonTheme

/**
 * 呵护模式的入口Activity
 */
class CareActivity : ComponentActivity() {
    private lateinit var textToSpeechService: TextToSpeechService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化文字转语音服务
        textToSpeechService = TextToSpeechService(this)
        
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CareApp(textToSpeechService)
                }
            }
        }
    }

    
    override fun onDestroy() {
        super.onDestroy()
        // 释放TTS资源
        textToSpeechService.shutdown()
    }
}

@Composable
fun CareApp(textToSpeechService: TextToSpeechService) {
    var currentScreen by remember { mutableStateOf("home") }
    
    when (currentScreen) {
        "home" -> CareHomeScreen(
            userName = "王伯伯",
            onNavigateToProfile = { 
                Log.d("CareActivity", "Navigating to Profile screen")
                textToSpeechService.speak("进入我和自己页面")
                currentScreen = "profile" 
            },
            onNavigateToFamily = { /* 暂未实现 */ },
            onNavigateToCommunity = { /* 暂未实现 */ },
            textToSpeechService = textToSpeechService
        )
        "profile" -> ProfileScreen(
            onBackToHome = { 
                Log.d("CareActivity", "Navigating back to Home")
                currentScreen = "home" 
            },
            textToSpeechService = textToSpeechService
        )
    }
}