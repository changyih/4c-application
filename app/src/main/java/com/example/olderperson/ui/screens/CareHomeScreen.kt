package com.example.olderperson.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.FontSizeConfig
import com.example.olderperson.ui.theme.Primary
import com.example.olderperson.utils.WeatherManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air

@Composable
fun CareHomeScreen(
    userName: String,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFamily: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},

    onNavigateToChat: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},

    onNavigateToSettings: () -> Unit = {},

    textToSpeechService: TextToSpeechService? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部问候区域
            TopGreetingSection(userName, textToSpeechService)
            
            // 主要内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 导航按钮区域
                item {
                    NavigationButtons(
                        onNavigateToProfile = {
                            textToSpeechService?.speak("进入我和自己页面")
                            onNavigateToProfile()
                        },
                        onNavigateToFamily = onNavigateToFamily,
                        onNavigateToCommunity = onNavigateToCommunity
                    )
                }
                
                // 今日安排
                item {
                    TodayScheduleCard()
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // 底部导航栏
            BottomNavigationBar(
                onHomeClick = { /* 当前在首页，无需导航 */ },
                onChatClick = onNavigateToChat,
                onExploreClick = onNavigateToExplore,
                onSettingsClick = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun TopGreetingSection(userName: String, textToSpeechService: TextToSpeechService? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "您好，$userName",
                    fontSize = FontSizeConfig.scaledSp(24).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = getCurrentDate(),
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // 用户头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF87CEEB))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // 智能助手对话框
        AssistantChatBox(textToSpeechService)
        
        // 交流按钮区域
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 语音交流按钮
            CommunicationButton(
                icon = Icons.Default.Mic,
                text = "语音交流",
                modifier = Modifier.weight(1f),
                onClick = { textToSpeechService?.speak("语音交流") }
            )

            // 文字交流按钮
            CommunicationButton(
                icon = Icons.Default.Keyboard,
                text = "文字交流",
                modifier = Modifier.weight(1f),
                onClick = { textToSpeechService?.speak("文字交流") }
            )
        }
    }
}

