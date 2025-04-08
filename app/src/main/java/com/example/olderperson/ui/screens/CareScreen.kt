package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 呵护模式主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareScreen(
    videoCallService: VideoCallService,
    textToSpeechService: TextToSpeechService,
    onVideoCallClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onServiceClick: () -> Unit = {}
) {
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val currentDate = remember { SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(Date()) }
    val dayOfWeek = remember { getDayOfWeek() }
    
    // 添加导航状态
    var showMySelf by remember { mutableStateOf(false) }
    
    // 显示"我和自己"页面
    if (showMySelf) {
        MySelfScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showMySelf = false }
        )
        return
    }
    
    Scaffold(
        bottomBar = {
            // 底部导航栏
            CareBottomNavigationBar(
                onVideoCallClick = onVideoCallClick,
                textToSpeechService = textToSpeechService,
                onProfileClick = onProfileClick,
                onMessageClick = onMessageClick,
                onHomeClick = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 用户问候区域
            item {
                UserGreetingCard(
                    name = "王伯伯",
                    date = "$currentDate 星期$dayOfWeek",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 智慧伴侣对话框
            item {
                AICompanionCard(
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 功能按钮区域
            item {
                FunctionButtonsRow(
                    textToSpeechService = textToSpeechService,
                    onMySelfClick = { showMySelf = true }
                )
            }
            
            // 今日安排
            item {
                TodayScheduleCard(
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 用户问候卡片
 */
@Composable
fun UserGreetingCard(
    name: String,
    date: String,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "您好，$name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = date,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        
        // 用户头像
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "用户头像",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * 智慧伴侣对话卡片
 */
@Composable
fun AICompanionCard(
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E6853)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI头像
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "智慧伴侣",
                        tint = Color(0xFF1E6853),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "我是您的智慧伴侣",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "今天有什么可以帮您的吗？",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 交流方式按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 语音交流按钮
                Button(
                    onClick = { textToSpeechService.speak("语音交流") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A8C6F)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "语音交流",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "语音交流",
                            color = Color.White
                        )
                    }
                }
                
                // 文字交流按钮
                Button(
                    onClick = { textToSpeechService.speak("文字交流") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A8C6F)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "文字交流",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "文字交流",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 功能按钮行
 */
@Composable
fun FunctionButtonsRow(
    textToSpeechService: TextToSpeechService,
    onMySelfClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 我和自己
        FunctionButton(
            icon = Icons.Default.Person,
            label = "我和自己",
            backgroundColor = Color(0xFF81C784),
            onClick = { 
                textToSpeechService.speak("我和自己")
                onMySelfClick()
            },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 我和家人
        FunctionButton(
            icon = Icons.Default.Home,
            label = "我和家人",
            backgroundColor = Color.Gray,
            onClick = { textToSpeechService.speak("我和家人") },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 我和社区
        FunctionButton(
            icon = Icons.Default.People,
            label = "我和社区",
            backgroundColor = Color.Gray,
            onClick = { textToSpeechService.speak("我和社区") },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 功能按钮
 */
@Composable
fun FunctionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 今日安排卡片
 */
@Composable
fun TodayScheduleCard(
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "今日安排",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "今日安排",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Text(
                    text = "全部 >",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.clickable { textToSpeechService.speak("查看全部安排") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 安排列表
            ScheduleItem(
                time = "08:00",
                title = "晨间服药",
                description = "降压药 1片，维生素 1片",
                textToSpeechService = textToSpeechService
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            ScheduleItem(
                time = "10:30",
                title = "心脏科复诊",
                description = "",
                textToSpeechService = textToSpeechService
            )
        }
    }
}

/**
 * 安排项
 */
@Composable
fun ScheduleItem(
    time: String,
    title: String,
    description: String,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$time，$title，$description") },
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(24.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 底部导航栏
 */
@Composable
fun CareBottomNavigationBar(
    onVideoCallClick: () -> Unit,
    textToSpeechService: TextToSpeechService,
    onProfileClick: () -> Unit,
    onMessageClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "首页",
                isSelected = true,
                onClick = {
                    textToSpeechService.speak("首页")
                    onHomeClick()
                }
            )
            
            // 对话
            BottomNavItem(
                icon = Icons.Default.Chat,
                label = "对话",
                isSelected = false,
                onClick = {
                    textToSpeechService.speak("对话")
                    onMessageClick()
                }
            )
            
            // 探索
            BottomNavItem(
                icon = Icons.Default.Explore,
                label = "探索",
                isSelected = false,
                onClick = {
                    textToSpeechService.speak("探索")
                    onVideoCallClick()
                }
            )
            
            // 设置
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "设置",
                isSelected = false,
                onClick = {
                    textToSpeechService.speak("设置")
                    onProfileClick()
                }
            )
        }
    }
}

/**
 * 底部导航项
 */
@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2D9E64) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF2D9E64) else Color.Gray
        )
    }
}

/**
 * 获取当前星期几
 */
fun getDayOfWeek(): String {
    return when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "一"
        Calendar.TUESDAY -> "二"
        Calendar.WEDNESDAY -> "三"
        Calendar.THURSDAY -> "四"
        Calendar.FRIDAY -> "五"
        Calendar.SATURDAY -> "六"
        Calendar.SUNDAY -> "日"
        else -> ""
    }
}