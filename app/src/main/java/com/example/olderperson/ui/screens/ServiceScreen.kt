package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.components.SectionTitle

/**
 * 服务页面
 */
@Composable
fun ServiceScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    var showMedicalConsultation by remember { mutableStateOf(false) }
    var showHomeService by remember { mutableStateOf(false) }
    var showEmergencyHelp by remember { mutableStateOf(false) }
    var showCommunityActivity by remember { mutableStateOf(false) }
    
    if (showMedicalConsultation) {
        MedicalConsultationScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showMedicalConsultation = false }
        )
    } else if (showHomeService) {
        HomeServiceScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showHomeService = false }
        )
    } else if (showEmergencyHelp) {
        EmergencyHelpScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showEmergencyHelp = false }
        )
    } else if (showCommunityActivity) {
        CommunityActivityScreen(
            textToSpeechService = textToSpeechService,
            onBackClick = { showCommunityActivity = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 顶部栏
            ServiceTopBar(onBackClick, textToSpeechService)
            
            // 内容区域
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 服务小贴士
                item {
                    ServiceTipsCard(
                        title = "服务小贴士",
                        content = "本页面提供多种老年人常用服务，包括医疗咨询、家政服务、紧急求助和社区活动等，点击相应卡片即可使用。",
                        textToSpeechService = textToSpeechService
                    )
                }
                
                // 常用服务
                item {
                    SectionTitle("常用服务", textToSpeechService)
                }
                
                // 服务列表
                items(serviceItems) { service ->
                    ServiceCard(
                        service = service,
                        textToSpeechService = textToSpeechService,
                        onClick = {
                            when (service.id) {
                                "medical" -> showMedicalConsultation = true
                                "home" -> showHomeService = true
                                "emergency" -> showEmergencyHelp = true
                                "community" -> showCommunityActivity = true
                            }
                        }
                    )
                }
                
                // 推荐服务
                item {
                    SectionTitle("推荐服务", textToSpeechService)
                }
                
                // 推荐服务列表
                items(recommendedServices) { service ->
                    RecommendedServiceCard(service, textToSpeechService)
                }
                
                // 服务预约
                item {
                    ServiceAppointmentSection(textToSpeechService)
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/**
 * 服务页面顶部栏
 */
@Composable
fun ServiceTopBar(
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
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "返回",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "服务",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("服务") }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "搜索",
            tint = Color.Black,
            modifier = Modifier
                .size(24.dp)
                .clickable { /* 搜索功能 */ }
        )
    }
}

/**
 * 服务小贴士卡片
 */
@Composable
fun ServiceTipsCard(
    title: String,
    content: String,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("$title：$content") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 服务卡片
 */
@Composable
fun ServiceCard(
    service: Service,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                textToSpeechService.speak(service.name + "。" + service.description)
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 服务图标
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = service.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 推荐服务卡片
 */
@Composable
fun RecommendedServiceCard(
    service: RecommendedService,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(service.name + "。" + service.description) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = service.provider,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = service.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { textToSpeechService.speak("预约" + service.name) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("立即预约")
                }
            }
        }
    }
}

/**
 * 服务预约区域
 */
@Composable
fun ServiceAppointmentSection(textToSpeechService: TextToSpeechService) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "预约上门服务",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "收起" else "展开",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "请选择您需要的上门服务类型：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 服务类型选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceTypeButton("家政保洁", textToSpeechService)
                    ServiceTypeButton("上门护理", textToSpeechService)
                    ServiceTypeButton("维修服务", textToSpeechService)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { textToSpeechService.speak("预约上门服务") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("提交预约")
                }
            }
        }
    }
}

/**
 * 服务类型按钮
 */
@Composable
fun ServiceTypeButton(text: String, textToSpeechService: TextToSpeechService) {
    var selected by remember { mutableStateOf(false) }
    
    OutlinedButton(
        onClick = {
            selected = !selected
            textToSpeechService.speak("已选择：$text")
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text)
    }
}

// 服务数据类
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

// 推荐服务数据类
data class RecommendedService(
    val name: String,
    val description: String,
    val provider: String,
    val icon: ImageVector
)

// 服务列表数据
val serviceItems = listOf(
    Service(
        id = "medical",
        name = "医疗咨询",
        description = "提供在线医疗咨询、预约挂号、健康指导等服务。",
        icon = Icons.Default.LocalHospital
    ),
    Service(
        id = "home",
        name = "家政服务",
        description = "提供上门保洁、维修、送餐等家政服务。",
        icon = Icons.Default.Home
    ),
    Service(
        id = "emergency",
        name = "紧急求助",
        description = "一键呼叫紧急救援，包括医疗急救、安全求助等。",
        icon = Icons.Default.Warning
    ),
    Service(
        id = "community",
        name = "社区活动",
        description = "查看和参与社区组织的各类老年人活动。",
        icon = Icons.Default.People
    )
)

// 推荐服务列表数据
val recommendedServices = listOf(
    RecommendedService(
        name = "老年人体检套餐",
        description = "专为65岁以上老年人定制的全面体检套餐，包含心脑血管、骨密度等检查项目。",
        provider = "仁爱医院",
        icon = Icons.Default.HealthAndSafety
    ),
    RecommendedService(
        name = "居家康复指导",
        description = "专业康复师上门指导，帮助老年人进行科学康复训练。",
        provider = "康复之家",
        icon = Icons.Default.FitnessCenter
    )
)

// 以下是子页面的占位实现，实际项目中需要完善

@Composable
fun MedicalConsultationScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("医疗咨询", onBackClick, textToSpeechService)
        
        // 内容区域 - 占位实现
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("医疗咨询页面 - 待实现")
        }
    }
}

@Composable
fun HomeServiceScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("家政服务", onBackClick, textToSpeechService)
        
        // 内容区域 - 占位实现
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("家政服务页面 - 待实现")
        }
    }
}

@Composable
fun EmergencyHelpScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("紧急求助", onBackClick, textToSpeechService)
        
        // 内容区域 - 占位实现
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("紧急求助页面 - 待实现")
        }
    }
}

@Composable
fun CommunityActivityScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("社区活动", onBackClick, textToSpeechService)
        
        // 内容区域 - 占位实现
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("社区活动页面 - 待实现")
        }
    }
}