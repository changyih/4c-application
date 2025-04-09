package com.example.olderperson.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CareHomeScreen(
    userName: String,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFamily: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},

    onNavigateToChat: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},

    onNavigateToSettings: () -> Unit = {},

    textToSpeechService: TextToSpeechService? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部问候区域
            TopGreetingSection(userName)
            
            // 主要内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 导航按钮区域
                item {
                    NavigationButtons(
                        onNavigateToProfile = {
                            textToSpeechService?.speak("进入我和自己页面")
                            onNavigateToProfile()
                        },
                        onNavigateToFamily = onNavigateToFamily,
                        onNavigateToCommunity = onNavigateToCommunity
                    )
                }
                
                // 今日安排
                item {
                    TodayScheduleCard()
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // 底部导航栏
            BottomNavigationBar(
                onHomeClick = { /* 当前在首页，无需导航 */ },
                onChatClick = onNavigateToChat,
                onExploreClick = onNavigateToExplore,
                onSettingsClick = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun TopGreetingSection(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "您好，$userName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = getCurrentDate(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // 用户头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF87CEEB))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // 智能助手对话框
        AssistantChatBox()
    }
}

@Composable
private fun AssistantChatBox() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E7D32)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 机器人图标
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "智能助手",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "我是您的智能伙伴",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "今天有什么可以帮您的吗？",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 语音交流按钮
                CommunicationButton(
                    icon = Icons.Default.Mic,
                    text = "语音交流",
                    modifier = Modifier.weight(1f)
                )

                // 文字交流按钮
                CommunicationButton(
                    icon = Icons.Default.Keyboard,
                    text = "文字交流",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CommunicationButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun NavigationButtons(
    onNavigateToProfile: () -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavigationButton(
            icon = Icons.Default.Person,
            text = "我和自己",
            onClick = {
                // 添加日志确认点击
                Log.d("CareHomeScreen", "我和自己按钮被点击")
                onNavigateToProfile()
            }
        )
        NavigationButton(
            icon = Icons.Default.Home,
            text = "我和家人",
            onClick = onNavigateToFamily
        )
        NavigationButton(
            icon = Icons.Default.People,
            text = "我和社区",
            onClick = onNavigateToCommunity
        )
    }
}

@Composable
private fun NavigationButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { 
                    onClick() 
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF87CEEB),
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun TodayScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和查看全部
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 标题图标和文字
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "今日安排",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "今日安排",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 查看全部
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 查看全部 */ }
                ) {
                    Text(
                        text = "全部",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看全部",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 晨间服药
            ScheduleItem(
                time = "08:00",
                title = "晨间服药",
                description = "降压药 1片，维生素 1片"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 心脏科复诊
            ScheduleItem(
                time = "10:30",
                title = "心脏科复诊",
                description = "市第一人民医院"
            )
        }
    }
}

@Composable
private fun ScheduleItem(
    time: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF87CEEB)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onChatClick: () -> Unit,
    onExploreClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp), // 增大高度
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                isSelected = true,
                onClick = onHomeClick
            )
            
            // 对话按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Chat,
                label = "对话",
                isSelected = false,
                onClick = onChatClick
            )
            
            // 探索按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Explore,
                label = "探索",
                isSelected = false,
                onClick = onExploreClick
            )
            
            // 设置按钮
            HomeBottomNavItem(
                icon = Icons.Outlined.Settings,
                label = "设置",
                isSelected = false,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun HomeBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
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
            tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            fontSize = 12.sp
        )
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINESE)
    return dateFormat.format(Date())
} 