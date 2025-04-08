package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
    val currentDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    
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
                            text = "智慧伙伴",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable { textToSpeechService.speak("智慧伙伴") }
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
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
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
            // 用户信息卡片
            item {
                UserInfoCard(
                    name = "王阿姨",
                    date = currentDate,
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 健康数据卡片
            item {
                HealthDataCard(
                    bloodPressure = "126/82",
                    heartRate = "72",
                    steps = "2305",
                    bloodOxygen = "96.2",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 今日天气
            item {
                WeatherCard(
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 家人联系方式
            item {
                FamilyContactsCard(
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 社区活动
            item {
                CommunityActivitiesCard(
                    textToSpeechService = textToSpeechService,
                    onCommunityClick = onServiceClick
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
 * 用户信息卡片
 */
@Composable
fun UserInfoCard(
    name: String,
    date: String,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("您好，$name") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "您好，$name",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$date 星期${getDayOfWeek()}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            // 用户头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D9E64)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // 健康状态提示
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8F5E9))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "健康状态",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "您的健康状态良好，请继续保持",
                    fontSize = 14.sp,
                    color = Color(0xFF2D9E64)
                )
            }
        }
    }
}

/**
 * 健康数据卡片
 */
@Composable
fun HealthDataCard(
    bloodPressure: String,
    heartRate: String,
    steps: String,
    bloodOxygen: String,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("您的健康数据：血压$bloodPressure，心率$heartRate，步数$steps，血氧$bloodOxygen") },
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
                Text(
                    text = "我的健康",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { textToSpeechService.speak("查看健康中心") }
                ) {
                    Text(
                        text = "健康中心",
                        fontSize = 14.sp,
                        color = Color(0xFF2D9E64)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看更多",
                        tint = Color(0xFF2D9E64),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康数据网格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 血压
                HealthDataItem(
                    value = bloodPressure,
                    unit = "mmHg",
                    label = "血压",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
                
                // 心率
                HealthDataItem(
                    value = heartRate,
                    unit = "次/分钟",
                    label = "心率",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 步数
                HealthDataItem(
                    value = steps,
                    unit = "步/天",
                    label = "步数",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
                
                // 血氧
                HealthDataItem(
                    value = bloodOxygen,
                    unit = "SpO2",
                    label = "血氧",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 今日提醒
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "今日提醒",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "今日提醒",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * 健康数据项
 */
@Composable
fun HealthDataItem(
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    textToSpeechService: TextToSpeechService
) {
    Column(
        modifier = modifier.clickable { textToSpeechService.speak("$label：$value $unit") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = unit,
            fontSize = 12.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

/**
 * 天气卡片
 */
@Composable
fun WeatherCard(
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("今日天气：成都，晴，气温17至27度") },
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
                Text(
                    text = "今日天气",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = "成都市",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 天气信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 白天天气
                WeatherItem(
                    time = "白天",
                    icon = Icons.Default.WbSunny,
                    temperature = "27°",
                    condition = "晴天",
                    iconTint = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
                
                // 分隔线
                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = Color.LightGray
                )
                
                // 夜间天气
                WeatherItem(
                    time = "夜间",
                    icon = Icons.Default.Bedtime,
                    temperature = "17°",
                    condition = "多云",
                    iconTint = Color(0xFF3F51B5),
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 空气质量
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = "空气质量",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "空气质量：优 AQI 45",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * 天气项
 */
@Composable
fun WeatherItem(
    time: String,
    icon: ImageVector,
    temperature: String,
    condition: String,
    iconTint: Color,
    modifier: Modifier = Modifier,
    textToSpeechService: TextToSpeechService
) {
    Column(
        modifier = modifier.clickable { textToSpeechService.speak("${time}天气：${condition}，${temperature}") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Icon(
            imageVector = icon,
            contentDescription = condition,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = temperature,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = condition,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

/**
 * 家人联系方式卡片
 */
@Composable
fun FamilyContactsCard(
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("我的家人") },
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
                Text(
                    text = "我的家人",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { textToSpeechService.speak("查看更多") }
                ) {
                    Text(
                        text = "查看更多",
                        fontSize = 14.sp,
                        color = Color(0xFF2D9E64)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看更多",
                        tint = Color(0xFF2D9E64),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 家人联系人列表
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(familyContacts) { contact ->
                    FamilyContactItem(
                        contact = contact,
                        textToSpeechService = textToSpeechService
                    )
                }
            }
        }
    }
}

/**
 * 家人联系人项
 */
@Composable
fun FamilyContactItem(
    contact: FamilyContact,
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { textToSpeechService.speak("${contact.relation}，${contact.name}") }
    ) {
        // 联系人头像
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(contact.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.first().toString(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = contact.relation,
            fontSize = 14.sp,
            color = Color.Black
        )
        
        Text(
            text = contact.name,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * 社区活动卡片
 */
@Composable
fun CommunityActivitiesCard(
    textToSpeechService: TextToSpeechService,
    onCommunityClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                textToSpeechService.speak("社区活动")
                onCommunityClick()
            },
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
                Text(
                    text = "社区活动",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { 
                        textToSpeechService.speak("查看更多")
                        onCommunityClick()
                    }
                ) {
                    Text(
                        text = "查看更多",
                        fontSize = 14.sp,
                        color = Color(0xFF2D9E64)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看更多",
                        tint = Color(0xFF2D9E64),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 社区活动列表
            communityActivities.forEach { activity ->
                CommunityActivityItem(
                    activity = activity,
                    textToSpeechService = textToSpeechService
                )
                
                if (activity != communityActivities.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

/**
 * 社区活动项
 */
@Composable
fun CommunityActivityItem(
    activity: CommunityActivity,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("${activity.name}，${activity.time}，${activity.location}") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 活动日期
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = activity.date.split("-")[0],
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = activity.date.split("-")[1],
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "时间",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = activity.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "地点",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = activity.location,
                    fontSize = 12.sp,
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
            
            // 消息
            BottomNavItem(
                icon = Icons.Default.Message,
                label = "消息",
                isSelected = false,
                onClick = {
                    textToSpeechService.speak("消息")
                    onMessageClick()
                }
            )
            
            // 视频通话
            BottomNavItem(
                icon = Icons.Default.Videocam,
                label = "视频",
                isSelected = false,
                onClick = {
                    textToSpeechService.speak("视频通话")
                    onVideoCallClick()
                }
            )
            
            // 我的
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "我的",
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

/**
 * 家人联系人数据类
 */
data class FamilyContact(
    val name: String,
    val relation: String,
    val color: Color
)

// 使用CommunityActivityScreen.kt中定义的CommunityActivity数据类

// 家人联系人列表
val familyContacts = listOf(
    FamilyContact("李明", "儿子", Color(0xFFE57373)),
    FamilyContact("张华", "女儿", Color(0xFF64B5F6)),
    FamilyContact("王芳", "孙女", Color(0xFF81C784)),
    FamilyContact("赵强", "孙子", Color(0xFFFFB74D))
)

// 社区活动列表
val communityActivities = listOf(
    CommunityActivity(
        id = "activity1",
        name = "太极拳教学",
        description = "由专业太极拳老师教授，适合老年人的太极拳基础动作，增强身体协调性和平衡能力。",
        date = "05-15",
        time = "9:00-10:30",
        location = "社区活动中心",
        participants = 28,
        icon = Icons.Default.SportsKabaddi
    ),
    CommunityActivity(
        id = "activity2",
        name = "健康讲座",
        description = "邀请社区医生讲解老年人常见疾病预防和健康生活方式，现场提供免费血压测量。",
        date = "05-18",
        time = "14:00-15:30",
        location = "社区会议室",
        participants = 45,
        icon = Icons.Default.HealthAndSafety
    )
)