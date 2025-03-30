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

@Composable
fun WellnessScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    var showDietWellness by remember { mutableStateOf(false) }
    var showExerciseWellness by remember { mutableStateOf(false) }
    var showTCMWellness by remember { mutableStateOf(false) }
    var showSeasonalWellness by remember { mutableStateOf(false) }
    
    if (showDietWellness) {
        DietWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showDietWellness = false }
        )
    } else if (showExerciseWellness) {
        ExerciseWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showExerciseWellness = false }
        )
    } else if (showTCMWellness) {
        TCMWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showTCMWellness = false }
        )
    } else if (showSeasonalWellness) {
        SeasonalWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showSeasonalWellness = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 顶部栏
            WellnessTopBar(onBackClick)
            
            // 内容区域
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 分类导航
                item {
                    val categories = listOf(
                        Category("饮食养生", Icons.Default.Restaurant) { showDietWellness = true },
                        Category("运动保健", Icons.Default.DirectionsRun) { showExerciseWellness = true },
                        Category("中医养生", Icons.Default.LocalHospital) { showTCMWellness = true },
                        Category("季节养生", Icons.Default.WbSunny) { showSeasonalWellness = true }
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItem(category, textToSpeechService)
                        }
                    }
                }
                
                // 每日养生小贴士
                item {
                    DailyTipsCard(textToSpeechService)
                }
                
                // 精选文章
                item {
                    FeaturedArticlesSection(textToSpeechService)
                }
                
                // 养生视频
                item {
                    WellnessVideosSection(textToSpeechService)
                }
            }
        }
    }
}

@Composable
fun WellnessTopBar(onBackClick: () -> Unit = {}) {
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
            text = "养生天地",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { /* 搜索功能 */ }
        )
    }
}

@Composable
fun CategoryNavigation(textToSpeechService: TextToSpeechService) {
    var showDietWellness by remember { mutableStateOf(false) }
    var showExerciseWellness by remember { mutableStateOf(false) }
    var showTCMWellness by remember { mutableStateOf(false) }
    var showSeasonalWellness by remember { mutableStateOf(false) }
    
    if (showDietWellness) {
        DietWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showDietWellness = false }
        )
    } else if (showExerciseWellness) {
        ExerciseWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showExerciseWellness = false }
        )
    } else if (showTCMWellness) {
        TCMWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showTCMWellness = false }
        )
    } else if (showSeasonalWellness) {
        SeasonalWellnessScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showSeasonalWellness = false }
        )
    } else {
        val categories = listOf(
            Category("饮食养生", Icons.Default.Restaurant) { showDietWellness = true },
            Category("运动保健", Icons.Default.DirectionsRun) { showExerciseWellness = true },
            Category("中医养生", Icons.Default.LocalHospital) { showTCMWellness = true },
            Category("季节养生", Icons.Default.WbSunny) { showSeasonalWellness = true }
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category, textToSpeechService)
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            textToSpeechService.speak(category.name)
            category.onClick()
        }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = category.name,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun DailyTipsCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("今日养生小贴士：春季养生要顺应自然，适当运动，清淡饮食。") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "今日养生小贴士",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "春季养生要顺应自然，适当运动，清淡饮食。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FeaturedArticlesSection(textToSpeechService: TextToSpeechService) {
    Column {
        SectionTitle("精选文章", textToSpeechService)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { textToSpeechService.speak("春季养生指南") },
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "春季养生指南",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "春季养生的关键在于顺应自然，根据春季阳气升发的特点，调整作息和饮食...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun WellnessVideosSection(textToSpeechService: TextToSpeechService) {
    Column {
        SectionTitle("养生视频", textToSpeechService)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { textToSpeechService.speak("八段锦教学视频") },
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
                            text = "八段锦教学视频",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "10分钟",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// SectionTitle组件已移至SharedComponents.kt

data class Category(
    val name: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)