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

/**
 * 常见问题数据类
 */
data class FaqItem(
    val question: String,
    val answer: String
)

/**
 * 使用指南数据类
 */
data class GuideItem(
    val title: String,
    val content: String
)

/**
 * 帮助页面
 */
@Composable
fun HelpScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        HelpTopBar(onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 帮助页面介绍
            item {
                HelpIntroCard(textToSpeechService)
            }
            
            // 常见问题解答
            item {
                Text(
                    text = "常见问题",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        .clickable { textToSpeechService.speak("常见问题") }
                )
            }
            
            // 常见问题列表
            items(faqItems) { faq ->
                FaqItem(faq, textToSpeechService)
            }
            
            // 使用指南
            item {
                Text(
                    text = "使用指南",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        .clickable { textToSpeechService.speak("使用指南") }
                )
            }
            
            // 使用指南列表
            items(guideItems) { guide ->
                GuideItem(guide, textToSpeechService)
            }
            
            // 联系客服
            item {
                ContactSupportCard(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 帮助页面顶部栏
 */
@Composable
fun HelpTopBar(
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
            text = "帮助中心",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("帮助中心") }
        )
    }
}

/**
 * 帮助页面介绍卡片
 */
@Composable
fun HelpIntroCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("欢迎来到帮助中心，这里提供了应用使用的常见问题解答和使用指南，如果您有其他问题，可以联系我们的客服。") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "欢迎来到帮助中心",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "这里提供了应用使用的常见问题解答和使用指南，如果您有其他问题，可以联系我们的客服。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * 常见问题项
 */
@Composable
fun FaqItem(
    faq: FaqItem,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) {
                            textToSpeechService.speak(faq.question + "。" + faq.answer)
                        } else {
                            textToSpeechService.speak(faq.question)
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "收起" else "展开",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationState)
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * 使用指南项
 */
@Composable
fun GuideItem(
    guide: GuideItem,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                        if (expanded) {
                            textToSpeechService.speak(guide.title + "。" + guide.content)
                        } else {
                            textToSpeechService.speak(guide.title)
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "收起" else "展开",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationState)
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = guide.content,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * 联系客服卡片
 */
@Composable
fun ContactSupportCard(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "联系客服",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.clickable { textToSpeechService.speak("联系客服") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { textToSpeechService.speak("客服电话：400-123-4567") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "电话",
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "客服电话",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "400-123-4567",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { textToSpeechService.speak("在线客服：工作时间 9:00-18:00") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "在线客服",
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "在线客服",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "工作时间 9:00-18:00",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { textToSpeechService.speak("提交反馈") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                )
            ) {
                Text(
                    text = "提交反馈",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// 模拟数据 - 常见问题
val faqItems = listOf(
    FaqItem(
        question = "如何修改个人信息？",
        answer = "您可以在\"我的\"页面点击头像或用户名，进入个人信息页面进行修改。"
    ),
    FaqItem(
        question = "如何开启语音播报功能？",
        answer = "语音播报功能默认已开启，您可以在\"设置\"中的\"辅助功能\"选项里调整语音播报的音量和语速。"
    ),
    FaqItem(
        question = "如何收藏喜欢的内容？",
        answer = "在浏览文章、视频或养生知识时，点击右上角的心形图标即可收藏内容，收藏的内容可以在\"我的喜欢\"页面查看。"
    ),
    FaqItem(
        question = "忘记密码怎么办？",
        answer = "您可以在登录页面点击\"忘记密码\"，通过绑定的手机号码接收验证码后重新设置密码。"
    ),
    FaqItem(
        question = "如何查看健康数据？",
        answer = "在首页点击\"健康数据\"模块，可以查看您的步数、睡眠、血压等健康数据记录。"
    )
)

// 模拟数据 - 使用指南
val guideItems = listOf(
    GuideItem(
        title = "首页功能介绍",
        content = "首页包含健康数据、每日推荐、养生资讯等模块，您可以通过点击相应模块进入详细页面。页面顶部显示天气信息和问候语，底部是导航栏，可以切换不同的功能页面。"
    ),
    GuideItem(
        title = "养生天地使用指南",
        content = "养生天地页面提供饮食养生、运动保健、中医养生和季节养生四个分类，您可以点击感兴趣的分类查看相关内容。每个分类下有文章、视频和小贴士等形式的内容供您浏览。"
    ),
    GuideItem(
        title = "社区互动功能",
        content = "在社区页面，您可以浏览其他用户分享的养生经验和健康生活方式。点击帖子可以查看详情，点击评论图标可以发表评论，点击点赞图标可以为帖子点赞。"
    ),
    GuideItem(
        title = "设备连接方法",
        content = "在\"我的设备\"页面，点击添加设备按钮，按照提示连接您的健康监测设备。连接成功后，设备数据将自动同步到应用中，您可以在健康数据页面查看详细信息。"
    )
)