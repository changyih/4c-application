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
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.data.ContactHelper
import com.example.olderperson.data.ContactRepository
import com.example.olderperson.data.FamilyContact
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

/**
 * 绑定家人页面
 */
@Composable
fun BindFamilyScreen(
    textToSpeechService: TextToSpeechService,
    onBackClick: () -> Unit = {}
) {
    // 上下文
    val context = LocalContext.current
    
    // 创建联系人仓库
    val contactRepository = remember { ContactRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // 联系人列表状态
    val contactsState = contactRepository.contacts.collectAsState(initial = ContactHelper.defaultContacts)
    
    // 对话框状态
    var showAddFamilyDialog by remember { mutableStateOf(false) }
    var showQRCodeDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<FamilyContact?>(null) }
    var showFamilyDetailDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // 新联系人表单状态
    var newContactName by remember { mutableStateOf("") }
    var newContactRelation by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    
    // 清空表单数据
    fun clearAddFamilyForm() {
        newContactName = ""
        newContactRelation = ""
        newContactPhone = ""
    }
    
    // 背景颜色 - 使用纯黑色背景
    val backgroundColor = Color.Black
    val textColor = Color.White
    val secondaryTextColor = Color.Gray
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部栏 - 简洁风格，与图片一致
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = textColor,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterStart)
                        .clickable { onBackClick() }
                )
            }
            
            // 主内容区域
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 绑定家人标题 - 使用更大字体
                item {
                    Text(
                        text = "绑定家人",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .clickable { textToSpeechService.speak("绑定家人") }
                    )
                }
                
                // 功能说明卡片
                item {
                    BindFamilyTipsCard(textToSpeechService, textColor)
                }
                
                // 已绑定家人标题
                item {
                    Text(
                        text = "已绑定家人 (${contactsState.value.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .clickable { 
                                textToSpeechService.speak("已绑定家人 ${contactsState.value.size}人") 
                            }
                    )
                }
                
                // 已绑定家人列表
                if (contactsState.value.isEmpty()) {
                    item {
                        EmptyFamilyView(textToSpeechService, textColor, secondaryTextColor)
                    }
                } else {
                    items(contactsState.value) { contact ->
                        FamilyMemberItem(
                            contact = contact,
                            textToSpeechService = textToSpeechService,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor,
                            onClick = { 
                                selectedContact = contact
                                showFamilyDetailDialog = true
                            }
                        )
                    }
                }
                
                // 添加家人按钮
                item {
                    AddFamilyButton(
                        onClick = { showAddFamilyDialog = true },
                        textToSpeechService = textToSpeechService
                    )
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    // 添加家人对话框
    if (showAddFamilyDialog) {
        AddFamilyDialog(
            onDismiss = { 
                showAddFamilyDialog = false
                clearAddFamilyForm()
            },
            onConfirm = { name, relation, phone ->
                // 创建并添加新联系人
                val newContact = FamilyContact(
                    id = ContactHelper.generateId(),
                    name = name,
                    relation = relation,
                    phoneNumber = phone,
                    colorHex = ContactHelper.getRandomColorHex()
                )
                
                coroutineScope.launch {
                    contactRepository.addContact(newContact)
                    textToSpeechService.speak("已添加家人${name}")
                }
                
                showAddFamilyDialog = false
                clearAddFamilyForm()
            },
            onShowQRCode = {
                showAddFamilyDialog = false
                showQRCodeDialog = true
                textToSpeechService.speak("请让家人扫描二维码完成绑定")
            },
            name = newContactName,
            relation = newContactRelation,
            phone = newContactPhone,
            onNameChange = { newContactName = it },
            onRelationChange = { newContactRelation = it },
            onPhoneChange = { newContactPhone = it }
        )
    }
    
    // 显示二维码对话框
    if (showQRCodeDialog) {
        QRCodeDialog(
            onDismiss = { showQRCodeDialog = false },
            textToSpeechService = textToSpeechService
        )
    }
    
    // 家人详情对话框
    if (showFamilyDetailDialog && selectedContact != null) {
        FamilyDetailDialog(
            contact = selectedContact!!,
            onDismiss = { showFamilyDetailDialog = false },
            onDelete = {
                showFamilyDetailDialog = false
                showDeleteConfirmDialog = true
            },
            textToSpeechService = textToSpeechService
        )
    }
    
    // 删除确认对话框
    if (showDeleteConfirmDialog && selectedContact != null) {
        DeleteConfirmDialog(
            contactName = selectedContact!!.name,
            onConfirm = {
                coroutineScope.launch {
                    contactRepository.deleteContact(selectedContact!!.id)
                    textToSpeechService.speak("已解除与${selectedContact!!.name}的绑定关系")
                }
                showDeleteConfirmDialog = false
                selectedContact = null
            },
            onDismiss = {
                showDeleteConfirmDialog = false
            }
        )
    }
}

