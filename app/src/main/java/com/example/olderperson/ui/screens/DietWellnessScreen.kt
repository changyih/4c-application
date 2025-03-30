package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * 饮食养生页面
 */
@Composable
fun DietWellnessScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("饮食养生", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 饮食养生小贴士
            item {
                TipsCard(
                    title = "饮食养生小贴士",
                    content = "老年人饮食应以清淡为主，多吃蔬果，少食多餐，注意补充蛋白质。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 饮食养生文章列表
            item {
                SectionTitle("推荐食谱", textToSpeechService)
            }
            
            // 食谱列表
            items(dietArticles) { article ->
                ArticleCard(article, textToSpeechService)
            }
            
            // 季节饮食推荐
            item {
                SectionTitle("季节饮食推荐", textToSpeechService)
            }
            
            // 季节食材
            item {
                SeasonalFoodRecommendation(textToSpeechService)
            }
            
            // 互动问答
            item {
                QASection(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 顶部栏组件
 */
@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit = {},
    textToSpeechService: TextToSpeechService
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
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak(title) }
        )
    }
}

/**
 * 小贴士卡片
 */
@Composable
fun TipsCard(
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
 * 文章卡片
 */
@Composable
fun ArticleCard(
    article: Article,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(article.title + "。" + article.summary) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = article.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = article.summary,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            if (article.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    article.tags.forEach { tag ->
                        Chip(tag)
                    }
                }
            }
        }
    }
}

/**
 * 标签组件
 */
@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 季节食材推荐
 */
@Composable
fun SeasonalFoodRecommendation(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("春季应多吃的食材：菠菜、春笋、荠菜、芦笋等。这些食材富含维生素和矿物质，有助于调节身体机能。") },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "春季应多吃的食材",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "菠菜、春笋、荠菜、芦笋等。这些食材富含维生素和矿物质，有助于调节身体机能。",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { textToSpeechService.speak("查看详细食材列表") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("查看详细")
            }
        }
    }
}

/**
 * 互动问答区域
 */
@Composable
fun QASection(textToSpeechService: TextToSpeechService) {
    var question by remember { mutableStateOf("") }
    var showAnswer by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "有问题？向我提问",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = { Text("例如：高血压患者应该注意什么？") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (question.isNotEmpty()) {
                            showAnswer = true
                            textToSpeechService.speak("您的问题是：$question。高血压患者应控制钠盐摄入，多吃富含钾的食物如香蕉、土豆等，避免高脂肪食物。")
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "发送")
                }
            }
            
            if (showAnswer) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "回答：",
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "高血压患者应控制钠盐摄入，多吃富含钾的食物如香蕉、土豆等，避免高脂肪食物。"
                        )
                    }
                }
            }
        }
    }
}

// 饮食养生文章数据
val dietArticles = listOf(
    Article(
        title = "老年人饮食营养搭配指南",
        summary = "合理的饮食搭配可以满足老年人的营养需求，本文介绍适合老年人的饮食搭配方案...",
        tags = listOf("营养搭配", "老年饮食")
    ),
    Article(
        title = "三高人群的饮食建议",
        summary = "针对高血压、高血脂、高血糖人群的饮食建议，包括食物选择和烹饪方法...",
        tags = listOf("三高", "健康饮食")
    ),
    Article(
        title = "春季养生粥的做法",
        summary = "春季养生粥可以帮助调节身体机能，增强免疫力，本文介绍几种简单易做的养生粥...",
        tags = listOf("养生粥", "春季养生")
    )
)

// 文章数据类
data class Article(
    val title: String,
    val summary: String,
    val tags: List<String> = emptyList()
)