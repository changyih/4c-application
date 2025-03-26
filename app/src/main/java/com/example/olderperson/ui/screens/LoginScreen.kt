package com.example.olderperson.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 登录界面
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 应用标题
        Text(
            text = "老年人关爱应用",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // 用户名输入框
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        // 错误信息
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 16.sp
            )
        }

        // 登录按钮
        Button(
            onClick = {
                if (username.isBlank()) {
                    errorMessage = "请输入用户名"
                } else if (password.isBlank()) {
                    errorMessage = "请输入密码"
                } else {
                    // 简单验证，后续可以接入实际的登录逻辑
                    if (username == "admin" && password == "123456") {
                        errorMessage = null
                        onLoginSuccess()
                    } else {
                        errorMessage = "用户名或密码错误"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "登 录",
                fontSize = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 简化登录 - 适合老年人的快速登录按钮
        OutlinedButton(
            onClick = {
                // 直接登录，不需要用户名和密码
                errorMessage = null
                onLoginSuccess()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "访客登录",
                fontSize = 20.sp
            )
        }
    }
} 