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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import androidx.compose.ui.platform.LocalContext
import com.baidu.mapapi.model.LatLng as BaiduLatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.example.olderperson.ui.components.NearbyPoiSearch
import com.example.olderperson.ui.theme.Primary
import com.example.olderperson.ui.components.BaiduMapView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baidu.mapapi.SDKInitializer
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.example.olderperson.ui.components.LocationService
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import androidx.compose.ui.res.painterResource
import com.example.olderperson.R
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.launch

// 搜索类型枚举
enum class NearbyServiceType(val title: String, val icon: Int, val keyword: String, val displayName: String) {
    HOSPITAL("医院", R.drawable.ic_hospital, "医院", "医院"),
    PHARMACY("药店", R.drawable.ic_pharmacy, "药店", "药店"),
    RESTAURANT("餐厅", R.drawable.ic_restaurant, "餐厅", "餐厅"),
    COMMUNITY("社区", R.drawable.ic_community, "社区服务", "社区服务")
}

@Composable
fun FamilyScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService? = null
) {
    val context = LocalContext.current
    val localTextToSpeechService = remember { TextToSpeechService(context) }
    
    // 初始化NearbyPoiSearch
    DisposableEffect(Unit) {
        try {
            // 确保百度地图SDK初始化成功
            Log.d("FamilyScreen", "初始化百度地图搜索服务")
            NearbyPoiSearch.init(context)
            
            // 预热搜索服务
            val preloadLocation = BaiduLatLng(43.90200, 125.27900)
            // 使用低优先级线程预加载搜索功能
            Handler(Looper.getMainLooper()).postDelayed({
                NearbyPoiSearch.search(
                    location = preloadLocation,
                    keyword = "医院",
                    radius = 3000,
                    onSuccess = { Log.d("FamilyScreen", "搜索服务预热成功，结果数量: ${it.size}") },
                    onError = { code, msg -> Log.e("FamilyScreen", "搜索服务预热失败: code=$code, msg=$msg") }
                )
            }, 1000)
        } catch (e: Exception) {
            Log.e("FamilyScreen", "初始化搜索服务失败: ${e.message}", e)
        }
        
        onDispose { }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部区域
            FamilyHeader(onBackToHome)
            
            // 主要内容区域
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 家人联系人区域
                item {
                    FamilyContactsCard()
                }
                
                // 通讯方式
                item {
                    CommunicationMethodsCard()
                }
                
                // 附近服务卡片（新增）
                item {
                    NearbyServicesCard(
                        modifier = Modifier,
                        textToSpeechService = localTextToSpeechService
                    )
                }
                
                // 家庭相册
                item {
                    FamilyPhotoAlbumCard()
                }
                
                // 位置共享
                item {
                    LocationSharingCard()
                }
                
                // 家庭活动
                item {
                    FamilyActivitiesCard()
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
private fun FamilyHeader(onBackToHome: () -> Unit) {
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
                text = "我和家人",
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
            text = "保持与亲人的紧密联系",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun FamilyContactsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和管理
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
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = "家人联系",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "家人联系",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 管理按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 管理联系人 */ }
                ) {
                    Text(
                        text = "管理",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "管理",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 联系人列表
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 小明
                ContactItem(
                    name = "小明",
                    relation = "儿子",
                    color = Color(0xFFFF9800)
                )
                
                // 丽丽
                ContactItem(
                    name = "丽丽",
                    relation = "女儿",
                    color = Color(0xFF9C27B0)
                )
                
                // 小红
                ContactItem(
                    name = "小红",
                    relation = "儿媳",
                    color = Color(0xFF2196F3)
                )
                
                // 添加
                AddContactItem()
            }
        }
    }
}

@Composable
private fun ContactItem(
    name: String,
    relation: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        // 联系人头像
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Text(
            text = relation,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun AddContactItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        // 添加按钮
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "添加",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Text(
            text = "新联系人",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun CommunicationMethodsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 视频通话
            CommunicationMethod(
                icon = Icons.Default.Videocam,
                text = "视频通话",
                backgroundColor = Color(0xFFE8F5E9)
            )
            
            // 语音通话
            CommunicationMethod(
                icon = Icons.Default.Call,
                text = "语音通话",
                backgroundColor = Color(0xFFE3F2FD)
            )
            
            // 发送消息
            CommunicationMethod(
                icon = Icons.Default.Message,
                text = "发送消息",
                backgroundColor = Color(0xFFF3E5F5)
            )
        }
    }
}

