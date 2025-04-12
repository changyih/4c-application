package com.example.olderperson.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.foundation.border
import androidx.compose.ui.text.TextStyle
import com.example.olderperson.service.AlibabaQianwenService
import android.widget.Toast
import android.content.Context
import com.example.olderperson.SoundSettings
import android.content.Intent
import android.net.Uri
import com.example.olderperson.utils.EmergencyContactsManager
import androidx.compose.foundation.BorderStroke

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
    val context = LocalContext.current
    // 获取紧急联系人数据
    val emergencyContact = EmergencyContactsManager.getEmergencyContact(context)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部问候区域
            TopGreetingSection(userName, textToSpeechService, context)
            
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
        
        // 紧急呼叫按钮（固定在右下角）
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 16.dp)
        ) {
            EmergencyCallButton(
                emergencyContact = emergencyContact,
                textToSpeechService = textToSpeechService,
                context = context
            )
        }
    }
}

@Composable
private fun TopGreetingSection(userName: String, textToSpeechService: TextToSpeechService? = null, context: Context) {
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
                onClick = { 
                    Toast.makeText(context, "语音输入功能开发中", Toast.LENGTH_SHORT).show()
                }
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
    
    // 新增：城市选择对话框状态
    var showCityDialog by remember { mutableStateOf(false) }
    
    // 新增：显示按钮状态
    var showButtons by remember { mutableStateOf(false) }
    
    // 新增：健康情况对话框状态
    var showHealthDialog by remember { mutableStateOf(false) }
    
    // 新增：养生方案对话框状态
    var showWellnessPlanDialog by remember { mutableStateOf(false) }
    
    // 新增：养生方案内容
    var wellnessPlanContent by remember { mutableStateOf("") }
    
    // 新增：加载中状态
    var isLoading by remember { mutableStateOf(false) }
    
    // 新增：获取通义千问服务实例
    val qianwenService = remember { AlibabaQianwenService(context) }
    
    // 新增：健康信息状态
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("男") }
    var medicalHistory by remember { mutableStateOf("") }
    var dietaryRestrictions by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    
    // 新增：城市列表
    val cityList = listOf("长春", "北京", "上海", "广州")
    
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
    
    // 新增：城市选择对话框
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("选择城市") },
            text = {
                Column {
                    cityList.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (city != currentCity) {
                                        currentCity = city
                                        // 播报切换城市的语音提示
                                        textToSpeechService?.speak("正在切换到${city}天气")
                                        Log.d("CareHomeScreen", "城市切换: ${weatherInfo?.city} -> $city")
                                    }
                                    showCityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (city == currentCity) Primary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = city,
                                fontSize = 16.sp,
                                fontWeight = if (city == currentCity) FontWeight.Bold else FontWeight.Normal,
                                color = if (city == currentCity) Primary else Color.Black
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 新增：健康情况输入对话框
    if (showHealthDialog) {
        AlertDialog(
            onDismissRequest = { showHealthDialog = false },
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { 
                Text(
                    text = "添加健康情况",
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
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 说明文字
                    Text(
                        text = "请填写您的健康信息，帮助我们为您提供更精准的养生建议",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 24.dp)
                    )
                    
                    // 年龄输入
                    Text(
                        text = "年龄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        placeholder = { Text("请输入您的年龄", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        singleLine = true,
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
                    
                    // 性别选择
                    Text(
                        text = "性别",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (gender == "男") Color.White.copy(alpha = 0.2f) else Color(0xFF4682B4).copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = if (gender == "男") 0.9f else 0.5f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { gender = "男" }
                                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            RadioButton(
                                selected = gender == "男",
                                onClick = { gender = "男" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                            Text(
                                text = "男",
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (gender == "女") Color.White.copy(alpha = 0.2f) else Color(0xFF4682B4).copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = if (gender == "女") 0.9f else 0.5f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { gender = "女" }
                                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            RadioButton(
                                selected = gender == "女",
                                onClick = { gender = "女" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                            Text(
                                text = "女",
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    // 疾病史输入
                    Text(
                        text = "疾病史（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = medicalHistory,
                        onValueChange = { medicalHistory = it },
                        placeholder = { Text("例如：高血压、糖尿病等", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
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
                    
                    // 忌口输入
                    Text(
                        text = "忌口（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = dietaryRestrictions,
                        onValueChange = { dietaryRestrictions = it },
                        placeholder = { Text("例如：海鲜、辛辣食物等", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
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
                    
                    // 过敏情况输入
                    Text(
                        text = "过敏情况（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        placeholder = { Text("例如：花粉、药物等", fontSize = 16.sp, color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 取消按钮
                        Button(
                            onClick = { showHealthDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "取消",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                        
                        // 确认按钮
                        Button(
                            onClick = {
                                // 保存健康信息并朗读确认信息
                                val confirmText = "已保存您的健康信息。年龄：$age，性别：$gender" +
                                        (if (medicalHistory.isNotEmpty()) "，疾病史：$medicalHistory" else "") +
                                        (if (dietaryRestrictions.isNotEmpty()) "，忌口：$dietaryRestrictions" else "") +
                                        (if (allergies.isNotEmpty()) "，过敏情况：$allergies" else "")
                                textToSpeechService?.speak(confirmText)
                                // TODO: 保存健康信息到数据库或SharedPreferences
                                showHealthDialog = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Text(
                                text = "确认",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4682B4)
                            )
                        }
                    }
                }
            },
            confirmButton = { },
            shape = RoundedCornerShape(24.dp)
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // 点击卡片切换显示按钮状态
                showButtons = !showButtons
                
                // 如果天气信息已加载，播报详细信息
                weatherInfo?.let { info ->
                    speakWeatherInfo(info, textToSpeechService)
                } ?: run {
                    // 如果尚未加载，播报加载中提示
                    textToSpeechService?.speak("正在获取天气信息，请稍候")
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE86A58)
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
                    // 城市名称 - 修改为可点击
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showCityDialog = true }
                    ) {
                        Text(
                            text = weatherInfo?.city ?: "长春",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "选择城市",
                            tint = Color.White
                        )
                    }
                    
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
                
                // 新增：显示两个按钮
                AnimatedVisibility(
                    visible = showButtons,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // 添加情况按钮
                            WeatherActionButton(
                                icon = Icons.Default.Add,
                                text = "添加情况",
                                onClick = {
                                    textToSpeechService?.speak("添加健康情况")
                                    showHealthDialog = true
                                }
                            )
                            
                            // 生成养生方案按钮
                            WeatherActionButton(
                                icon = Icons.Default.Favorite,
                                text = "生成养生方案",
                                onClick = {
                                    textToSpeechService?.speak("正在为您生成养生方案")
                                    // 显示养生方案对话框并开始加载
                                    isLoading = true
                                    showWellnessPlanDialog = true
                                    
                                    // 使用千问API生成养生方案
                                    coroutineScope.launch {
                                        try {
                                            // 构建提示词
                                            val prompt = buildPromptForQianwen(
                                                age = age,
                                                gender = gender,
                                                medicalHistory = medicalHistory,
                                                dietaryRestrictions = dietaryRestrictions,
                                                allergies = allergies,
                                                weatherInfo = weatherInfo
                                            )
                                            
                                            // 调用千问API
                                            wellnessPlanContent = qianwenService.sendTextMessage(prompt)
                                            isLoading = false
                                        } catch (e: Exception) {
                                            // 异常处理
                                            Log.e("CareHomeScreen", "生成养生方案失败", e)
                                            wellnessPlanContent = "抱歉，生成养生方案时遇到问题。请稍后再试。"
                                            isLoading = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 新增：养生方案对话框
    if (showWellnessPlanDialog) {
        AlertDialog(
            onDismissRequest = { showWellnessPlanDialog = false },
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { 
                Text(
                    text = "个性化养生方案",
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
                    if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "正在生成养生方案...",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = wellnessPlanContent,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color.White
                            )
                            
                            // 按钮区域
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 朗读按钮
                                Button(
                                    onClick = { textToSpeechService?.speak(wellnessPlanContent) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp, 
                                            contentDescription = "朗读", 
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "朗读",
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                                
                                // 关闭按钮
                                Button(
                                    onClick = { showWellnessPlanDialog = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "关闭",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF4682B4)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// 新增：天气操作按钮组件
@Composable
private fun WeatherActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
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
        // 确保使用全局设置的语速
        it.setSpeechRate(SoundSettings.speechRate.value)
        it.setVolume(SoundSettings.volume.value)
        
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

/**
 * 构建发送给千问的提示词
 */
private fun buildPromptForQianwen(
    age: String,
    gender: String,
    medicalHistory: String,
    dietaryRestrictions: String,
    allergies: String,
    weatherInfo: WeatherManager.Companion.WeatherInfo?
): String {
    val weather = weatherInfo?.weather ?: "晴"
    val temperature = weatherInfo?.temperature ?: "25°C"
    val airQuality = weatherInfo?.airQuality ?: "良"
    val airIndex = weatherInfo?.airIndex ?: "75"
    val city = weatherInfo?.city ?: "长春"
    val date = weatherInfo?.date ?: SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINA).format(Date())
    val solarTerm = weatherInfo?.solarTerm ?: "立夏"
    
    return """
        不要使用'**'和'##'这种符号
        你好！我需要根据以下个人信息和当前天气情况，生成一份个性化养生方案。请结合中医养生原则、现代医学建议及天气特征，提供具体、可操作的指导。
        用户信息：
        - 年龄：$age
        - 性别：$gender
        - 疾病史：${medicalHistory.ifEmpty { "无" }}
        - 饮食禁忌：${dietaryRestrictions.ifEmpty { "无" }}
        - 过敏情况：${allergies.ifEmpty { "无" }}
        
        当前天气情况：
        - 城市：$city
        - 日期：$date
        - 节气：$solarTerm
        - 天气：$weather
        - 温度：$temperature
        - 空气质量：$airQuality（指数：$airIndex）
        
        请根据以下维度生成方案，并标注关键注意事项：

饮食建议：
根据节气、温度、空气质量及用户体质，推荐今日食材（标注寒热属性），避免与疾病或过敏相关的禁忌食物（如高血压患者减少高盐食物）。
若空气质量差，增加清肺润燥的食疗方案（如参考《黄帝内经》"天人相应"理论）。
运动与作息：
根据天气温度和健康状况，推荐适合的运动类型与时长（如高温天建议室内拉伸，低温天推荐温补性活动）。
结合节气调整作息（如冬至后"早卧晚起"，夏季"夜卧早起"）。
若用户有慢性病（如关节炎），需避免特定动作（如剧烈跳跃）。
中医调理与防护：
针对用户体质（如阳虚/阴虚），提供穴位按摩、茶饮或艾灸建议。
若空气质量差，建议室内净化措施（如使用加湿器、选择开窗时段）。
结合节气重点（如"芒种防暑湿""冬至补肾"）说明调理方法。
预警与禁忌：
根据疾病史，列出今日需警惕的症状或风险（如高温天中暑预警、心脏病患者避免暴晒）。
若推荐药物或补品，标注每日安全用量及与现有药物的相互作用（参考《中国药典》）。
格式要求：

分点清晰，用标题分隔饮食、运动、中医调理等模块。
语言通俗，必要时解释专业术语（如"痰湿体质"）。
关键建议引用权威来源（如《黄帝内经》或知识库模板）。
给出具体操作描述（如"每天按压足三里穴3分钟"）。
补充说明：

若用户未提供生活习惯，默认饮食无特殊要求，运动强度为低至中等（适合老年人或慢性病患者）。
若用户有应酬或特殊事件（如饮酒），请补充"应酬防护"模块（参考知识库"【应酬防护】模板"）。
需特别标注儿童/孕妇/老年人的安全边界（如运动强度、补品选择）。
请确保方案科学严谨，无伪养生内容（参考知识库"4步揪出伪科学"原则），并包含以下模块：

今日健康重点（核心需求+节气提示）
饮食建议（食材、禁忌、食谱示例）
运动与作息（类型、时间、防护措施）
中医调理方案（穴位/茶饮、环境调节）
预警与禁忌（症状预警、就医信号）
不要使用'**'和'##'这种符号
    """.trimIndent()
}

/**
 * 紧急呼叫按钮
 */
@Composable
private fun EmergencyCallButton(
    emergencyContact: EmergencyContactsManager.EmergencyContact?,
    textToSpeechService: TextToSpeechService?,
    context: Context
) {
    // 是否显示确认对话框
    var showConfirmDialog by remember { mutableStateOf(false) }
    
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
                    onClick = { showConfirmDialog = false },
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("取消")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(80.dp)
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
            .clickable {
                if (emergencyContact != null) {
                    // 直接拨打电话
                    textToSpeechService?.speak("正在拨打紧急联系人${emergencyContact.name}的电话")
                    val intent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:${emergencyContact.phone}")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "无法拨打电话，请检查应用权限", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    textToSpeechService?.speak("请先设置紧急联系人")
                    Toast.makeText(context, "请先设置紧急联系人", Toast.LENGTH_SHORT).show()
                }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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