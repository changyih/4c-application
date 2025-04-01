package com.example.olderperson.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 设备数据类
 */
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val status: DeviceStatus,
    val batteryLevel: Int,
    val location: String,
    val lastActiveTime: String
)

/**
 * 设备类型枚举
 */
enum class DeviceType(val label: String, val icon: @Composable () -> Unit) {
    HEALTH_MONITOR("健康监测设备", { Icon(Icons.Default.Favorite, contentDescription = null) }),
    SMART_SPEAKER("智能音箱", { Icon(Icons.Default.Speaker, contentDescription = null) }),
    SMART_WATCH("智能手表", { Icon(Icons.Default.Watch, contentDescription = null) }),
    EMERGENCY_BUTTON("紧急呼叫器", { Icon(Icons.Default.SupportAgent, contentDescription = null) }),
    SMART_PILL_BOX("智能药盒", { Icon(Icons.Default.Medication, contentDescription = null) })
}

/**
 * 设备状态枚举
 */
enum class DeviceStatus(val label: String, val color: Color) {
    ONLINE("在线", Color(0xFF4CAF50)),
    OFFLINE("离线", Color(0xFF9E9E9E)),
    LOW_BATTERY("电量低", Color(0xFFFFA000)),
    ERROR("异常", Color(0xFFF44336))
}

/**
 * 我的设备页面
 */
@Composable
fun MyDevicesScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    // 当前选中的设备详情
    var selectedDevice by remember { mutableStateOf<Device?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        MyDevicesTopBar(onBackClick, textToSpeechService)
        
        // 如果有选中的设备，显示设备详情页面
        if (selectedDevice != null) {
            DeviceDetailScreen(
                device = selectedDevice!!,
                textToSpeechService = textToSpeechService,
                onBackClick = { selectedDevice = null }
            )
        } else {
            // 设备列表页面
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 设备列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 设备管理提示卡片
                    item {
                        DeviceManagementTipsCard(textToSpeechService)
                    }
                    
                    // 设备列表标题
                    item {
                        Text(
                            text = "已绑定设备 (${devices.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                                .clickable { textToSpeechService.speak("已绑定设备 ${devices.size}台") }
                        )
                    }
                    
                    // 设备列表
                    if (devices.isEmpty()) {
                        item {
                            EmptyDeviceView(textToSpeechService)
                        }
                    } else {
                        items(devices) { device ->
                            DeviceItem(
                                device = device,
                                textToSpeechService = textToSpeechService,
                                onClick = { selectedDevice = device }
                            )
                        }
                    }
                    
                    // 添加设备按钮
                    item {
                        AddDeviceButton(textToSpeechService)
                    }
                    
                    // 底部间距
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

/**
 * 我的设备页面顶部栏
 */
@Composable
fun MyDevicesTopBar(
    onBackClick: () -> Unit = {},
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "我的设备",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("我的设备") }
        )
    }
}

/**
 * 设备管理提示卡片
 */
@Composable
fun DeviceManagementTipsCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("设备管理提示：您可以在此页面查看和管理已绑定的智能设备，点击设备卡片可查看详情和进行控制。") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "设备管理提示",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "您可以在此页面查看和管理已绑定的智能设备，点击设备卡片可查看详情和进行控制。",
                fontSize = 14.sp,
                color = Color(0xFF1976D2).copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 设备项组件
 */
