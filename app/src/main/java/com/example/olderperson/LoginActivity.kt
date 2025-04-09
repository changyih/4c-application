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
import com.example.olderperson.ui.theme.OlderPersonTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
                    LoginScreen(
                        onLoginSuccess = { isCareMode ->
                            if (isCareMode) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                // 跳转到呵护模式界面
                                val intent = Intent(this, CareActivity::class.java)
                                startActivity(intent)
                                finish()
               
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (Boolean) -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberPassword by remember { mutableStateOf(false) }
    var isCareMode by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // 应用标题和副标题
        Text(
            text = "颐年",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "请登录",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
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
        
        // 记住密码
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberPassword,
                onCheckedChange = { rememberPassword = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "记住密码",
                modifier = Modifier.clickable { rememberPassword = !rememberPassword }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 登录按钮
        Button(
            onClick = {
                if (phoneNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "请输入手机号和密码", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                onLoginSuccess(isCareMode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A2E)
            )
        ) {
            Text("登录", fontSize = 16.sp)
        }
        
        // 游客登录按钮
        TextButton(
            onClick = { onLoginSuccess(isCareMode) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("游客登录", fontSize = 15.sp)
        }
        
        // 其他登录方式
        Text(
            text = "其他登录方式",
            modifier = Modifier.padding(top = 12.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        // 第三方登录图标
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 微信登录
            ThirdPartyLoginButton(
                iconResourceId = android.R.drawable.ic_dialog_info,
                description = "微信登录",
                tint = Color(0xFF07C160)
            )
            
            Spacer(modifier = Modifier.width(40.dp))
            
            // QQ登录
            ThirdPartyLoginButton(
                iconResourceId = android.R.drawable.ic_dialog_info,
                description = "QQ登录",
                tint = Color(0xFF12B7F5)
            )
            
            Spacer(modifier = Modifier.width(40.dp))
            
            // 支付宝登录
            ThirdPartyLoginButton(
                iconResourceId = android.R.drawable.ic_dialog_info,
                description = "支付宝登录",
                tint = Color(0xFF1677FF)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 底部选项
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "忘记密码",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "|",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "立即注册",
                color = Color(0xFF1677FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ThirdPartyLoginButton(
    iconResourceId: Int,
    description: String,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconResourceId),
            contentDescription = description,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}