@Composable
private fun CommunicationMethod(
    icon: ImageVector,
    text: String,
    backgroundColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun FamilyPhotoAlbumCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和管理
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
                        imageVector = Icons.Default.Photo,
                        contentDescription = "家庭相册",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "家庭相册",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 全部按钮
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
                        contentDescription = "全部",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 相册预览
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 第一行照片
                GridRow()
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第二行照片
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                GridRow()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 添加照片按钮
            Button(
                onClick = { /* 添加照片 */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "添加照片",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun GridRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(3) {
            ImagePlaceholder()
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "图片",
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun LocationSharingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和设置
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
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "位置共享",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "位置共享",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 设置按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* 查看设置 */ }
                ) {
                    Text(
                        text = "设置",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "设置",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 位置共享说明
            Text(
                text = "让家人了解您的位置，增强安全感",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 当前位置共享状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 位置图标
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "位置",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 共享状态描述
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "当前位置共享状态",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "已与 小明、丽丽 共享",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                // 开关
                Switch(
                    checked = true,
                    onCheckedChange = { /* 切换开关 */ },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2E7D32),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}

@Composable
private fun FamilyActivitiesCard() {
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
                        contentDescription = "家庭活动",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "家庭活动",
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
            
            // 周末家庭聚餐
            ActivityItem(
                day = "06",
                month = "4月",
                title = "周末家庭聚餐",
                location = "小明家",
                time = "中午 12:00"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 小明生日聚会
            ActivityItem(
                day = "10",
                month = "4月",
                title = "小明生日聚会",
                location = "金源餐厅",
                time = "晚上 18:00"
            )
        }
    }
}

@Composable
private fun ActivityItem(
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
                .background(Color(0xFFFF9800))
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
            FamilyBottomNavItem(
                icon = Icons.Outlined.Home,
                label = "首页",
                onClick = onHomeClick
            )
            
            // 对话按钮
            FamilyBottomNavItem(
                icon = Icons.Outlined.Chat,
                label = "对话"
            )
            
            // 探索按钮
            FamilyBottomNavItem(
                icon = Icons.Outlined.Explore,
                label = "探索"
            )
            
            // 设置按钮
            FamilyBottomNavItem(
                icon = Icons.Outlined.Settings,
                label = "设置"
            )
        }
    }
}

@Composable
private fun FamilyBottomNavItem(
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

/**
 * 附近服务卡片 - 查找周边医院、药店、餐厅等设施
 */
@Composable
private fun NearbyServicesCard(
    modifier: Modifier = Modifier,
    textToSpeechService: TextToSpeechService? = null
) {
    val context = LocalContext.current
    var selectedService by remember { mutableStateOf<NearbyServiceType?>(null) }
    var searchResults by remember { mutableStateOf<List<PoiInfo>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // 定义吉林大学前卫南区的位置（精确坐标）
    val jilinUniversityLocation = remember { BaiduLatLng(43.90200, 125.27900) }
    
    // 直接执行搜索的函数
    fun searchDirectly(type: NearbyServiceType) {
        // 根据类型生成更详细的关键词
        val keyword = when (type) {
            NearbyServiceType.HOSPITAL -> "医院 综合医院 专科医院 诊所"
            NearbyServiceType.PHARMACY -> "药店 药房 大药房 医药"
            NearbyServiceType.RESTAURANT -> "餐饮 美食 饭店 餐厅"
            else -> type.keyword
        }
        
        Log.d("NearbyServicesCard", "搜索: ${type.title}, 关键词: $keyword, 坐标: lat=${jilinUniversityLocation.latitude}, lng=${jilinUniversityLocation.longitude}")
        
        // 设置当前搜索类型
        selectedService = type
        isSearching = true
        error = null
        
        // 直接使用静态方法搜索
        NearbyPoiSearch.search(
            location = jilinUniversityLocation,
            keyword = keyword,
            radius = 5000, // 扩大搜索半径，提高找到结果的可能性
            onSuccess = { poiList ->
                // 确保在主线程更新UI
                Handler(Looper.getMainLooper()).post {
                    Log.d("NearbyServicesCard", "搜索成功: ${type.name}, 原始结果数量: ${poiList.size}")
                    
                    // 记录每个结果的详细信息用于调试
                    poiList.forEachIndexed { index, poi ->
                        Log.d("NearbyServicesCard", "结果[$index]: ${poi.name}, 地址: ${poi.address}, 距离: ${poi.distance}米")
                    }
                    
                    if (poiList.isEmpty()) {
                        error = "未找到${type.displayName}，请尝试其他类型或扩大搜索范围"
                        searchResults = emptyList()
                    } else {
                        // 过滤和排序结果，确保最相关的结果在前面
                        val filteredResults = poiList
                            .filter { it.name.isNotEmpty() && it.address != null } // 过滤掉没有名称或地址的结果
                            .sortedBy { it.distance } // 按距离排序
                        
                        searchResults = filteredResults
                        error = null
                    }
                    
                    Log.d("NearbyServicesCard", "最终显示结果数量: ${searchResults.size}")
                    isSearching = false
                }
            },
            onError = { code, message ->
                // 确保在主线程更新UI
                Handler(Looper.getMainLooper()).post {
                    isSearching = false
                    error = "搜索失败: $message (错误码: $code)"
                    searchResults = emptyList()
                    Log.e("NearbyServicesCard", "搜索失败: ${type.name}, 错误码: $code, 错误信息: $message")
                }
            }
        )
    }
    
    // 当选择服务类型时触发搜索
    LaunchedEffect(selectedService) {
        selectedService?.let { serviceType ->
            searchDirectly(serviceType)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "附近服务",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 服务类型选择行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton(
                icon = painterResource(id = R.drawable.ic_hospital),
                text = "医院",
                isSelected = selectedService == NearbyServiceType.HOSPITAL,
                onClick = { selectedService = NearbyServiceType.HOSPITAL }
            )
            
            ServiceButton(
                icon = painterResource(id = R.drawable.ic_pharmacy),
                text = "药店",
                isSelected = selectedService == NearbyServiceType.PHARMACY,
                onClick = { selectedService = NearbyServiceType.PHARMACY }
            )
            
            ServiceButton(
                icon = painterResource(id = R.drawable.ic_restaurant),
                text = "餐厅",
                isSelected = selectedService == NearbyServiceType.RESTAURANT,
                onClick = { selectedService = NearbyServiceType.RESTAURANT }
            )
            
            ServiceButton(
                icon = painterResource(id = R.drawable.ic_community),
                text = "社区服务",
                isSelected = selectedService == NearbyServiceType.COMMUNITY,
                onClick = { selectedService = NearbyServiceType.COMMUNITY }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 地图视图（可折叠）
        AnimatedVisibility(
            visible = selectedService != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 使用我们的自定义BaiduMapView组件
                    BaiduMapView(
                        poiList = searchResults,
                        centerLatLng = BaiduLatLng(43.90200, 125.27900)
                    )
                    
                    // 地图关闭按钮
                    IconButton(
                        onClick = { selectedService = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭地图",
                            tint = Color.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 搜索结果或加载状态区域
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isSearching) {
                // 加载状态
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "正在搜索附近的${selectedService?.title ?: "服务"}...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else if (error != null) {
                // 错误信息
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "错误信息",
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { selectedService?.let { searchDirectly(it) } }
                        ) {
                            Text("重试")
                        }
                    }
                }
            } else if (searchResults.isEmpty() && selectedService != null) {
                // 无搜索结果
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "未找到附近的${selectedService?.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (selectedService != null) {
                // 搜索结果列表
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // 结果标题
                    Text(
                        text = "附近${selectedService?.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val displayResults = searchResults.take(5) // 显示前5个结果
                    
                    if (displayResults.isNotEmpty()) {
                        displayResults.forEach { poi ->
                            NearbyServiceItem(
                                name = poi.name,
                                address = poi.address ?: "地址未知",
                                distance = "${poi.distance}米",
                                onClick = { 
                                    // 显示地图并选中此POI
                                    searchResults = listOf(poi)
                                    selectedService = selectedService
                                    textToSpeechService?.speak("已选择${poi.name}")
                                }
                            )
                            
                            if (poi != displayResults.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                            }
                        }
                        
                        if (searchResults.size > 5) {
                            TextButton(
                                onClick = { /* 查看全部 */ },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("查看全部(${searchResults.size})")
                            }
                        }
                    }
                }
            } else {
                // 服务选择提示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "请选择上方服务类型以查看附近服务",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 服务类型按钮
 */
@Composable
private fun ServiceButton(
    icon: Painter,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) Color(0xFF2E7D32) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = if (isSelected) Color.White else Color(0xFF2E7D32),
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

/**
 * 附近服务项
 */
@Composable
private fun NearbyServiceItem(
    name: String,
    address: String,
    distance: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { 
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 设施图标 - 根据名称选择合适的图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        name.contains("医院") || name.contains("诊所") || name.contains("门诊") -> Color(0xFFE57373) // 红色
                        name.contains("药") || name.contains("医药") -> Color(0xFF81C784) // 绿色
                        name.contains("餐厅") || name.contains("食") || name.contains("饭店") -> Color(0xFFFFB74D) // 橙色
                        else -> Color(0xFF2E7D32) // 默认绿色
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when {
                    name.contains("医院") || name.contains("诊所") || name.contains("门诊") -> Icons.Default.LocalHospital
                    name.contains("药") || name.contains("医药") -> Icons.Default.LocalPharmacy
                    name.contains("餐厅") || name.contains("食") || name.contains("饭店") -> Icons.Default.Restaurant
                    else -> Icons.Default.Place
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = address,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = distance,
            fontSize = 14.sp,
            color = Primary
        )
    }
} 