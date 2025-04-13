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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
 * 服务订单数据类
 */
data class ServiceOrder(
    val id: String,
    val serviceName: String,
    val serviceType: String,
    val orderTime: String,
    val serviceTime: String,
    val status: OrderStatus,
    val price: String,
    val address: String,
    val serviceProvider: String,
    val contactPhone: String,
    val description: String = ""
)

/**
 * 订单状态枚举
 */
enum class OrderStatus(val label: String, val color: Color) {
    PENDING("待服务", Color(0xFFFFA000)),
    IN_PROGRESS("服务中", Color(0xFF2196F3)),
    COMPLETED("已完成", Color(0xFF4CAF50)),
    CANCELLED("已取消", Color(0xFF9E9E9E))
}

/**
 * 服务订单页面
 */
@Composable
fun ServiceOrderScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    // 当前选中的订单类型（进行中/历史订单）
    var selectedTab by remember { mutableStateOf(0) }
    // 当前选中的订单详情
    var selectedOrder by remember { mutableStateOf<ServiceOrder?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        ServiceOrderTopBar(onBackClick, textToSpeechService)
        
        // 如果有选中的订单，显示订单详情页面
        if (selectedOrder != null) {
            OrderDetailScreen(
                order = selectedOrder!!,
                textToSpeechService = textToSpeechService,
                onBackClick = { selectedOrder = null }
            )
        } else {
            // 订单列表页面
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 标签页
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF5722),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 2.dp,
                            color = Color(0xFFFF5722)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { 
                            selectedTab = 0 
                            textToSpeechService.speak("进行中订单")
                        },
                        text = { 
                            Text(
                                text = "进行中",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ) 
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { 
                            selectedTab = 1 
                            textToSpeechService.speak("历史订单")
                        },
                        text = { 
                            Text(
                                text = "历史订单",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ) 
                        }
                    )
                }
                
                // 订单列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 根据选中的标签页显示不同的订单列表
                    val filteredOrders = if (selectedTab == 0) {
                        // 进行中订单（待服务和服务中）
                        serviceOrders.filter { it.status == OrderStatus.PENDING || it.status == OrderStatus.IN_PROGRESS }
                    } else {
                        // 历史订单（已完成和已取消）
                        serviceOrders.filter { it.status == OrderStatus.COMPLETED || it.status == OrderStatus.CANCELLED }
                    }
                    
                    if (filteredOrders.isEmpty()) {
                        item {
                            EmptyOrderView(selectedTab, textToSpeechService)
                        }
                    } else {
                        items(filteredOrders) { order ->
                            OrderItem(
                                order = order,
                                textToSpeechService = textToSpeechService,
                                onClick = { selectedOrder = order }
                            )
                        }
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
 * 服务订单页面顶部栏
 */
@Composable
fun ServiceOrderTopBar(
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
            text = "服务订单",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("服务订单") }
        )
    }
}

/**
 * 订单项组件
 */
