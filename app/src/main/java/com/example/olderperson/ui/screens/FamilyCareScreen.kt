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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 家庭关爱界面 - 专为老人子女设计
 */
@Composable
fun FamilyCareScreen(onBackToHome: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部栏
        FamilyCareTopBar(onBackToHome)
        
        // 关注的老人快捷方式
        ElderShortcuts()
        
        // 消息标签切换
        CareTabs()
        
        // 动态列表
        UpdatesList()
    }
}

@Composable
fun FamilyCareTopBar(onBackToHome: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackToHome() }
                    .padding(end = 16.dp)
            )
            
            Text(
                text = "对话",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White
        )
    }
}

@Composable
fun ElderShortcuts() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // 添加快捷方式
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "添加",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // 关注的老人列表
        items(elderList) { elder ->
            ElderItem(elder)
        }
    }
}

@Composable
fun ElderItem(elder: Elder) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(elder.bgColor),
            contentAlignment = Alignment.Center
        ) {
            // 这里用图标代替头像
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = elder.name,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = elder.name,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CareTabs() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF333333))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selectedTab == 0) Color.White else Color.Transparent)
                .clickable { selectedTab = 0 }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "健康动态",
                    color = if (selectedTab == 0) Color.Black else Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                if (selectedTab == 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selectedTab == 1) Color.White else Color.Transparent)
                .clickable { selectedTab = 1 }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "日常照料",
                color = if (selectedTab == 1) Color.Black else Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UpdatesList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(updatesList) { update ->
            UpdateItem(update)
        }
    }
}

@Composable
fun UpdateItem(update: ElderUpdate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 处理点击事件 */ },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 联系人头像或图标
        Box {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(update.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = update.title,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            // 若有应用图标，显示在右下角
            update.hasNotification.let {
                if (it) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Notification",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
        
        // 消息内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = update.title,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Text(
                    text = update.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = update.content,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 数据类
data class Elder(
    val name: String,
    val bgColor: Color,
    val relation: String
)

data class ElderUpdate(
    val title: String,
    val content: String,
    val time: String,
    val bgColor: Color,
    val hasNotification: Boolean = false
)

// 模拟数据
val elderList = listOf(
    Elder("父亲", Color(0xFF2196F3), "父亲"),
    Elder("母亲", Color(0xFFFF9800), "母亲"),
    Elder("爷爷", Color(0xFF4CAF50), "爷爷"),
    Elder("奶奶", Color(0xFFE91E63), "奶奶")
)

val updatesList = listOf(
    ElderUpdate(
        title = "健康监测",
        content = "父亲今日心率正常，血压略高",
        time = "10分钟前",
        bgColor = Color(0xFF4CAF50),
        hasNotification = true
    ),
    ElderUpdate(
        title = "用药提醒",
        content = "父亲已服用上午的药物",
        time = "30分钟前",
        bgColor = Color(0xFF2196F3)
    ),
    ElderUpdate(
        title = "运动记录",
        content = "母亲今日健走达标5000步",
        time = "1小时前",
        bgColor = Color(0xFFFF9800)
    ),
    ElderUpdate(
        title = "医疗预约",
        content = "父亲明日9:00有复诊预约",
        time = "2小时前",
        bgColor = Color(0xFFE91E63),
        hasNotification = true
    ),
    ElderUpdate(
        title = "药物管理",
        content = "母亲的降压药需要续方",
        time = "昨天",
        bgColor = Color(0xFF9C27B0),
        hasNotification = true
    ),
    ElderUpdate(
        title = "社区活动",
        content = "社区老年活动中心本周六有太极班",
        time = "昨天",
        bgColor = Color(0xFF795548)
    )
) 