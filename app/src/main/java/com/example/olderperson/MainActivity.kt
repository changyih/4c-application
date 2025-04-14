package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.olderperson.service.AlibabaQianwenService
import com.example.olderperson.service.PhoneCallService
import com.example.olderperson.service.SpeechRecognitionService
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.ui.screens.*
import com.example.olderperson.ui.theme.OlderPersonTheme
import androidx.compose.runtime.DisposableEffect
import com.example.olderperson.utils.ScheduleManager
import android.content.Intent


class MainActivity : ComponentActivity() {
    // 视频通话服务
    private lateinit var videoCallService: VideoCallService
    // 文字转语音服务
    private lateinit var textToSpeechService: TextToSpeechService
    // 语音识别服务
    private lateinit var speechRecognitionService: SpeechRecognitionService
    // 通义千问服务
    private lateinit var alibabaQianwenService: AlibabaQianwenService
    // 电话服务
    private lateinit var phoneCallService: PhoneCallService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startServices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate开始执行")
        
        // 重置所有闹钟提醒
        resetScheduleReminders()
        
        try {
            // 初始化服务
            videoCallService = VideoCallService(this)
            textToSpeechService = TextToSpeechService(this)
            speechRecognitionService = SpeechRecognitionService(this)
            alibabaQianwenService = AlibabaQianwenService(this)
            phoneCallService = PhoneCallService(this)
            
            // 检查权限
            checkPermissions()
        } catch (e: Exception) {
            Log.e("MainActivity", "onCreate异常: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d("MainActivity", "onCreate执行完成")
    }

    private fun checkPermissions() {
        Log.d("MainActivity", "开始检查权限")
        
        // 先启动服务，避免卡死在权限检查
        startServices()
        Log.d("MainActivity", "先启动服务，然后请求权限")
        
        // 需要请求的权限列表
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
        )
        
        // 不再筛选已授予权限，直接请求所有权限
        Log.d("MainActivity", "请求所有权限")
        requestPermissionLauncher.launch(permissions)
        
        // 不再阻塞UI线程等待权限结果
        Log.d("MainActivity", "权限请求已发送，继续执行")
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCallService.release()
        textToSpeechService.shutdown()
        speechRecognitionService.shutdown()
    }

    private fun startServices() {
        Log.d("MainActivity", "开始启动主界面服务")
        
        setContent {
            OlderPersonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 使用安全加载模式
                    Log.d("MainActivity", "开始加载HomeScreen")
                    
                    SafeMainContent(
                        videoCallService = videoCallService, 
                        textToSpeechService = textToSpeechService,
                        speechRecognitionService = speechRecognitionService,
                        phoneCallService = phoneCallService
                    )
                    
                    Log.d("MainActivity", "HomeScreen加载完成")
                }
            }
        }
        
        Log.d("MainActivity", "主界面服务启动完成")
    }

    @Composable
    fun SafeMainContent(
        videoCallService: VideoCallService,
        textToSpeechService: TextToSpeechService,
        speechRecognitionService: SpeechRecognitionService,
        phoneCallService: PhoneCallService
    ) {
        // 使用rememberSaveable保持状态
        var hasError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        
        // 设置错误处理器
        DisposableEffect(Unit) {
            val handler = Thread.UncaughtExceptionHandler { _, throwable ->
                Log.e("MainActivity", "捕获到未处理异常: ${throwable.message}")
                throwable.printStackTrace()
                hasError = true
                errorMessage = throwable.message
            }
            
            val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(handler)
            
            onDispose {
                Thread.setDefaultUncaughtExceptionHandler(defaultHandler)
            }
        }
        
        if (hasError) {
            MainErrorScreen(
                errorMessage = errorMessage ?: "加载界面时发生未知错误",
                retry = { 
                    hasError = false
                    errorMessage = null
                }
            )
        } else {
            MainContent(
                videoCallService = videoCallService, 
                textToSpeechService = textToSpeechService,
                speechRecognitionService = speechRecognitionService,
                phoneCallService = phoneCallService
            )
        }
    }

    /**
     * 重置所有日程提醒
     */
    private fun resetScheduleReminders() {
        try {
            // 使用ScheduleManager的重置方法
            val scheduleManager = ScheduleManager.getInstance(this)
            scheduleManager.resetAllReminders()
        } catch (e: Exception) {
            Log.e("MainActivity", "重置日程提醒失败: ${e.message}")
        }
    }
}

// 定义导航部分的枚举类型
enum class NavSection {
    HOME, CLOUD_CARE, SELF_CHECK, MESSAGE, PROFILE
}

@Composable
fun MainContent(
    videoCallService: VideoCallService,
    textToSpeechService: TextToSpeechService,
    speechRecognitionService: SpeechRecognitionService,
    phoneCallService: PhoneCallService
) {
    var currentSection by remember { mutableStateOf(NavSection.HOME) }
    var showVideoCall by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // 记住在UI中需要的服务实例
    val speechService = remember { speechRecognitionService }

    // 处理返回键
    BackHandler(enabled = currentSection != NavSection.HOME || showVideoCall) {
        if (showVideoCall) {
            showVideoCall = false
        } else {
            currentSection = NavSection.HOME
        }
    }

    // 退出登录功能
    val onLogout: () -> Unit = {
        // 跳转到登录界面
        val intent = Intent(context, LoginActivity::class.java)
        // 清除任务栈，防止用户按返回键回到当前界面
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    if (showVideoCall) {
        VideoCallScreen(
            videoCallService = videoCallService,
            onBackClick = { showVideoCall = false }
        )
    } else {
        when (currentSection) {
            NavSection.HOME -> HomeScreen(
                videoCallService = videoCallService,
                textToSpeechService = textToSpeechService,
                phoneCallService = phoneCallService,
                onVideoCallClick = { showVideoCall = true },
                onProfileClick = { currentSection = NavSection.PROFILE },
                onMessageClick = { currentSection = NavSection.MESSAGE }
            )
            NavSection.PROFILE -> ProfileScreen(
                onBackToHome = { currentSection = NavSection.HOME },
                textToSpeechService = textToSpeechService,
                onLogout = onLogout
            )
            NavSection.MESSAGE -> ChatScreen(
                onBackClick = { currentSection = NavSection.HOME },
                textToSpeechService = textToSpeechService,
                speechRecognitionService = speechService,
                onRequestPermission = {
                    // 权限已经在MainActivity中请求过了
                }
            )
            NavSection.SELF_CHECK -> HealthPlanScreen(
                textToSpeechService = textToSpeechService
            )
            else -> Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "开发中...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun MainErrorScreen(errorMessage: String, retry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "错误",
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "出错了",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = retry,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("重试")
        }
    }
}