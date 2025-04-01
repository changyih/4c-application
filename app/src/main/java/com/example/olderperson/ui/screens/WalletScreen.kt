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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 钱包页面
 */
@Composable
fun WalletScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部栏
        WalletTopBar(onBackClick, textToSpeechService)
        
        // 内容区域
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 账户余额卡片
            item {
                BalanceCard(balance = "2,580.00", textToSpeechService = textToSpeechService)
            }
            
            // 操作按钮
            item {
                ActionButtons(textToSpeechService)
            }
            
            // 交易记录标题
            item {
                Text(
                    text = "交易记录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        .clickable { textToSpeechService.speak("交易记录") }
                )
            }
            
            // 交易记录列表
            items(transactionList) { transaction ->
                TransactionItem(transaction, textToSpeechService)
            }
            
            // 支付方式标题
            item {
                Text(
                    text = "支付方式",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        .clickable { textToSpeechService.speak("支付方式") }
                )
            }
            
            // 支付方式列表
            items(paymentMethods) { method ->
                PaymentMethodItem(method, textToSpeechService)
            }
            
            // 底部间距
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * 钱包页面顶部栏
 */
@Composable
fun WalletTopBar(
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
            text = "我的钱包",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { textToSpeechService.speak("我的钱包") }
        )
    }
}

/**
 * 账户余额卡片
 */
@Composable
fun BalanceCard(
    balance: String,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { textToSpeechService.speak("您的账户余额为${balance}元") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(OrangeGradient, OrangeGradientEnd))
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "账户余额",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "¥",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = balance,
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "点击查看详情",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 操作按钮区域
 */
@Composable
fun ActionButtons(textToSpeechService: TextToSpeechService) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Add,
            text = "充值",
            textToSpeechService = textToSpeechService
        )
        
        ActionButton(
            icon = Icons.Default.ArrowUpward,
            text = "提现",
            textToSpeechService = textToSpeechService
        )
        
        ActionButton(
            icon = Icons.Default.SwapHoriz,
            text = "转账",
            textToSpeechService = textToSpeechService
        )
        
        ActionButton(
            icon = Icons.Default.Receipt,
            text = "账单",
            textToSpeechService = textToSpeechService
        )
    }
}

/**
 * 操作按钮
 */
@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    textToSpeechService: TextToSpeechService
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { textToSpeechService.speak(text) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 交易记录项
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                textToSpeechService.speak(
                    "${transaction.title}，${transaction.date}，${transaction.amount}元"
                ) 
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 交易图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(transaction.iconBackground.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.icon,
                    contentDescription = transaction.title,
                    tint = transaction.iconBackground,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = transaction.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = transaction.amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) Green500 else Color.Black
            )
        }
    }
}

/**
 * 支付方式项
 */
@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    textToSpeechService: TextToSpeechService
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak(method.name) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 支付方式图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(method.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = method.icon,
                    contentDescription = method.name,
                    tint = method.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                if (method.detail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = method.detail,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (method.isDefault) {
                Text(
                    text = "默认",
                    fontSize = 12.sp,
                    color = Primary,
                    modifier = Modifier
                        .background(
                            color = Primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// 数据模型
data class Transaction(
    val title: String,
    val date: String,
    val amount: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val isIncome: Boolean = false
)

data class PaymentMethod(
    val name: String,
    val detail: String = "",
    val icon: ImageVector,
    val color: Color,
    val isDefault: Boolean = false
)

// 示例数据
val transactionList = listOf(
    Transaction(
        title = "健康服务订阅",
        date = "2023-06-15 14:30",
        amount = "-¥99.00",
        icon = Icons.Default.Favorite,
        iconBackground = Red500
    ),
    Transaction(
        title = "养老金到账",
        date = "2023-06-10 09:15",
        amount = "+¥2,800.00",
        icon = Icons.Default.AccountBalance,
        iconBackground = Green500,
        isIncome = true
    ),
    Transaction(
        title = "药品购买",
        date = "2023-06-05 16:45",
        amount = "-¥120.50",
        icon = Icons.Default.LocalPharmacy,
        iconBackground = PurpleGradient
    ),
    Transaction(
        title = "社区活动费用",
        date = "2023-06-01 10:30",
        amount = "-¥50.00",
        icon = Icons.Default.People,
        iconBackground = BrightBlue
    )
)

val paymentMethods = listOf(
    PaymentMethod(
        name = "银行卡",
        detail = "工商银行 ****6789",
        icon = Icons.Default.CreditCard,
        color = BrightBlue,
        isDefault = true
    ),
    PaymentMethod(
        name = "微信支付",
        icon = Icons.AutoMirrored.Filled.Message,
        color = Green500
    ),
    PaymentMethod(
        name = "支付宝",
        icon = Icons.Default.AccountBalance,
        color = OrangeGradient
    )
)