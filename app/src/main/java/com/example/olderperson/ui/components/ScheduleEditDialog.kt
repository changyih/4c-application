package com.example.olderperson.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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
import android.util.Log

/**
 * 时间选择对话框
 */
@Composable
fun TimePickerDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
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
    // 如果传入了scheduleItem，则为编辑模式；否则为添加模式
    val isEditMode = scheduleItem != null
    
    // 状态变量
    var time by remember { mutableStateOf(scheduleItem?.time ?: "08:00") }
    var title by remember { mutableStateOf(scheduleItem?.title ?: "") }
    var description by remember { mutableStateOf(scheduleItem?.description ?: "") }
    var reminderEnabled by remember { mutableStateOf(scheduleItem?.reminderEnabled ?: true) }
    
    // 显示时间选择器对话框
    var showTimePicker by remember { mutableStateOf(false) }
    
    // 表单验证
    val isFormValid = title.isNotBlank() && time.isNotBlank()
    
    // 时间选择器对话框
    if (showTimePicker) {
        TimePickerDialog(
            initialTime = time,
            onTimeSelected = { selectedTime ->
                time = selectedTime
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { 
                Text(
                    text = if (isEditMode) "编辑日程" else "添加日程",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // 时间选择
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("时间") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true },
                        enabled = false,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "时间"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "编辑时间"
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 标题输入
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("标题") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "标题"
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 描述输入
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("描述") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "描述"
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 闹钟提醒开关
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "提醒",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Text(
                            text = "开启提醒",
                            modifier = Modifier.weight(1f)
                        )
                        
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { 
                                try {
                                    // 使用安全的状态更新
                                    reminderEnabled = it
                                } catch (e: Exception) {
                                    // 捕获并记录可能的异常
                                    Log.e("ScheduleEditDialog", "切换提醒状态时出错: ${e.message}", e)
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val item = ScheduleManager.ScheduleItem(
                                id = scheduleItem?.id ?: UUID.randomUUID().toString(),
                                time = time,
                                title = title,
                                description = description,
                                reminderEnabled = reminderEnabled
                            )
                            
                            // 安全地调用回调
                            try {
                                onScheduleSave(item)
                            } catch (e: Exception) {
                                Log.e("ScheduleEditDialog", "保存日程时出错: ${e.message}", e)
                            }
                            
                            // 播报反馈
                            try {
                                val actionText = if (isEditMode) "编辑" else "添加"
                                textToSpeechService?.speak("${actionText}成功：$title，时间：$time")
                            } catch (e: Exception) {
                                Log.e("ScheduleEditDialog", "播报反馈时出错: ${e.message}", e)
                            }
                            
                            // 关闭对话框
                            onDialogDismiss()
                        } catch (e: Exception) {
                            Log.e("ScheduleEditDialog", "保存按钮点击处理错误: ${e.message}", e)
                        }
                    },
                    enabled = isFormValid
                ) {
                    Text(if (isEditMode) "保存" else "添加")
                }
            },
            dismissButton = {
                TextButton(onClick = onDialogDismiss) {
                    Text("取消")
                }
            }
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
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog && scheduleItem != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            title = { 
                Text(
                    text = "删除安排", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE57373)
                ) 
            },
            text = { 
                Column {
                    Text(
                        text = "确定要删除以下安排吗？", 
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF1976D2)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = scheduleItem.time,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1976D2)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = scheduleItem.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (scheduleItem.description.isNotEmpty()) {
                                Text(
                                    text = scheduleItem.description,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "此操作无法撤销",
                        fontSize = 14.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("确认删除")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text("取消")
                }
            }
        )
    }
} 