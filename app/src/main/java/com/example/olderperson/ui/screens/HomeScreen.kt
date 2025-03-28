package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
 * 首页界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    videoCallService: VideoCallService,
    textToSpeechService: TextToSpeechService,
    onVideoCallClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    var showHealthData by remember { mutableStateOf(false) }
    
    if (showHealthData) {
        HealthDataScreen(textToSpeechService)
    } else {
        val scrollState = rememberScrollState()
        val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
        var isVoiceMode by remember { mutableStateOf(false) }
        
        Scaffold(
            topBar = {
                // 顶部状态栏
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "老年关爱",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.clickable { textToSpeechService.speak("老年关爱") }
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable { textToSpeechService.speak("现在时间是 $currentTime") }
                                )
                                
                                // 模式切换按钮
                                IconButton(
                                    onClick = { 
                                        isVoiceMode = !isVoiceMode
                                        textToSpeechService.speak(if (isVoiceMode) "已切换到语音模式" else "已切换到普通模式")
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Primary)
                                ) {
                                    Text(
                                        text = if (isVoiceMode) "文" else "音",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Background
                    )
                )
            },
            bottomBar = {
                // 底部导航栏
                BottomNavigationBar(
                    onVideoCallClick = onVideoCallClick,
                    textToSpeechService = textToSpeechService,
                    onProfileClick = onProfileClick,
                    onMessageClick = onMessageClick
                )
            }
        ) { paddingValues ->
            if (isVoiceMode) {
                // 语音模式界面
                VoiceModeScreen(textToSpeechService)
            } else {
                // 普通模式界面
                NormalModeScreen(
                    scrollState = scrollState,
                    paddingValues = paddingValues,
                    textToSpeechService = textToSpeechService,
                    onHealthDataClick = { showHealthData = true }
                )
            }
        }
    }
}

/**
 * 语音模式界面
 */
