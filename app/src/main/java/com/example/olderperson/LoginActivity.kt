package com.example.olderperson

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.olderperson.data.UserManager
import com.example.olderperson.data.UserRole
import com.example.olderperson.ui.theme.OlderPersonTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查是否已登录
        lifecycleScope.launch {
            val (userId, rememberLogin) = UserManager.loadLoginState(this@LoginActivity)
            if (userId != null && rememberLogin) {
                val user = UserManager.getUserById(userId)
                if (user != null) {
                    // 根据用户角色跳转到相应界面
                    if (user.role == UserRole.FAMILY) {
                        // 跳转到关爱模式界面
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // 跳转到呵护模式界面
                        val intent = Intent(this@LoginActivity, CareActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        
        // 添加全局异常处理
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val message = "应用发生错误: ${throwable.message}"
            
            // 记录错误日志
            Log.e("LoginActivity", message, throwable)
            
            // 显示一个错误提示
            runOnUiThread {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            
            // 等待提示显示
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                // 忽略
            }
        }
        
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
    
    // 登录处理函数
    private fun handleLogin(phoneNumber: String, password: String, rememberLogin: Boolean) {
        lifecycleScope.launch {
            // 验证用户
            val user = UserManager.authenticateUser(phoneNumber, password)
            if (user != null) {
                // 保存当前用户ID和记住登录状态
                UserManager.saveLoginState(this@LoginActivity, user.id, rememberLogin)
                
                // 根据用户角色跳转到相应界面
                if (user.role == UserRole.FAMILY) {
                    // 跳转到关爱模式界面
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // 跳转到呵护模式界面
                    val intent = Intent(this@LoginActivity, CareActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                // 登录失败
                Toast.makeText(this@LoginActivity, "手机号或密码错误，请重试", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // 注册处理函数
    private fun handleRegister(phoneNumber: String, password: String, name: String, role: UserRole) {
        // 验证输入
        if (phoneNumber.isBlank() || password.isBlank() || name.isBlank()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 验证手机号格式
        if (!phoneNumber.matches(Regex("^1[3-9]\\d{9}$"))) {
            Toast.makeText(this, "请输入有效的手机号码", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 检查手机号是否已注册
        if (UserManager.isPhoneNumberExists(phoneNumber)) {
            Toast.makeText(this, "该手机号已注册，请直接登录", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            // 注册新用户
            val newUser = UserManager.registerUser(phoneNumber, password, name, role)
            
            // 显示注册成功消息
            Toast.makeText(this@LoginActivity, "注册成功，请使用新账号登录", Toast.LENGTH_SHORT).show()
            
            // 显示账号信息对话框
            showAccountsInfoDialog()
            
            // 清空输入框，并切换到登录模式
            // 注意：这里不再自动登录，让用户手动输入账号密码登录
            clearInputFields()
            switchToLoginMode()
        } catch (e: Exception) {
            // 记录错误
            Log.e("LoginActivity", "注册失败: ${e.message}", e)
            Toast.makeText(this@LoginActivity, "注册失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 清空输入框
    private fun clearInputFields() {
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(clearInputs = true)
                }
            }
        }
    }
    
    // 切换到登录模式
    private fun switchToLoginMode() {
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(switchToLogin = true)
                }
            }
        }
    }
    
    // 显示账号信息对话框
    private fun showAccountsInfoDialog() {
        setContent {
            OlderPersonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(showAccounts = true)
                }
            }
        }
    }
    
    // 游客登录处理函数
    private fun handleGuestLogin(isCareMode: Boolean) {
        if (isCareMode) {
            // 跳转到关爱模式界面
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // 跳转到呵护模式界面
            val intent = Intent(this, CareActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    @Composable
    fun LoginScreen(
        clearInputs: Boolean = false,
        switchToLogin: Boolean = false,
        showAccounts: Boolean = false
    ) {
        var isLoginMode by remember { mutableStateOf(true) }
        var phoneNumber by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }
        var rememberLogin by remember { mutableStateOf(false) }

        var isCareMode by remember { mutableStateOf(false) }

        
        // 用于显示提示信息
        var showAccountsInfo by remember { mutableStateOf(false) }
        
        // 处理传入的参数
        LaunchedEffect(clearInputs, switchToLogin, showAccounts) {
            if (clearInputs) {
                phoneNumber = ""
                password = ""
                userName = ""
            }
            
            if (switchToLogin) {
                isLoginMode = true
            }
            
            if (showAccounts) {
                showAccountsInfo = true
            }
        }
        
        // 每次打开对话框时强制重新获取用户列表
        if (showAccountsInfo) {
            AlertDialog(
                onDismissRequest = { showAccountsInfo = false },
                title = { Text("可用账号信息") },
                text = {
                    // 直接获取最新的用户列表并显示，不使用remember缓存
                    val latestUsers = UserManager.getUsers()
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        latestUsers.forEach { user ->
                            val modeText = if (user.role == UserRole.ELDER) "呵护模式" else "关爱模式"
                            Text(
                                "${user.name} (${modeText}):",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1677FF)
                            )
                            Text("手机号: ${user.phoneNumber}")
                            Text("密码: ${user.password}")
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        if (latestUsers.isEmpty()) {
                            Text("当前没有可用账号")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAccountsInfo = false }) {
                        Text("了解")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // 应用标题和副标题
            Text(
                text = "慧龄",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (isLoginMode) "请登录" else "新用户注册",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // 查看账号按钮（只在登录模式显示）
            if (isLoginMode) {
                TextButton(onClick = { showAccountsInfo = true }) {
                    Text("查看现有账号", color = Color(0xFF1677FF))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 模式选择
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ModeSelectorButton(
                    text = "关爱模式",
                    isSelected = isCareMode,
                    onClick = { isCareMode = true }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                ModeSelectorButton(
                    text = "呵护模式",
                    isSelected = !isCareMode,
                    onClick = { isCareMode = false }
                )
            }
            
            // 如果是注册模式，显示姓名输入框
            if (!isLoginMode) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("姓名") },
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "姓名"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8F8F8),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 手机号输入框
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                placeholder = { Text("手机号") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "手机号"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 密码输入框
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                placeholder = { Text("密码") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "密码"
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
            
            // 记住密码选项（只在登录模式显示）
            if (isLoginMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberLogin,
                        onCheckedChange = { rememberLogin = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF1677FF)
                        )
                    )
                    
                    Text(
                        text = "记住密码",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "忘记密码?",
                        fontSize = 14.sp,
                        color = Color(0xFF1677FF),
                        modifier = Modifier.clickable { /* 忘记密码功能 */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 登录/注册按钮
            Button(
                onClick = {
                    if (isLoginMode) {
                        // 登录处理
                        handleLogin(phoneNumber, password, rememberLogin)
                    } else {
                        // 注册处理
                        val role = if (isCareMode) UserRole.FAMILY else UserRole.ELDER
                        handleRegister(phoneNumber, password, userName, role)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1677FF)
                )
            ) {
                Text(
                    text = if (isLoginMode) "登录" else "注册",
                    fontSize = 16.sp
                )
            }
            

            Spacer(modifier = Modifier.height(16.dp))
            
            // 游客模式登录
            if (isLoginMode) {
                OutlinedButton(
                    onClick = { handleGuestLogin(isCareMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1677FF)
                    )
                ) {
                    Text(
                        text = "游客模式",
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            
            // 切换登录/注册模式
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLoginMode) "没有账号？" else "已有账号？",
                    color = Color.Gray
                )
                
                Text(
                    text = if (isLoginMode) "立即注册" else "去登录",
                    color = Color(0xFF1677FF),
                    modifier = Modifier.clickable {
                        isLoginMode = !isLoginMode
                    }
                )
            }
        }
    }
    
    @Composable
    fun ModeSelectorButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        val backgroundColor = if (isSelected) Color(0xFF1677FF) else Color.White
        val textColor = if (isSelected) Color.White else Color.Black
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}