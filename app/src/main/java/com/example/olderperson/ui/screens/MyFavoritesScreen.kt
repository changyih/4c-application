package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

/**
 * 我的喜欢页面
 */
@Composable
fun MyFavoritesScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    // 当前选中的分类
    var selectedCategory by remember { mutableStateOf("全部") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        FavoritesTopBar(onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 分类导航
            item {
                CategoryTabs(selectedCategory, onCategorySelected = { category ->
                    selectedCategory = category
                    textToSpeechService.speak(category)
                })
            }
            
            // 收藏内容列表
            when (selectedCategory) {
                "全部" -> {
                    // 文章
                    item {
                        Text(
                            text = "收藏的文章",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                .clickable { textToSpeechService.speak("收藏的文章") }
                        )
                    }
                    
                    items(favoriteArticles) { article ->
                        FavoriteArticleItem(article, textToSpeechService)
                    }
                    
                    // 视频
                    item {
                        Text(
                            text = "收藏的视频",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                .clickable { textToSpeechService.speak("收藏的视频") }
                        )
                    }
                    
                    items(favoriteVideos) { video ->
                        FavoriteVideoItem(video, textToSpeechService)
                    }
                    
                    // 养生知识
                    item {
                        Text(
                            text = "收藏的养生知识",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                .clickable { textToSpeechService.speak("收藏的养生知识") }
                        )
                    }
                    
                    items(favoriteWellnessTips) { tip ->
                        FavoriteWellnessTipItem(tip, textToSpeechService)
                    }
                }
                "文章" -> {
                    items(favoriteArticles) { article ->
                        FavoriteArticleItem(article, textToSpeechService)
                    }
                }
                "视频" -> {
                    items(favoriteVideos) { video ->
                        FavoriteVideoItem(video, textToSpeechService)
                    }
                }
                "养生知识" -> {
                    items(favoriteWellnessTips) { tip ->
                        FavoriteWellnessTipItem(tip, textToSpeechService)
                    }
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
 * 我的喜欢页面顶部栏
 */
@Composable
fun FavoritesTopBar(
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
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "我的喜欢",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("我的喜欢") }
        )
    }
}

/**
 * 分类标签栏
 */
@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("全部", "文章", "视频", "养生知识")
    
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        edgePadding = 0.dp,
        containerColor = Color.White,
        contentColor = Color(0xFFFF5722),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[categories.indexOf(selectedCategory)]),
                height = 2.dp,
                color = Color(0xFFFF5722)
            )
        }
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = category,
                        fontSize = 16.sp,
                        fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = Color(0xFFFF5722),
                unselectedContentColor = Color.Gray
            )
        }
    }
}

/**
 * 收藏的文章项
 */
@Composable
fun FavoriteArticleItem(
    article: FavoriteArticle,
    textToSpeechService: TextToSpeechService
) {
    var isFavorite by remember { mutableStateOf(true) }
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "取消收藏" else "收藏",
                    tint = if (isFavorite) Color(0xFFFF5722) else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isFavorite = !isFavorite
                            if (isFavorite) {
                                textToSpeechService.speak("已收藏")
                            } else {
                                textToSpeechService.speak("已取消收藏")
                            }
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = article.summary,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                article.tags.forEach { tag ->
                    FavoriteChip(tag)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = article.date,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * 收藏的视频项
 */
@Composable
fun FavoriteVideoItem(
    video: FavoriteVideo,
    textToSpeechService: TextToSpeechService
) {
    var isFavorite by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(video.title) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 视频缩略图区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "播放视频",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = video.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "取消收藏" else "收藏",
                    tint = if (isFavorite) Color(0xFFFF5722) else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isFavorite = !isFavorite
                            if (isFavorite) {
                                textToSpeechService.speak("已收藏")
                            } else {
                                textToSpeechService.speak("已取消收藏")
                            }
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = video.duration,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 收藏的养生知识项
 */
@Composable
fun FavoriteWellnessTipItem(
    tip: FavoriteWellnessTip,
    textToSpeechService: TextToSpeechService
) {
    var isFavorite by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(tip.title + "。" + tip.content) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    text = tip.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "取消收藏" else "收藏",
                    tint = if (isFavorite) Color(0xFFFF5722) else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isFavorite = !isFavorite
                            if (isFavorite) {
                                textToSpeechService.speak("已收藏")
                            } else {
                                textToSpeechService.speak("已取消收藏")
                            }
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = tip.category,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * 收藏标签组件
 */
@Composable
fun FavoriteChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEEEEEE))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

// 模拟数据 - 收藏的文章
val favoriteArticles = listOf(
    FavoriteArticle(
        title = "老年人饮食健康指南",
        summary = "针对老年人的饮食需求，提供全面的营养建议和食谱推荐。",
        tags = listOf("饮食", "健康", "营养"),
        date = "2023-05-15"
    ),
    FavoriteArticle(
        title = "秋季养生小常识",
        summary = "秋季气候干燥，如何调整饮食和生活习惯来保持健康。",
        tags = listOf("季节养生", "秋季", "保健"),
        date = "2023-09-10"
    ),
    FavoriteArticle(
        title = "中医按摩缓解关节疼痛",
        summary = "通过中医按摩手法，有效缓解老年人常见的关节疼痛问题。",
        tags = listOf("中医", "按摩", "关节健康"),
        date = "2023-07-22"
    )
)

// 模拟数据 - 收藏的视频
val favoriteVideos = listOf(
    FavoriteVideo(
        title = "每日五分钟太极拳教学",
        duration = "15:30"
    ),
    FavoriteVideo(
        title = "老年人居家健身指南",
        duration = "20:45"
    )
)

// 模拟数据 - 收藏的养生知识
val favoriteWellnessTips = listOf(
    FavoriteWellnessTip(
        title = "冬季保暖小贴士",
        content = "老年人冬季保暖要注意：穿着宽松保暖的衣物，保持室内温度适宜，避免剧烈温差变化，多喝温水，适当运动增强体质。",
        category = "季节养生"
    ),
    FavoriteWellnessTip(
        title = "健康睡眠指南",
        content = "保持规律的作息时间，睡前避免饮用咖啡和浓茶，睡前可以喝杯温牛奶，保持卧室安静、黑暗和适宜的温度。",
        category = "日常保健"
    )
)

// 数据类
data class FavoriteArticle(
    val title: String,
    val summary: String,
    val tags: List<String>,
    val date: String
)

data class FavoriteVideo(
    val title: String,
    val duration: String
)

data class FavoriteWellnessTip(
    val title: String,
    val content: String,
    val category: String
)