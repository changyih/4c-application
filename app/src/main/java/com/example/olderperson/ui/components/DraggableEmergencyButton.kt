package com.example.olderperson.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.utils.EmergencyContactsManager
import kotlin.math.roundToInt

/**
 * 可拖动的紧急呼叫按钮
 */
@Composable
fun DraggableEmergencyButton(
    emergencyContact: EmergencyContactsManager.EmergencyContact?,
    textToSpeechService: TextToSpeechService?,
    context: Context,
    parentSize: IntSize,
    initialX: Int,
    initialY: Int
) {
    // 按钮位置状态
    var offsetX by remember { mutableStateOf(initialX.toFloat()) }
    var offsetY by remember { mutableStateOf(initialY.toFloat()) }
    
    // 按钮大小
    val buttonSize = 80.dp
    val buttonSizePx = with(LocalDensity.current) { buttonSize.toPx().roundToInt() }
    
    // 是否显示确认对话框
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    // 动画效果
    val scale by animateFloatAsState(targetValue = if (showConfirmDialog) 1.2f else 1f)
    
    // 边界检查，确保按钮不会拖出屏幕
    fun checkBounds() {
        offsetX = offsetX.coerceIn(0f, (parentSize.width - buttonSizePx).toFloat().coerceAtLeast(0f))
        offsetY = offsetY.coerceIn(0f, (parentSize.height - buttonSizePx).toFloat().coerceAtLeast(0f))
    }
    
    // 初始化时检查边界
    LaunchedEffect(parentSize) {
        checkBounds()
    }
    
    // 确认对话框
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("紧急呼叫", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { 
                Text(
                    text = emergencyContact?.let { 
                        "确认拨打${it.name}的电话：${it.phone}？" 
                    } ?: "您尚未设置紧急联系人。请先在设置页面添加紧急联系人。",
                    fontSize = 16.sp
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        if (emergencyContact != null) {
                            textToSpeechService?.speak("正在拨打紧急联系人${emergencyContact.name}的电话")
                            // 直接拨打电话
                            val intent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:${emergencyContact.phone}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "无法拨打电话，请检查应用权限", Toast.LENGTH_SHORT).show()
                                Log.e("EmergencyCall", "无法拨打电话", e)
                            }
                        }
                    },
                    enabled = emergencyContact != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("确认拨打", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // 可拖动的紧急呼叫按钮
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(buttonSize * scale)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = Color.Red.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFD50000)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        // 开始拖动时的效果
                    },
                    onDragEnd = {
                        // 结束拖动时检查边界
                        checkBounds()
                    },
                    onDragCancel = {
                        // 取消拖动时检查边界
                        checkBounds()
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    // 实时检查边界
                    checkBounds()
                }
            }
            .clickable {
                showConfirmDialog = true
            }
            .border(
                width = 2.dp,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.3f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "紧急呼叫",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "紧急呼叫",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
} 