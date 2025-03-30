package com.example.olderperson.ui.screens

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
 * 季节养生页面
 */
@Composable
fun SeasonalWellnessScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("季节养生", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 季节养生小贴士
            item {
                TipsCard(
                    title = "季节养生小贴士",
                    content = "根据四季变化调整生活习惯和饮食，顺应自然规律，是中国传统养生的重要方法。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 当前季节养生
            item {
                CurrentSeasonSection(textToSpeechService)
            }
            
            // 四季养生
            item {
                SectionTitle("四季养生指南", textToSpeechService)
            }
            
            // 四季养生列表
            items(seasons) { season ->
                SeasonCard(season, textToSpeechService)
            }
            
            // 节气养生
            item {
                SectionTitle("节气养生", textToSpeechService)
            }
            
            // 节气养生列表
            items(solarTerms.take(3)) { solarTerm ->
                SolarTermCard(solarTerm, textToSpeechService)
            }
            
            // 查看更多按钮
            item {
                Button(
                    onClick = { textToSpeechService.speak("查看更多节气养生知识") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看更多节气养生知识")
                }
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 当前季节养生区域
 */
@Composable
fun CurrentSeasonSection(textToSpeechService: TextToSpeechService) {
    // 这里假设当前是春季，实际应用中可以根据系统时间判断
    val currentSeason = seasons[0] // 春季
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("当前季节：${currentSeason.name}。${currentSeason.description}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 季节图标
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentSeason.icon,
                        contentDescription = currentSeason.name,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "当前季节：${currentSeason.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "宜：${currentSeason.suitable}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "忌：${currentSeason.avoid}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentSeason.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 季节卡片
 */
@Composable
fun SeasonCard(
    season: Season,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 季节图标
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = season.icon,
                            contentDescription = season.name,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = season.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = { 
                    expanded = !expanded 
                    if (expanded) {
                        textToSpeechService.speak(season.name + "养生要点：" + season.description)
                    }
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开"
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = season.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "宜",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = season.suitable,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "忌",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = season.avoid,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

/**
 * 节气养生卡片
 */
@Composable
fun SolarTermCard(
    solarTerm: SolarTerm,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(solarTerm.name + "：" + solarTerm.description) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = solarTerm.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "时间：${solarTerm.time}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = solarTerm.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "饮食建议",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = solarTerm.dietSuggestion,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "养生要点",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = solarTerm.healthTips,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// 季节数据类
data class Season(
    val name: String,
    val description: String,
    val suitable: String,
    val avoid: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// 节气数据类
data class SolarTerm(
    val name: String,
    val time: String,
    val description: String,
    val dietSuggestion: String,
    val healthTips: String
)

// 四季养生数据
val seasons = listOf(
    Season(
        name = "春季",
        description = "春季养生重在养肝，应顺应春季阳气生发的特点，注意保持情绪舒畅，适当运动。",
        suitable = "早睡早起，户外活动，清淡饮食",
        avoid = "过度劳累，情绪激动，过食辛辣",
        icon = Icons.Default.WbSunny
    ),
    Season(
        name = "夏季",
        description = "夏季养生重在养心，应注意防暑降温，保持心情舒畅，饮食宜清淡。",
        suitable = "适当午休，清淡饮食，补充水分",
        avoid = "过度贪凉，暴饮暴食，剧烈运动",
        icon = Icons.Default.Brightness7
    ),
    Season(
        name = "秋季",
        description = "秋季养生重在养肺，应注意保持情绪稳定，预防呼吸道疾病，饮食宜滋阴润燥。",
        suitable = "早睡早起，适当运动，滋阴润肺",
        avoid = "过度悲伤，过食辛辣，受凉着凉",
        icon = Icons.Default.FilterDrama
    ),
    Season(
        name = "冬季",
        description = "冬季养生重在养肾，应注意保暖，早睡晚起，饮食宜温补。",
        suitable = "早睡晚起，保暖防寒，温补饮食",
        avoid = "过度劳累，受寒着凉，过食生冷",
        icon = Icons.Default.AcUnit
    )
)

// 节气养生数据
val solarTerms = listOf(
    SolarTerm(
        name = "立春",
        time = "2月3日-5日",
        description = "立春是春季的开始，此时阳气开始上升，万物复苏。",
        dietSuggestion = "宜食用葱、蒜、韭菜等辛温食物，少食酸味",
        healthTips = "适当增加户外活动，保持心情舒畅，注意保暖"
    ),
    SolarTerm(
        name = "惊蛰",
        time = "3月5日-7日",
        description = "惊蛰时节雷声始，万物生长加速，阳气更加旺盛。",
        dietSuggestion = "宜食用新鲜蔬菜，如春笋、菠菜等，少食油腻",
        healthTips = "注意调节情绪，避免肝火上升，适当运动"
    ),
    SolarTerm(
        name = "清明",
        time = "4月4日-6日",
        description = "清明时节雨纷纷，气温回升，但早晚温差大。",
        dietSuggestion = "宜食用清淡食物，如青菜、豆芽等，少食辛辣",
        healthTips = "注意添减衣物，预防感冒，保持充足睡眠"
    ),
    SolarTerm(
        name = "立夏",
        time = "5月5日-7日",
        description = "立夏标志着夏季的开始，气温升高，阳气旺盛。",
        dietSuggestion = "宜食用清热解暑食物，如绿豆、苦瓜等",
        healthTips = "注意防暑降温，保持心情舒畅，适当午休"
    ),
    SolarTerm(
        name = "小暑",
        time = "7月6日-8日",
        description = "小暑是一年中最热的时期开始，暑气逐渐增强。",
        dietSuggestion = "宜食用清热解暑食物，如西瓜、绿豆汤等",
        healthTips = "避免在烈日下活动，注意补充水分，保持室内通风"
    ),
    SolarTerm(
        name = "立秋",
        time = "8月7日-9日",
        description = "立秋标志着秋季的开始，暑热渐退，阴气渐生。",
        dietSuggestion = "宜食用滋阴润燥食物，如梨、银耳等",
        healthTips = "注意保护呼吸道，预防秋燥，保持情绪稳定"
    )
)