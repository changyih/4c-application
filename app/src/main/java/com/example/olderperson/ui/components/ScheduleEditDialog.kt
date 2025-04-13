package com.example.olderperson.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.utils.ScheduleManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间选择对话框
 */
@Composable
fun TimePickerDialog(
    showDialog: Boolean,
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var selectedHour by remember { 
            // 解析初始时间
            val initialHour = if (initialTime.isNotEmpty()) {
                initialTime.split(":")[0].toIntOrNull() ?: 8
            } else 8
            mutableStateOf(initialHour) 
        }
        
        var selectedMinute by remember { 
            val initialMinute = if (initialTime.isNotEmpty() && initialTime.contains(":")) {
                initialTime.split(":")[1].toIntOrNull() ?: 0
            } else 0
            mutableStateOf(initialMinute) 
        }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("选择时间", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 小时选择
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("小时", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    if (selectedHour < 23) selectedHour++ 
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("▲", fontSize = 16.sp)
                            }
                            
                            Text(
                                text = String.format("%02d", selectedHour),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            Button(
                                onClick = { 
                                    if (selectedHour > 0) selectedHour-- 
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("▼", fontSize = 16.sp)
                            }
                        }
                        
                        Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        
                        // 分钟选择
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("分钟", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    if (selectedMinute < 59) selectedMinute++ 
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("▲", fontSize = 16.sp)
                            }
                            
                            Text(
                                text = String.format("%02d", selectedMinute),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            
                            Button(
                                onClick = { 
                                    if (selectedMinute > 0) selectedMinute-- 
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("▼", fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                        onTimeSelected(timeString)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 日程安排编辑对话框
 */
@Composable
fun ScheduleEditDialog(
    showDialog: Boolean,
    scheduleItem: ScheduleManager.ScheduleItem? = null,
    onDialogDismiss: () -> Unit,
    onScheduleSave: (ScheduleManager.ScheduleItem) -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    if (showDialog) {
        // 是否为编辑模式
        val isEditMode = scheduleItem != null
        
        // 日程安排项状态
        var time by remember { mutableStateOf(scheduleItem?.time ?: "08:00") }
        var title by remember { mutableStateOf(scheduleItem?.title ?: "") }
        var description by remember { mutableStateOf(scheduleItem?.description ?: "") }
        
        // 时间选择对话框状态
        var showTimePickerDialog by remember { mutableStateOf(false) }
        
        // 时间选择对话框
        TimePickerDialog(
            showDialog = showTimePickerDialog,
            initialTime = time,
            onTimeSelected = { selectedTime ->
                time = selectedTime
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
        
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { 
                Text(
                    text = if (isEditMode) "编辑安排" else "添加新安排",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF5D8AA8),
                                    Color(0xFF4682B4),
                                    Color(0xFF36648B)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)
                ) {
                    // 说明文字
                    Text(
                        text = if (isEditMode) "修改您的日程安排" else "添加一个新的日程安排",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // 时间选择
                    Text(
                        text = "时间",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF4682B4).copy(alpha = 0.3f))
                            .clickable { showTimePickerDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "选择时间",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = time,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 标题输入
                    Text(
                        text = "标题",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("例如：服药、复诊等", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White,
                            focusedContainerColor = Color(0xFF4682B4).copy(alpha = 0.5f),
                            unfocusedContainerColor = Color(0xFF4682B4).copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    // 详情输入
                    Text(
                        text = "详情",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("具体安排细节", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White,
                            focusedContainerColor = Color(0xFF4682B4).copy(alpha = 0.5f),
                            unfocusedContainerColor = Color(0xFF4682B4).copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    // 按钮区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 取消按钮
                        OutlinedButton(
                            onClick = onDialogDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text("取消", fontSize = 18.sp)
                        }
                        
                        // 保存按钮
                        Button(
                            onClick = {
                                if (title.isNotEmpty()) {
                                    val item = ScheduleManager.ScheduleItem(
                                        id = scheduleItem?.id ?: UUID.randomUUID().toString(),
                                        time = time,
                                        title = title,
                                        description = description
                                    )
                                    onScheduleSave(item)
                                    
                                    // 朗读确认信息
                                    textToSpeechService?.speak(
                                        if (isEditMode) "已更新安排：$time $title" 
                                        else "已添加新安排：$time $title"
                                    )
                                    
                                    onDialogDismiss()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF4682B4)
                            ),
                            enabled = title.isNotEmpty()
                        ) {
                            Text(
                                text = "保存",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = { },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

/**
 * 日程删除确认对话框
 */
@Composable
fun ScheduleDeleteConfirmDialog(
    showDialog: Boolean,
    scheduleItem: ScheduleManager.ScheduleItem?,
    onDialogDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    if (showDialog && scheduleItem != null) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { Text("删除安排", color = Color.Red) },
            text = {
                Text("确定要删除以下安排吗？\n\n时间：${scheduleItem.time}\n标题：${scheduleItem.title}")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmDelete()
                        textToSpeechService?.speak("已删除安排：${scheduleItem.time} ${scheduleItem.title}")
                        onDialogDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDialogDismiss) {
                    Text("取消")
                }
            }
        )
    }
} 