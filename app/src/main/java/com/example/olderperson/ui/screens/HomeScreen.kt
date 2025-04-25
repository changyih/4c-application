package com.example.olderperson.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.google.android.gms.maps.model.LatLng
import com.example.olderperson.ui.components.LocationMapCard
import com.example.olderperson.ui.components.MapLocation
import com.example.olderperson.ui.components.NearbyPoiSearch
import com.baidu.mapapi.search.core.PoiInfo
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.service.VideoCallService
import com.example.olderperson.service.PhoneCallService
import com.example.olderperson.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import com.baidu.mapapi.model.LatLng as BaiduLatLng
import android.widget.Toast
import androidx.compose.ui.geometry.Offset

/**
 * 首页界面
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    videoCallService: VideoCallService,
    textToSpeechService: TextToSpeechService,
    phoneCallService: PhoneCallService,
    onVideoCallClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    var showHealthData by remember { mutableStateOf(false) }
    var showHealthPlan by remember { mutableStateOf(false) }
    var showRehabilitation by remember { mutableStateOf(false) }
    var showWellness by remember { mutableStateOf(false) }
    var showService by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Background)) {
        if (showHealthData) {
            HealthDataScreen(
                textToSpeechService = textToSpeechService,
                onBackClick = { showHealthData = false }
            )
        } else if (showHealthPlan) {
            HealthPlanScreen(
                textToSpeechService = textToSpeechService,
                onBackClick = { showHealthPlan = false }
            )
        } else if (showRehabilitation) {
            RehabilitationScreen(
                textToSpeechService = textToSpeechService,
                onBackClick = { showRehabilitation = false }
            )
        } else if (showService) {
            ServiceScreen(
                textToSpeechService = textToSpeechService,
                onBackClick = { showService = false }
            )
        } else if (showWellness) {
            WellnessScreen(
                textToSpeechService = textToSpeechService,
                onBackClick = { showWellness = false }
            )
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
                                    text = "慧龄",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.clickable { textToSpeechService.speak("慧龄") }
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
                },
                containerColor = Background
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
                        onHealthDataClick = { showHealthData = true },
                        onHealthPlanClick = { showHealthPlan = true },
                        onRehabilitationClick = { showRehabilitation = true },
                        onWellnessClick = { showWellness = true }
                    )
                }
            }
        }
    }
}

/**
 * 语音模式界面
 */
@Composable
fun VoiceModeScreen(textToSpeechService: TextToSpeechService) {
    val context = LocalContext.current
    
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
                onClick = { 
                    Toast.makeText(context, "语音输入功能开发中", Toast.LENGTH_SHORT).show() 
                },
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
    onHealthDataClick: () -> Unit = {},
    onHealthPlanClick: () -> Unit = {},
    onRehabilitationClick: () -> Unit = {},
    onWellnessClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
            .background(Background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 功能导航区域
        FunctionNavigation(
            textToSpeechService = textToSpeechService,
            onHealthDataClick = onHealthDataClick,
            onHealthPlanClick = onHealthPlanClick,
            onRehabilitationClick = onRehabilitationClick,
            onWellnessClick = onWellnessClick
        )
        
        // 健康数据卡片区域
        HealthDataCards(textToSpeechService)
        
        // 地图和位置信息
        MapInfoCard(textToSpeechService = textToSpeechService)
        
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
    var showService by remember { mutableStateOf(false) }
    
    if (showService) {
        ServiceScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showService = false }
        )
        return
    }
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
                onClick = { 
                    textToSpeechService.speak("服务")
                    showService = true
                }
            )
            
            // 消息按钮
            BottomNavItem(
                text = "对话",
                isSelected = false,
                onClick = { 
                    textToSpeechService.speak("对话")
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
    onHealthDataClick: () -> Unit = {},
    onHealthPlanClick: () -> Unit = {},
    onRehabilitationClick: () -> Unit = {},
    onWellnessClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FunctionItem(
            title = "健康数据", 
            icon = Icons.Default.Favorite, 
            textToSpeechService = textToSpeechService, 
            onClick = onHealthDataClick
        )
        FunctionItem(
            title = "健康计划", 
            icon = Icons.Default.DateRange, 
            textToSpeechService = textToSpeechService,
            onClick = onHealthPlanClick
        )
        FunctionItem(
            title = "养生天地", 
            icon = Icons.Default.LocalCafe, 
            textToSpeechService = textToSpeechService,
            onClick = onWellnessClick
        )
        FunctionItem(
            title = "康复指导", 
            icon = Icons.Default.Healing, 
            textToSpeechService = textToSpeechService,
            onClick = onRehabilitationClick
        )
    }
}