/**
 * 绑定家人提示卡片
 */
@Composable
fun BindFamilyTipsCard(
    textToSpeechService: TextToSpeechService,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { textToSpeechService.speak("绑定家人提示：您可以在此页面添加和管理绑定的家人，让家人随时了解您的状况，为您提供帮助和关爱。") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F) // 深灰色背景
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "绑定家人提示",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "您可以在此页面添加和管理绑定的家人，让家人随时了解您的状况，为您提供帮助和关爱。",
                fontSize = 15.sp,
                color = Color.LightGray,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * 无家人视图
 */
@Composable
fun EmptyFamilyView(
    textToSpeechService: TextToSpeechService,
    textColor: Color,
    secondaryTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.DarkGray
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "暂无绑定家人",
            fontSize = 15.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { textToSpeechService.speak("暂无绑定家人，请点击下方按钮添加") }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "添加家人可以让他们关注您的状态",
            fontSize = 12.sp,
            color = secondaryTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

/**
 * 家人列表项
 */
@Composable
fun FamilyMemberItem(
    contact: FamilyContact,
    textToSpeechService: TextToSpeechService,
    textColor: Color,
    secondaryTextColor: Color,
    onClick: () -> Unit = {}
) {
    val colorValue = try {
        Color(android.graphics.Color.parseColor(contact.colorHex))
    } catch (e: Exception) {
        Color(0xFF4CAF50) // 默认颜色
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                onClick()
                textToSpeechService.speak("${contact.name}，${contact.relation}，电话号码：${contact.phoneNumber}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F) // 深灰色卡片背景
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colorValue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1),
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // 信息区域
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = contact.relation,
                    fontSize = 12.sp,
                    color = secondaryTextColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = contact.phoneNumber,
                    fontSize = 12.sp,
                    color = secondaryTextColor
                )
            }
            
            // 绑定状态指示 - 绿色对勾
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已绑定",
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * 添加家人按钮
 */
@Composable
fun AddFamilyButton(
    onClick: () -> Unit,
    textToSpeechService: TextToSpeechService
) {
    // 使用无填充的按钮样式，带边框
    OutlinedButton(
        onClick = { 
            onClick()
            textToSpeechService.speak("添加家人") 
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.White),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "添加家人",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

/**
 * 添加家人对话框
 */
@Composable
fun AddFamilyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    onShowQRCode: () -> Unit,
    name: String,
    relation: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onRelationChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加家人") },
        text = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 姓名输入
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("姓名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 关系输入
                OutlinedTextField(
                    value = relation,
                    onValueChange = onRelationChange,
                    label = { Text("与您的关系") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 电话号码输入
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("电话号码") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 分割线
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 使用二维码选项
                OutlinedButton(
                    onClick = onShowQRCode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text("使用二维码添加")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, relation, phone) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 二维码对话框
 */
@Composable
fun QRCodeDialog(
    onDismiss: () -> Unit,
    textToSpeechService: TextToSpeechService
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("扫描二维码绑定") },
        text = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 二维码图像（示例）
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White)
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .clickable { textToSpeechService.speak("请让家人扫描此二维码进行绑定") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "二维码",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "请让家人用手机扫描此二维码\n完成绑定",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "二维码15分钟内有效",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9800)
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

/**
 * 家人详情对话框
 */
@Composable
fun FamilyDetailDialog(
    contact: FamilyContact,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    textToSpeechService: TextToSpeechService
) {
    val colorValue = try {
        Color(android.graphics.Color.parseColor(contact.colorHex))
    } catch (e: Exception) {
        Color(0xFF4CAF50) // 默认颜色
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("家人详情") },
        text = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(colorValue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 姓名
                Text(
                    text = contact.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                // 关系
                Text(
                    text = contact.relation,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 详细信息卡片
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // 电话信息
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "电话",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = contact.phoneNumber,
                                color = Color.Black
                            )
                        }
                        
                        // 绑定时间信息（示例）
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "绑定时间",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "2025年5月15日绑定",
                                color = Color.Black
                            )
                        }
                        
                        // 绑定方式信息
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "绑定方式",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "手动添加",
                                color = Color.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 查看授权权限按钮
                OutlinedButton(
                    onClick = { 
                        textToSpeechService.speak("${contact.name}可以查看您的健康数据、位置信息和设备状态") 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看授权权限")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {
                Text("解除绑定")
            }
        }
    )
}

/**
 * 删除确认对话框
 */
@Composable
fun DeleteConfirmDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("解除绑定") },
        text = { 
            Text("确定要解除与\"$contactName\"的绑定关系吗？解除后他/她将无法查看您的信息。")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("解除绑定")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 