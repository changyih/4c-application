package com.example.olderperson.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    // 拆分为小时和分钟
    var hour by remember { 
        mutableStateOf(
            if (time.contains(":")) {
                time.split(":")[0]
            } else "08"
        )
    }
    var minute by remember { 
        mutableStateOf(
            if (time.contains(":")) {
                time.split(":")[1]
            } else "00"
        )
    }
    var title by remember { mutableStateOf(scheduleItem?.title ?: "") }
    var description by remember { mutableStateOf(scheduleItem?.description ?: "") }
    var reminderEnabled by remember { mutableStateOf(scheduleItem?.reminderEnabled ?: true) }
    
    // 提醒方式选择状态
    var notificationEnabled by remember { mutableStateOf(scheduleItem?.notificationEnabled ?: true) }
    var vibrationEnabled by remember { mutableStateOf(scheduleItem?.vibrationEnabled ?: true) }
    var alarmSoundEnabled by remember { mutableStateOf(scheduleItem?.alarmSoundEnabled ?: true) }
    var voiceEnabled by remember { mutableStateOf(scheduleItem?.voiceEnabled ?: true) }
    var showReminderError by remember { mutableStateOf(false) }
    
    // 表单验证
    fun isAtLeastOneReminderSelected(): Boolean {
        return notificationEnabled || vibrationEnabled || alarmSoundEnabled || voiceEnabled
    }
    
    // 验证时间格式和表单是否有效
    val isHourValid = hour.toIntOrNull()?.let { it in 0..23 } ?: false
    val isMinuteValid = minute.toIntOrNull()?.let { it in 0..59 } ?: false
    val isFormValid = title.isNotBlank() && isHourValid && isMinuteValid &&
                       (!reminderEnabled || isAtLeastOneReminderSelected())
    
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
                    // 小时和分钟输入区
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "设置时间",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 小时输入
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "小时",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                                
                                OutlinedTextField(
                                    value = hour,
                                    onValueChange = { newValue ->
                                        // 限制最多2位数
                                        if (newValue.length <= 2) {
                                            hour = newValue
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 28.sp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color(0xFFBDBDBD),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    )
                                )
                                
                                // 小时验证消息
                                if (hour.isNotEmpty() && !isHourValid) {
                                    Text(
                                        text = "0-23之间",
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                    )
                                }
                            }
                            
                            // 分隔符
                            Box(
                                modifier = Modifier
                                    .padding(top = 40.dp)
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ":",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            // 分钟输入
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "分钟",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                                
                                OutlinedTextField(
                                    value = minute,
                                    onValueChange = { newValue ->
                                        // 限制最多2位数
                                        if (newValue.length <= 2) {
                                            minute = newValue
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 28.sp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color(0xFFBDBDBD),
                                        unfocusedContainerColor = Color(0xFFF5F5F5)
                                    )
                                )
                                
                                // 分钟验证消息
                                if (minute.isNotEmpty() && !isMinuteValid) {
                                    Text(
                                        text = "0-59之间",
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        // 时间预览
                        if (isHourValid && isMinuteValid) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "您设置的时间是：",
                                    fontSize = 18.sp
                                )
                                
                                Text(
                                    text = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                    
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
                    
                    // 提醒方式选择列表（仅在提醒开启时显示）
                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 标题和错误提示
                        Column {
                            Text(
                                text = "提醒方式（至少选择一项）",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            
                            // 显示错误消息
                            if (showReminderError) {
                                Text(
                                    text = "请至少选择一种提醒方式",
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        
                        // 使用可滚动的Column来确保所有选项都能显示
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            // 通知提醒选项
                            ReminderTypeCheckbox(
                                isChecked = notificationEnabled,
                                text = "通知提醒",
                                onCheckedChange = { checked ->
                                    if (checked || (vibrationEnabled || alarmSoundEnabled || voiceEnabled)) {
                                        notificationEnabled = checked
                                        showReminderError = false
                                    } else {
                                        showReminderError = true
                                        textToSpeechService?.speak("请至少选择一种提醒方式")
                                    }
                                }
                            )
                            
                            // 震动提醒选项
                            ReminderTypeCheckbox(
                                isChecked = vibrationEnabled,
                                text = "震动提醒",
                                onCheckedChange = { checked ->
                                    if (checked || (notificationEnabled || alarmSoundEnabled || voiceEnabled)) {
                                        vibrationEnabled = checked
                                        showReminderError = false
                                    } else {
                                        showReminderError = true
                                        textToSpeechService?.speak("请至少选择一种提醒方式")
                                    }
                                }
                            )
                            
                            // 闹铃声音选项
                            ReminderTypeCheckbox(
                                isChecked = alarmSoundEnabled,
                                text = "闹铃声音",
                                onCheckedChange = { checked ->
                                    if (checked || (notificationEnabled || vibrationEnabled || voiceEnabled)) {
                                        alarmSoundEnabled = checked
                                        showReminderError = false
                                    } else {
                                        showReminderError = true
                                        textToSpeechService?.speak("请至少选择一种提醒方式")
                                    }
                                }
                            )
                            
                            // 语音播报选项
                            ReminderTypeCheckbox(
                                isChecked = voiceEnabled,
                                text = "语音播报",
                                onCheckedChange = { checked ->
                                    if (checked || (notificationEnabled || vibrationEnabled || alarmSoundEnabled)) {
                                        voiceEnabled = checked
                                        showReminderError = false
                                    } else {
                                        showReminderError = true
                                        textToSpeechService?.speak("请至少选择一种提醒方式")
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 检查是否至少选择了一种提醒方式
                        if (reminderEnabled && !isAtLeastOneReminderSelected()) {
                            showReminderError = true
                            textToSpeechService?.speak("请至少选择一种提醒方式")
                            return@Button
                        }
                        
                        try {
                            val item = ScheduleManager.ScheduleItem(
                                id = scheduleItem?.id ?: UUID.randomUUID().toString(),
                                time = "$hour:$minute",
                                title = title,
                                description = description,
                                reminderEnabled = reminderEnabled,
                                notificationEnabled = notificationEnabled,
                                vibrationEnabled = vibrationEnabled,
                                alarmSoundEnabled = alarmSoundEnabled,
                                voiceEnabled = voiceEnabled
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
                                textToSpeechService?.speak("${actionText}成功：$title，时间：$hour:$minute")
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

/**
 * 提醒类型复选框组件
 */
@Composable
fun ReminderTypeCheckbox(
    isChecked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            fontSize = 16.sp
        )
    }
} 