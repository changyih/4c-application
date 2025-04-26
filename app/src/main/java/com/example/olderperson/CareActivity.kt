package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.olderperson.service.SpeechRecognitionService
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.service.PhoneCallService

import com.example.olderperson.ui.screens.*

import com.example.olderperson.ui.screens.CareHomeScreen
import com.example.olderperson.ui.screens.CommunityScreen
import com.example.olderperson.ui.screens.FamilyScreen

import com.example.olderperson.ui.screens.SettingsScreen
import com.example.olderperson.ui.screens.ChatScreen
import com.example.olderperson.ui.screens.ExploreScreen
import com.example.olderperson.ui.screens.MagnifierScreen

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
import com.example.olderperson.data.UserManager
import kotlinx.coroutines.CoroutineExceptionHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.layout.Arrangement

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
    private lateinit var videoCallService: VideoCallService
    private lateinit var textToSpeechService: TextToSpeechService
    private lateinit var speechRecognitionService: SpeechRecognitionService
    private lateinit var phoneCallService: PhoneCallService
    private val TAG = "CareActivity"
    
    // 录音权限请求
    private val requestMicrophonePermissionLauncher = registerForActivityResult(
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
    
    // 电话权限请求
    private val requestPhonePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "拨打电话权限已授予")
            Toast.makeText(this, "电话权限已授予，现在可以拨打电话", Toast.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "拨打电话权限被拒绝")
            Toast.makeText(this, "需要电话权限来拨打电话", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "关爱模式Activity创建开始")
        
        try {
            // 请求录音权限
            requestMicrophonePermission()
            
            // 请求电话权限
            requestPhonePermission()
            
            // 创建一个标记，记录服务初始化状态
            var servicesInitialized = true
            
            // 安全初始化各种服务
            try {
                videoCallService = VideoCallService(this)
                Log.d(TAG, "视频通话服务初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "视频通话服务初始化失败: ${e.message}", e)
                servicesInitialized = false
            }
            
            try {
                textToSpeechService = TextToSpeechService(this)
                Log.d(TAG, "语音转文字服务初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "语音转文字服务初始化失败: ${e.message}", e)
                textToSpeechService = TextToSpeechService(this)
            }
            
            try {
                speechRecognitionService = SpeechRecognitionService(this)
                Log.d(TAG, "语音识别服务初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "语音识别服务初始化失败: ${e.message}", e)
                speechRecognitionService = SpeechRecognitionService(this)
            }
            
            try {
                phoneCallService = PhoneCallService(this)
                Log.d(TAG, "电话服务初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "电话服务初始化失败: ${e.message}", e)
                phoneCallService = PhoneCallService(this)
            }
            
            setContent {
                // 错误状态管理
                var hasServiceError by remember { mutableStateOf(!servicesInitialized) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                
                // 创建全局错误处理器
                val errorHandler = CoroutineExceptionHandler { _, exception ->
                    Log.e(TAG, "界面渲染异常: ${exception.message}", exception)
                    hasServiceError = true
                    errorMessage = "应用遇到问题: ${exception.message}"
                }
                
                // 从DataStore加载字体大小设置
                LaunchedEffect(Unit) {
                    try {
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
                    } catch (e: Exception) {
                        Log.e(TAG, "加载设置失败: ${e.message}", e)
                        // 使用默认设置
                        FontSizeConfig.setFontSize(1.0f)
                        SoundSettings.setVoiceEnabled(true)
                        SoundSettings.setVolume(0.7f)
                        SoundSettings.setSpeechRate(0.8f)
                    }
                }
                
                // 监听字体大小变化并保存
                LaunchedEffect(FontSizeConfig.fontSize.value) {
                    try {
                        dataStore.edit { preferences ->
                            preferences[FONT_SIZE_KEY] = FontSizeConfig.fontSize.value
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "保存字体大小失败: ${e.message}", e)
                    }
                }
                
                // 监听语音开关变化并保存
                LaunchedEffect(SoundSettings.voiceEnabled.value) {
                    try {
                        dataStore.edit { preferences ->
                            preferences[VOICE_ENABLED_KEY] = SoundSettings.voiceEnabled.value
                        }
                        // 更新TTS服务状态
                        textToSpeechService.setEnabled(SoundSettings.voiceEnabled.value)
                    } catch (e: Exception) {
                        Log.e(TAG, "保存语音设置失败: ${e.message}", e)
                    }
                }
                
                // 监听音量变化并保存
                LaunchedEffect(SoundSettings.volume.value) {
                    try {
                        dataStore.edit { preferences ->
                            preferences[VOICE_VOLUME_KEY] = SoundSettings.volume.value
                        }
                        // 更新TTS服务音量
                        textToSpeechService.setVolume(SoundSettings.volume.value)
                    } catch (e: Exception) {
                        Log.e(TAG, "保存音量设置失败: ${e.message}", e)
                    }
                }
                
                // 监听语速变化并保存
                LaunchedEffect(SoundSettings.speechRate.value) {
                    try {
                        dataStore.edit { preferences ->
                            preferences[SPEECH_RATE_KEY] = SoundSettings.speechRate.value
                        }
                        // 更新TTS服务语速
                        textToSpeechService.setSpeechRate(SoundSettings.speechRate.value)
                    } catch (e: Exception) {
                        Log.e(TAG, "保存语速设置失败: ${e.message}", e)
                    }
                }
                
                OlderPersonTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // 如果有服务初始化错误，显示错误屏幕
                        if (hasServiceError) {
                            CareErrorScreen(
                                errorMessage = errorMessage ?: "应用初始化遇到问题，请重启应用",
                                onRetry = {
                                    hasServiceError = false
                                    errorMessage = null
                                }
                            )
                        } else {
                            var showMicPermissionDialog by remember { mutableStateOf(false) }
                            var showPhonePermissionDialog by remember { mutableStateOf(false) }
                            
                            // 麦克风权限对话框
                            if (showMicPermissionDialog) {
                                AlertDialog(
                                    onDismissRequest = { showMicPermissionDialog = false },
                                    title = { Text("需要麦克风权限") },
                                    text = { Text("为了使用语音转文字功能，应用需要访问您的麦克风。请在接下来的提示中授予权限。") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showMicPermissionDialog = false
                                                requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        ) {
                                            Text("确定")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { showMicPermissionDialog = false }
                                        ) {
                                            Text("取消")
                                        }
                                    }
                                )
                            }
                            
                            // 电话权限对话框
                            if (showPhonePermissionDialog) {
                                AlertDialog(
                                    onDismissRequest = { showPhonePermissionDialog = false },
                                    title = { Text("需要电话权限") },
                                    text = { Text("为了能够直接拨打电话，应用需要电话权限。请在接下来的提示中授予权限。") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                showPhonePermissionDialog = false
                                                requestPhonePermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                                            }
                                        ) {
                                            Text("确定")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { showPhonePermissionDialog = false }
                                        ) {
                                            Text("取消")
                                        }
                                    }
                                )
                            }
                            
CareApp(
    textToSpeechService = textToSpeechService,
    speechRecognitionService = speechRecognitionService,
    phoneCallService = phoneCallService,
    onRequestMicPermission = {
        // 检查麦克风权限状态
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showMicPermissionDialog = true
        }
    },
    onRequestPhonePermission = {
        // 检查电话权限状态
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showPhonePermissionDialog = true
        }
    },
    onLogout = {
        // 处理退出登录逻辑
        Log.d(TAG, "Logging out")
        
        // 使用lifecycleScope启动协程
        lifecycleScope.launch {
            // 清除用户登录状态
            UserManager.clearCurrentUser(this@CareActivity)
            
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
        } catch (e: Exception) {
            // 捕获应用初始化异常
            Log.e(TAG, "关爱模式初始化发生严重错误: ${e.message}", e)
            Toast.makeText(this, "应用初始化失败，请重启应用", Toast.LENGTH_LONG).show()
            
            // 返回登录界面
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        Log.d(TAG, "关爱模式Activity创建完成")
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
                requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // 首次请求权限
                requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    /**
     * 请求电话权限
     */
    private fun requestPhonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限
                Log.d(TAG, "已经有电话权限")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                // 用户之前拒绝过，需要解释为什么需要该权限
                Toast.makeText(this, "需要电话权限来直接拨打电话", Toast.LENGTH_LONG).show()
                requestPhonePermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            else -> {
                // 首次请求权限
                requestPhonePermissionLauncher.launch(Manifest.permission.CALL_PHONE)
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
    phoneCallService: PhoneCallService,
    onRequestMicPermission: () -> Unit = {},
    onRequestPhonePermission: () -> Unit = {},
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
                onRequestMicPermission()
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
            textToSpeechService = textToSpeechService,
            phoneCallService = phoneCallService,
            onRequestPhonePermission = onRequestPhonePermission
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
            onRequestPermission = onRequestMicPermission
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
            onNavigateToMagnifier = {
                Log.d("CareActivity", "Navigating to Magnifier screen from Settings")
                textToSpeechService.speak("进入简易放大镜")
                currentScreen = "magnifier"
            },
            textToSpeechService = textToSpeechService
        )
        "magnifier" -> MagnifierScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = {
                Log.d("CareActivity", "Returning from Magnifier screen")
                textToSpeechService.speak("返回设置页面")
                currentScreen = "settings"
            }
        )
    }
}

/**
 * 错误屏幕显示
 */
@Composable
fun CareErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "错误",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "出现问题",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = errorMessage,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重试",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("重试")
                }
            }
        }
    }
}