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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.components.SectionTitle

/**
 * 中医养生页面
 */
@Composable
fun TCMWellnessScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        TopBar("中医养生", onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 中医养生小贴士
            item {
                TipsCard(
                    title = "中医养生小贴士",
                    content = "中医养生讲究顺应自然，调和阴阳，注重精神调养与身体保健相结合。",
                    textToSpeechService = textToSpeechService
                )
            }
            
            // 中医理论
            item {
                SectionTitle("中医基础理论", textToSpeechService)
            }
            
            // 中医理论文章
            items(tcmTheories) { theory ->
                TCMTheoryCard(theory, textToSpeechService)
            }
            
            // 穴位按摩
            item {
                SectionTitle("穴位按摩指导", textToSpeechService)
            }
            
            // 穴位按摩列表
            items(acupointMassages) { acupoint ->
                AcupointCard(acupoint, textToSpeechService)
            }
            
            // 中药养生
            item {
                SectionTitle("中药养生", textToSpeechService)
            }
            
            // 中药养生知识
            item {
                HerbalMedicineSection(textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 中医理论卡片
 */
@Composable
fun TCMTheoryCard(
    theory: TCMTheory,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(theory.title + "。" + theory.content) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = theory.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = theory.content,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * 穴位按摩卡片
 */
@Composable
fun AcupointCard(
    acupoint: Acupoint,
    textToSpeechService: TextToSpeechService
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp)
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
                    // 穴位图标
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = acupoint.name.first().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = acupoint.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(onClick = { 
                    expanded = !expanded 
                    if (expanded) {
                        textToSpeechService.speak(acupoint.name + "。" + acupoint.location + "。" + acupoint.benefit)
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
                    text = "位置：${acupoint.location}",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "功效：${acupoint.benefit}",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "按摩方法：${acupoint.method}",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { textToSpeechService.speak("按摩方法：${acupoint.method}") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("语音指导")
                }
            }
        }
    }
}

/**
 * 中药养生区域
 */
@Composable
fun HerbalMedicineSection(textToSpeechService: TextToSpeechService) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "常见养生中药材",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { textToSpeechService.speak("常见养生中药材") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 中药材列表
            herbalMedicines.forEach { herb ->
                HerbalMedicineItem(herb, textToSpeechService)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "注意：以上中药材仅供参考，具体使用请咨询专业中医师。",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { 
                    textToSpeechService.speak("注意：以上中药材仅供参考，具体使用请咨询专业中医师。") 
                }
            )
        }
    }
}

/**
 * 中药材项目
 */
@Composable
fun HerbalMedicineItem(
    herb: HerbalMedicine,
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(herb.name + "。" + herb.effect) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = herb.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        
        Text(
            text = herb.effect,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// 中医理论数据类
data class TCMTheory(
    val title: String,
    val content: String
)

// 穴位数据类
data class Acupoint(
    val name: String,
    val location: String,
    val benefit: String,
    val method: String
)

// 中药材数据类
data class HerbalMedicine(
    val name: String,
    val effect: String
)

// 中医理论数据
val tcmTheories = listOf(
    TCMTheory(
        title = "阴阳平衡",
        content = "中医认为人体内阴阳平衡是健康的基础，老年人应注重调和阴阳，保持身体各系统功能的协调。"
    ),
    TCMTheory(
        title = "五脏养生",
        content = "中医五脏指心、肝、脾、肺、肾，老年人应根据自身情况，有针对性地进行五脏养护。"
    )
)

// 穴位按摩数据
val acupointMassages = listOf(
    Acupoint(
        name = "足三里",
        location = "外膝盖下方四横指，胫骨外侧一横指处",
        benefit = "强健脾胃，增强免疫力，是老年人保健的要穴",
        method = "用拇指按压穴位，顺时针按摩30秒，每天早晚各一次"
    ),
    Acupoint(
        name = "百会",
        location = "头顶正中央，两耳尖连线中点",
        benefit = "安神醒脑，改善睡眠，缓解头痛",
        method = "用食指指腹轻轻按揉，每次1-2分钟，每天2-3次"
    ),
    Acupoint(
        name = "涌泉",
        location = "脚底心前三分之一处的凹陷中",
        benefit = "滋养肾阴，安神定志，改善失眠",
        method = "晚上睡前用拇指按揉脚底涌泉穴，每只脚1-2分钟"
    )
)

// 中药养生数据
val herbalMedicines = listOf(
    HerbalMedicine(
        name = "枸杞",
        effect = "滋补肝肾，明目，适合肝肾阴虚、视力减退的老年人"
    ),
    HerbalMedicine(
        name = "红枣",
        effect = "补中益气，养血安神，适合气血不足、失眠的老年人"
    ),
    HerbalMedicine(
        name = "山药",
        effect = "健脾益肺，固肾益精，适合脾胃虚弱、食欲不振的老年人"
    ),
    HerbalMedicine(
        name = "菊花",
        effect = "清热解毒，平肝明目，适合肝火旺盛、眼睛干涩的老年人"
    )
)