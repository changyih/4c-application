package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.FontSizeConfig

/**
 * 我和自己页面
 */
@Composable
fun SelfScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // 顶部栏
        TopBar(onBackToHome = onBackToHome, textToSpeechService = textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 用户信息区域
            item {
                UserInfoSection(textToSpeechService = textToSpeechService)
            }
            
            // 近期医疗安排
            item {
                MedicalScheduleSection(textToSpeechService = textToSpeechService)
            }
            
            // 个人成长
            item {
                PersonalGrowthSection(textToSpeechService = textToSpeechService)
            }
            
            // 健康中心区域
            item {
                HealthCenterSection(textToSpeechService = textToSpeechService)
            }
            
            // 健康生活建议
            item {
                HealthAdviceSection(textToSpeechService = textToSpeechService)
            }
            
            // 今日用药区域
            item {
                MedicationSection(textToSpeechService = textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 顶部栏
 */
@Composable
private fun TopBar(
    onBackToHome: () -> Unit,
    textToSpeechService: TextToSpeechService
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            // 返回按钮
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable { 
                        textToSpeechService.speak("返回")
                        onBackToHome() 
                    }
            )
            
            Text(
                text = "我和自己界面",
                modifier = Modifier.align(Alignment.Center),
                fontSize = FontSizeConfig.scaledSp(18).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

/**
 * 用户信息区域
 */
@Composable
private fun UserInfoSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF87CEEB))
                        .clickable { textToSpeechService.speak("用户头像") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "用户头像",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "我和自己",
                        fontSize = FontSizeConfig.scaledSp(20).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable { textToSpeechService.speak("我和自己") }
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "关注自我健康和成长",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { textToSpeechService.speak("关注自我健康和成长") }
                    )
                }
            }
        }
    }
}

/**
 * 健康中心区域
 */
@Composable
private fun HealthCenterSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和详情按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { textToSpeechService.speak("健康中心") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "健康中心",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "健康中心",
                        fontSize = FontSizeConfig.scaledSp(18).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 详情按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { textToSpeechService.speak("详情") }
                ) {
                    Text(
                        text = "详情",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "详情",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康数据网格
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 血压
                HealthMetricItem(
                    value = "126/82",
                    unit = "血压 (mmHg)",
                    modifier = Modifier.weight(1f),
                    onClick = { textToSpeechService.speak("血压126/82毫米汞柱") }
                )
                
                // 心率
                HealthMetricItem(
                    value = "72",
                    unit = "心率 (次/分)",
                    modifier = Modifier.weight(1f),
                    onClick = { textToSpeechService.speak("心率72次每分钟") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 今日步数
                HealthMetricItem(
                    value = "2305",
                    unit = "今日步数",
                    modifier = Modifier.weight(1f),
                    onClick = { textToSpeechService.speak("今日步数2305步") }
                )
                
                // 睡眠时间
                HealthMetricItem(
                    value = "6.2",
                    unit = "睡眠 (小时)",
                    modifier = Modifier.weight(1f),
                    onClick = { textToSpeechService.speak("睡眠时间6.2小时") }
                )
            }
        }
    }
}

/**
 * 健康指标项
 */
@Composable
private fun HealthMetricItem(
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = FontSizeConfig.scaledSp(24).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = unit,
                fontSize = FontSizeConfig.scaledSp(12).sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 近期医疗安排部分
 */
@Composable
private fun MedicalScheduleSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和查看全部
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { textToSpeechService.speak("近期医疗安排") }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "近期医疗安排",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "近期医疗安排",
                        fontSize = FontSizeConfig.scaledSp(18).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 全部按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { textToSpeechService.speak("查看全部医疗安排") }
                ) {
                    Text(
                        text = "全部",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "查看全部",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 心脏科复诊
            MedicalAppointmentItem(
                day = "02",
                label = "今天",
                title = "心脏科复诊",
                location = "市第一医院",
                time = "上午 10:30",
                onClick = { textToSpeechService.speak("今天上午10点30分，市第一医院心脏科复诊") }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )
            
            // 血常规检查
            MedicalAppointmentItem(
                day = "15",
                label = "4月",
                title = "血常规检查",
                location = "社区医院",
                time = "上午 8:30",
                onClick = { textToSpeechService.speak("4月15日上午8点30分，社区医院血常规检查") }
            )
        }
    }
}

