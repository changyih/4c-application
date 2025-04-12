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
import com.example.olderperson.ui.screens.SelfScreen
import com.example.olderperson.ui.screens.SettingsScreen

import com.example.olderperson.ui.theme.OlderPersonTheme
import com.example.olderperson.ui.theme.FontSizeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// 定义DataStore
val Context.dataStore by preferencesDataStore(name = "settings")
private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
private val VOICE_ENABLED_KEY = booleanPreferencesKey("voice_enabled")
private val VOICE_VOLUME_KEY = floatPreferencesKey("voice_volume")
private val SPEECH_RATE_KEY = floatPreferencesKey("speech_rate")

// 全局声音设置
object SoundSettings {
    // 语音播报开关
    private val _voiceEnabled = mutableStateOf(true)
    val voiceEnabled = _voiceEnabled
    
    fun setVoiceEnabled(enabled: Boolean) {
        _voiceEnabled.value = enabled
    }
    
    // 语音音量
    private val _volume = mutableStateOf(0.7f)
    val volume = _volume
    
    fun setVolume(value: Float) {
        _volume.value = value
    }
    
    // 语音语速
    private val _speechRate = mutableStateOf(0.8f)
    val speechRate = _speechRate
    
    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.2f, 3.0f)  // 更新为新的范围
    }
}

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
            // 从DataStore加载字体大小设置
            LaunchedEffect(Unit) {
                // 尝试从存储中读取字体大小
                val savedFontSize = dataStore.data
                    .map { preferences ->
                        preferences[FONT_SIZE_KEY] ?: 1.0f // 默认为1.0
                    }
                    .first()
                
                // 应用保存的字体大小
                FontSizeConfig.setFontSize(savedFontSize)
                
                // 尝试从存储中读取语音设置
                val savedVoiceEnabled = dataStore.data
                    .map { preferences ->
                        preferences[VOICE_ENABLED_KEY] ?: true // 默认为开启
                    }
                    .first()
                SoundSettings.setVoiceEnabled(savedVoiceEnabled)
                
                // 尝试从存储中读取音量设置
                val savedVolume = dataStore.data
                    .map { preferences ->
                        preferences[VOICE_VOLUME_KEY] ?: 0.7f // 默认为70%
                    }
                    .first()
                SoundSettings.setVolume(savedVolume)
                
                // 尝试从存储中读取语速设置
                val savedSpeechRate = dataStore.data
                    .map { preferences ->
                        preferences[SPEECH_RATE_KEY] ?: 0.8f // 默认为0.8
                    }
                    .first()
                SoundSettings.setSpeechRate(savedSpeechRate)
                
                // 根据保存的设置调整TTS服务
                textToSpeechService.setEnabled(savedVoiceEnabled)
                textToSpeechService.setVolume(savedVolume)
            }
            
            // 监听字体大小变化并保存
            LaunchedEffect(FontSizeConfig.fontSize.value) {
                dataStore.edit { preferences ->
                    preferences[FONT_SIZE_KEY] = FontSizeConfig.fontSize.value
                }
            }
            
            // 监听语音开关变化并保存
            LaunchedEffect(SoundSettings.voiceEnabled.value) {
                dataStore.edit { preferences ->
                    preferences[VOICE_ENABLED_KEY] = SoundSettings.voiceEnabled.value
                }
                // 更新TTS服务状态
                textToSpeechService.setEnabled(SoundSettings.voiceEnabled.value)
            }
            
            // 监听音量变化并保存
            LaunchedEffect(SoundSettings.volume.value) {
                dataStore.edit { preferences ->
                    preferences[VOICE_VOLUME_KEY] = SoundSettings.volume.value
                }
                // 更新TTS服务音量
                textToSpeechService.setVolume(SoundSettings.volume.value)
            }
            
            // 监听语速变化并保存
            LaunchedEffect(SoundSettings.speechRate.value) {
                dataStore.edit { preferences ->
                    preferences[SPEECH_RATE_KEY] = SoundSettings.speechRate.value
                }
                // 更新TTS服务语速
                textToSpeechService.setSpeechRate(SoundSettings.speechRate.value)
            }
            
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
                        },
                        onLogout = {
                            // 处理退出登录逻辑
                            Log.d(TAG, "Logging out")
                            
                            // 使用lifecycleScope启动协程
                            lifecycleScope.launch {
                                // 在协程内调用suspend函数
                                dataStore.edit { preferences ->
                                    // 可以清除所有保存的偏好设置，或者只清除登录状态
                                }
                                
                                // 返回到登录界面
                                val intent = Intent(this@CareActivity, LoginActivity::class.java)
                                // 清除任务栈，防止用户按返回键回到当前界面
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
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
    onRequestPermission: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf("home") }
    
    when (currentScreen) {
        "home" -> CareHomeScreen(
            userName = "李伯伯",
            onNavigateToProfile = { 
                Log.d("CareActivity", "Navigating to Self screen")
                textToSpeechService.speak("进入我和自己页面")
                currentScreen = "self" 
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
            onNavigateToSettings = {
                Log.d("CareActivity", "Navigating to Settings screen")
                textToSpeechService.speak("进入设置页面")
                currentScreen = "settings"
            },
            textToSpeechService = textToSpeechService
        )
        "self" -> SelfScreen(
            onBackToHome = { 
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
        "settings" -> SettingsScreen(
            onBackToHome = {
                Log.d("CareActivity", "Navigating back to Home from Settings")
                currentScreen = "home"
            },
            onLogout = {
                Log.d("CareActivity", "Logging out")
                onLogout()
            },
            textToSpeechService = textToSpeechService
        )
    }
}