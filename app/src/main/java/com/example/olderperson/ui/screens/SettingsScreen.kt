package com.example.olderperson.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.FontSizeConfig
import com.example.olderperson.SoundSettings
import com.example.olderperson.utils.EmergencyContactsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackToHome: () -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToMagnifier: () -> Unit = {},
    textToSpeechService: TextToSpeechService? = null
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    // 确认退出对话框状态
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // 紧急联系人设置对话框状态
    var showEmergencyContactDialog by remember { mutableStateOf(false) }
    
    // 获取当前紧急联系人信息
    val currentEmergencyContact = remember { 
        mutableStateOf(EmergencyContactsManager.getEmergencyContact(context)) 
    }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出当前账号吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        textToSpeechService?.speak("退出登录")
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
    
    // 紧急联系人设置对话框
    if (showEmergencyContactDialog) {
        EmergencyContactDialog(
            currentContact = currentEmergencyContact.value,
            onDismiss = { showEmergencyContactDialog = false },
            onSave = { contact ->
                EmergencyContactsManager.saveEmergencyContact(context, contact)
                currentEmergencyContact.value = contact
                showEmergencyContactDialog = false
                Toast.makeText(context, "紧急联系人已保存", Toast.LENGTH_SHORT).show()
                textToSpeechService?.speak("紧急联系人已设置为${contact.name}")
            },
            textToSpeechService = textToSpeechService
        )
    }
    
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
            
            // 放大镜功能
            SettingsItem(
                icon = Icons.Outlined.ZoomIn,
                title = "简易放大镜",
                description = "使用相机放大查看细小字体或物体",
                onClick = {
                    textToSpeechService?.speak("打开简易放大镜")
                    onNavigateToMagnifier()
                }
            )
            
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
                description = if (currentEmergencyContact.value != null) 
                    "已设置：${currentEmergencyContact.value?.name} (${currentEmergencyContact.value?.phone})" 
                    else "设置紧急情况下的联系人",
                onClick = {
                    textToSpeechService?.speak("紧急联系人设置")
                    showEmergencyContactDialog = true
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
                    textToSpeechService?.speak("确认退出登录")
                    showLogoutDialog = true
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

/**
 * 紧急联系人设置对话框
 */
@Composable
fun EmergencyContactDialog(
    currentContact: EmergencyContactsManager.EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (EmergencyContactsManager.EmergencyContact) -> Unit,
    textToSpeechService: TextToSpeechService?
) {
    var name by remember { mutableStateOf(currentContact?.name ?: "") }
    var phone by remember { mutableStateOf(currentContact?.phone ?: "") }
    var relationship by remember { mutableStateOf(currentContact?.relationship ?: "") }
    
    val isValid = name.isNotBlank() && phone.isNotBlank()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "设置紧急联系人",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // 联系人姓名
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("联系人姓名") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
                
                // 联系人电话
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("联系人电话") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                
                // 联系人关系
                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("与您的关系 (选填)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )
                
                // 提示文本
                Text(
                    text = "紧急情况下，按下主页上的紧急呼叫按钮将直接拨打此联系人电话",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val contact = EmergencyContactsManager.EmergencyContact(
                        name = name.trim(),
                        phone = phone.trim(),
                        relationship = relationship.trim()
                    )
                    onSave(contact)
                },
                enabled = isValid
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 