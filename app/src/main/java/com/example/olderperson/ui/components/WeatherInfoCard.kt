package com.example.olderperson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.utils.WeatherManager
import com.example.olderperson.ui.theme.Primary
import com.example.olderperson.ui.theme.PrimaryLight
import com.example.olderperson.ui.theme.Background
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherInfoCard(
    weatherInfo: WeatherManager.Companion.WeatherInfo,
    textToSpeechService: TextToSpeechService? = null,
    onCardClick: () -> Unit = {}
) {
    val updateTime = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { 
                textToSpeechService?.speak(
                    "天气信息: ${weatherInfo.city}, ${weatherInfo.weather}, 气温${weatherInfo.temperature}, " +
                    "空气${weatherInfo.airQuality}, 空气指数${weatherInfo.airIndex}, " +
                    "${weatherInfo.date}, ${weatherInfo.solarTerm}"
                )
                onCardClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 顶部城市和天气信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧城市和日期信息
                Column {
                    Text(
                        text = weatherInfo.city,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = weatherInfo.date,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = weatherInfo.solarTerm,
                        fontSize = 14.sp,
                        color = Primary
                    )
                }
                
                // 右侧天气图标和温度
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // 天气图标
                    Icon(
                        imageVector = getWeatherIcon(weatherInfo.weather),
                        contentDescription = weatherInfo.weather,
                        modifier = Modifier.size(48.dp),
                        tint = Primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 温度
                    Text(
                        text = weatherInfo.temperature,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 下方空气质量信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = PrimaryLight,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 天气描述
                Text(
                    text = weatherInfo.weather,
                    fontSize = 18.sp,
                    color = Color.DarkGray
                )
                
                // 空气质量指数
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "空气指数",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = weatherInfo.airIndex,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                
                // 空气质量
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "空气质量",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = weatherInfo.airQuality,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = getAirQualityColor(weatherInfo.airQuality)
                    )
                }
            }
            
            // 更新时间
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "更新时间: $updateTime",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// 加载中或无数据状态的卡片
@Composable
fun WeatherLoadingCard(
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "正在获取天气信息...",
                    color = Color.Gray
                )
            }
        }
    }
}

// 根据天气描述获取对应的图标
@Composable
fun getWeatherIcon(weather: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        weather.contains("晴") -> Icons.Default.WbSunny
        weather.contains("云") -> Icons.Default.Cloud
        weather.contains("阴") -> Icons.Default.Cloud
        weather.contains("雨") -> Icons.Default.Grain
        weather.contains("雪") -> Icons.Default.AcUnit
        weather.contains("雾") || weather.contains("霾") -> Icons.Default.CloudQueue
        weather.contains("风") || weather.contains("飓风") -> Icons.Default.Air
        else -> Icons.Default.WbSunny // 默认晴天图标
    }
}

// 根据空气质量获取对应的颜色
fun getAirQualityColor(quality: String): Color {
    return when (quality) {
        "优" -> Color(0xFF66BB6A) // 绿色
        "良" -> Color(0xFFAED581) // 浅绿色
        "轻度污染" -> Color(0xFFFFD54F) // 黄色
        "中度污染" -> Color(0xFFFF9800) // 橙色
        "重度污染" -> Color(0xFFE53935) // 红色
        else -> Color(0xFF66BB6A) // 默认绿色
    }
} 