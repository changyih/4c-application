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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    onVideoCallClick: () -> Unit
) {
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
            BottomNavigationBar(onVideoCallClick, textToSpeechService)
        }
    ) { paddingValues ->
        if (isVoiceMode) {
            // 语音模式界面
            VoiceModeScreen(textToSpeechService)
        } else {
            // 普通模式界面
            NormalModeScreen(scrollState, paddingValues, textToSpeechService)
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
fun NormalModeScreen(scrollState: ScrollState, paddingValues: PaddingValues, textToSpeechService: TextToSpeechService) {
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
        FunctionNavigation(textToSpeechService)
        
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
    textToSpeechService: TextToSpeechService
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
                onClick = { textToSpeechService.speak("消息") }
            )
            
            // 我的按钮
            BottomNavItem(
                text = "我的",
                isSelected = false,
                onClick = { textToSpeechService.speak("我的") }
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
fun FunctionNavigation(textToSpeechService: TextToSpeechService) {
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
            FunctionItem(title = "健康数据", color = BrightBlue, textToSpeechService)
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
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { textToSpeechService.speak(title) }
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