@Composable
fun VoiceModeScreen(textToSpeechService: TextToSpeechService) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 语音输入按钮
            Button(
                onClick = { /* TODO: 实现语音输入功能 */ },
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = "音",
                    fontSize = 48.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "点击开始语音输入",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

/**
 * 普通模式界面
 */
@Composable
fun NormalModeScreen(
    scrollState: ScrollState,
    paddingValues: PaddingValues,
    textToSpeechService: TextToSpeechService,
    onHealthDataClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(scrollState)
            .background(Background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 功能导航区域
        FunctionNavigation(
            textToSpeechService = textToSpeechService,
            onHealthDataClick = onHealthDataClick
        )
        
        // 健康数据卡片区域
        HealthDataCards(textToSpeechService)
        
        // 地图和位置信息
        MapInfoCard()
        
        // 健康提醒
        HealthReminderCard()
    }
}

/**
 * 底部导航栏
 */
@Composable
fun BottomNavigationBar(
    onVideoCallClick: () -> Unit,
    textToSpeechService: TextToSpeechService,
    onProfileClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    Surface(
        color = Surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页按钮
            BottomNavItem(
                text = "首页",
                isSelected = true,
                onClick = { textToSpeechService.speak("首页") }
            )
            
            // 服务按钮
            BottomNavItem(
                text = "服务",
                isSelected = false,
                onClick = { textToSpeechService.speak("服务") }
            )
            
            // 消息按钮
            BottomNavItem(
                text = "消息",
                isSelected = false,
                onClick = { 
                    textToSpeechService.speak("消息")
                    onMessageClick()
                }
            )
            
            // 我的按钮
            BottomNavItem(
                text = "我的",
                isSelected = false,
                onClick = { 
                    textToSpeechService.speak("我的")
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
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight()
            .width(80.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Primary else Color.Transparent,
                contentColor = if (isSelected) Color.White else Color.Gray
            ),
            modifier = Modifier
                .size(40.dp),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            // 这里可以放图标，暂时用文字首字母代替
            Text(
                text = text.first().toString(),
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

/**
 * 功能导航区域
 */
@Composable
fun FunctionNavigation(
    textToSpeechService: TextToSpeechService,
    onHealthDataClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "健康服务",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FunctionItem(title = "健康数据", color = BrightBlue, textToSpeechService, onClick = onHealthDataClick)
            FunctionItem(title = "健康计划", color = WarmPink, textToSpeechService)
            FunctionItem(title = "养生天地", color = FreshGreen, textToSpeechService)
            FunctionItem(title = "康复指导", color = OrangeGradient, textToSpeechService)
        }
    }
}

/**
 * 功能导航项
 */
@Composable
fun FunctionItem(
    title: String,
    color: Color,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit = { textToSpeechService.speak(title) }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.first().toString(),
                color = Color.White,
                fontSize = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 健康数据卡片区域
 */
@Composable
fun HealthDataCards(textToSpeechService: TextToSpeechService) {
    Column {
        Text(
            text = "实时健康监测",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 血氧卡片
            HealthDataCard(
                title = "血氧饱和度",
                value = "98%",
                unit = "SaO2",
                time = "3:00 pm",
                modifier = Modifier.weight(1f),
                gradientColors = listOf(PurpleGradient, PurpleGradientEnd),
                textToSpeechService
            )
            
            // 心率卡片
            HealthDataCard(
                title = "心率",
                value = "75",
                unit = "bpm",
                time = "3:05 pm",
                modifier = Modifier.weight(1f),
                gradientColors = listOf(OrangeGradient, OrangeGradientEnd),
                textToSpeechService
            )
        }
    }
}

/**
 * 健康数据卡片
 */
@Composable
fun HealthDataCard(
    title: String,
    value: String,
    unit: String,
    time: String,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { textToSpeechService.speak("$title 为 $value $unit，测量时间是 $time") },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = gradientColors)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 地图信息卡片
 */
@Composable
fun MapInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "地图区域\n(当前位置信息)",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 健康提醒卡片
 */
@Composable
fun HealthReminderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Error),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "健康提醒",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "如有异常数据，系统将自动通知家人",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun HealthDataScreen(
    textToSpeechService: TextToSpeechService
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        HealthDataTopBar()
        
        // 用户信息
        UserInfoSection()
        
        // 数据标签切换
        DataTabs()
        
        // 健康数据图表
        HealthDataChart()
        
        // 健康事件统计
        HealthEvents()
    }
}

@Composable
fun HealthDataTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "健康数据",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "分享",
            tint = Color.Black
        )
    }
}

@Composable
fun UserInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 用户头像
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                tint = Color.White
            )
        }
        
        // 用户信息
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = "李爱梅",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DataItem("78", "年龄")
                DataItem("160", "身高")
                DataItem("54", "体重")
            }
        }
    }
}

@Composable
fun DataItem(value: String, label: String) {
    Column {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun DataTabs() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("数据展示", "统计分析", "健康服务")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTabIndex = index }
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == index) Color.Black else Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(40.dp)
                        .background(
                            if (selectedTabIndex == index) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun HealthDataChart() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF9C27B0), CircleShape)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "98",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康评级",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 这里只是一个占位，实际应该是曲线图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            ) {
                // 简单模拟曲线
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9C27B0).copy(alpha = 0.2f),
                                    Color(0xFF9C27B0).copy(alpha = 0.5f),
                                    Color(0xFF9C27B0).copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun HealthEvents() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 标题
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近一周发生了",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "6",
                fontSize = 16.sp,
                color = Color(0xFFFF5722),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = " 次事件",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
        
        // 事件标尺
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(vertical = 16.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF5722),
                            Color(0xFFFF9800),
                            Color(0xFF4CAF50)
                        )
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
        )
        
        // 事件类型
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EventTypeItem("3 次", "血压偏高警报")
            EventTypeItem("1 次", "身体疼痛")
            EventTypeItem("2 次", "健康电子提醒")
        }
    }
}

@Composable
fun EventTypeItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
} 