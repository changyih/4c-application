package com.example.olderperson.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.components.SectionTitle

/**
 * 运动保健页面
 */
@Composable
fun ExerciseWellnessScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("运动保健", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 运动保健小贴士
            item {
                TipsCard(
                    title = "运动保健小贴士",
                    content = "老年人应选择低强度、持续性的有氧运动，如散步、太极拳等，每天坚持30分钟。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 推荐运动
            item {
                SectionTitle("推荐运动", textToSpeechService)
            }
            
            // 运动列表
            items(exerciseItems) { exercise ->
                ExerciseCard(exercise, textToSpeechService)
            }
            
            // 视频教程
            item {
                SectionTitle("视频教程", textToSpeechService)
            }
            
            // 视频列表
            items(exerciseVideos) { video ->
                VideoCard(video, textToSpeechService)
            }
            
            // 运动计划制定
            item {
                ExercisePlanSection(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 运动卡片
 */
@Composable
fun ExerciseCard(
    exercise: Exercise,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(exercise.name + "。" + exercise.description) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 运动图标
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = exercise.icon,
                    contentDescription = exercise.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = exercise.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = exercise.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "时长",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = exercise.duration,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "强度",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = exercise.intensity,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * 视频卡片
 */
@Composable
fun VideoCard(
    video: ExerciseVideo,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(video.title + "。" + video.description) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "播放",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = video.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = video.duration,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = video.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { textToSpeechService.speak("开始播放" + video.title) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("开始练习")
            }
        }
    }
}

/**
 * 运动计划制定区域
 */
@Composable
fun ExercisePlanSection(textToSpeechService: TextToSpeechService) {
    var expanded by remember { mutableStateOf(false) }
    
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
                    text = "制定个人运动计划",
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
                    text = "根据您的身体状况，我们可以为您制定个性化的运动计划。请选择您的健康状况：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 健康状况选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HealthConditionButton("一般健康", textToSpeechService)
                    HealthConditionButton("有慢性病", textToSpeechService)
                    HealthConditionButton("康复期", textToSpeechService)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { textToSpeechService.speak("为您生成个性化运动计划") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("生成计划")
                }
            }
        }
    }
}

/**
 * 健康状况按钮
 */
@Composable
fun HealthConditionButton(text: String, textToSpeechService: TextToSpeechService) {
    var selected by remember { mutableStateOf(false) }
    
    OutlinedButton(
        onClick = {
            selected = !selected
            textToSpeechService.speak("已选择：$text")
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text)
    }
}

// 运动数据类
data class Exercise(
    val name: String,
    val description: String,
    val duration: String,
    val intensity: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// 视频数据类
data class ExerciseVideo(
    val title: String,
    val duration: String,
    val description: String
)

// 运动列表数据
val exerciseItems = listOf(
    Exercise(
        name = "晨间散步",
        description = "早晨进行30分钟的轻松散步，可以促进血液循环，唤醒身体。",
        duration = "30分钟",
        intensity = "低强度",
        icon = Icons.Default.DirectionsWalk
    ),
    Exercise(
        name = "太极拳",
        description = "太极拳动作柔和，有助于提高平衡能力和关节灵活性。",
        duration = "20分钟",
        intensity = "中强度",
        icon = Icons.Default.SelfImprovement
    ),
    Exercise(
        name = "椅子健身操",
        description = "适合行动不便的老年人，坐在椅子上也能锻炼身体。",
        duration = "15分钟",
        intensity = "低强度",
        icon = Icons.Default.Chair
    )
)

// 视频教程数据
val exerciseVideos = listOf(
    ExerciseVideo(
        title = "老年人太极拳基础教学",
        duration = "15分钟",
        description = "简单易学的太极拳动作，适合老年人在家练习。"
    ),
    ExerciseVideo(
        title = "坐姿伸展运动",
        duration = "10分钟",
        description = "适合行动不便的老年人，坐着就能完成的伸展运动。"
    )
)