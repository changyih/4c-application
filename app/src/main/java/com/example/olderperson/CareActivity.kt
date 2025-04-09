package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.olderperson.service.SpeechRecognitionService
import com.example.olderperson.service.TextToSpeechService

import com.example.olderperson.ui.screens.*

import com.example.olderperson.ui.screens.CareHomeScreen
import com.example.olderperson.ui.screens.CommunityScreen
import com.example.olderperson.ui.screens.FamilyScreen
import com.example.olderperson.ui.screens.ProfileScreen
import com.example.olderperson.ui.screens.SettingsScreen

import com.example.olderperson.ui.theme.OlderPersonTheme

/**
 * 呵护模式的入口Activity
 */
class CareActivity : ComponentActivity() {
    private lateinit var textToSpeechService: TextToSpeechService
    private lateinit var speechRecognitionService: SpeechRecognitionService
    private val TAG = "CareActivity"
    
    // 录音权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "麦克风权限已授予")
            Toast.makeText(this, "麦克风权限已授予，现在可以使用语音功能", Toast.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "麦克风权限被拒绝")
            Toast.makeText(this, "需要麦克风权限来使用语音功能", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 请求录音权限
        requestMicrophonePermission()
        
        // 初始化文字转语音服务
        textToSpeechService = TextToSpeechService(this)
        
        // 初始化语音识别服务
        speechRecognitionService = SpeechRecognitionService(this)
        
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showPermissionDialog by remember { mutableStateOf(false) }
                    
                    // 权限对话框
                    if (showPermissionDialog) {
                        AlertDialog(
                            onDismissRequest = { showPermissionDialog = false },
                            title = { Text("需要麦克风权限") },
                            text = { Text("为了使用语音转文字功能，应用需要访问您的麦克风。请在接下来的提示中授予权限。") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showPermissionDialog = false
                                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                ) {
                                    Text("确定")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showPermissionDialog = false }
                                ) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                    
                    CareApp(
                        textToSpeechService = textToSpeechService,
                        speechRecognitionService = speechRecognitionService,
                        onRequestPermission = {
                            // 检查权限状态
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.RECORD_AUDIO
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                showPermissionDialog = true
                            }
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 请求麦克风权限
     */
    private fun requestMicrophonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限
                Log.d(TAG, "已经有麦克风权限")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // 用户之前拒绝过，需要解释为什么需要该权限
                Toast.makeText(this, "需要麦克风权限来使用语音功能", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // 首次请求权限
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    
    override fun onDestroy() {
        super.onDestroy()
        // 释放TTS资源
        textToSpeechService.shutdown()
        // 释放语音识别资源
        speechRecognitionService.shutdown()
    }
}

@Composable
fun CareApp(
    textToSpeechService: TextToSpeechService,
    speechRecognitionService: SpeechRecognitionService,
    onRequestPermission: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("home") }
    
    when (currentScreen) {
        "home" -> CareHomeScreen(
            userName = "王伯伯",
            onNavigateToProfile = { 
                Log.d("CareActivity", "Navigating to Profile screen")
                textToSpeechService.speak("进入我和自己页面")
                currentScreen = "profile" 
            },
            onNavigateToChat = {
                Log.d("CareActivity", "Navigating to Chat screen")
                textToSpeechService.speak("进入智慧伙伴对话页面")
                currentScreen = "chat"
                // 进入聊天页面时请求权限
                onRequestPermission()
            },
            onNavigateToExplore = {
                Log.d("CareActivity", "Navigating to Explore screen")
                textToSpeechService.speak("进入探索页面")
                currentScreen = "explore"
            },
            onNavigateToFamily = { 
                Log.d("CareActivity", "Navigating to Family screen")
                textToSpeechService.speak("进入我和家人页面")
                currentScreen = "family" 
            },
            onNavigateToCommunity = { 
                Log.d("CareActivity", "Navigating to Community screen")
                textToSpeechService.speak("进入我和社区页面")
                currentScreen = "community" 
            },
            onNavigateToSettings = {
                Log.d("CareActivity", "Navigating to Settings screen")
                textToSpeechService.speak("进入设置页面")
                currentScreen = "settings"
            },
            textToSpeechService = textToSpeechService
        )
        "profile" -> ProfileScreen(
            onBackToHome = { 
                Log.d("CareActivity", "Navigating back to Home")
                currentScreen = "home" 
            },
            textToSpeechService = textToSpeechService
        )
        "chat" -> ChatScreen(
            onBackClick = {
                Log.d("CareActivity", "Navigating back to Home")
                currentScreen = "home"
            },
            textToSpeechService = textToSpeechService,
            speechRecognitionService = speechRecognitionService,
            onRequestPermission = onRequestPermission
        )
        "explore" -> ExploreScreen(
            onBackClick = {
                Log.d("CareActivity", "Navigating back to Home")
                currentScreen = "home"
            },
            textToSpeechService = textToSpeechService
        )
        "family" -> FamilyScreen(
            onBackToHome = {
                Log.d("CareActivity", "Navigating back to Home from Family")
                currentScreen = "home"
            },
            textToSpeechService = textToSpeechService
        )
        "community" -> CommunityScreen(
            onBackToHome = {
                Log.d("CareActivity", "Navigating back to Home from Community")
                currentScreen = "home"
            },
            textToSpeechService = textToSpeechService
        )
        "settings" -> SettingsScreen(
            onBackToHome = {
                Log.d("CareActivity", "Navigating back to Home from Settings")
                currentScreen = "home"
            },
            textToSpeechService = textToSpeechService
        )
    }
}