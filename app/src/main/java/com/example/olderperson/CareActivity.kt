package com.example.olderperson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.ui.screens.CareScreen
import com.example.olderperson.ui.theme.OlderPersonTheme

/**
 * 呵护模式的入口Activity
 */
class CareActivity : ComponentActivity() {
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
                // 显示呵护模式界面
                CareScreen(
                    videoCallService = videoCallService,
                    textToSpeechService = textToSpeechService,
                    onVideoCallClick = { /* 视频通话功能 */ },
                    onProfileClick = { /* 个人资料功能 */ },
                    onMessageClick = { /* 消息功能 */ },
                    onServiceClick = { /* 服务功能 */ }
                )
            }
        }
    }
}