/**
 * 导航按钮
 */
@Composable
fun FunctionItem(
    title: String,
    icon: ImageVector,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit = { textToSpeechService.speak(title) }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 健康数据卡片区域
 */
@Composable
fun HealthDataCards(textToSpeechService: TextToSpeechService) {
    var showMoreData by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "实时健康监测",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 更多按钮
            TextButton(
                onClick = { 
                    showMoreData = !showMoreData 
                    textToSpeechService.speak(if (showMoreData) "展开更多健康数据" else "收起健康数据")
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (showMoreData) "收起" else "更多",
                    color = Color.White
                )
                Icon(
                    imageVector = if (showMoreData) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (showMoreData) "收起" else "更多",
                    tint = Color.White
                )
            }
        }
        
        // 主要健康数据（始终显示）
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
        
        // 更多健康数据（点击"更多"按钮后显示）
        AnimatedVisibility(
            visible = showMoreData,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                // 第一行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 血压卡片
                    HealthDataCard(
                        title = "血压",
                        value = "120/80",
                        unit = "mmHg",
                        time = "2:50 pm",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(BrightBlue, Tertiary),
                        textToSpeechService
                    )
                    
                    // 血糖卡片
                    HealthDataCard(
                        title = "血糖",
                        value = "5.4",
                        unit = "mmol/L",
                        time = "2:30 pm",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(FreshGreen, Color(0xFF4CAF50)),
                        textToSpeechService
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 第二行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 视觉能力卡片
                    HealthDataCard(
                        title = "视觉能力",
                        value = "0.8",
                        unit = "视力",
                        time = "昨天",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF9C27B0), Color(0xFFE040FB)),
                        textToSpeechService
                    )
                    
                    // 听觉能力卡片
                    HealthDataCard(
                        title = "听觉能力",
                        value = "正常",
                        unit = "",
                        time = "昨天",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF3F51B5), Color(0xFF7986CB)),
                        textToSpeechService
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 第三行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 肢体体征卡片
                    HealthDataCard(
                        title = "肢体体征",
                        value = "良好",
                        unit = "",
                        time = "今天",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF00BCD4), Color(0xFF80DEEA)),
                        textToSpeechService
                    )
                    
                    // 认知能力卡片
                    HealthDataCard(
                        title = "认知能力",
                        value = "90",
                        unit = "分",
                        time = "上周",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFFF5722), Color(0xFFFF8A65)),
                        textToSpeechService
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 第四行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 血脂卡片
                    HealthDataCard(
                        title = "血脂",
                        value = "4.2",
                        unit = "mmol/L",
                        time = "上周",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF8BC34A), Color(0xFFAED581)),
                        textToSpeechService
                    )
                    
                    // RM值卡片
                    HealthDataCard(
                        title = "RM值",
                        value = "1.5",
                        unit = "",
                        time = "上周",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF795548), Color(0xFFA1887F)),
                        textToSpeechService
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 第五行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 肌肉维度卡片
                    HealthDataCard(
                        title = "肌肉维度",
                        value = "良好",
                        unit = "",
                        time = "本月",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF607D8B), Color(0xFF90A4AE)),
                        textToSpeechService
                    )
                    
                    // 肺活量卡片
                    HealthDataCard(
                        title = "肺活量",
                        value = "3200",
                        unit = "ml",
                        time = "上月",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF009688), Color(0xFF80CBC4)),
                        textToSpeechService
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 第六行额外数据
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 握力卡片
                    HealthDataCard(
                        title = "握力",
                        value = "25",
                        unit = "kg",
                        time = "上月",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFFF9800), Color(0xFFFFB74D)),
                        textToSpeechService
                    )
                    
                    // 人体成分卡片
                    HealthDataCard(
                        title = "人体成分",
                        value = "正常",
                        unit = "",
                        time = "上月",
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFC2185B), Color(0xFFF06292)),
                        textToSpeechService
                    )
                }
            }
        }
    }
}

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
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = gradientColors)
                )
                .padding(16.dp)
        ) {
            // 顶部信息区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 左侧标题和单位
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 标题和单位
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "($unit)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // 右侧头像
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            title.contains("血氧") -> Icons.Default.Air
                            title.contains("心率") -> Icons.Default.Favorite
                            title.contains("血压") -> Icons.Default.LocalHospital
                            title.contains("血糖") -> Icons.Default.Restaurant
                            else -> Icons.Default.Analytics
                        },
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // 中间数值显示区域
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 8.dp)
            ) {
                // 数值指示器（刻度条）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 刻度值
                    listOf("1.00", "2.00", "3.00", "4.00", "5.00").forEachIndexed { index, mark ->
                        Text(
                            text = mark,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (index == 2) Color.Black else Color.White.copy(alpha = 0.5f),
                            fontSize = if (index == 2) 10.sp else 8.sp,
                            modifier = Modifier
                                .background(
                                    if (index == 2) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(if (index == 2) 4.dp else 0.dp)
                        )
                    }
                }
                
                // 数值和单位
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 左侧数值显示
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                    
                    // 右侧可视化指示器
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(80.dp)
                    ) {
                        // 背景条
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .align(Alignment.Center)
                        )
                        
                        // 数值指示条
                        val fillPercentage = when {
                            value.contains("%") -> value.replace("%", "").toFloatOrNull() ?: 0f
                            value.toFloatOrNull() != null -> {
                                val numValue = value.toFloat()
                                when {
                                    title.contains("心率") -> (numValue / 200f) * 100f // 假设最大心率200
                                    title.contains("血糖") -> (numValue / 10f) * 100f  // 假设最大血糖10
                                    else -> 70f // 默认值
                                }
                            }
                            else -> 70f // 默认值
                        } / 100f
                        
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(fillPercentage)
                                .width(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White)
                                .align(Alignment.BottomCenter)
                        )
                        
                        // 顶部指示器
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .offset(y = (-fillPercentage * 80f).dp)
                                .clip(CircleShape)
                                .background(Color.Cyan)
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
            
            // 底部时间显示
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.BottomStart)
            )
            
            // 底部单位显示
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * 地图信息卡片
 * 使用简单卡片替代地图，避免OpenGL错误
 */
