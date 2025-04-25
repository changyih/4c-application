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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.components.SectionTitle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 社区活动页面
 */




@Composable
fun CommunityActivityScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("社区活动", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 社区活动小贴士
            item {
                CommunityActivityTipsCard(
                    title = "社区活动小贴士",
                    content = "本页面提供多种社区活动信息，包括文化娱乐、健康讲座、志愿服务等，可以查看活动详情并在线报名参加。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 近期活动
            item {
                SectionTitle("近期活动", textToSpeechService)
            }
            
            // 近期活动列表
            items(upcomingActivities) { activity ->
                UpcomingActivityCard(activity, textToSpeechService)
            }
            
            // 热门活动
            item {
                SectionTitle("热门活动", textToSpeechService)
            }
            
            // 热门活动列表
            items(popularActivities) { activity ->
                PopularActivityCard(activity, textToSpeechService)
            }
            
            // 活动报名
            item {
                ActivityRegistrationSection(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 社区活动小贴士卡片
 */
@Composable
fun CommunityActivityTipsCard(
    title: String,
    content: String,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$title：$content") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 近期活动卡片
 */
@Composable
fun UpcomingActivityCard(
    activity: CommunityActivity,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(activity.name + "。" + activity.description) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 活动图标
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = activity.icon,
                        contentDescription = activity.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = activity.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "时间：${activity.date} ${activity.time}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "地点：${activity.location}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = activity.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { textToSpeechService.speak("查看${activity.name}详情") },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("查看详情")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { textToSpeechService.speak("报名参加${activity.name}") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("立即报名")
                }
            }
        }
    }
}

/**
 * 热门活动卡片
 */
@Composable
fun PopularActivityCard(
    activity: CommunityActivity,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(activity.name + "。" + activity.description) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = activity.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = activity.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 参与人数标签
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${activity.participants}人参与",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = activity.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "日期",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = activity.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "时间",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = activity.time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { textToSpeechService.speak("报名参加${activity.name}") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("立即报名")
                }
            }
        }
    }
}

/**
 * 活动报名区域
 */
@Composable
fun ActivityRegistrationSection(textToSpeechService: TextToSpeechService) {
    var expanded by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var participantCount by remember { mutableStateOf(1) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "快速报名活动",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "收起" else "展开",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "请选择活动：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 活动选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActivityButton("太极班", isSelected = selectedActivity == "太极班") {
                        selectedActivity = "太极班"
                        textToSpeechService.speak("已选择活动：太极班")
                    }
                    ActivityButton("书法班", isSelected = selectedActivity == "书法班") {
                        selectedActivity = "书法班"
                        textToSpeechService.speak("已选择活动：书法班")
                    }
                    ActivityButton("合唱团", isSelected = selectedActivity == "合唱团") {
                        selectedActivity = "合唱团"
                        textToSpeechService.speak("已选择活动：合唱团")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 日期选择
                Text(
                    text = "请选择参加日期：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateButton("本周六", isSelected = selectedDate == "本周六") {
                        selectedDate = "本周六"
                        textToSpeechService.speak("已选择参加日期：本周六")
                    }
                    DateButton("本周日", isSelected = selectedDate == "本周日") {
                        selectedDate = "本周日"
                        textToSpeechService.speak("已选择参加日期：本周日")
                    }
                    DateButton("下周一", isSelected = selectedDate == "下周一") {
                        selectedDate = "下周一"
                        textToSpeechService.speak("已选择参加日期：下周一")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 参与人数
                Text(
                    text = "参与人数：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (participantCount > 1) {
                                participantCount--
                                textToSpeechService.speak("参与人数：$participantCount")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "减少",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Text(
                        text = participantCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    IconButton(
                        onClick = {
                            participantCount++
                            textToSpeechService.speak("参与人数：$participantCount")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "增加",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        if (selectedActivity.isNotEmpty() && selectedDate.isNotEmpty()) {
                            textToSpeechService.speak("已成功报名${selectedDate}的${selectedActivity}活动，参与人数${participantCount}人")
                        } else {
                            textToSpeechService.speak("请先选择活动和参加日期")
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = selectedActivity.isNotEmpty() && selectedDate.isNotEmpty()
                ) {
                    Text("提交报名")
                }
            }
        }
    }
}

/**
 * 活动选择按钮
 */
@Composable
fun ActivityButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text)
    }
}

