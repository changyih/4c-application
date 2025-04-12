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
import com.example.olderperson.ui.screens.WalletScreen
import com.example.olderperson.ui.screens.MyFavoritesScreen
import com.example.olderperson.ui.screens.HelpScreen
import com.example.olderperson.ui.screens.ServiceOrderScreen
import com.example.olderperson.ui.screens.MyDevicesScreen

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
                .background(Color.Black)
        ) {
            // 顶部工具栏添加返回按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackToHome() }
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // 顶部个人信息区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 头像
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Avatar",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 用户名和位置
                Text(
                    text = "李明远",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "四川 成都",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // 统计数据区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(count = "03", label = "健康计划")
                StatItem(
                    count = "01",
                    label = "服务订单",
                    onClick = {
                        textToSpeechService.speak("服务订单")
                        showServiceOrder = true
                    }
                )
                StatItem(
                    count = "03",
                    label = "我的设备",
                    onClick = {
                        textToSpeechService.speak("我的设备")
                        showMyDevices = true
                    }
                )
            }

            // 菜单列表
            MenuListItem(
                title = "我的喜欢",
                iconTint = Color(0xFFFF5722),
                onClick = {
                    textToSpeechService.speak("我的喜欢")
                    showFavorites = true
                }
            )
            MenuListItem(
                title = "我的钱包",
                onClick = {
                    textToSpeechService.speak("我的钱包")
                    showWallet = true
                }
            )
            MenuListItem(title = "修改密码")
            MenuListItem(
                title = "帮助",
                onClick = {
                    textToSpeechService.speak("帮助")
                    showHelp = true
                }
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