package com.example.olderperson.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.components.ScheduleDeleteConfirmDialog
import com.example.olderperson.ui.components.ScheduleEditDialog
import com.example.olderperson.ui.theme.FontSizeConfig
import com.example.olderperson.utils.ScheduleManager

/**
 * 日程安排页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scheduleManager = remember { 
        try {
            ScheduleManager.getInstance(context) 
        } catch (e: Exception) {
            Log.e("ScheduleScreen", "获取ScheduleManager实例失败: ${e.message}", e)
            null
        }
    }
    
    // 状态管理
    var scheduleItems by remember { 
        mutableStateOf(
            try {
                scheduleManager?.getAllScheduleItems() ?: emptyList()
            } catch (e: Exception) {
                Log.e("ScheduleScreen", "获取日程列表失败: ${e.message}", e)
                emptyList()
            }
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedScheduleItem by remember { mutableStateOf<ScheduleManager.ScheduleItem?>(null) }

    // 使用key强制重新组合
    val refreshKey = remember { mutableStateOf(0) }

    // 订阅日程更新
    LaunchedEffect(refreshKey.value) {
        try {
            scheduleItems = scheduleManager?.getAllScheduleItems() ?: emptyList()
        } catch (e: Exception) {
            Log.e("ScheduleScreen", "自动刷新日程列表失败: ${e.message}", e)
        }
    }
    
    // 刷新日程列表
    fun refreshScheduleItems() {
        try {
            scheduleItems = scheduleManager?.getAllScheduleItems() ?: emptyList()
            refreshKey.value = refreshKey.value + 1  // 触发重组
        } catch (e: Exception) {
            Log.e("ScheduleScreen", "刷新日程列表失败: ${e.message}", e)
        }
    }

    // 编辑对话框
    if (showEditDialog && selectedScheduleItem != null) {
        ScheduleEditDialog(
            showDialog = true,
            scheduleItem = selectedScheduleItem,
            onDialogDismiss = { 
                showEditDialog = false 
            },
            onScheduleSave = { item ->
                try {
                    scheduleManager?.updateScheduleItem(item)
                    refreshScheduleItems()
                } catch (e: Exception) {
                    Log.e("ScheduleScreen", "更新日程失败: ${e.message}", e)
                }
            },
            textToSpeechService = textToSpeechService
        )
    }
    
    // 添加对话框
    if (showAddDialog) {
        ScheduleEditDialog(
            showDialog = true,
            onDialogDismiss = { 
                showAddDialog = false 
            },
            onScheduleSave = { item ->
                try {
                    scheduleManager?.addScheduleItem(item)
                    refreshScheduleItems()
                } catch (e: Exception) {
                    Log.e("ScheduleScreen", "添加日程失败: ${e.message}", e)
                }
            },
            textToSpeechService = textToSpeechService
        )
    }
    
    // 删除确认对话框
    if (showDeleteDialog && selectedScheduleItem != null) {
        ScheduleDeleteConfirmDialog(
            showDialog = true,
            scheduleItem = selectedScheduleItem,
            onConfirm = {
                try {
                    selectedScheduleItem?.id?.let { scheduleManager?.deleteScheduleItem(it) }
                    refreshScheduleItems()
                    showDeleteDialog = false
                    textToSpeechService.speak("已删除安排：${selectedScheduleItem?.title}")
                } catch (e: Exception) {
                    Log.e("ScheduleScreen", "删除日程失败: ${e.message}", e)
                    showDeleteDialog = false
                }
            },
            onDismiss = { 
                showDeleteDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "今日安排",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 标题和添加按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 标题图标和文字
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = "今日安排",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(26.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "今日安排",
                                fontSize = FontSizeConfig.scaledSp(18).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        
                        // 添加按钮
                        IconButton(
                            onClick = { 
                                showAddDialog = true
                                textToSpeechService.speak("添加新日程安排")
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "添加安排",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 没有日程时显示的内容
                    if (scheduleItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.EventBusy,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "今日暂无安排",
                                    fontSize = FontSizeConfig.scaledSp(16).sp,
                                    color = Color.Gray
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Button(
                                    onClick = { 
                                        showAddDialog = true
                                        textToSpeechService.speak("添加新日程安排")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("添加安排")
                                }
                            }
                        }
                    } else {
                        // 日程列表
                        scheduleItems.forEachIndexed { index, item ->
                            if (index > 0) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFEEEEEE),
                                    thickness = 1.dp
                                )
                            }
                            
                            ScheduleItemWithActions(
                                scheduleItem = item,
                                scheduleManager = scheduleManager,
                                onEdit = {
                                    selectedScheduleItem = item
                                    showEditDialog = true
                                    textToSpeechService.speak("编辑安排：${item.title}")
                                },
                                onDelete = {
                                    selectedScheduleItem = item
                                    showDeleteDialog = true
                                },
                                onStatusChange = {
                                    refreshScheduleItems()
                                    if (item.isCompleted) {
                                        textToSpeechService.speak("${item.title}已标记为未完成")
                                    } else {
                                        textToSpeechService.speak("${item.title}已标记为完成")
                                    }
                                }
                            )
                        }
                        
                        // 空间不足时的添加按钮
                        if (scheduleItems.size < 4) {
                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color(0xFFEEEEEE),
                                thickness = 1.dp
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        showAddDialog = true
                                        textToSpeechService.speak("添加新日程安排")
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "添加安排",
                                        tint = Color(0xFF4CAF50)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "添加安排",
                                        fontSize = FontSizeConfig.scaledSp(16).sp,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemWithActions(
    scheduleItem: ScheduleManager.ScheduleItem,
    scheduleManager: ScheduleManager?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: () -> Unit = {}
) {
    var showActions by remember { mutableStateOf(false) }
    
    // 根据完成状态确定背景颜色
    val backgroundColor = if (scheduleItem.isCompleted) {
        Color(0xFFE8F5E9) // 淡绿色背景表示已完成
    } else {
        Color(0xFFE3F2FD) // 原来的蓝色背景
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (scheduleItem.isCompleted) Color(0xFFE8F5E9) else Color.White)
            .clickable { showActions = !showActions }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = scheduleItem.time,
                    fontSize = FontSizeConfig.scaledSp(18).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (scheduleItem.isCompleted) Color(0xFF2E7D32) else Color(0xFF1976D2)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scheduleItem.title,
                    fontSize = FontSizeConfig.scaledSp(16).sp,
                    fontWeight = FontWeight.Medium,
                    color = if (scheduleItem.isCompleted) Color(0xFF2E7D32) else Color.Black,
                    textDecoration = if (scheduleItem.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = scheduleItem.description,
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = if (scheduleItem.isCompleted) Color(0xFF81C784) else Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 完成状态指示
                if (scheduleItem.isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "已完成",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "已完成",
                            fontSize = FontSizeConfig.scaledSp(12).sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            // 当操作按钮未显示时，显示展开箭头指示可点击
            if (!showActions) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "显示更多",
                    tint = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        
        // 操作按钮
        AnimatedVisibility(
            visible = showActions,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 已完成按钮
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            scheduleManager?.updateScheduleItemCompletionStatus(
                                scheduleItem.id,
                                !scheduleItem.isCompleted
                            )
                            onStatusChange()
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = scheduleItem.isCompleted,
                        onCheckedChange = { isChecked ->
                            scheduleManager?.updateScheduleItemCompletionStatus(
                                scheduleItem.id,
                                isChecked
                            )
                            onStatusChange()
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (scheduleItem.isCompleted) "已完成" else "标记为已完成",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = if (scheduleItem.isCompleted) Color(0xFF4CAF50) else Color.Gray
                    )
                }
                
                Row {
                    // 编辑按钮
                    IconButton(
                        onClick = { onEdit() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = Color(0xFF2196F3)
                        )
                    }
                    
                    // 删除按钮
                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = Color(0xFFE57373)
                        )
                    }
                }
            }
        }
    }
} 