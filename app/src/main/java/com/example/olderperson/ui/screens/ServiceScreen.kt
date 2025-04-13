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
        icon = Icons.Default.Groups
    )
)

// 推荐服务列表数据
val recommendedServices = listOf(
    RecommendedService(
        name = "老年人体检套餐",
        description = "专为65岁以上老年人定制的全面体检套餐，包含心脑血管、骨密度等检查项目。",
        provider = "吉林大学白求恩第一医院",
        icon = Icons.Default.HealthAndSafety
    ),
    RecommendedService(
        name = "居家康复指导",
        description = "专业康复师上门指导，帮助老年人进行科学康复训练。",
        provider = "康复之家",
        icon = Icons.Default.FitnessCenter
    )
)

// 医生数据类
data class Doctor(
    val name: String,
    val title: String,
    val hospital: String,
    val specialty: String,
    val rating: Float = 5.0f
)

// 医院数据类
data class Hospital(
    val name: String,
    val address: String,
    val description: String,
    val departments: List<String> = emptyList()
)

// 疾病自查数据类
data class DiseaseCheck(
    val name: String,
    val description: String,
    val symptoms: String,
    val icon: ImageVector,
    val color: Color
)

// 医生列表数据
val doctors = listOf(
    Doctor(
        name = "张医生",
        title = "主任医师",
        hospital = "吉林大学白求恩第一医院",
        specialty = "心血管疾病"
    ),
    Doctor(
        name = "李医生",
        title = "副主任医师",
        hospital = "长春市中心医院",
        specialty = "骨科疾病"
    ),
    Doctor(
        name = "王医生",
        title = "主任医师",
        hospital = "长春市中心医院",
        specialty = "神经内科"
    )
)

// 医院列表数据
val hospitals = listOf(
    Hospital(
        name = "长春市中心医院",
        address = "长春市宽城区南京大街728号",
        description = "综合性三甲医院，设有内科、外科、妇科、儿科等多个科室，老年人就诊绿色通道。"
    ),
    Hospital(
        name = "吉林大学白求恩第一医院",
        address = "长春市朝阳区新民大街1号",
        description = "大型综合医院，老年医学科特色突出，提供老年人专属服务窗口。"
    )
)

// 疾病自查列表数据
val diseaseChecks = listOf(
    DiseaseCheck(
        name = "高血压自查",
        description = "高血压是常见的慢性疾病，老年人是高发人群，定期监测血压很重要。",
        symptoms = "头晕、头痛、耳鸣、颈部不适、疲劳乏力等。",
        icon = Icons.Default.Favorite,
        color = Color.Red
    ),
    DiseaseCheck(
        name = "糖尿病自查",
        description = "糖尿病是一种代谢紊乱疾病，老年人应定期检测血糖。",
        symptoms = "多饮、多食、多尿、体重减轻、疲乏无力等。",
        icon = Icons.Default.WaterDrop,
        color = Color.Blue
    ),
    DiseaseCheck(
        name = "骨质疏松自查",
        description = "骨质疏松是老年人常见疾病，可能导致骨折风险增加。",
        symptoms = "身高减少、驼背、骨痛、活动受限等。",
        icon = Icons.Default.FitnessCenter,
        color = Color(0xFF8D6E63)
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
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 医疗咨询小贴士
            item {
                MedicalTipsCard(
                    title = "医疗咨询小贴士",
                    content = "在线问诊可以帮助您快速获取医生建议，预约挂号可以节省您的排队时间。如有紧急情况，请立即拨打急救电话。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 在线问诊
            item {
                SectionTitle("在线问诊", textToSpeechService)
            }
            
            // 医生列表
            items(doctors) { doctor ->
                DoctorCard(doctor, textToSpeechService)
            }
            
            // 预约挂号
            item {
                SectionTitle("预约挂号", textToSpeechService)
            }
            
            // 医院列表
            items(hospitals) { hospital ->
                HospitalCard(hospital, textToSpeechService)
            }
            
            // 健康档案
            item {
                HealthRecordCard(textToSpeechService)
            }
            
            // 常见疾病自查
            item {
                SectionTitle("常见疾病自查", textToSpeechService)
            }
            
            // 疾病自查列表
            items(diseaseChecks) { diseaseCheck ->
                DiseaseCheckCard(diseaseCheck, textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 医疗咨询小贴士卡片
 */
@Composable
fun MedicalTipsCard(
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
 * 医生卡片
 */
@Composable
fun DoctorCard(
    doctor: Doctor,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("${doctor.name}，${doctor.title}，${doctor.hospital}，专长：${doctor.specialty}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 医生头像
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = doctor.name.first().toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = doctor.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = doctor.title,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = doctor.hospital,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "专长：${doctor.specialty}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Button(
                onClick = { 
                    textToSpeechService.speak("开始与${doctor.name}医生在线问诊")
                    // 实际应用中这里应该跳转到在线问诊界面
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("问诊")
            }
        }
    }
}

/**
 * 医院卡片
 */
@Composable
fun HospitalCard(
    hospital: Hospital,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("${hospital.name}，${hospital.address}，${hospital.description}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 医院图标
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = hospital.name,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = hospital.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = hospital.address,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = hospital.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { 
                        textToSpeechService.speak("预约${hospital.name}挂号")
                        // 实际应用中这里应该跳转到预约挂号界面
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("预约挂号")
                }
            }
        }
    }
}

/**
 * 健康档案卡片
 */
@Composable
fun HealthRecordCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("健康档案，查看您的健康记录和体检报告") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = "健康档案",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "健康档案",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthRecordItem("体检报告", Icons.Default.Description, textToSpeechService)
                HealthRecordItem("用药记录", Icons.Default.Medication, textToSpeechService)
                HealthRecordItem("就诊记录", Icons.Default.History, textToSpeechService)
            }
        }
    }
}

