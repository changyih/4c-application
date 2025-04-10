package com.example.olderperson.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.olderperson.LoginActivity
import com.example.olderperson.data.UserManager
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.FontSizeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService? = null
) {
    val context = LocalContext.current
    var showWallet by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showServiceOrder by remember { mutableStateOf(false) }
    var showMyDevices by remember { mutableStateOf(false) }
    
    // 显示确认对话框状态
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // 获取当前用户数据
    val currentUser = remember { UserManager.getCurrentUser() }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("确认退出登录") },
            text = { Text("您确定要退出登录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // 退出登录，清除用户数据
                        CoroutineScope(Dispatchers.Main).launch {
                            UserManager.clearCurrentUser(context)
                            // 跳转到登录界面
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { 
                            textToSpeechService?.speak("返回首页")
                            onBackToHome() 
                        }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 添加退出登录按钮
                TextButton(
                    onClick = {
                        textToSpeechService?.speak("退出登录")
                        showLogoutDialog = true
                    }
                ) {
                    Text(
                        text = "退出登录",
                        color = Color.White,
                        fontSize = FontSizeConfig.scaledSp(14).sp
                    )
                }
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
                    text = currentUser?.name ?: "游客",
                    color = Color.White,
                    fontSize = FontSizeConfig.scaledSp(18).sp,
                    fontWeight = FontWeight.Normal
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = currentUser?.location ?: "未知位置",
                        color = Color.Gray,
                        fontSize = FontSizeConfig.scaledSp(12).sp
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
                StatItem(
                    count = currentUser?.healthPlans?.toString() ?: "0", 
                    label = "健康计划"
                )
                StatItem(
                    count = currentUser?.serviceOrders?.toString() ?: "0", 
                    label = "服务订单",
                    onClick = {
                        textToSpeechService?.speak("服务订单")
                        showServiceOrder = true
                    }
                )
                StatItem(
                    count = currentUser?.devices?.toString() ?: "0", 
                    label = "我的设备",
                    onClick = {
                        textToSpeechService?.speak("我的设备")
                        showMyDevices = true
                    }
                )
            }

            // 菜单列表
            MenuListItem(
                title = "我的喜欢", 
                iconTint = Color(0xFFFF5722),
                onClick = { 
                    textToSpeechService?.speak("我的喜欢")
                    showFavorites = true 
                }
            )
            MenuListItem(
                title = "我的钱包", 
                onClick = { 
                    textToSpeechService?.speak("我的钱包")
                    showWallet = true 
                }
            )
            MenuListItem(title = "修改密码")
            MenuListItem(
                title = "帮助",
                onClick = { 
                    textToSpeechService?.speak("帮助")
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
            fontSize = FontSizeConfig.scaledSp(18).sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(12).sp
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
            fontSize = FontSizeConfig.scaledSp(15).sp
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// 子界面占位Composable
@Composable
fun WalletScreen(textToSpeechService: TextToSpeechService? = null, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { 
                    textToSpeechService?.speak("返回")
                    onBackClick() 
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "我的钱包",
            color = Color.White,
            fontSize = FontSizeConfig.scaledSp(24).sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "钱包功能正在开发中...",
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(16).sp
        )
    }
}

@Composable
fun MyFavoritesScreen(textToSpeechService: TextToSpeechService? = null, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { 
                    textToSpeechService?.speak("返回")
                    onBackClick() 
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "我的喜欢",
            color = Color.White,
            fontSize = FontSizeConfig.scaledSp(24).sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "收藏夹为空",
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(16).sp
        )
    }
}

@Composable
fun HelpScreen(textToSpeechService: TextToSpeechService? = null, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { 
                    textToSpeechService?.speak("返回")
                    onBackClick() 
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "帮助中心",
            color = Color.White,
            fontSize = FontSizeConfig.scaledSp(24).sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "如有问题请联系客服: 400-123-4567",
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(16).sp
        )
    }
}

@Composable
fun ServiceOrderScreen(textToSpeechService: TextToSpeechService? = null, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { 
                    textToSpeechService?.speak("返回")
                    onBackClick() 
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "服务订单",
            color = Color.White,
            fontSize = FontSizeConfig.scaledSp(24).sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "您有1个服务订单待处理",
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(16).sp
        )
    }
}

@Composable
fun MyDevicesScreen(textToSpeechService: TextToSpeechService? = null, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { 
                    textToSpeechService?.speak("返回")
                    onBackClick() 
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "我的设备",
            color = Color.White,
            fontSize = FontSizeConfig.scaledSp(24).sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "已绑定3台设备",
            color = Color.Gray,
            fontSize = FontSizeConfig.scaledSp(16).sp
        )
    }
}