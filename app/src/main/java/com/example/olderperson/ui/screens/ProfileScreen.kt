package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.automirrored.filled.*
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

@Composable
fun ProfileScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService
) {
    var showWallet by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showServiceOrder by remember { mutableStateOf(false) }
    var showMyDevices by remember { mutableStateOf(false) }
    
    if (showWallet) {
        WalletScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showWallet = false }
        )
    } else if (showFavorites) {
        MyFavoritesScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showFavorites = false }
        )
    } else if (showHelp) {
        HelpScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showHelp = false }
        )
    } else if (showServiceOrder) {
        ServiceOrderScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showServiceOrder = false }
        )
    } else if (showMyDevices) {
        MyDevicesScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showMyDevices = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部区域
            ProfileHeader(onBackToHome)
            
            // 健康中心卡片
            HealthCenterCard()
            
            // 用药信息卡片
            MedicationCard()
        }
    }
}

@Composable
private fun ProfileHeader(onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            IconButton(
                onClick = onBackToHome
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }
            
            Text(
                text = "我和自己",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
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
        
        // 副标题
        Text(
            text = "关注自我健康和成长",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun HealthCenterCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和详情入口
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "健康中心",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "健康中心",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "详情",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看详情",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康数据行 - 第一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 血压
                HealthDataItem(
                    value = "126/82",
                    label = "血压 (mmHg)",
                    modifier = Modifier.weight(1f)
                )
                
                // 心率
                HealthDataItem(
                    value = "72",
                    label = "心率 (次/分)",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 健康数据行 - 第二行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 今日步数
                HealthDataItem(
                    value = "2305",
                    label = "今日步数",
                    modifier = Modifier.weight(1f)
                )
                
                // 睡眠
                HealthDataItem(
                    value = "6.2",
                    label = "睡眠 (小时)",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HealthDataItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun MedicationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "今日用药",
                    tint = Color(0xFF2E7D32),
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
            
            // 降压药
            MedicationItem(
                name = "降压药",
                time = "上午 8:00",
                isCompleted = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 钙片
            MedicationItem(
                name = "钙片",
                time = "下午 12:30",
                isCompleted = false
            )
        }
    }
}

@Composable
private fun MedicationItem(
    name: String,
    time: String,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 药品图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFC107).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = name,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 药品信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = time,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 状态标签
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isCompleted) "已完成" else "待服用",
                color = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFFFA000),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatItem(count: String, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF1C1B1F), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = count,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MenuListItem(title: String, iconTint: Color = Color.Gray, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 15.sp
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}