/**
 * 健康档案项目
 */
@Composable
fun HealthRecordItem(
    title: String,
    icon: ImageVector,
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { textToSpeechService.speak(title) }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

/**
 * 疾病自查卡片
 */
@Composable
fun DiseaseCheckCard(
    diseaseCheck: DiseaseCheck,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 疾病图标
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(diseaseCheck.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = diseaseCheck.icon,
                            contentDescription = diseaseCheck.name,
                            tint = diseaseCheck.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = diseaseCheck.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = { 
                    expanded = !expanded 
                    if (expanded) {
                        textToSpeechService.speak(diseaseCheck.name + "。" + diseaseCheck.description)
                    }
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起" else "展开"
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = diseaseCheck.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "常见症状：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = diseaseCheck.symptoms,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { textToSpeechService.speak("开始${diseaseCheck.name}自查") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("开始自查")
                }
            }
        }
    }
}

// HomeServiceScreen已移至单独文件实现
// 参见HomeServiceScreen.kt

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
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 紧急求助提示
            item {
                EmergencyTipsCard(
                    title = "紧急求助小贴士",
                    content = "本页面提供紧急情况下的快速求助功能，包括一键呼叫紧急联系人、发送位置信息和请求紧急服务等。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 一键呼叫按钮
            item {
                EmergencyCallButton(textToSpeechService)
            }
            
            // 紧急联系人列表
            item {
                SectionTitle("紧急联系人", textToSpeechService)
            }
            
            // 联系人列表
            items(emergencyContacts) { contact ->
                EmergencyContactCard(contact, textToSpeechService)
            }
            
            // 紧急服务
            item {
                SectionTitle("紧急服务", textToSpeechService)
            }
            
            // 紧急服务列表
            items(emergencyServices) { service ->
                EmergencyServiceCard(service, textToSpeechService)
            }
            
            // 位置共享
            item {
                LocationSharingCard(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 紧急提示卡片
 */
@Composable
fun EmergencyTipsCard(
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
            containerColor = Color(0xFFFFF3F3)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "提示",
                    tint = Color.Red
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 一键呼叫紧急按钮
 */
@Composable
fun EmergencyCallButton(textToSpeechService: TextToSpeechService) {
    Button(
        onClick = { 
            textToSpeechService.speak("正在拨打紧急电话120")
            // 实际应用中这里应该调用拨打电话的功能
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "紧急呼叫",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "一键紧急呼叫 (120)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * 紧急联系人卡片
 */
@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("${contact.name}，${contact.relationship}，电话号码：${contact.phone}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 联系人头像
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.first().toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = contact.relationship,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = contact.phone,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            IconButton(
                onClick = { 
                    textToSpeechService.speak("正在拨打${contact.name}的电话")
                    // 实际应用中这里应该调用拨打电话的功能
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "拨打电话",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 紧急服务卡片
 */
@Composable
fun EmergencyServiceCard(
    service: EmergencyService,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                textToSpeechService.speak(service.name + "。" + service.description)
                // 实际应用中这里应该调用相应的服务
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
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(service.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.name,
                    tint = service.color,
                    modifier = Modifier.size(24.dp)
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
            
            Button(
                onClick = { 
                    textToSpeechService.speak("正在请求${service.name}服务")
                    // 实际应用中这里应该调用相应的服务
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = service.color
                )
            ) {
                Text("请求")
            }
        }
    }
}

/**
 * 位置共享卡片
 */
@Composable
fun LocationSharingCard(textToSpeechService: TextToSpeechService) {
    var isSharing by remember { mutableStateOf(false) }
    
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "位置",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "位置共享",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "在紧急情况下，您可以快速共享您的位置信息给紧急联系人或救援服务。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSharing) "位置共享中..." else "未共享位置",
                    fontSize = 14.sp,
                    color = if (isSharing) Color.Green else Color.Gray
                )
                
                Switch(
                    checked = isSharing,
                    onCheckedChange = { 
                        isSharing = it 
                        if (isSharing) {
                            textToSpeechService.speak("已开启位置共享，您的紧急联系人将能够看到您的位置")
                        } else {
                            textToSpeechService.speak("已关闭位置共享")
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
            
            if (isSharing) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        textToSpeechService.speak("已向所有紧急联系人发送您的位置信息")
                        // 实际应用中这里应该调用发送位置的功能
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("发送位置给联系人")
                }
            }
        }
    }
}

// 紧急联系人数据类
data class EmergencyContact(
    val name: String,
    val relationship: String,
    val phone: String
)

// 紧急服务数据类
data class EmergencyService(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

// 紧急联系人列表数据
val emergencyContacts = listOf(
    EmergencyContact(
        name = "张医生",
        relationship = "家庭医生",
        phone = "138-1234-5678"
    ),
    EmergencyContact(
        name = "李明远",
        relationship = "子女",
        phone = "139-8765-4321"
    ),
    EmergencyContact(
        name = "王护士",
        relationship = "社区护士",
        phone = "137-2468-1357"
    )
)

// 紧急服务列表数据
val emergencyServices = listOf(
    EmergencyService(
        name = "医疗急救",
        description = "呼叫120医疗急救服务",
        icon = Icons.Default.LocalHospital,
        color = Color.Red
    ),
    EmergencyService(
        name = "火警",
        description = "呼叫119消防救援",
        icon = Icons.Default.Fireplace,
        color = Color(0xFFFF5722)
    ),
    EmergencyService(
        name = "警察",
        description = "呼叫110报警服务",
        icon = Icons.Default.Security,
        color = Color.Blue
    )
)

// CommunityActivityScreen已在CommunityActivityScreen.kt中实现