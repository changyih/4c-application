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

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.olderperson.utils.ScheduleManager

@Composable
fun ProfileScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService,
    onLogout: () -> Unit = {}
) {
    var showWallet by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showServiceOrder by remember { mutableStateOf(false) }
    var showMyDevices by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSchedule by remember { mutableStateOf(false) }

    // 获取日程数量
    val context = LocalContext.current
    val scheduleManager = remember { 
        try {
            ScheduleManager.getInstance(context) 
        } catch (e: Exception) {
            Log.e("ProfileScreen", "获取ScheduleManager实例失败: ${e.message}", e)
            null
        }
    }
    
    // 获取日程数量
    val schedulesCount = remember {
        try {
            val count = scheduleManager?.getAllScheduleItems()?.size ?: 0
            count.toString().padStart(2, '0')
        } catch (e: Exception) {
            Log.e("ProfileScreen", "获取日程数量失败: ${e.message}", e)
            "00"
        }
    }

    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出关爱模式吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        textToSpeechService.speak("退出登录")
                        showLogoutDialog = false
                        onLogout()
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
    }  else if (showChangePassword) {
        ChangePasswordScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showChangePassword = false }
        )
    } else if (showSchedule) {
        ScheduleScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showSchedule = false }
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
                        text = "吉林 长春",
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
                StatItem(
                    count = schedulesCount,
                    label = "今日安排",
                    onClick = {
                        textToSpeechService.speak("今日安排")
                        showSchedule = true
                    }
                )
                StatItem(
                    count = "01",
                    label = "服务订单",
                    onClick = {
                        textToSpeechService.speak("服务订单")
                        showServiceOrder = true
                    }
                )
                StatItem(
                    count = "02",
                    label = "绑定家人",
                    onClick = {
                        textToSpeechService.speak("绑定家人")
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
            MenuListItem(
                title = "修改密码",
                onClick = {
                    textToSpeechService.speak("修改密码")
                    showChangePassword = true
                }
            )
            MenuListItem(
                title = "帮助",
                onClick = {
                    textToSpeechService.speak("帮助")
                    showHelp = true
                }
            )
            
            // 添加退出登录菜单项
            MenuListItem(
                title = "退出登录",
                iconTint = Color.Red,
                onClick = {
                    textToSpeechService.speak("退出登录")
                    showLogoutDialog = true
                }
            )
        }
    }
}

@Composable
fun ChangePasswordScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBackClick()
            },
            title = { Text("成功") },
            text = { Text("密码修改成功！") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        onBackClick()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("错误") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部工具栏
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
                    .clickable { onBackClick() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "修改密码",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 当前密码输入框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "当前密码",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                TextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    placeholder = { Text("请输入当前密码", color = Color.Gray, fontSize = 14.sp) },
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                )
                
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                    Icon(
                        imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (currentPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 新密码输入框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "新密码",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("请输入新密码", color = Color.Gray, fontSize = 14.sp) },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                )
                
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (newPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color.Gray
                    )
                }
            }
            
            // 添加密码要求提示
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person, // 这里用了一个替代图标，实际应该使用圆形提示图标
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "密码由8-16位数字、字母或符号组成",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person, // 这里用了一个替代图标
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "至少含2种以上字符",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 确认新密码输入框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "确认新密码",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1C1B1F), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("请再次输入新密码", color = Color.Gray, fontSize = 14.sp) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                )
                
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 确认按钮
        Button(
            onClick = {
                when {
                    currentPassword.isEmpty() -> {
                        errorMessage = "请输入当前密码"
                        showErrorDialog = true
                    }
                    newPassword.isEmpty() -> {
                        errorMessage = "请输入新密码"
                        showErrorDialog = true
                    }
                    confirmPassword.isEmpty() -> {
                        errorMessage = "请确认新密码"
                        showErrorDialog = true
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "两次输入的新密码不一致"
                        showErrorDialog = true
                    }
                    newPassword.length < 8 || newPassword.length > 16 -> {
                        errorMessage = "新密码长度应为8-16位"
                        showErrorDialog = true
                    }
                    !isPasswordComplex(newPassword) -> {
                        errorMessage = "密码应包含至少两种不同类型的字符"
                        showErrorDialog = true
                    }
                    currentPassword == "123456" -> { // 模拟验证当前密码
                        textToSpeechService.speak("密码修改成功")
                        showSuccessDialog = true
                    }
                    else -> {
                        errorMessage = "当前密码不正确"
                        showErrorDialog = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
        ) {
            Text(
                text = "确认修改",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // 添加底部安全提示
        Text(
            text = "安全提示: 新密码请勿与旧密码过于相似。",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

// 添加密码复杂度验证函数
private fun isPasswordComplex(password: String): Boolean {
    var hasLetter = false
    var hasDigit = false
    var hasSpecial = false
    
    for (char in password) {
        when {
            char.isLetter() -> hasLetter = true
            char.isDigit() -> hasDigit = true
            else -> hasSpecial = true
        }
    }
    
    // 检查是否至少包含两种不同类型的字符
    return (hasLetter && hasDigit) || (hasLetter && hasSpecial) || (hasDigit && hasSpecial)
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