@Composable
fun OrderItem(
    order: ServiceOrder,
    textToSpeechService: TextToSpeechService,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                onClick()
                textToSpeechService.speak("${order.serviceName}，${order.status.label}，服务时间${order.serviceTime}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 订单头部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.serviceName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = order.status.label,
                    fontSize = 14.sp,
                    color = order.status.color,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 订单详细信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = order.serviceType,
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
                    text = "服务时间：${order.serviceTime}",
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
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = order.address,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 订单底部操作区
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¥${order.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
                
                Row {
                    if (order.status == OrderStatus.PENDING) {
                        OutlinedButton(
                            onClick = { textToSpeechService.speak("取消订单") },
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "取消订单",
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Button(
                        onClick = { textToSpeechService.speak("查看详情") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5722)
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
}

/**
 * 空订单视图
 */
@Composable
fun EmptyOrderView(
    tabIndex: Int,
    textToSpeechService: TextToSpeechService
) {
    val message = if (tabIndex == 0) "暂无进行中的订单" else "暂无历史订单"
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .clickable { textToSpeechService.speak(message) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 订单详情页面
 */
@Composable
fun OrderDetailScreen(
    order: ServiceOrder,
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
                text = "订单详情",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { textToSpeechService.speak("订单详情") }
            )
        }
        
        // 订单状态卡片
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
                    Text(
                        text = "订单状态",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = order.status.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = order.status.color
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 订单状态跟踪
                OrderStatusTracker(order.status)
            }
        }
        
        // 订单信息卡片
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
                    text = "订单信息",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                        .clickable { textToSpeechService.speak("订单信息") }
                )
                
                Divider()
                
                OrderInfoItem("订单编号", order.id)
                OrderInfoItem("服务项目", order.serviceName)
                OrderInfoItem("服务类型", order.serviceType)
                OrderInfoItem("下单时间", order.orderTime)
                OrderInfoItem("服务时间", order.serviceTime)
                OrderInfoItem("服务地址", order.address)
                OrderInfoItem("服务金额", "¥${order.price}")
            }
        }
        
        // 服务人员信息卡片
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
                    text = "服务人员",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                        .clickable { textToSpeechService.speak("服务人员") }
                )
                
                Divider()
                
                OrderInfoItem("服务人员", order.serviceProvider)
                OrderInfoItem("联系电话", order.contactPhone, true)
            }
        }
        
        // 备注信息
        if (order.description.isNotEmpty()) {
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
                        text = "备注信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                            .clickable { textToSpeechService.speak("备注信息") }
                    )
                    
                    Divider()
                    
                    Text(
                        text = order.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                            .clickable { textToSpeechService.speak(order.description) }
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
            if (order.status == OrderStatus.PENDING) {
                OutlinedButton(
                    onClick = { textToSpeechService.speak("取消订单") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "取消订单",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            
            Button(
                onClick = { textToSpeechService.speak("联系服务人员") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                )
            ) {
                Text(
                    text = "联系服务人员",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * 订单信息项
 */
@Composable
fun OrderInfoItem(
    label: String,
    value: String,
    isPhone: Boolean = false
) {
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
        
        if (isPhone) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFFFF5722),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

/**
 * 订单状态跟踪器
 */
@Composable
fun OrderStatusTracker(currentStatus: OrderStatus) {
    val statuses = listOf(
        "下单成功" to (true),
        "待服务" to (currentStatus != OrderStatus.CANCELLED),
        "服务中" to (currentStatus == OrderStatus.IN_PROGRESS || currentStatus == OrderStatus.COMPLETED),
        "已完成" to (currentStatus == OrderStatus.COMPLETED)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        statuses.forEachIndexed { index, (status, isActive) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 状态点
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (isActive) Color(0xFFFF5722) else Color.LightGray,
                            shape = CircleShape
                        )
                )
                
                // 连接线
                if (index < statuses.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(2.dp)
                            .background(
                                color = if (isActive && statuses[index + 1].second) Color(0xFFFF5722) else Color.LightGray
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 状态文本
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = if (isActive) Color(0xFFFF5722) else Color.Gray
                )
            }
        }
    }
}

// 模拟数据 - 服务订单
val serviceOrders = listOf(
    ServiceOrder(
        id = "SO20230001",
        serviceName = "居家保洁服务",
        serviceType = "家政服务",
        orderTime = "2025-06-15 10:30",
        serviceTime = "2025-06-20 14:00-16:00",
        status = OrderStatus.PENDING,
        price = "128.00",
        address = "长春市宽城区庆丰路",
        serviceProvider = "张阿姨",
        contactPhone = "13812345678"
    ),
    ServiceOrder(
        id = "SO20230002",
        serviceName = "上门理发服务",
        serviceType = "生活服务",
        orderTime = "2025-06-10 15:20",
        serviceTime = "2025-06-12 10:00-11:00",
        status = OrderStatus.COMPLETED,
        price = "68.00",
        address = "长春市二道区公平路",
        serviceProvider = "李师傅",
        contactPhone = "13987654321"
    ),
    ServiceOrder(
        id = "SO20230003",
        serviceName = "中医推拿服务",
        serviceType = "健康服务",
        orderTime = "2025-06-18 09:15",
        serviceTime = "2025-06-19 15:30-16:30",
        status = OrderStatus.IN_PROGRESS,
        price = "158.00",
        address = "长春市朝阳区前进大街",
        serviceProvider = "王医生",
        contactPhone = "13567891234",
        description = "请带好相关证件，需要提前15分钟到达"
    ),
    ServiceOrder(
        id = "SO20230004",
        serviceName = "家电维修服务",
        serviceType = "维修服务",
        orderTime = "2025-06-05 14:30",
        serviceTime = "2025-06-07 09:00-11:00",
        status = OrderStatus.CANCELLED,
        price = "98.00",
        address = "长春市朝阳区前进大街",
        serviceProvider = "赵师傅",
        contactPhone = "13612345678"
    )
)