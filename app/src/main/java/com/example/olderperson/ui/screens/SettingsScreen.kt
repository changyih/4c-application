package com.example.olderperson.ui.screens

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService

@Composable
fun SettingsScreen(
    onBackToHome: () -> Unit = {},
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
            // 顶部标题栏 - 大字体，明确返回按钮
            SettingsHeader(onBackToHome)
            
            // 主要内容区域 - 间距大，选项少
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp) // 增大间距
            ) {
                // 显示设置卡片
                item {
                    DisplaySettingsCard()
                }
                
                // 声音设置卡片
                item {
                    SoundSettingsCard()
                }
                
                // 通知设置卡片
                item {
                    NotificationSettingsCard()
                }
                
                // 帮助与支持卡片
                item {
                    HelpSupportCard()
                }
                
                // 紧急联系人
                item {
                    EmergencyContactCard()
                }
                
                // 退出登录按钮 - 大按钮，明确文字
                item {
                    LogoutButton()
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // 简化的底部导航栏 - 大图标
            BottomNavigationBar(onHomeClick = onBackToHome)
        }
    }
}

@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 明确且大的返回按钮
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp) // 增大点击区域
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp) // 增大图标
                )
            }
            
            // 大标题
            Text(
                text = "设置",
                fontSize = 20.sp, // 修改为20sp与其他页面一致
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun LargeSettingItem(
    icon: ImageVector,
    title: String,
    showToggle: Boolean = false,
    isToggled: Boolean = false,
    onToggleChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick ?: {})
            .padding(vertical = 16.dp), // 增大垂直间距
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 更大的图标
        Box(
            modifier = Modifier
                .size(60.dp) // 增大尺寸
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(36.dp) // 增大图标
            )
        }
        
        Spacer(modifier = Modifier.width(20.dp)) // 增大间距
        
        // 更大的文字
        Text(
            text = title,
            fontSize = 18.sp, // 修改为18sp与其他页面一致
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // 更大的开关
        if (showToggle) {
            Switch(
                checked = isToggled,
                onCheckedChange = onToggleChange,
                modifier = Modifier.scale(1.3f), // 放大开关
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2E7D32),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
private fun DisplaySettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // 增加阴影使卡片更突出
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "显示设置",
                fontSize = 20.sp, // 保持20sp
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 字体大小
            LargeSettingItem(
                icon = Icons.Default.FormatSize,
                title = "字体大小",
                onClick = { /* 打开简化的字体大小设置 */ }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 2.dp // 加粗分隔线
            )
            
            // 屏幕亮度
            LargeSettingItem(
                icon = Icons.Default.BrightnessHigh,
                title = "屏幕亮度",
                onClick = { /* 打开简化的亮度调节 */ }
            )
        }
    }
}

@Composable
private fun SoundSettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "声音设置",
                fontSize = 20.sp, // 保持20sp
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 语音朗读
            LargeSettingItem(
                icon = Icons.Default.VolumeUp,
                title = "语音朗读",
                showToggle = true,
                isToggled = true,
                onToggleChange = { /* 切换语音朗读 */ }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 2.dp
            )
            
            // 按键音效
            LargeSettingItem(
                icon = Icons.Default.TouchApp,
                title = "按键音效",
                showToggle = true,
                isToggled = false,
                onToggleChange = { /* 切换按键音效 */ }
            )
        }
    }
}

@Composable
private fun NotificationSettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "通知设置",
                fontSize = 20.sp, // 保持20sp
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 通知开关 - 单一选项
            LargeSettingItem(
                icon = Icons.Default.Notifications,
                title = "接收通知",
                showToggle = true,
                isToggled = true,
                onToggleChange = { /* 切换通知设置 */ }
            )
        }
    }
}

@Composable
private fun HelpSupportCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "帮助与支持",
                fontSize = 20.sp, // 保持20sp
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 使用帮助
            LargeSettingItem(
                icon = Icons.Default.Help,
                title = "使用帮助",
                onClick = { /* 打开使用帮助 */ }
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 2.dp
            )
            
            // 联系客服
            LargeSettingItem(
                icon = Icons.Default.Call,
                title = "联系客服",
                onClick = { /* 拨打客服电话 */ }
            )
        }
    }
}

@Composable
private fun EmergencyContactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题
            Text(
                text = "紧急联系人",
                fontSize = 20.sp, // 保持20sp
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 紧急联系人列表 - 显示已设置的联系人
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 联系人图标
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE57373)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "联系人",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 联系人信息
                Column {
                    Text(
                        text = "小明 (儿子)",
                        fontSize = 18.sp, // 修改为18sp
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "138****6789",
                        fontSize = 16.sp, // 修改为16sp
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 添加/修改按钮
            Button(
                onClick = { /* 添加或修改紧急联系人 */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "修改联系人",
                    fontSize = 16.sp, // 修改为16sp
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LogoutButton() {
    Button(
        onClick = { /* 退出登录 */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), // 增大按钮高度
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE57373)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "退出登录",
            tint = Color.White,
            modifier = Modifier.size(28.dp) // 增大图标
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = "退出登录",
            color = Color.White,
            fontSize = 18.sp // 修改为18sp
        )
    }
}

@Composable
private fun BottomNavigationBar(onHomeClick: () -> Unit) {
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
            SettingsBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                isSelected = false,
                onClick = onHomeClick
            )
            
            // 对话按钮
            SettingsBottomNavItem(
                icon = Icons.Outlined.Chat,
                label = "对话",
                isSelected = false
            )
            
            // 探索按钮
            SettingsBottomNavItem(
                icon = Icons.Outlined.Explore,
                label = "探索",
                isSelected = false
            )
            
            // 设置按钮
            SettingsBottomNavItem(
                icon = Icons.Outlined.Settings,
                label = "设置",
                isSelected = true
            )
        }
    }
}

@Composable
private fun SettingsBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(32.dp) // 增大图标
        )
        
        Text(
            text = label,
            fontSize = 14.sp, // 修改为14sp
            color = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
} 