@Composable
fun MapInfoCard(textToSpeechService: TextToSpeechService) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "附近服务",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.White
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable { textToSpeechService.speak("当前位置：长春市") },
            colors = CardDefaults.cardColors(
                containerColor = Surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 使用图标代替地图
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "位置",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "长春市",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "位置服务暂不可用",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 显示一些简化的服务按钮
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ServiceButton(
                            icon = Icons.Default.LocalHospital,
                            label = "医疗机构",
                            onClick = { textToSpeechService.speak("附近医疗机构功能暂不可用") }
                        )
                        
                        ServiceButton(
                            icon = Icons.Default.LocalPharmacy,
                            label = "药店",
                            onClick = { textToSpeechService.speak("附近药店功能暂不可用") }
                        )
                        
                        ServiceButton(
                            icon = Icons.Default.Restaurant,
                            label = "餐厅",
                            onClick = { textToSpeechService.speak("附近餐厅功能暂不可用") }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 简化的服务按钮
 */
@Composable
fun ServiceButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            maxLines = 1
        )
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
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // 顶部栏
        HealthDataTopBar(onBackClick)
        
        // 用户信息
        UserInfoSection()
        
        // 数据标签切换
        DataTabs(selectedTabIndex) { selectedTabIndex = it }
        
        // 不同标签页内容
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTabIndex) {
                0 -> DataDisplayTab()
                1 -> StatisticsTab()
                2 -> HealthServiceTab()
            }
        }
    }
}

@Composable
fun HealthDataTopBar(onBackClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3D85C6),
                        Color(0xFF5B9BD5)
                    )
                )
            )
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "健康数据",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "分享",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* 分享功能 */ }
            )
        }
    }
}

@Composable
fun UserInfoSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户头像
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .border(width = 3.dp, color = Color(0xFFE1F5FE), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(35.dp),
                    tint = Color(0xFF5B9BD5)
                )
            }
            
            // 用户信息
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "李长青",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DataItem("78", "年龄")
                    DataItem("175", "身高")
                    DataItem("74", "体重")
                }
            }
        }
    }
}

