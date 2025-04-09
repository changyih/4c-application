package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun CommunityScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部区域
            CommunityHeader(onBackToHome)
            
            // 主要内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 周边服务卡片
                item {
                    NearbyServicesCard()
                }
                
                // 社区活动卡片
                item {
                    CommunityActivitiesCard()
                }
                
                // 社区公告卡片
                item {
                    CommunityAnnouncementsCard()
                }
                
                // 社区服务卡片
                item {
                    CommunityServicesCard()
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // 底部导航栏
            BottomNavigationBar(onHomeClick = onBackToHome)
        }
    }
}

@Composable
private fun CommunityHeader(onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            IconButton(
                onClick = onBackToHome
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.Black
                )
            }
            
            Text(
                text = "我和社区",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // 用户头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF87CEEB))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }
        
        // 副标题
        Text(
            text = "参与社区活动，融入集体生活",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun CommunityActivitiesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和查看全部
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 标题图标和文字
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "社区活动",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "社区活动",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 查看全部
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 查看全部 */ }
                ) {
                    Text(
                        text = "全部",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看全部",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 社区棋牌活动
            CommunityActivityItem(
                day = "02",
                month = "今天",
                title = "社区棋牌活动",
                location = "小区活动中心",
                time = "下午 3:00 - 5:00"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 健康讲座
            CommunityActivityItem(
                day = "05",
                month = "4月",
                title = "健康讲座",
                location = "社区医院会议室",
                time = "上午 9:30 - 11:00"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 春季郊游活动
            CommunityActivityItem(
                day = "08",
                month = "4月",
                title = "春季郊游活动",
                location = "市植物园",
                time = "上午 8:30 - 下午 2:00"
            )
        }
    }
}

@Composable
private fun CommunityActivityItem(
    day: String,
    month: String,
    title: String,
    location: String,
    time: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 日期显示
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50))
                .padding(8.dp)
        ) {
            Text(
                text = day,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = month,
                fontSize = 12.sp,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 活动详情
        Column {
            // 活动标题
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 地点
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "地点",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 时间
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "时间",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun CommunityServicesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和更多
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 标题图标和文字
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "社区服务",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "社区服务",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 更多
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 查看更多 */ }
                ) {
                    Text(
                        text = "更多",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "查看更多",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 日常生活用品配送
            CommunityServiceItem(
                icon = Icons.Default.ShoppingCart,
                title = "日常生活用品配送",
                backgroundColor = Color(0xFFE3F2FD)
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 上门医疗服务
            CommunityServiceItem(
                icon = Icons.Default.LocalHospital,
                title = "上门医疗服务",
                backgroundColor = Color(0xFFE8F5E9)
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 家政保洁服务
            CommunityServiceItem(
                icon = Icons.Default.Refresh,
                title = "家政保洁服务",
                backgroundColor = Color(0xFFFFF8E1)
            )
        }
    }
}

@Composable
private fun CommunityServiceItem(
    icon: ImageVector,
    title: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 服务图标
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 服务标题
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomNavigationBar(onHomeClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页按钮
            CommunityBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                onClick = onHomeClick
            )
            
            // 对话按钮
            CommunityBottomNavItem(
                icon = Icons.Outlined.Chat,
                label = "对话"
            )
            
            // 探索按钮
            CommunityBottomNavItem(
                icon = Icons.Outlined.Explore,
                label = "探索"
            )
            
            // 设置按钮
            CommunityBottomNavItem(
                icon = Icons.Outlined.Settings,
                label = "设置"
            )
        }
    }
}

@Composable
private fun CommunityBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NearbyServicesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 社区医院
            NearbyServiceItem(
                icon = Icons.Default.LocalHospital,
                title = "社区医院",
                distance = "距离 0.5 公里 | 步行约 8 分钟",
                backgroundColor = Color(0xFFE3F2FD)
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 超市
            NearbyServiceItem(
                icon = Icons.Default.ShoppingCart,
                title = "超市",
                distance = "距离 0.3 公里 | 步行约 5 分钟",
                backgroundColor = Color(0xFFE8F5E9)
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 老年餐厅
            NearbyServiceItem(
                icon = Icons.Default.Restaurant,
                title = "老年餐厅",
                distance = "距离 0.4 公里 | 步行约 7 分钟",
                backgroundColor = Color(0xFFF3E5F5)
            )
        }
    }
}

@Composable
private fun NearbyServiceItem(
    icon: ImageVector,
    title: String,
    distance: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 服务图标
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 服务信息
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = distance,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun CommunityAnnouncementsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Announcement,
                    contentDescription = "社区公告",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "社区公告",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 水电费缴纳通知
            AnnouncementItem(
                title = "水电费缴纳通知",
                content = "4月份水电费将于4月10日前缴纳，请各位居民注意时间。"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 小区绿化维护
            AnnouncementItem(
                title = "小区绿化维护",
                content = "小区将于4月4日进行绿化维护，请各位居民配合工作。"
            )
        }
    }
}

@Composable
private fun AnnouncementItem(
    title: String,
    content: String
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
} 