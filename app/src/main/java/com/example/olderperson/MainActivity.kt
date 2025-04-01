package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.ui.screens.HomeScreen
import com.example.olderperson.ui.screens.LoginScreen
import com.example.olderperson.ui.screens.MessageScreen
import com.example.olderperson.ui.screens.ProfileScreen
import com.example.olderperson.ui.screens.VideoCallScreen
import com.example.olderperson.ui.theme.OlderPersonTheme

class MainActivity : ComponentActivity() {
    // 视频通话服务
    private lateinit var videoCallService: VideoCallService
    // 文字转语音服务
    private lateinit var textToSpeechService: TextToSpeechService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startServices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化服务
        videoCallService = VideoCallService(this)
        textToSpeechService = TextToSpeechService(this)

        checkPermissions()
        startServices()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoCallService.release()
        textToSpeechService.shutdown()
    }

    private fun startServices() {
        setContent {
            OlderPersonTheme {
                // 登录状态
                var isLoggedIn by remember { mutableStateOf(false) }
                
                if (isLoggedIn) {
                    // 应用主界面和视频通话界面
                    MainContent(videoCallService, textToSpeechService)
                } else {
                    // 登录界面
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
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
    textToSpeechService: TextToSpeechService
) {
    var currentSection by remember { mutableStateOf(NavSection.HOME) }
    var showVideoCall by remember { mutableStateOf(false) }

    // 处理返回键
    BackHandler(enabled = currentSection != NavSection.HOME || showVideoCall) {
        if (showVideoCall) {
            showVideoCall = false
        } else {
            currentSection = NavSection.HOME
        }
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
                onVideoCallClick = { showVideoCall = true },
                onProfileClick = { currentSection = NavSection.PROFILE },
                onMessageClick = { currentSection = NavSection.MESSAGE }
            )
            NavSection.PROFILE -> ProfileScreen(
                onBackToHome = { currentSection = NavSection.HOME },
                textToSpeechService = textToSpeechService
            )
            NavSection.MESSAGE -> MessageScreen(
                onBackToHome = { currentSection = NavSection.HOME }
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