/**
 * 医疗预约项
 */
@Composable
private fun MedicalAppointmentItem(
    day: String,
    label: String,
    title: String,
    location: String,
    time: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 日期标签
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50))
                .padding(4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day,
                fontSize = FontSizeConfig.scaledSp(22).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = label,
                fontSize = FontSizeConfig.scaledSp(12).sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        // 预约信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "位置",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = location,
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "时间",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = time,
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 个人成长部分
 */
@Composable
private fun PersonalGrowthSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和更多
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { textToSpeechService.speak("个人成长") }
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "个人成长",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "个人成长",
                        fontSize = FontSizeConfig.scaledSp(18).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 更多按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { textToSpeechService.speak("更多个人成长活动") }
                ) {
                    Text(
                        text = "更多",
                        fontSize = FontSizeConfig.scaledSp(14).sp,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 老年健康讲座
            GrowthActivityItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "老年健康讲座",
                        tint = Color(0xFF4CAF50)
                    )
                },
                title = "老年健康讲座",
                time = "4月5日 上午 9:30",
                onClick = { textToSpeechService.speak("4月5日上午9点30分，老年健康讲座") }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )
            
            // 智能手机操作课程
            GrowthActivityItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "智能手机操作课程",
                        tint = Color(0xFF2196F3)
                    )
                },
                title = "智能手机操作课程",
                time = "4月8日 下午 2:00",
                onClick = { textToSpeechService.speak("4月8日下午2点，智能手机操作课程") }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )
            
            // 社区绘画活动
            GrowthActivityItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "社区绘画活动",
                        tint = Color(0xFFFF9800)
                    )
                },
                title = "社区绘画活动",
                time = "4月10日 下午 3:00",
                onClick = { textToSpeechService.speak("4月10日下午3点，社区绘画活动") }
            )
        }
    }
}

/**
 * 成长活动项
 */
@Composable
private fun GrowthActivityItem(
    icon: @Composable () -> Unit,
    title: String,
    time: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 活动图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 活动信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = time,
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 健康生活建议部分
 */
@Composable
private fun HealthAdviceSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { textToSpeechService.speak("健康生活建议") }
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "健康生活建议",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康生活建议",
                    fontSize = FontSizeConfig.scaledSp(18).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 春季健康提示
            Text(
                text = "春季健康提示",
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.clickable { textToSpeechService.speak("春季健康提示") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 春季健康建议内容
            Text(
                text = "春季天气多变，建议适当增减衣物，防止感冒。多喝水，适量运动，保持良好睡眠。早晚温差大，外出记得带上外套。",
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = Color.Gray,
                modifier = Modifier.clickable { 
                    textToSpeechService.speak("春季天气多变，建议适当增减衣物，防止感冒。多喝水，适量运动，保持良好睡眠。早晚温差大，外出记得带上外套。") 
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 获取更多建议按钮
            Button(
                onClick = { textToSpeechService.speak("获取更多健康建议") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "获取更多建议",
                    color = Color.White,
                    fontSize = FontSizeConfig.scaledSp(14).sp
                )
            }
        }
    }
}

/**
 * 今日用药区域
 */
@Composable
private fun MedicationSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { textToSpeechService.speak("今日用药") }
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = "今日用药",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "今日用药",
                    fontSize = FontSizeConfig.scaledSp(18).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 降压药
            MedicationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "降压药",
                        tint = Color(0xFFFFA000)
                    )
                },
                name = "降压药",
                time = "上午 8:00",
                status = "已完成",
                statusColor = Color(0xFF4CAF50),
                onClick = { textToSpeechService.speak("降压药 上午8点 已完成") }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )
            
            // 钙片
            MedicationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "钙片",
                        tint = Color(0xFFFFA000)
                    )
                },
                name = "钙片",
                time = "下午 12:30",
                status = "待服用",
                statusColor = Color(0xFFFFA000),
                onClick = { textToSpeechService.speak("钙片 下午12点30分 待服用") }
            )
        }
    }
}

/**
 * 药物项
 */
@Composable
private fun MedicationItem(
    icon: @Composable () -> Unit,
    name: String,
    time: String,
    status: String,
    statusColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 药物图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF3E0))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 药物信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = time,
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = Color.Gray
            )
        }
        
        // 状态
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(statusColor.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = status,
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = statusColor
            )
        }
    }
} 