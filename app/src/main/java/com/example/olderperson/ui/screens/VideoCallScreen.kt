package com.example.olderperson.ui.screens

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.olderperson.R
import com.example.olderperson.service.VideoCallService
import android.view.SurfaceView

/**
 * 视频通话界面
 */
@Composable
fun VideoCallScreen(
    videoCallService: VideoCallService,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isInCall by videoCallService.isInCall.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 视频视图区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            // 占位视频区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "视频区域\n(实际通话时会显示视频画面)",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            // 真正的视频视图在实际实现时会在这里添加
            // 目前使用占位框架
            LaunchedEffect(Unit) {
                // 模拟通话开始
                videoCallService.startCall("test-channel")
            }
        }

        // 控制按钮区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            Button(
                onClick = {
                    // 结束通话并返回
                    videoCallService.endCall()
                    onBackClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text("返回", fontSize = 18.sp)
            }

            // 结束通话按钮
            Button(
                onClick = { videoCallService.endCall() },
                enabled = isInCall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text("结束通话", fontSize = 18.sp)
            }
        }
    }
} 