@Composable
fun DataItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5B9BD5)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun DataTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("数据展示", "统计分析", "健康服务")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == index) Color(0xFF5B9BD5) else Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(40.dp)
                        .background(
                            if (selectedTabIndex == index) Color(0xFF5B9BD5) else Color.Transparent,
                            shape = RoundedCornerShape(1.5.dp)
                        )
                )
            }
        }
    }
}

// 数据展示页面
@Composable
fun DataDisplayTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 位置信息
        item { LocationInfoCard() }
        
        // 步数统计
        item { StepsCard() }
        
        // 心率卡片
        item { HeartRateCard() }
        
        // 底部间距
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun LocationInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GPS定位",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "吉林省长春市朝阳区",
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF4081),
                                Color(0xFFAA00FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "播放",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StepsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFECB3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = "步数",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "步数",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "2180+",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 步数图表
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                StepBarImproved(height = 40.dp, label = "15", color = Color(0xFFFFA000))
                StepBarImproved(height = 70.dp, label = "12", color = Color(0xFFFF7043))
                StepBarImproved(height = 30.dp, label = "9", color = Color(0xFFAA00FF))
                StepBarImproved(height = 50.dp, label = "08", color = Color(0xFF7986CB))
                StepBarImproved(height = 90.dp, label = "今", color = Color(0xFF5B9BD5))
            }
        }
    }
}

@Composable
fun StepBarImproved(height: Dp, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 9.dp, topEnd = 9.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.7f),
                            color
                        ),
                        startY = 0f,
                        endY = height.value
                    )
                )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun HeartRateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8BBD0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "心率",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "心率",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "102 Bpm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 心率图表 - 使用动态的波形曲线
            HeartRateChart()
        }
    }
}

@Composable
fun HeartRateChart() {
    val colors = listOf(
        Color(0xFFE91E63),
        Color(0xFFEC407A),
        Color(0xFFAD1457)
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2
            
            val path = Path()
            path.moveTo(0f, midY)
            
            // 创建波动的心率图
            val segmentWidth = width / 10
            var x = 0f
            
            while (x < width) {
                val randomHeight = (Math.random() * height * 0.4f).toFloat()
                val step = (Math.random() * segmentWidth * 0.8f + segmentWidth * 0.2f).toFloat()
                
                path.lineTo(x + step/4, midY - randomHeight)
                path.lineTo(x + step/2, midY + randomHeight)
                path.lineTo(x + step*3/4, midY - randomHeight/2)
                path.lineTo(x + step, midY)
                
                x += step
            }
            
            // 绘制曲线
            drawPath(
                path = path,
                color = colors[0],
                style = Stroke(width = 2.5f)
            )
        }
    }
}

