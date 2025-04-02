package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * 家政服务页面
 */
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
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 家政服务小贴士
            item {
                HomeServiceTipsCard(
                    title = "家政服务小贴士",
                    content = "本页面提供多种家政服务，包括保洁、维修、送餐等，可以根据需要选择服务类型并预约上门服务时间。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 服务分类
            item {
                SectionTitle("服务分类", textToSpeechService)
            }
            
            // 服务分类列表
            items(homeServiceCategories) { category ->
                HomeServiceCategoryCard(category, textToSpeechService)
            }
            
            // 热门服务
            item {
                SectionTitle("热门服务", textToSpeechService)
            }
            
            // 热门服务列表
            items(popularHomeServices) { service ->
                PopularHomeServiceCard(service, textToSpeechService)
            }
            
            // 服务预约
            item {
                HomeServiceAppointmentSection(textToSpeechService)
            }
            
            // 服务评价
            item {
                SectionTitle("服务评价", textToSpeechService)
            }
            
            // 服务评价列表
            items(homeServiceReviews) { review ->
                HomeServiceReviewCard(review, textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 家政服务小贴士卡片
 */
@Composable
fun HomeServiceTipsCard(
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
 * 家政服务分类卡片
 */
@Composable
fun HomeServiceCategoryCard(
    category: HomeServiceCategory,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(category.name + "。" + category.description) },
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
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = category.description,
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
 * 热门家政服务卡片
 */
@Composable
fun PopularHomeServiceCard(
    service: PopularHomeService,
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
                    text = "¥${service.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
 * 家政服务预约区域
 */
@Composable
fun HomeServiceAppointmentSection(textToSpeechService: TextToSpeechService) {
    var expanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    
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
                    text = "快速预约服务",
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
                    text = "请选择服务类型：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 服务类型选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceTypeButton("家政保洁", textToSpeechService)
                    ServiceTypeButton("上门维修", textToSpeechService)
                    ServiceTypeButton("送餐服务", textToSpeechService)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 日期选择
                Text(
                    text = "请选择服务日期：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DateButton("今天", isSelected = selectedDate == "今天") {
                        selectedDate = "今天"
                        textToSpeechService.speak("已选择服务日期：今天")
                    }
                    DateButton("明天", isSelected = selectedDate == "明天") {
                        selectedDate = "明天"
                        textToSpeechService.speak("已选择服务日期：明天")
                    }
                    DateButton("后天", isSelected = selectedDate == "后天") {
                        selectedDate = "后天"
                        textToSpeechService.speak("已选择服务日期：后天")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时间选择
                Text(
                    text = "请选择服务时间：",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TimeButton("上午", isSelected = selectedTime == "上午") {
                        selectedTime = "上午"
                        textToSpeechService.speak("已选择服务时间：上午")
                    }
                    TimeButton("下午", isSelected = selectedTime == "下午") {
                        selectedTime = "下午"
                        textToSpeechService.speak("已选择服务时间：下午")
                    }
                    TimeButton("晚上", isSelected = selectedTime == "晚上") {
                        selectedTime = "晚上"
                        textToSpeechService.speak("已选择服务时间：晚上")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                            textToSpeechService.speak("已预约${selectedDate}${selectedTime}的家政服务")
                        } else {
                            textToSpeechService.speak("请先选择服务日期和时间")
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                ) {
                    Text("提交预约")
                }
            }
        }
    }
}

/**
 * 日期选择按钮
 */
@Composable
fun DateButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text)
    }
}

/**
 * 时间选择按钮
 */
@Composable
fun TimeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(text)
    }
}

/**
 * 家政服务评价卡片
 */
@Composable
fun HomeServiceReviewCard(
    review: HomeServiceReview,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("${review.userName}对${review.serviceName}的评价：${review.content}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.userName.first().toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = review.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 评分星星
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "评分",
                                tint = if (index < review.rating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = review.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review.serviceName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = review.content,
                fontSize = 14.sp
            )
        }
    }
}

// 家政服务分类数据类
data class HomeServiceCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector
)

// 热门家政服务数据类
data class PopularHomeService(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val icon: ImageVector
)

// 家政服务评价数据类
data class HomeServiceReview(
    val id: String,
    val userName: String,
    val serviceName: String,
    val content: String,
    val rating: Int,
    val date: String
)

// 家政服务分类列表数据
val homeServiceCategories = listOf(
    HomeServiceCategory(
        id = "cleaning",
        name = "家居保洁",
        description = "提供专业的家居清洁服务，包括日常保洁、深度清洁等。",
        icon = Icons.Default.CleaningServices
    ),
    HomeServiceCategory(
        id = "repair",
        name = "家电维修",
        description = "提供各类家电的维修服务，包括空调、冰箱、洗衣机等。",
        icon = Icons.Default.Build
    ),
    HomeServiceCategory(
        id = "meal",
        name = "送餐服务",
        description = "为老年人提供营养均衡的餐食配送服务，按需定制。",
        icon = Icons.Default.Restaurant
    ),
    HomeServiceCategory(
        id = "care",
        name = "生活照料",
        description = "提供日常生活照料服务，包括陪伴、代购等。",
        icon = Icons.Default.Favorite
    )
)

// 热门家政服务列表数据
val popularHomeServices = listOf(
    PopularHomeService(
        id = "cleaning1",
        name = "标准家居保洁（2小时）",
        description = "包含客厅、卧室、厨房、卫生间等区域的全面清洁，适合80-120平方米住宅。",
        price = "128",
        icon = Icons.Default.CleaningServices
    ),
    PopularHomeService(
        id = "repair1",
        name = "空调清洗保养",
        description = "专业空调清洗、消毒、检测服务，延长空调使用寿命，改善室内空气质量。",
        price = "168",
        icon = Icons.Default.AcUnit
    ),
    PopularHomeService(
        id = "meal1",
        name = "老年人营养餐（每周7天）",
        description = "专为老年人定制的营养均衡餐食，每日配送，含午餐和晚餐。",
        price = "798/周",
        icon = Icons.Default.Restaurant
    )
)

// 家政服务评价列表数据
val homeServiceReviews = listOf(
    HomeServiceReview(
        id = "review1",
        userName = "张阿姨",
        serviceName = "标准家居保洁",
        content = "阿姨打扫得很认真，特别是厨房和卫生间非常干净，下次还会预约。",
        rating = 5,
        date = "2023-10-15"
    ),
    HomeServiceReview(
        id = "review2",
        userName = "李大爷",
        serviceName = "空调清洗保养",
        content = "师傅很专业，不仅把空调清洗干净了，还教我如何日常保养，服务态度很好。",
        rating = 5,
        date = "2023-10-10"
    ),
    HomeServiceReview(
        id = "review3",
        userName = "王奶奶",
        serviceName = "老年人营养餐",
        content = "餐食很合我的口味，种类多样，每天按时送达，解决了我一个人做饭的困扰。",
        rating = 4,
        date = "2023-10-08"
    )
)