@Composable
private fun AssistantChatBox(textToSpeechService: TextToSpeechService? = null) {
    var weatherInfo by remember { mutableStateOf<WeatherManager.Companion.WeatherInfo?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentCity by remember { mutableStateOf("长春") }
    
    // 获取天气信息
    LaunchedEffect(currentCity) {
        coroutineScope.launch {
            try {
                val info = WeatherManager.getWeatherInfo(context, currentCity)
                weatherInfo = info
                
                // 添加日志输出，确认API调用成功
                Log.d("CareHomeScreen", "天气数据获取成功: ${info.city}, ${info.weather}, ${info.temperature}, 空气质量:${info.airQuality}, 指数:${info.airIndex}")
            } catch (e: Exception) {
                Log.e("CareHomeScreen", "天气数据获取失败", e)
            }
        }
    }

    // 当天更新时间
    val updateTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // 如果天气信息已加载，播报详细信息
                weatherInfo?.let { info ->
                    speakWeatherInfo(info, textToSpeechService)
                } ?: run {
                    // 如果尚未加载，播报加载中提示
                    textToSpeechService?.speak("正在获取天气信息，请稍候")
                }
                
                // 城市切换逻辑
                coroutineScope.launch {
                    val oldCity = currentCity
                    if (currentCity == "长春") {
                        currentCity = "北京"
                    } else if (currentCity == "北京") {
                        currentCity = "上海"
                    } else if (currentCity == "上海") {
                        currentCity = "广州"
                    } else {
                        currentCity = "长春"
                    }
                    
                    // 播报切换城市的语音提示
                    textToSpeechService?.speak("正在切换到${currentCity}天气")
                    
                    Log.d("CareHomeScreen", "城市切换: $oldCity -> $currentCity")
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (weatherInfo == null) {
            // 加载中状态
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "正在获取天气信息...",
                        color = Color.White
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 顶部城市和时间信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 城市名称
                    Text(
                        text = weatherInfo?.city ?: "长春",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    // 更新时间
                    Text(
                        text = "更新: $updateTime",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                // 中间日期和节气
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weatherInfo?.date ?: "-",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterVintage,
                            contentDescription = "节气",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = weatherInfo?.solarTerm ?: "-",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 天气信息主体
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧温度信息
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 温度图标
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = "温度",
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // 温度值
                        Text(
                            text = weatherInfo?.temperature?.replace("°C", "") ?: "-",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Text(
                            text = "°C",
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    
                    // 右侧天气图标
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 天气图标
                        Icon(
                            imageVector = getWeatherIcon(weatherInfo?.weather ?: "晴"),
                            contentDescription = weatherInfo?.weather ?: "天气",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // 天气描述
                        Text(
                            text = weatherInfo?.weather ?: "-",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 下方空气质量信息
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 空气质量图标
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = "空气质量",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    // 空气质量指数
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "指数",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = weatherInfo?.airIndex ?: "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    // 空气质量
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "质量",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = weatherInfo?.airQuality ?: "-",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                // 提示文本(点击切换城市)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "点击切换城市",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// 根据天气描述获取对应的图标
private fun getWeatherIcon(weather: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        weather.contains("晴") -> Icons.Default.WbSunny
        weather.contains("多云") || weather.contains("部分多云") -> Icons.Default.Cloud
        weather.contains("阴") -> Icons.Default.Cloud
        weather.contains("小雨") || weather.contains("中雨") || weather.contains("大雨") -> Icons.Default.Grain
        weather.contains("暴雨") || weather.contains("雷阵雨") -> Icons.Default.Thunderstorm
        weather.contains("雪") || weather.contains("小雪") || weather.contains("中雪") || weather.contains("大雪") -> Icons.Default.AcUnit
        weather.contains("雾") || weather.contains("霾") -> Icons.Default.CloudQueue
        weather.contains("风") || weather.contains("飓风") -> Icons.Default.Air
        weather.contains("沙尘") || weather.contains("扬沙") -> Icons.Default.BrightnessLow
        weather.contains("雷") || weather.contains("闪电") -> Icons.Default.ElectricBolt
        weather.contains("冰雹") -> Icons.Default.Grain
        weather.contains("雨夹雪") -> Icons.Default.AcUnit
        else -> Icons.Default.WbSunny // 默认晴天图标
    }
}

@Composable
private fun NavigationButtons(
    onNavigateToProfile: () -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavigationButton(
            icon = Icons.Default.Person,
            text = "我和自己",
            onClick = {
                // 添加日志确认点击
                Log.d("CareHomeScreen", "我和自己按钮被点击")
                onNavigateToProfile()
            }
        )
        NavigationButton(
            icon = Icons.Default.Home,
            text = "我和家人",
            onClick = onNavigateToFamily
        )
        NavigationButton(
            icon = Icons.Default.People,
            text = "我和社区",
            onClick = onNavigateToCommunity
        )
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { 
                    onClick() 
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF87CEEB),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            fontSize = FontSizeConfig.scaledSp(14).sp,
            color = Color.Black
        )
    }
}

@Composable
private fun TodayScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和查看全部
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
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "今日安排",
                        fontSize = FontSizeConfig.scaledSp(18).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 查看全部
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 查看全部 */ }
                ) {
                    Text(
                        text = "全部",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看全部",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 晨间服药
            ScheduleItem(
                time = "08:00",
                title = "晨间服药",
                description = "降压药 1片，维生素 1片"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 心脏科复诊
            ScheduleItem(
                time = "10:30",
                title = "心脏科复诊",
                description = "市第一人民医院"
            )
        }
    }
}

@Composable
private fun ScheduleItem(
    time: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontSize = FontSizeConfig.scaledSp(16).sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF87CEEB)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = description,
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onChatClick: () -> Unit,
    onExploreClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp), // 增大高度
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                isSelected = true,
                onClick = onHomeClick
            )
            
            // 对话按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Chat,
                label = "对话",
                isSelected = false,
                onClick = onChatClick
            )
            
            // 探索按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Explore,
                label = "探索",
                isSelected = false,
                onClick = onExploreClick
            )
            
            // 设置按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Settings,
                label = "设置",
                isSelected = false,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun HomeBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
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
            tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            fontSize = FontSizeConfig.scaledSp(12).sp
        )
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINESE)
    return dateFormat.format(Date())
}

@Composable
private fun CommunicationButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = text,
                color = Color.White,
                fontSize = FontSizeConfig.scaledSp(14).sp
            )
        }
    }
}

// 播报完整天气信息
fun speakWeatherInfo(info: WeatherManager.Companion.WeatherInfo, tts: TextToSpeechService?) {
    tts?.let {
        val weatherText = """
            ${info.city}今天天气${info.weather}，
            气温${info.temperature}，
            空气质量${info.airQuality}，
            空气指数${info.airIndex}，
            今天是${info.date}，
            ${info.solarTerm}节气期间。
        """.trimIndent()
        
        it.speak(weatherText)
        Log.d("CareHomeScreen", "播报天气信息: $weatherText")
    }
} 