package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.olderperson.ui.theme.FontSizeConfig
import com.example.olderperson.SoundSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackToHome: () -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置",
                        fontSize = FontSizeConfig.scaledSp(20).sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        textToSpeechService?.speak("返回首页")
                        onBackToHome()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // 设置分组标题
            SettingsSectionTitle(title = "显示与辅助", icon = Icons.Outlined.Visibility)
            
            // 字体大小设置
            FontSizeSettings(textToSpeechService = textToSpeechService)
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 声音设置
            SoundSettings(textToSpeechService = textToSpeechService)
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 设置分组标题
            SettingsSectionTitle(title = "个人信息", icon = Icons.Outlined.Person)
            
            // 账号与安全
            SettingsItem(
                icon = Icons.Outlined.Security,
                title = "账号与安全",
                description = "账号信息、隐私设置",
                onClick = {
                    textToSpeechService?.speak("账号与安全")
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 紧急联系人
            SettingsItem(
                icon = Icons.Outlined.ContactPhone,
                title = "紧急联系人",
                description = "设置紧急情况下的联系人",
                onClick = {
                    textToSpeechService?.speak("紧急联系人")
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 设置分组标题
            SettingsSectionTitle(title = "系统", icon = Icons.Outlined.Settings)
            
            // 关于我们
            SettingsItem(
                icon = Icons.Outlined.Info,
                title = "关于我们",
                description = "版本信息、使用条款",
                onClick = {
                    textToSpeechService?.speak("关于我们")
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // 退出登录
            SettingsItem(
                icon = Icons.Outlined.Logout,
                title = "退出登录",
                description = "退出当前账号",
                onClick = {
                    textToSpeechService?.speak("退出登录")
                },
                tintColor = Color.Red
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FontSizeSettings(textToSpeechService: TextToSpeechService? = null) {
    var currentFontSize by remember { mutableStateOf(FontSizeConfig.fontSize.value) }
    val fontSizeOptions = listOf(
        Pair("小", 0.8f),
        Pair("标准", 1.0f),
        Pair("大", 1.2f),
        Pair("特大", 1.4f)
    )
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 字体大小标题和说明
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatSize,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 文字说明
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "字体大小",
                    fontSize = FontSizeConfig.scaledSp(16).sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "调整应用界面字体大小",
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = Color.Gray
                )
            }
        }
        
        // 字体大小选择卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 当前预览文本
                Text(
                    text = "这是字体大小预览",
                    fontSize = (16 * currentFontSize).sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 字体大小选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    fontSizeOptions.forEach { (label, size) ->
                        FontSizeOption(
                            label = label,
                            isSelected = currentFontSize == size,
                            onClick = {
                                currentFontSize = size
                                FontSizeConfig.setFontSize(size)
                                textToSpeechService?.speak("已选择${label}字体")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FontSizeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color(0xFF2E7D32) else Color.White
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aa",
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = FontSizeConfig.scaledSp(14).sp,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Gray
        )
    }
}

@Composable
fun SettingsSectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            fontSize = FontSizeConfig.scaledSp(18).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    tintColor: Color = Color(0xFF2E7D32)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 文字说明
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = FontSizeConfig.scaledSp(16).sp,
                fontWeight = FontWeight.Medium,
                color = if (tintColor == Color.Red) tintColor else Color.Black
            )
            
            Text(
                text = description,
                fontSize = FontSizeConfig.scaledSp(14).sp,
                color = Color.Gray
            )
        }
        
        // 箭头图标
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "查看详情",
            tint = Color.Gray
        )
    }
}

@Composable
fun SoundSettings(textToSpeechService: TextToSpeechService? = null) {
    var voiceModeEnabled by remember { mutableStateOf(SoundSettings.voiceEnabled.value) }
    var volume by remember { mutableStateOf(SoundSettings.volume.value) }
    
    // 监听全局设置变化
    LaunchedEffect(SoundSettings.voiceEnabled.value) {
        voiceModeEnabled = SoundSettings.voiceEnabled.value
    }
    
    LaunchedEffect(SoundSettings.volume.value) {
        volume = SoundSettings.volume.value
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 声音设置标题和说明
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.VolumeUp,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 文字说明
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "声音设置",
                    fontSize = FontSizeConfig.scaledSp(16).sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "调整语音播报与提示音",
                    fontSize = FontSizeConfig.scaledSp(14).sp,
                    color = Color.Gray
                )
            }
        }
        
        // 声音设置卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 语音模式开关
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "语音播报",
                            fontSize = FontSizeConfig.scaledSp(16).sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (voiceModeEnabled) "已开启" else "已关闭",
                            fontSize = FontSizeConfig.scaledSp(14).sp,
                            color = Color.Gray
                        )
                    }
                    
                    Switch(
                        checked = voiceModeEnabled,
                        onCheckedChange = { 
                            voiceModeEnabled = it
                            SoundSettings.setVoiceEnabled(it)
                            if (it) {
                                textToSpeechService?.speak("语音播报已开启")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF2E7D32),
                            checkedTrackColor = Color(0xFFAED581)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 音量调节
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "音量",
                            fontSize = FontSizeConfig.scaledSp(16).sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            fontSize = FontSizeConfig.scaledSp(14).sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 音量低图标
                        Icon(
                            imageVector = Icons.Outlined.VolumeMute,
                            contentDescription = "音量低",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        // 音量滑块
                        Slider(
                            value = volume,
                            onValueChange = { 
                                volume = it
                                SoundSettings.setVolume(it)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2E7D32),
                                activeTrackColor = Color(0xFFAED581)
                            )
                        )
                        
                        // 音量高图标
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = "音量高",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // 测试按钮
                    Button(
                        onClick = { 
                            if (voiceModeEnabled) {
                                textToSpeechService?.speak("这是语音播报测试，当前音量为${(volume * 100).toInt()}%") 
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "测试",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "测试",
                            fontSize = FontSizeConfig.scaledSp(14).sp
                        )
                    }
                }
            }
        }
    }
} 