package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.olderperson.ui.components.TopBar
import com.example.olderperson.ui.theme.FreshGreen

/**
 * 我的页面界面
 */
@Composable
fun MySelfScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部栏
        TopBar("我和自己", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 副标题
            item {
                Text(
                    text = "关注自我健康和成长",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // 健康中心
            item {
                HealthCenterCard(textToSpeechService)
            }
            
            // 今日用药
            item {
                TodayMedicationCard(textToSpeechService)
            }
            
            // 近期医疗安排
            item {
                MedicalAppointmentsCard(textToSpeechService)
            }
            
            // 个人成长
            item {
                PersonalGrowthCard(textToSpeechService)
            }
            
            // 健康生活建议
            item {
                HealthyLifeTipsCard(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 健康中心卡片
 */
@Composable
fun HealthCenterCard(textToSpeechService: TextToSpeechService) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "健康中心",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康中心",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "详情 >",
                    fontSize = 14.sp,
                    color = Color(0xFF2D9E64),
                    modifier = Modifier.clickable { textToSpeechService.speak("查看健康中心详情") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康数据网格
            Row(modifier = Modifier.fillMaxWidth()) {
                // 血压
                HealthDataItem(
                    value = "126/82",
                    unit = "血压 (mmHg)",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
                
                // 心率
                HealthDataItem(
                    value = "72",
                    unit = "心率 (次/分)",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                // 今日步数
                HealthDataItem(
                    value = "2305",
                    unit = "今日步数",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
                )
                
                // 睡眠时间
                HealthDataItem(
                    value = "6.2",
                    unit = "睡眠 (小时)",
                    modifier = Modifier.weight(1f),
                    textToSpeechService = textToSpeechService
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
    modifier: Modifier = Modifier,
    textToSpeechService: TextToSpeechService
) {
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { textToSpeechService.speak("$unit: $value") }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D9E64)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = unit,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 今日用药卡片
 */
@Composable
fun TodayMedicationCard(textToSpeechService: TextToSpeechService) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = "今日用药",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "今日用药",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用药列表
            MedicationItem(
                icon = Icons.Default.Medication,
                name = "降压药",
                time = "上午 8:00",
                status = "已完成",
                textToSpeechService = textToSpeechService
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            MedicationItem(
                icon = Icons.Default.Medication,
                name = "钙片",
                time = "下午 12:30",
                status = "待服用",
                isCompleted = false,
                textToSpeechService = textToSpeechService
            )
        }
    }
}

/**
 * 用药项
 */
@Composable
fun MedicationItem(
    icon: ImageVector,
    name: String,
    time: String,
    status: String,
    isCompleted: Boolean = true,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$name, $time, $status") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF9E6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color(0xFFFFA726),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 中间内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = time,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 右侧状态
        val statusColor = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFFFA726)
        val statusBgColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(statusBgColor)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = status,
                fontSize = 12.sp,
                color = statusColor
            )
        }
    }
}

/**
 * 近期医疗安排卡片
 */
@Composable
fun MedicalAppointmentsCard(textToSpeechService: TextToSpeechService) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "近期医疗安排",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "近期医疗安排",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "全部 >",
                    fontSize = 14.sp,
                    color = Color(0xFF2D9E64),
                    modifier = Modifier.clickable { textToSpeechService.speak("查看全部医疗安排") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 医疗安排列表
            AppointmentItem(
                day = "02",
                dateInfo = "今天",
                title = "心脏科复诊",
                location = "市第一医院",
                time = "上午 10:30",
                textToSpeechService = textToSpeechService
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppointmentItem(
                day = "15",
                dateInfo = "4月",
                title = "血常规检查",
                location = "社区医院",
                time = "上午 8:30",
                textToSpeechService = textToSpeechService
            )
        }
    }
}

/**
 * 医疗安排项
 */
@Composable
fun AppointmentItem(
    day: String,
    dateInfo: String,
    title: String,
    location: String,
    time: String,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$dateInfo $day, $title, $location, $time") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 日期框
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D9E64)
                )
                
                Text(
                    text = dateInfo,
                    fontSize = 12.sp,
                    color = Color(0xFF2D9E64)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 预约详情
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "地点",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = location,
                    fontSize = 14.sp,
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
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 个人成长卡片
 */
@Composable
fun PersonalGrowthCard(textToSpeechService: TextToSpeechService) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = "个人成长",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "个人成长",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "更多 >",
                    fontSize = 14.sp,
                    color = Color(0xFF2D9E64),
                    modifier = Modifier.clickable { textToSpeechService.speak("查看更多个人成长内容") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 活动列表
            ActivityItem(
                icon = Icons.Default.MenuBook,
                title = "老年健康讲座",
                time = "4月5日 上午 9:30",
                textToSpeechService = textToSpeechService
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            ActivityItem(
                icon = Icons.Default.PhoneAndroid,
                title = "智能手机操作课程",
                time = "4月8日 下午 2:00",
                textToSpeechService = textToSpeechService
            )
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            ActivityItem(
                icon = Icons.Default.Palette,
                title = "社区绘画活动",
                time = "4月10日 下午 3:00",
                textToSpeechService = textToSpeechService
            )
        }
    }
}

/**
 * 活动项
 */
@Composable
fun ActivityItem(
    icon: ImageVector,
    title: String,
    time: String,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$title, $time") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标背景
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 活动信息
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = time,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 健康生活建议卡片
 */
@Composable
fun HealthyLifeTipsCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("健康生活建议：春季天气多变，建议适当增减衣物，防止感冒。多喝水，适量运动，保持良好睡眠。早晚温差大，外出记得带上外套。") },
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "健康生活建议",
                    tint = Color(0xFF2D9E64),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康生活建议",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "春季健康提示",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "春季天气多变，建议适当增减衣物，防止感冒。多喝水，适量运动，保持良好睡眠。早晚温差大，外出记得带上外套。",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { textToSpeechService.speak("获取更多健康生活建议") },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D9E64)
                )
            ) {
                Text("获取更多建议")
            }
        }
    }
} 