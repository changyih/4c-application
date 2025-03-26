package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.ui.screens.HomeScreen
import com.example.olderperson.ui.screens.LoginScreen
import com.example.olderperson.ui.screens.VideoCallScreen
import com.example.olderperson.ui.theme.OlderPersonTheme

class MainActivity : ComponentActivity() {
    // 视频通话服务
    private lateinit var videoCallService: VideoCallService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startServices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化视频通话服务
        videoCallService = VideoCallService(this)

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
    }

    private fun startServices() {
        setContent {
            OlderPersonTheme {
                // 登录状态
                var isLoggedIn by remember { mutableStateOf(false) }
                
                if (isLoggedIn) {
                    // 应用主界面和视频通话界面
                    MainContent(videoCallService)
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
    HOME, FAMILY, SETTINGS
}

@Composable
fun MainContent(videoCallService: VideoCallService) {
    // 是否显示视频通话界面的状态
    var showVideoCall by remember { mutableStateOf(false) }

    if (showVideoCall) {
        // 显示视频通话界面
        VideoCallScreen(
            videoCallService = videoCallService,
            onBackClick = { showVideoCall = false }
        )
    } else {
        // 显示主页面
        HomeScreen(
            videoCallService = videoCallService,
            onVideoCallClick = { showVideoCall = true }
        )
    }
}