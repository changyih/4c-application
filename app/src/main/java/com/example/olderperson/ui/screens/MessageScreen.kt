package com.example.olderperson.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageScreen(onBackToHome: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部栏
        TopAppBar(onBackToHome)
        
        // 联系人快捷方式
        ContactShortcuts()
        
        // 消息标签切换
        MessageTabs()
        
        // 消息列表
        MessageList()
    }
}

@Composable
fun TopAppBar(onBackToHome: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackToHome() }
                    .padding(end = 16.dp)
            )
            
            Text(
                text = "消息",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White
        )
    }
}

@Composable
fun ContactShortcuts() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // 添加快捷方式
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "添加",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // 联系人列表
        items(contactList) { contact ->
            ContactItem(contact)
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(contact.bgColor),
            contentAlignment = Alignment.Center
        ) {
            // 这里用图标代替头像
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contact.name,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = contact.name,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MessageTabs() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF333333))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selectedTab == 0) Color.White else Color.Transparent)
                .clickable { selectedTab = 0 }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "消息",
                    color = if (selectedTab == 0) Color.Black else Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                if (selectedTab == 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selectedTab == 1) Color.White else Color.Transparent)
                .clickable { selectedTab = 1 }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "群聊",
                color = if (selectedTab == 1) Color.Black else Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MessageList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(messageList) { message ->
            MessageItem(message)
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 处理点击事件 */ },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 联系人头像和图标
        Box {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(message.contact.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = message.contact.name,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            // 若有应用图标，显示在右下角
            message.appIcon?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(message.appIconBgColor ?: Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "App Icon",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        // 消息内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.contact.name,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Text(
                    text = message.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = message.content,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 数据类
data class Contact(
    val name: String,
    val bgColor: Color
)

data class Message(
    val contact: Contact,
    val content: String,
    val time: String,
    val appIcon: Any? = null,
    val appIconBgColor: Color? = null
)

// 模拟数据
val contactList = listOf(
    Contact("老奶奶", Color(0xFFFF9800)),
    Contact("马爱国", Color(0xFF4CAF50)),
    Contact("马大爷", Color(0xFF2196F3)),
    Contact("飞猪旅行", Color(0xFFE91E63))
)

val messageList = listOf(
    Message(
        contact = Contact("薄荷健康", Color(0xFF4CAF50)),
        content = "[1+8] 老人人体监测设置",
        time = "下午 11:00",
        appIcon = true,
        appIconBgColor = Color(0xFF4CAF50)
    ),
    Message(
        contact = Contact("马爱国", Color(0xFF2196F3)),
        content = "[语音通话]",
        time = "上午 12:30"
    ),
    Message(
        contact = Contact("马大爷", Color(0xFF2196F3)),
        content = "明天我过来看一看你，老兄弟。",
        time = "昨天"
    ),
    Message(
        contact = Contact("飞猪旅行", Color(0xFFE91E63)),
        content = "[通知] 您预定的一张成人门票",
        time = "昨天",
        appIcon = true,
        appIconBgColor = Color(0xFFE91E63)
    ),
    Message(
        contact = Contact("郭阿姨", Color(0xFFFF9800)),
        content = "[语音通话]",
        time = "星期二"
    ),
    Message(
        contact = Contact("丁医生", Color(0xFF9C27B0)),
        content = "最近睡眠怎么样？记得吃药。",
        time = "星期二"
    )
) 