// 统计分析页面
@Composable
fun StatisticsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 健康评级卡片
        item {
            HealthScoreCard()
        }
        
        // 事件统计卡片
        item {
            EventsStatisticsCard()
        }
        
        // 分享按钮
        item {
            Button(
                onClick = { /* 分享功能 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B9BD5)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "分享",
                        tint = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "分享健康报告",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
        
        // 底部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HealthScoreCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1BEE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "健康评级",
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "健康评级",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "98 分",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "优秀",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 曲线图
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 图表区域
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        // 简化的曲线
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // 绘制网格线
                            val horizontalLines = 5
                            val gridColor = Color.LightGray.copy(alpha = 0.5f)
                            
                            for (i in 0..horizontalLines) {
                                val y = size.height * i / horizontalLines
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1f
                                )
                            }
                            
                            // 绘制曲线
                            val path = Path().apply {
                                moveTo(0f, size.height * 0.6f)
                                cubicTo(
                                    size.width * 0.2f, size.height * 0.7f,
                                    size.width * 0.4f, size.height * 0.3f,
                                    size.width * 0.6f, size.height * 0.2f
                                )
                                cubicTo(
                                    size.width * 0.8f, size.height * 0.1f,
                                    size.width * 0.9f, size.height * 0.3f,
                                    size.width, size.height * 0.25f
                                )
                            }
                            
                            // 绘制曲线下方的填充区域
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF9C27B0).copy(alpha = 0.2f),
                                        Color(0xFF9C27B0).copy(alpha = 0.05f)
                                    )
                                )
                            )
                            
                            drawPath(
                                path = path,
                                color = Color(0xFF9C27B0),
                                style = Stroke(width = 3f, cap = StrokeCap.Round)
                            )
                            
                            // 绘制数据点
                            val dataPoints = listOf(
                                Offset(0f, size.height * 0.6f),
                                Offset(size.width * 0.2f, size.height * 0.55f),
                                Offset(size.width * 0.4f, size.height * 0.3f),
                                Offset(size.width * 0.6f, size.height * 0.2f),
                                Offset(size.width * 0.8f, size.height * 0.15f),
                                Offset(size.width, size.height * 0.25f)
                            )
                            
                            dataPoints.forEach { point ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 8f,
                                    center = point
                                )
                                drawCircle(
                                    color = Color(0xFF9C27B0),
                                    radius = 5f,
                                    center = point
                                )
                            }
                        }
                    }
                    
                    // X轴标签
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventsStatisticsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCCBC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "事件统计",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "最近一周发生了 ",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
                
                Text(
                    text = "6",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
                
                Text(
                    text = " 次健康事件",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 事件进度条
            LinearProgressIndicator(
                progress = 0.6f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFF5722),
                trackColor = Color(0xFFEEEEEE)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 事件类型列表
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EventTypeRow("血压偏高", "3次", Color(0xFFF44336))
                EventTypeRow("身体疼痛", "1次", Color(0xFFFF9800))
                EventTypeRow("健康提醒", "2次", Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun EventTypeRow(title: String, count: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 颜色标记
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 事件名称
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        
        // 事件次数
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// 健康服务页面
@Composable
fun HealthServiceTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // VIP会员卡
        item {
            VipMembershipCard()
        }
        
        // 专业健康师
        item {
            HealthSpecialistCard()
        }
        
        // 会员权益
        item {
            MembershipBenefitsCard()
        }
        
        // 底部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun VipMembershipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFBA68C8),
                            Color(0xFFE91E63)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "慧龄",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "优质健康服务等你领",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "VIP",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFECB3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "预约时间",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "预约时间",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = "2025-05-16",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
            
            Button(
                onClick = { /* 立即使用 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("立即使用")
            }
        }
    }
}

@Composable
fun HealthSpecialistCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD1C4E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = "专业健康师",
                        tint = Color(0xFF673AB7),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "专业健康师",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康师信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "李月",
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                // 信息
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "李月",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Text(
                        text = "认证健康师 · 4年",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // 评分
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "4.6",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (it < 4) Color(0xFFFF9800) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MembershipBenefitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFB3E5FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = "会员权益",
                        tint = Color(0xFF03A9F4),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "会员权益",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 服务项目网格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BenefitItem(
                    icon = Icons.Default.Description,
                    title = "健康体检报告"
                )
                
                BenefitItem(
                    icon = Icons.Default.Assignment,
                    title = "定制健康计划"
                )
                
                BenefitItem(
                    icon = Icons.Default.Analytics,
                    title = "营养专业分析"
                )
            }
        }
    }
}

@Composable
fun BenefitItem(
    icon: ImageVector,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { /* 权益点击 */ }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF03A9F4),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 健康计划页面
 */
@Composable
fun HealthPlanScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        HealthPlanTopBar(onBackClick)
        
        // 计划列表
        HealthPlansList(textToSpeechService)
    }
}

/**
 * 健康计划顶部栏
 */
@Composable
fun HealthPlanTopBar(onBackClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "健康计划",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加计划",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { /* 添加计划 */ }
        )
    }
}

/**
 * 健康计划列表
 */
