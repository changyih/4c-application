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
            title = { 
                Text(
                    text = "添加健康情况",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 说明文字
                    Text(
                        text = "请填写您的健康信息，帮助我们为您提供更精准的养生建议",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 年龄输入
                    Text(
                        text = "年龄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        placeholder = { Text("请输入您的年龄", fontSize = 16.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Primary,
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.DarkGray
                        )
                    )
                    
                    // 性别选择
                    Text(
                        text = "性别",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (gender == "男") Primary.copy(alpha = 0.1f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (gender == "男") Primary else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { gender = "男" }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = gender == "男",
                                onClick = { gender = "男" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Primary
                                )
                            )
                            Text(
                                text = "男",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (gender == "女") Primary.copy(alpha = 0.1f) else Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = if (gender == "女") Primary else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { gender = "女" }
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = gender == "女",
                                onClick = { gender = "女" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Primary
                                )
                            )
                            Text(
                                text = "女",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    
                    // 疾病史输入
                    Text(
                        text = "疾病史（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = medicalHistory,
                        onValueChange = { medicalHistory = it },
                        placeholder = { Text("例如：高血压、糖尿病等", fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Primary,
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.DarkGray
                        )
                    )
                    
                    // 忌口输入
                    Text(
                        text = "忌口（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = dietaryRestrictions,
                        onValueChange = { dietaryRestrictions = it },
                        placeholder = { Text("例如：海鲜、辛辣食物等", fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Primary,
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.DarkGray
                        )
                    )
                    
                    // 过敏情况输入
                    Text(
                        text = "过敏情况（选填）",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        placeholder = { Text("例如：花粉、药物等", fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(fontSize = 18.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Primary,
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.DarkGray
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 取消按钮
                    Button(
                        onClick = { showHealthDialog = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 18.sp,
                            color = Color.Black
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
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        )
                    ) {
                        Text(
                            text = "确认",
                            fontSize = 18.sp
                        )
                    }
                }
            }
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
                                    
                                    // 使用本地生成的养生方案
                                    coroutineScope.launch {
                                        try {
                                            // 短暂延迟模拟生成过程
                                            kotlinx.coroutines.delay(1000)
                                            
                                            // 本地生成养生方案内容
                                            wellnessPlanContent = generateLocalWellnessPlan(
                                                age = age,
                                                gender = gender,
                                                medicalHistory = medicalHistory,
                                                dietaryRestrictions = dietaryRestrictions,
                                                allergies = allergies,
                                                weatherInfo = weatherInfo
                                            )
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
            title = { 
                Text(
                    text = "个性化养生方案",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "正在生成养生方案...",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = wellnessPlanContent,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        textToSpeechService?.speak(wellnessPlanContent)
                        showWellnessPlanDialog = false 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text(
                        text = if (isLoading) "请稍候" else "朗读并关闭",
                        fontSize = 18.sp
                    )
                }
            }
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

// 添加本地养生方案生成函数
private fun generateLocalWellnessPlan(
    age: String,
    gender: String,
    medicalHistory: String,
    dietaryRestrictions: String,
    allergies: String,
    weatherInfo: WeatherManager.Companion.WeatherInfo?
): String {
    val ageInt = age.toIntOrNull() ?: 65
    val weather = weatherInfo?.weather ?: "晴"
    val temperature = weatherInfo?.temperature?.replace("°C", "")?.toIntOrNull() ?: 25
    val airQuality = weatherInfo?.airQuality ?: "良"
    val city = weatherInfo?.city ?: "长春"
    val solarTerm = weatherInfo?.solarTerm ?: ""
    
    val sb = StringBuilder()
    
    // 添加标题和个性化问候
    sb.append("【个性化养生方案】\n\n")
    sb.append("尊敬的${if (gender == "男") "先生" else "女士"}，以下是根据您的个人情况和今天的天气为您定制的养生建议：\n\n")
    
    // 天气相关建议
    sb.append("【天气调养建议】\n")
    
    when {
        weather.contains("雨") -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 雨天空气湿度较大，注意保持室内通风，预防风湿病和关节炎症状加重。\n")
            sb.append("• 出门请携带雨具，穿防滑鞋，避免因路滑而跌倒。\n")
        }
        weather.contains("雪") -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 雪天温度低，注意保暖，特别是颈部、腰部和关节处，预防寒气入侵。\n")
            sb.append("• 减少外出，若必须外出，请穿防滑鞋，注意路面结冰情况。\n")
        }
        weather.contains("风") -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 风天外出注意保暖，尤其是头部和颈部，预防受风寒。\n")
            sb.append("• 风大时减少户外活动，以免灰尘和花粉对呼吸道造成刺激。\n")
        }
        temperature > 30 -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 高温天气请避免在室外高温环境中长时间活动，预防中暑。\n")
            sb.append("• 多喝水，每天不少于1500ml，及时补充水分和电解质。\n")
            sb.append("• 可适当食用清热解暑的食物，如绿豆汤、西瓜等。\n")
        }
        temperature < 5 -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 低温天气注意保暖，尤其是头部、颈部和腰部，穿着宽松保暖的衣物。\n")
            sb.append("• 增加热量摄入，可适当食用生姜、大枣等温性食物。\n")
        }
        else -> {
            sb.append("• 今天${city}天气${weather}，气温${weatherInfo?.temperature}，空气质量${airQuality}。\n")
            sb.append("• 天气适宜，可进行适度的户外活动，如散步、太极拳等。\n")
            sb.append("• 注意早晚温差，随时增减衣物，预防感冒。\n")
        }
    }
    
    // 空气质量建议
    if (airQuality.contains("优")) {
        sb.append("• 空气质量优良，适合进行户外活动，建议晨练或傍晚散步15-30分钟。\n")
    } else if (airQuality.contains("良")) {
        sb.append("• 空气质量良好，适合进行户外活动，但时间不宜过长，建议控制在1小时以内。\n")
    } else {
        sb.append("• 空气质量${airQuality}，建议减少户外活动，外出佩戴口罩，回家后及时清洁面部和鼻腔。\n")
    }
    
    // 饮食建议
    sb.append("\n【饮食调养建议】\n")
    
    // 根据节气提供饮食建议
    if (solarTerm.isNotEmpty()) {
        when {
            solarTerm.contains("立春") || solarTerm.contains("雨水") || solarTerm.contains("惊蛰") -> {
                sb.append("• 春季养生宜温补阳气，可多食用葱、姜、蒜等辛温食物。\n")
                sb.append("• 早春时节肝气渐长，宜食用菠菜、荠菜等春季时令蔬菜。\n")
            }
            solarTerm.contains("春分") || solarTerm.contains("清明") || solarTerm.contains("谷雨") -> {
                sb.append("• 仲春时节阳气上升，宜食清淡，多吃绿色蔬菜和水果。\n")
                sb.append("• 可适量食用山药、枸杞等滋补肝肾的食物。\n")
            }
            solarTerm.contains("立夏") || solarTerm.contains("小满") || solarTerm.contains("芒种") -> {
                sb.append("• 初夏饮食宜清淡，可多食用绿豆、苦瓜等清热食物。\n")
                sb.append("• 注意补充水分，可饮用菊花茶、绿茶等清热解暑的饮品。\n")
            }
            solarTerm.contains("夏至") || solarTerm.contains("小暑") || solarTerm.contains("大暑") -> {
                sb.append("• 盛夏饮食宜清热解暑，可多食用西瓜、黄瓜等生津止渴的食物。\n")
                sb.append("• 适当食用红豆、薏仁等利水渗湿的食物。\n")
            }
            solarTerm.contains("立秋") || solarTerm.contains("处暑") || solarTerm.contains("白露") -> {
                sb.append("• 初秋养生宜润燥，可多食用梨、银耳等滋阴润肺的食物。\n")
                sb.append("• 适当食用芝麻、蜂蜜等养阴润燥的食物。\n")
            }
            solarTerm.contains("秋分") || solarTerm.contains("寒露") || solarTerm.contains("霜降") -> {
                sb.append("• 深秋时节燥气当令，宜食用滋阴润肺食物，如百合、银耳等。\n")
                sb.append("• 可适量食用山药、莲子等健脾益肺的食物。\n")
            }
            solarTerm.contains("立冬") || solarTerm.contains("小雪") || solarTerm.contains("大雪") -> {
                sb.append("• 初冬养生宜温补阳气，可适量食用羊肉、核桃等温补食物。\n")
                sb.append("• 多食用当季蔬菜，如白萝卜、白菜等。\n")
            }
            solarTerm.contains("冬至") || solarTerm.contains("小寒") || solarTerm.contains("大寒") -> {
                sb.append("• 深冬时节寒气盛行，宜温补肾阳，可适量食用羊肉、狗肉等温补食物。\n")
                sb.append("• 多食用黑豆、黑芝麻等黑色食物，有助于补肾。\n")
            }
            else -> {
                sb.append("• 应季饮食，多吃时令蔬果，保持饮食均衡。\n")
                sb.append("• 注意少盐少油，多吃新鲜蔬菜和水果。\n")
            }
        }
    } else {
        sb.append("• 应季饮食，多吃时令蔬果，保持饮食均衡。\n")
        sb.append("• 注意少盐少油，多吃新鲜蔬菜和水果。\n")
    }
    
    // 根据医疗史提供建议
    if (medicalHistory.isNotEmpty()) {
        if (medicalHistory.contains("高血压")) {
            sb.append("• 高血压人群建议限制钠盐摄入，每日食盐摄入量控制在5克以内。\n")
            sb.append("• 多食用芹菜、菠菜等富含钾的食物，有助于降压。\n")
        }
        if (medicalHistory.contains("糖尿病")) {
            sb.append("• 糖尿病人群建议控制碳水化合物摄入，少食多餐，避免食用精制糖和高糖食物。\n")
            sb.append("• 可适量食用苦瓜、黄瓜等降糖食物。\n")
        }
        if (medicalHistory.contains("心脏")) {
            sb.append("• 心脏病患者建议低盐低脂饮食，避免食用油炸、高脂肪食物。\n")
            sb.append("• 适量食用鱼类、坚果等富含不饱和脂肪酸的食物。\n")
        }
    }
    
    // 根据忌口提供建议
    if (dietaryRestrictions.isNotEmpty()) {
        sb.append("• 根据您的饮食忌口（${dietaryRestrictions}），请避免食用这些食物，可选择其他替代品。\n")
    }
    
    // 根据过敏情况提供建议
    if (allergies.isNotEmpty()) {
        sb.append("• 您有${allergies}过敏情况，请特别注意避免接触相关过敏原。\n")
    }
    
    // 运动建议
    sb.append("\n【运动调养建议】\n")
    
    // 根据年龄提供运动建议
    if (ageInt < 60) {
        sb.append("• 可进行中等强度有氧运动，如快走、慢跑、游泳等，每次30-40分钟，每周3-5次。\n")
        sb.append("• 适当进行力量训练，如哑铃、弹力带等，增强肌肉力量和骨密度。\n")
    } else if (ageInt < 70) {
        sb.append("• 建议进行低强度有氧运动，如散步、太极拳、健身操等，每次20-30分钟，每天1次。\n")
        sb.append("• 进行适度的肌肉力量训练，如轻度哑铃、弹力带等，每周2-3次。\n")
    } else {
        sb.append("• 建议进行轻柔的活动，如散步、太极拳、八段锦等，每次15-20分钟，每天1-2次。\n")
        sb.append("• 注重平衡性训练，预防跌倒，可进行简单的站立平衡训练。\n")
    }
    
    // 天气特殊情况的运动建议
    if (weather.contains("雨") || weather.contains("雪")) {
        sb.append("• 今天天气不适宜户外运动，可在室内进行适度活动，如八段锦、站桩等。\n")
    } else if (temperature > 30) {
        sb.append("• 高温天气应避免在中午前后户外运动，建议在清晨或傍晚进行，注意补充水分。\n")
    } else if (temperature < 5) {
        sb.append("• 低温天气外出运动前应充分热身，注意保暖，运动强度不宜过大。\n")
    }
    
    // 根据医疗史调整运动建议
    if (medicalHistory.isNotEmpty()) {
        if (medicalHistory.contains("高血压") || medicalHistory.contains("心脏")) {
            sb.append("• 有高血压或心脏病史，运动时应避免剧烈活动，控制心率不超过(220-年龄)×60%。\n")
            sb.append("• 运动前后测量血压，如有不适立即停止。\n")
        }
        if (medicalHistory.contains("关节炎") || medicalHistory.contains("骨质")) {
            sb.append("• 有关节问题，建议选择低冲击运动，如游泳、太极等，避免跑跳等剧烈活动。\n")
        }
    }
    
    // 作息建议
    sb.append("\n【作息调养建议】\n")
    sb.append("• 保持规律作息，建议晚上10点前入睡，早晨6-7点起床。\n")
    sb.append("• 午休20-30分钟，有助于恢复精力，但不宜时间过长。\n")
    
    // 根据天气调整作息
    if (weather.contains("雨") || weather.contains("阴")) {
        sb.append("• 阴雨天气容易引起情绪低落，可适当增加室内照明，多听轻松愉快的音乐。\n")
    }
    
    // 结语
    sb.append("\n请记住，养生贵在坚持，希望您保持健康愉快的生活！\n")
    sb.append("如有不适，请及时咨询医生。")
    
    return sb.toString()
} 