@Composable
fun DeviceItem(
    device: Device,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                onClick()
                textToSpeechService.speak("${device.name}，${device.type.label}，状态${device.status.label}，电量${device.batteryLevel}%")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 设备头部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        device.type.icon()
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = device.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Text(
                            text = device.type.label,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // 设备状态
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 电池状态
                    BatteryIndicator(device.batteryLevel)
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 在线状态
                    Text(
                        text = device.status.label,
                        fontSize = 14.sp,
                        color = device.status.color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 设备详细信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "位置：${device.location}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "最近活动：${device.lastActiveTime}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 设备底部操作区
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { textToSpeechService.speak("查看详情") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text(
                        text = "查看详情",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * 电池指示器
 */
@Composable
fun BatteryIndicator(batteryLevel: Int) {
    val batteryColor = when {
        batteryLevel <= 20 -> Color(0xFFF44336) // 红色
        batteryLevel <= 50 -> Color(0xFFFFA000) // 橙色
        else -> Color(0xFF4CAF50) // 绿色
    }
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = when {
                batteryLevel <= 20 -> Icons.Default.BatteryAlert
                batteryLevel <= 50 -> Icons.Default.Battery3Bar
                batteryLevel <= 80 -> Icons.Default.Battery6Bar
                else -> Icons.Default.BatteryFull
            },
            contentDescription = null,
            tint = batteryColor,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = "${batteryLevel}%",
            fontSize = 12.sp,
            color = batteryColor
        )
    }
}

/**
 * 空设备视图
 */
@Composable
fun EmptyDeviceView(textToSpeechService: TextToSpeechService) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .clickable { textToSpeechService.speak("暂无绑定设备，点击下方按钮添加设备") },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Devices,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无绑定设备",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "点击下方按钮添加设备",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 添加设备按钮
 */
@Composable
fun AddDeviceButton(textToSpeechService: TextToSpeechService) {
    Button(
        onClick = { textToSpeechService.speak("添加设备") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "添加设备",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

/**
 * 设备详情页面
 */
@Composable
fun DeviceDetailScreen(
    device: Device,
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "设备详情",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { textToSpeechService.speak("设备详情") }
            )
        }
        
        // 设备状态卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            device.type.icon()
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = device.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Text(
                                text = device.type.label,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    
                    Text(
                        text = device.status.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = device.status.color
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 电池状态条
                BatteryStatusBar(device.batteryLevel, textToSpeechService)
            }
        }
        
        // 设备信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "设备信息",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                        .clickable { textToSpeechService.speak("设备信息") }
                )
                
                Divider()
                
                DeviceInfoItem("设备ID", device.id)
                DeviceInfoItem("设备类型", device.type.label)
                DeviceInfoItem("设备位置", device.location)
                DeviceInfoItem("最近活动", device.lastActiveTime)
            }
        }
        
        // 设备控制卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "设备控制",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                        .clickable { textToSpeechService.speak("设备控制") }
                )
                
                Divider()
                
                // 设备控制按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ControlButton(
                        icon = Icons.Default.PowerSettingsNew,
                        label = "开关机",
                        textToSpeechService = textToSpeechService
                    )
                    
                    ControlButton(
                        icon = Icons.Default.Refresh,
                        label = "刷新状态",
                        textToSpeechService = textToSpeechService
                    )
                    
                    ControlButton(
                        icon = Icons.Default.Settings,
                        label = "设置",
                        textToSpeechService = textToSpeechService
                    )
                }
            }
        }
        
        // 底部操作按钮
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { textToSpeechService.speak("解绑设备") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    text = "解绑设备",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Button(
                onClick = { textToSpeechService.speak("设备检测") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text(
                    text = "设备检测",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * 电池状态条
 */
@Composable
fun BatteryStatusBar(
    batteryLevel: Int,
    textToSpeechService: TextToSpeechService
) {
    val batteryColor = when {
        batteryLevel <= 20 -> Color(0xFFF44336) // 红色
        batteryLevel <= 50 -> Color(0xFFFFA000) // 橙色
        else -> Color(0xFF4CAF50) // 绿色
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("电池电量${batteryLevel}%") }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "电池电量",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Text(
                text = "${batteryLevel}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = batteryColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 进度条
        LinearProgressIndicator(
            progress = { batteryLevel / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = batteryColor,
            trackColor = Color.LightGray
        )
    }
}

/**
 * 设备信息项
 */
@Composable
fun DeviceInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

/**
 * 控制按钮
 */
@Composable
fun ControlButton(
    icon: ImageVector,
    label: String,
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { textToSpeechService.speak(label) }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

// 模拟设备数据
val devices = listOf(
    Device(
        id = "DV001",
        name = "健康手环",
        type = DeviceType.SMART_WATCH,
        status = DeviceStatus.ONLINE,
        batteryLevel = 85,
        location = "卧室",
        lastActiveTime = "今天 08:30"
    ),
    Device(
        id = "DV002",
        name = "智能音箱",
        type = DeviceType.SMART_SPEAKER,
        status = DeviceStatus.ONLINE,
        batteryLevel = 100,
        location = "客厅",
        lastActiveTime = "今天 09:15"
    ),
    Device(
        id = "DV003",
        name = "紧急呼叫器",
        type = DeviceType.EMERGENCY_BUTTON,
        status = DeviceStatus.LOW_BATTERY,
        batteryLevel = 15,
        location = "卧室",
        lastActiveTime = "昨天 20:45"
    )
)