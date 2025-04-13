package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.Uri
import androidx.compose.ui.platform.LocalFocusManager
import com.example.olderperson.service.TextToSpeechService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onBackClick: () -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    var searchText by remember { mutableStateOf("") }
    var showContentDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }
    var showDevelopingDialog by remember { mutableStateOf(false) }
    
    // 添加百度搜索相关状态
    var showSearchResults by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "探索页面",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部标题
            item {
                Column {
                    Text(
                        text = "探索发现",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    
                    Text(
                        text = "为您推荐的内容与服务",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 搜索框
            item {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("搜索内容和服务...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                if (searchText.isNotEmpty()) {
                                    textToSpeechService?.speak("正在搜索${searchText}")
                                    searchQuery = searchText
                                    showSearchResults = true
                                    focusManager.clearFocus()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "执行搜索"
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchText.isNotEmpty()) {
                                textToSpeechService?.speak("正在搜索${searchText}")
                                searchQuery = searchText
                                showSearchResults = true
                                focusManager.clearFocus()
                            }
                        }
                    )
                )
            }
            
            // 显示百度搜索结果
            if (showSearchResults) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // 搜索结果页面标题栏
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "搜索结果: $searchQuery",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                
                                IconButton(onClick = { showSearchResults = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "关闭搜索结果"
                                    )
                                }
                            }
                            
                            Divider()
                            
                            // 百度搜索结果WebView
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { context ->
                                    WebView(context).apply {
                                        settings.javaScriptEnabled = true
                                        settings.loadWithOverviewMode = true
                                        settings.useWideViewPort = true
                                        settings.setSupportZoom(true)
                                        settings.textZoom = 120 // 增大文字以适应老年人
                                        webViewClient = WebViewClient()
                                        
                                        // 构建百度搜索URL
                                        val encodedQuery = Uri.encode(searchQuery)
                                        val baiduSearchUrl = "https://m.baidu.com/s?word=$encodedQuery&sa=tp_wise&tn=baidu"
                                        loadUrl(baiduSearchUrl)
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // 健康资讯
                item {
                    SectionHeader(
                        title = "健康资讯",
                        icon = Icons.Outlined.Favorite,
                        onMore = { showDevelopingDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // 春季养生饮食指南
                            InfoItem(
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Outlined.Restaurant,
                                title = "春季养生饮食指南",
                                description = "适合老年人的滋补食谱",
                                onClick = {
                                    textToSpeechService?.speak("春季养生饮食指南")
                                    dialogTitle = "春季养生饮食指南"
                                    dialogContent = "1. 多吃应季蔬菜：春季应选择新鲜的绿叶蔬菜，如菠菜、油菜和春笋等。\n\n" +
                                            "2. 适量进食温性食物：如葱、生姜、韭菜等，帮助调节体内阳气。\n\n" +
                                            "3. 控制咸味食物摄入：减少盐的摄入，有助于预防高血压。\n\n" +
                                            "4. 多喝温水：每天至少饮用1500毫升水，保持身体水分。\n\n" +
                                            "5. 少吃辛辣刺激食物：避免辛辣及油炸食物，防止燥热。"
                                    showContentDialog = true
                                }
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            // 适合春季的健身活动
                            InfoItem(
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Outlined.DirectionsRun,
                                title = "适合春季的健身活动",
                                description = "缓解关节疼痛的运动方法",
                                onClick = {
                                    textToSpeechService?.speak("适合春季的健身活动")
                                    dialogTitle = "适合春季的健身活动"
                                    dialogContent = "1. 太极拳：柔和的动作有助于增强平衡感和关节灵活性，非常适合老年人。\n\n" +
                                            "2. 散步：每天30分钟的散步可以改善心肺功能，最好在公园等空气清新的地方进行。\n\n" +
                                            "3. 伸展运动：每天进行简单的伸展，可以缓解肌肉僵硬和关节疼痛。\n\n" +
                                            "4. 水中运动：如果条件允许，水中行走或简单的水中体操对关节压力小。\n\n" +
                                            "5. 八段锦：传统养生功法，动作简单易学，有助于调节气血。"
                                    showContentDialog = true
                                }
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            // 老年人认知健康指南
                            InfoItem(
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Outlined.Psychology,
                                title = "老年人认知健康指南",
                                description = "保持大脑活力的方法与技巧",
                                onClick = {
                                    textToSpeechService?.speak("老年人认知健康指南")
                                    dialogTitle = "老年人认知健康指南"
                                    dialogContent = "1. 定期进行思维活动：如阅读、填字游戏、围棋等，都能激活大脑神经。\n\n" +
                                            "2. 保持社交活动：与亲友聊天、参加社区活动，减少孤独感。\n\n" +
                                            "3. 充足的睡眠：保证每晚7-8小时的优质睡眠，有助于大脑休息和记忆巩固。\n\n" +
                                            "4. 均衡饮食：多摄入富含抗氧化物的食物，如深色水果和蔬菜。\n\n" +
                                            "5. 学习新技能：尝试学习新的知识或技能，如绘画、书法或使用智能手机等。"
                                    showContentDialog = true
                                }
                            )

                            // 在健康资讯部分添加新的内容
                            InfoItem(
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Outlined.MonitorHeart,
                                title = "血压管理指南",
                                description = "日常血压监测与管理方法",
                                onClick = {
                                    textToSpeechService?.speak("血压管理指南")
                                    dialogTitle = "血压管理指南"
                                    dialogContent = "科学管理血压的要点：\n\n" +
                                            "1. 定时测量：每天固定时间测量血压，建议早晚各一次。\n\n" +
                                            "2. 合理用药：按医嘱服用降压药，不要随意更改剂量。\n\n" +
                                            "3. 饮食控制：\n" +
                                            "   - 控制盐分摄入，每日不超过6克\n" +
                                            "   - 多吃蔬菜水果\n" +
                                            "   - 限制高脂肪食物\n\n" +
                                            "4. 生活方式：\n" +
                                            "   - 规律作息\n" +
                                            "   - 适度运动\n" +
                                            "   - 保持心情愉悦\n\n" +
                                            "5. 注意事项：\n" +
                                            "   - 避免剧烈运动\n" +
                                            "   - 保持良好心态\n" +
                                            "   - 定期复查"
                                    showContentDialog = true
                                }
                            )

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            InfoItem(
                                iconBackgroundColor = Color(0xFFE8F5E9),
                                icon = Icons.Outlined.Medication,
                                title = "常见用药指南",
                                description = "老年人用药注意事项",
                                onClick = {
                                    textToSpeechService?.speak("常见用药指南")
                                    dialogTitle = "常见用药指南"
                                    dialogContent = "安全用药须知：\n\n" +
                                            "1. 用药原则：\n" +
                                            "   - 遵医嘱用药\n" +
                                            "   - 不随意加减剂量\n" +
                                            "   - 按时服用药物\n\n" +
                                            "2. 注意事项：\n" +
                                            "   - 检查药品有效期\n" +
                                            "   - 注意药品储存条件\n" +
                                            "   - 记录服药时间\n\n" +
                                            "3. 用药禁忌：\n" +
                                            "   - 避免自行配药\n" +
                                            "   - 不要同时服用性质相近的药物\n" +
                                            "   - 注意药物相互作用\n\n" +
                                            "4. 常见问题：\n" +
                                            "   - 如出现不适及时就医\n" +
                                            "   - 定期复查，调整用药\n\n" +
                                            "5. 用药记录：\n" +
                                            "   - 建议保存处方\n" +
                                            "   - 记录服药反应"
                                    showContentDialog = true
                                }
                            )
                        }
                    }
                }
            }
            
            // 本周专题
            item {
                SectionHeader(
                    title = "本周专题",
                    icon = Icons.Outlined.Star,
                    onMore = { showDevelopingDialog = true }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { 
                            textToSpeechService?.speak("春季健康生活指南")
                            dialogTitle = "春季健康生活指南"
                            dialogContent = "随着春天的到来，我们需要注意以下几个方面来保持健康：\n\n" +
                                    "1. 调整作息：逐渐调整作息时间，早睡早起，配合自然节律。\n\n" +
                                    "2. 适当运动：每天保持30分钟以上的适度运动，如散步、太极等。\n\n" +
                                    "3. 防花粉过敏：花粉过敏高发季节，外出戴口罩，回家及时洗手洗脸。\n\n" +
                                    "4. 心理调适：保持积极乐观的心态，多与家人朋友交流。\n\n" +
                                    "5. 合理穿衣：注意天气变化，及时增减衣物，防止感冒。"
                            showContentDialog = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF81C784)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = "春季健康生活指南",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "了解如何在春季保持健康与活力",
                                color = Color.White,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // 学习资源
            item {
                SectionHeader(
                    title = "学习资源",
                    icon = Icons.Outlined.School,
                    onMore = { showDevelopingDialog = true }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LearningResource(
                        icon = Icons.Outlined.PhoneAndroid,
                        title = "手机使用课程",
                        description = "10节入门课程",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            textToSpeechService?.speak("手机使用课程")
                            dialogTitle = "手机使用课程"
                            dialogContent = "课程目录：\n\n" +
                                    "1. 智能手机基础认识与操作\n" +
                                    "2. 如何管理联系人\n" +
                                    "3. 拨打电话与视频通话\n" +
                                    "4. 发送短信与使用微信\n" +
                                    "5. 拍照与相册管理\n" +
                                    "6. 使用地图导航\n" +
                                    "7. 安装与使用常用应用\n" +
                                    "8. 手机支付基础\n" +
                                    "9. 手机安全与防诈骗\n" +
                                    "10. 手机常见问题解决"
                            showContentDialog = true
                        }
                    )
                    
                    LearningResource(
                        icon = Icons.Outlined.Alarm,
                        title = "智能设备使用",
                        description = "8节实用教程",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            textToSpeechService?.speak("智能设备使用")
                            dialogTitle = "智能设备使用"
                            dialogContent = "课程目录：\n\n" +
                                    "1. 智能音箱的基础使用\n" +
                                    "2. 智能手表的健康监测功能\n" +
                                    "3. 智能电视操作指南\n" +
                                    "4. 智能家居控制入门\n" +
                                    "5. 智能门锁与安防设备\n" +
                                    "6. 智能厨房电器使用\n" +
                                    "7. 远程监控与看护系统\n" +
                                    "8. 智能设备互联与故障排除"
                            showContentDialog = true
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 添加更多学习资源
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LearningResource(
                        icon = Icons.Outlined.HealthAndSafety,
                        title = "健康监测课程",
                        description = "6节实用课程",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            textToSpeechService?.speak("健康监测课程")
                            dialogTitle = "健康监测课程"
                            dialogContent = "课程目录：\n\n" +
                                    "1. 血压测量方法与注意事项\n" +
                                    "2. 血糖监测技巧与记录\n" +
                                    "3. 心率监测与异常判断\n" +
                                    "4. 睡眠质量监测与改善\n" +
                                    "5. 运动量追踪与管理\n" +
                                    "6. 营养摄入记录与分析\n\n" +
                                    "每节课程包含：\n" +
                                    "- 详细的操作指导\n" +
                                    "- 实践练习\n" +
                                    "- 常见问题解答"
                            showContentDialog = true
                        }
                    )

                    LearningResource(
                        icon = Icons.Outlined.SelfImprovement,
                        title = "心理健康课程",
                        description = "5节关爱课程",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            textToSpeechService?.speak("心理健康课程")
                            dialogTitle = "心理健康课程"
                            dialogContent = "课程目录：\n\n" +
                                    "1. 情绪管理技巧：\n" +
                                    "   - 认识情绪\n" +
                                    "   - 调节方法\n\n" +
                                    "2. 压力缓解方法：\n" +
                                    "   - 呼吸练习\n" +
                                    "   - 放松技巧\n\n" +
                                    "3. 社交活动指导：\n" +
                                    "   - 沟通技巧\n" +
                                    "   - 人际关系维护\n\n" +
                                    "4. 兴趣培养：\n" +
                                    "   - 发现爱好\n" +
                                    "   - 坚持方法\n\n" +
                                    "5. 心理调适：\n" +
                                    "   - 积极思维\n" +
                                    "   - 幸福感提升"
                            showContentDialog = true
                        }
                    )
                }
            }
            
            // 底部空间
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // 显示开发中对话框
        if (showDevelopingDialog) {
            AlertDialog(
                onDismissRequest = { showDevelopingDialog = false },
                title = { Text("功能开发中") },
                text = { Text("此功能正在开发中，敬请期待！") },
                confirmButton = {
                    Button(onClick = { showDevelopingDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }
        
        // 显示内容详情对话框
        if (showContentDialog) {
            Dialog(onDismissRequest = { showContentDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = dialogTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = dialogContent,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showContentDialog = false }
                            ) {
                                Text("关闭")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = content,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            textToSpeechService?.speak(content)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "朗读内容"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("朗读")
                    }
                    
                    Button(
                        onClick = onDismiss
                    ) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

@Composable
fun DevelopingFeatureDialog(
    onDismiss: () -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    val message = "此功能正在开发中，敬请期待！"
    
    LaunchedEffect(Unit) {
        textToSpeechService?.speak(message)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF2E7D32)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss
                ) {
                    Text("我知道了")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onMore: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        TextButton(
            onClick = onMore
        ) {
            Text(
                text = "更多",
                color = Color(0xFF2E7D32)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "查看更多",
                tint = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun InfoItem(
    iconBackgroundColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LearningResource(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
} 