// 活动分类数据类
data class ActivityCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

// 社区活动数据类
data class CommunityActivity(
    val id: String,
    val name: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val participants: Int,
    val icon: ImageVector
)

// 活动分类列表数据
val activityCategories = listOf(
    ActivityCategory(
        id = "culture",
        name = "文化娱乐",
        icon = Icons.Default.MusicNote
    ),
    ActivityCategory(
        id = "health",
        name = "健康讲座",
        icon = Icons.Default.HealthAndSafety
    ),
    ActivityCategory(
        id = "volunteer",
        name = "志愿服务",
        icon = Icons.Default.Favorite
    ),
    ActivityCategory(
        id = "travel",
        name = "旅游出行",
        icon = Icons.Default.DirectionsBus
    ),
    ActivityCategory(
        id = "education",
        name = "学习教育",
        icon = Icons.Default.School
    )
)

// 近期活动列表数据
val upcomingActivities = listOf(
    CommunityActivity(
        id = "activity1",
        name = "社区太极班",
        description = "由专业太极拳老师教授，适合老年人的太极拳基础动作，增强身体协调性和平衡能力。",
        date = "2023-06-15",
        time = "上午 9:00-10:30",
        location = "社区活动中心一楼",
        participants = 28,
        icon = Icons.Default.SportsKabaddi
    ),
    CommunityActivity(
        id = "activity2",
        name = "健康知识讲座",
        description = "邀请社区医生讲解老年人常见疾病预防和健康生活方式，现场提供免费血压测量。",
        date = "2023-06-16",
        time = "下午 2:00-4:00",
        location = "社区活动中心二楼会议室",
        participants = 45,
        icon = Icons.Default.HealthAndSafety
    ),
    CommunityActivity(
        id = "activity3",
        name = "书法兴趣班",
        description = "由退休书法老师教授毛笔书法基础，提供书法用具，适合有兴趣学习书法的老年人。",
        date = "2023-06-17",
        time = "上午 10:00-11:30",
        location = "社区文化站",
        participants = 15,
        icon = Icons.Default.Edit
    ),
    CommunityActivity(
        id = "activity4",
        name = "社区义工活动",
        description = "组织社区义工参与小区环境整治，包括绿化带维护和公共区域清洁，欢迎有爱心的居民参与。",
        date = "2023-06-18",
        time = "上午 8:30-10:30",
        location = "小区中心广场集合",
        participants = 20,
        icon = Icons.Default.Favorite
    )
)

// 热门活动列表数据
val popularActivities = listOf(
    CommunityActivity(
        id = "popular1",
        name = "老年合唱团",
        description = "每周定期排练，学习经典歌曲，参与社区文艺演出，丰富老年人精神文化生活。",
        date = "每周二、四",
        time = "下午 3:00-5:00",
        location = "社区文化活动中心",
        participants = 36,
        icon = Icons.Default.MusicNote
    ),
    CommunityActivity(
        id = "popular2",
        name = "老年人智能手机使用培训",
        description = "教授老年人智能手机基本操作、常用APP使用方法，帮助老年人跟上信息时代步伐。",
        date = "每周五",
        time = "上午 9:30-11:00",
        location = "社区学习中心",
        participants = 50,
        icon = Icons.Default.PhoneAndroid
    ),
    CommunityActivity(
        id = "popular3",
        name = "社区一日游",
        description = "组织老年人参观市内名胜古迹，增进邻里感情，丰富老年人生活。含午餐和交通。",
        date = "每周六",
        time = "上午 8:00-下午 5:00",
        location = "社区门口集合出发",
        participants = 65,
        icon = Icons.Default.DirectionsBus
    )
)

// 活动日期数据（用于日历显示）
val activityDates = listOf(
    "2023-06-15",
    "2023-06-16",
    "2023-06-17",
    "2023-06-18",
    "2023-06-20",
    "2023-06-22",
    "2023-06-23",
    "2023-06-27",
    "2023-06-29",
    "2023-06-30"
)