@Composable
fun HealthPlansList(textToSpeechService: TextToSpeechService) {
    // 示例计划数据
    val plans = listOf(
        HealthPlan(
            id = 1,
            title = "每日慢走计划",
            subtitle = "中低强度有氧运动",
            description = "每天进行30分钟的散步，保持心率在90-110bpm之间，可以有效提高心肺功能。",
            timeText = "每天 9:00-9:30",
            progress = 0.7f,
            progressText = "已完成7天/10天",
            icon = Icons.Default.DirectionsWalk,
            iconBackground = Color(0xFFFF9800),
            isActive = true
        ),
        HealthPlan(
            id = 2,
            title = "血压监测计划",
            subtitle = "定时测量记录",
            description = "每天早晚各测量一次血压，并记录数据，有助于医生掌握血压变化趋势。",
            timeText = "每天 7:00, 19:00",
            progress = 0.3f,
            progressText = "已完成3天/10天",
            icon = Icons.Default.Favorite,
            iconBackground = Color(0xFFE91E63),
            isActive = true
        ),
        HealthPlan(
            id = 3,
            title = "健康饮食计划",
            subtitle = "低盐低油饮食",
            description = "控制每日盐分摄入量在5g以下，减少油脂摄入，多食用蔬果，帮助控制血压。",
            timeText = "每日三餐",
            progress = 0.5f,
            progressText = "已完成5天/10天",
            icon = Icons.Default.Restaurant,
            iconBackground = Color(0xFF4CAF50),
            isActive = false
        ),
        HealthPlan(
            id = 4,
            title = "睡眠改善计划",
            subtitle = "规律作息时间",
            description = "保持规律的睡眠时间，每晚10点前入睡，确保7-8小时的充足睡眠，提高睡眠质量。",
            timeText = "每天 22:00-6:00",
            progress = 0.8f,
            progressText = "已完成8天/10天",
            icon = Icons.Default.Bedtime,
            iconBackground = Color(0xFF673AB7),
            isActive = false
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 活跃计划标题
        Text(
            text = "进行中的计划",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // 活跃计划列表
        plans.filter { it.isActive }.forEach { plan ->
            HealthPlanCard(plan = plan, textToSpeechService = textToSpeechService)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 非活跃计划标题
        Text(
            text = "其他计划",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        
        // 非活跃计划列表
        plans.filter { !it.isActive }.forEach { plan ->
            HealthPlanCard(plan = plan, textToSpeechService = textToSpeechService)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * 健康计划卡片
 */
@Composable
fun HealthPlanCard(
    plan: HealthPlan,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                expanded = !expanded
                textToSpeechService.speak(plan.title + "，" + plan.description)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 计划标题和图标
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 图标
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(plan.iconBackground.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = plan.icon,
                        contentDescription = plan.title,
                        tint = plan.iconBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 标题和副标题
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = plan.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = plan.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // 展开/收起图标
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开",
                        tint = Color.Gray
                    )
                }
            }
            
            // 时间信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "时间",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = plan.timeText,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // 进度条
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = plan.progressText,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                LinearProgressIndicator(
                    progress = { plan.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = Color.LightGray,
                    color = plan.iconBackground
                )
            }
            
            // 详情描述 (仅当展开时显示)
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "计划详情",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = plan.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { /* 编辑 */ }
                        ) {
                            Text("编辑", color = Color.Gray)
                        }
                        
                        TextButton(
                            onClick = { /* 暂停/开始 */ }
                        ) {
                            Text(
                                text = if (plan.isActive) "暂停" else "开始",
                                color = plan.iconBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 健康计划数据类
 */
data class HealthPlan(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val timeText: String,
    val progress: Float,
    val progressText: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val isActive: Boolean
)

/**
 * 康复指导页面
 */
@Composable
fun RehabilitationScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("所有") }
    var selectedGuide by remember { mutableStateOf<RehabilitationGuide?>(null) }
    
    // 筛选选项
    val filterOptions = listOf("所有", "脑血管及脑部", "骨科疾病", "肾脏及泌尿")
    
    // 康复指导列表数据
    val guideList = listOf(
        RehabilitationGuide(
            id = 1,
            title = "初级水疗康复定制",
            location = "康复科 | 朝阳区",
            rating = 5.0f,
            distance = "15 km",
            imageUrl = "",
            description = "专业水疗康复服务，适合轻度关节疼痛、术后康复阶段的老年人。通过水中运动减轻关节压力，促进血液循环，改善肌肉功能。",
            category = "骨科疾病"
        ),
        RehabilitationGuide(
            id = 2,
            title = "老年性痴呆认知复健",
            location = "康复科 | 宽城区",
            rating = 4.0f,
            distance = "22 km",
            imageUrl = "",
            description = "针对轻中度老年痴呆患者的认知功能训练，包括记忆力训练、注意力训练、语言能力恢复等多方面综合干预，延缓认知功能下降。",
            category = "脑血管及脑部"
        ),
        RehabilitationGuide(
            id = 3,
            title = "脑卒中后康复复建",
            location = "康复科 | 朝阳区",
            rating = 4.5f,
            distance = "48 km",
            imageUrl = "",
            description = "专为脑卒中后遗症患者设计的综合康复计划，包括肢体功能训练、平衡训练、言语治疗等，帮助患者最大程度恢复生活自理能力。",
            category = "脑血管及脑部"
        ),
        RehabilitationGuide(
            id = 4,
            title = "腿中风，踝助康复",
            location = "康复科 | 二道区",
            rating = 3.5f,
            distance = "89 km",
            imageUrl = "",
            description = "针对下肢功能障碍患者的专项康复治疗，采用现代康复技术与传统推拿相结合，促进血液循环，改善肌肉萎缩，提高行走能力。",
            category = "脑血管及脑部"
        ),
        RehabilitationGuide(
            id = 5,
            title = "心血管疾病康复",
            location = "康复科 | 绿园区",
            rating = 5.0f,
            distance = "106 km",
            imageUrl = "",
            description = "为心血管疾病患者提供的专业康复指导，包括心肺功能训练、营养调理、生活方式管理等，提高心肺功能，预防疾病复发。",
            category = "肾脏及泌尿"
        )
    )
    
    // 根据筛选条件过滤列表
    val filteredGuides = if (selectedFilter == "所有") {
        guideList
    } else {
        guideList.filter { it.category == selectedFilter }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 主要内容
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部栏
            RehabilitationTopBar(onBackClick)
            
            // 搜索栏
            SearchBar()
            
            // 筛选栏
            FilterBar(
                options = filterOptions,
                selectedOption = selectedFilter,
                onOptionSelected = { selectedFilter = it }
            )
            
            // 服务统计
            ServiceCount(count = filteredGuides.size)
            
            // 康复指导列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredGuides) { guide ->
                    RehabilitationGuideItem(
                        guide = guide,
                        textToSpeechService = textToSpeechService,
                        onClick = { selectedGuide = guide }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        
        // 详情弹窗
        if (selectedGuide != null) {
            GuideDetailDialog(
                guide = selectedGuide!!,
                onDismiss = { selectedGuide = null },
                textToSpeechService = textToSpeechService
            )
        }
    }
}

/**
 * 康复指导顶部栏
 */
@Composable
fun RehabilitationTopBar(onBackClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "康复指导",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 搜索栏
 */
@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "搜索服务",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "清除",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 筛选栏
 */
@Composable
fun FilterBar(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                option = option,
                isSelected = option == selectedOption,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

/**
 * 筛选选项
 */
@Composable
fun FilterChip(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF333333) else Color(0xFFF5F5F5)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 14.sp
        )
    }
}

/**
 * 服务数量显示
 */
@Composable
fun ServiceCount(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "服务总计(${count}条)",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

/**
 * 康复指导项
 */
@Composable
fun RehabilitationGuideItem(
    guide: RehabilitationGuide,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                onClick()
                textToSpeechService.speak(guide.title)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图片
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Healing,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = guide.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = guide.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 评分
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 星级
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < guide.rating) Color(0xFFFFB900) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            
            // 距离
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(8.dp)
                        .background(Color(0xFF4CAF50))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = guide.distance,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 康复指导详情弹窗
 */
@Composable
fun GuideDetailDialog(
    guide: RehabilitationGuide,
    onDismiss: () -> Unit,
    textToSpeechService: TextToSpeechService
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .clickable { /* 防止点击穿透 */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题
                Text(
                    text = guide.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 位置和评分
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = guide.location,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "距离: ${guide.distance}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 评分
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 星级
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < guide.rating) Color(0xFFFFB900) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 分割线
                Divider(color = Color.LightGray)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 简介
                Text(
                    text = "服务简介",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = guide.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        ),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("关闭")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { 
                            textToSpeechService.speak("已预约${guide.title}") 
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("立即预约")
                    }
                }
            }
        }
    }
}

/**
 * 康复指导数据类
 */
data class RehabilitationGuide(
    val id: Int,
    val title: String,
    val location: String,
    val rating: Float,
    val distance: String,
    val imageUrl: String,
    val description: String,
    val category: String
)