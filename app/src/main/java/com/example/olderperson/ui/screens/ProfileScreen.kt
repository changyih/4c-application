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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(onBackToHome: () -> Unit = {}) {
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
                text = "曾教家",
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
            StatItem(count = "01", label = "服务订单")
            StatItem(count = "03", label = "我的设备")
        }

        // 菜单列表
        MenuListItem(title = "我的喜欢", iconTint = Color(0xFFFF5722))
        MenuListItem(title = "我的钱包")
        MenuListItem(title = "修改密码")
        MenuListItem(title = "帮助")
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF1C1B1F), RoundedCornerShape(12.dp))
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
fun MenuListItem(title: String, iconTint: Color = Color.Gray) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
} 