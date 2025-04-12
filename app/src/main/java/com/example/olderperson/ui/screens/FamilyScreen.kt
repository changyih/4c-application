package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.olderperson.service.PhoneCallService
import com.example.olderperson.data.ContactHelper
import com.example.olderperson.data.ContactRepository
import com.example.olderperson.data.FamilyContact
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FamilyScreen(
    onBackToHome: () -> Unit = {},
    textToSpeechService: TextToSpeechService? = null,
    phoneCallService: PhoneCallService? = null,
    onRequestPhonePermission: () -> Unit = {}
) {
    val context = LocalContext.current
    val localTextToSpeechService = remember { TextToSpeechService(context) }
    
    // 创建联系人仓库
    val contactRepository = remember { ContactRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // 联系人列表状态
    val contactsState = contactRepository.contacts.collectAsState(initial = ContactHelper.defaultContacts)
    
    // 拨打电话对话框状态
    var showCallDialog by remember { mutableStateOf(false) }
    var currentContactName by remember { mutableStateOf("") }
    var currentPhoneNumber by remember { mutableStateOf("") }
    
    // 添加联系人对话框状态
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactRelation by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    
    // 联系人管理对话框状态
    var showManageContactsDialog by remember { mutableStateOf(false) }
    
    // 编辑联系人对话框状态
    var showEditContactDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<FamilyContact?>(null) }
    var editContactName by remember { mutableStateOf("") }
    var editContactRelation by remember { mutableStateOf("") }
    var editContactPhone by remember { mutableStateOf("") }
    
    // 删除确认对话框状态
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<FamilyContact?>(null) }
    
    // 设置要编辑的联系人
    fun prepareEditContact(contact: FamilyContact) {
        editingContact = contact
        editContactName = contact.name
        editContactRelation = contact.relation
        editContactPhone = contact.phoneNumber
    }
    
    // 清除编辑联系人表单
    fun clearEditContactForm() {
        editingContact = null
        editContactName = ""
        editContactRelation = ""
        editContactPhone = ""
    }
    
    // 清除添加联系人对话框内容
    fun clearAddContactForm() {
        newContactName = ""
        newContactRelation = ""
        newContactPhone = ""
    }
    
    // 拨打电话确认对话框
    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("拨打电话") },
            text = { 
                Column {
                    Text("确定要拨打 $currentContactName 的电话吗？")
                    Text("电话号码：$currentPhoneNumber")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 检查是否有权限，如果没有则请求权限
                        if (phoneCallService?.hasCallPhonePermission() == true) {
                            phoneCallService.makePhoneCall(currentPhoneNumber)
                            showCallDialog = false
                            textToSpeechService?.speak("正在拨打${currentContactName}的电话")
                        } else {
                            // 请求电话权限
                            onRequestPhonePermission()
                            // 关闭当前对话框，权限获取后用户需要重新点击
                            showCallDialog = false
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                Button(onClick = { showCallDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 添加联系人对话框
    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddContactDialog = false
                clearAddContactForm()
            },
            title = { Text("添加联系人") },
            text = { 
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 姓名输入
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("姓名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 关系输入
                    OutlinedTextField(
                        value = newContactRelation,
                        onValueChange = { newContactRelation = it },
                        label = { Text("与您的关系") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 电话号码输入
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("电话号码") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 验证输入
                        if (newContactName.isNotBlank() && newContactPhone.isNotBlank()) {
                            // 创建新联系人
                            val newContact = FamilyContact(
                                id = ContactHelper.generateId(),
                                name = newContactName,
                                relation = newContactRelation,
                                phoneNumber = newContactPhone,
                                colorHex = ContactHelper.getRandomColorHex()
                            )
                            
                            // 添加联系人
                            coroutineScope.launch {
                                contactRepository.addContact(newContact)
                                textToSpeechService?.speak("已添加联系人${newContactName}")
                            }
                            
                            // 清空表单并关闭对话框
                            clearAddContactForm()
                            showAddContactDialog = false
                        } else {
                            // 提示用户填写必要字段
                            Toast.makeText(context, "请填写姓名和电话号码", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        showAddContactDialog = false
                        clearAddContactForm()
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    // 联系人管理对话框
    if (showManageContactsDialog) {
        AlertDialog(
            onDismissRequest = { showManageContactsDialog = false },
            title = { Text("管理联系人") },
            text = { 
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .heightIn(max = 300.dp)
                ) {
                    if (contactsState.value.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无联系人，请添加")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(contactsState.value) { contact ->
                                ManageContactItem(
                                    contact = contact,
                                    onEdit = {
                                        prepareEditContact(contact)
                                        showEditContactDialog = true
                                        showManageContactsDialog = false
                                    },
                                    onDelete = {
                                        contactToDelete = contact
                                        showDeleteConfirmDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showManageContactsDialog = false
                        clearAddContactForm()
                        showAddContactDialog = true
                    }
                ) {
                    Text("添加新联系人")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showManageContactsDialog = false }
                ) {
                    Text("关闭")
                }
            }
        )
    }
    
    // 编辑联系人对话框
    if (showEditContactDialog) {
        AlertDialog(
            onDismissRequest = { 
                showEditContactDialog = false
                clearEditContactForm()
            },
            title = { Text("编辑联系人") },
            text = { 
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 姓名输入
                    OutlinedTextField(
                        value = editContactName,
                        onValueChange = { editContactName = it },
                        label = { Text("姓名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 关系输入
                    OutlinedTextField(
                        value = editContactRelation,
                        onValueChange = { editContactRelation = it },
                        label = { Text("与您的关系") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 电话号码输入
                    OutlinedTextField(
                        value = editContactPhone,
                        onValueChange = { editContactPhone = it },
                        label = { Text("电话号码") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 验证输入
                        if (editContactName.isNotBlank() && editContactPhone.isNotBlank() && editingContact != null) {
                            // 创建更新后的联系人
                            val updatedContact = editingContact!!.copy(
                                name = editContactName,
                                relation = editContactRelation,
                                phoneNumber = editContactPhone
                            )
                            
                            // 更新联系人
                            coroutineScope.launch {
                                contactRepository.updateContact(updatedContact)
                                textToSpeechService?.speak("已更新联系人${editContactName}")
                                // 关闭对话框并显示管理界面
                                showEditContactDialog = false
                                clearEditContactForm()
                                showManageContactsDialog = true
                            }
                        } else {
                            // 提示用户填写必要字段
                            Toast.makeText(context, "请填写姓名和电话号码", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        showEditContactDialog = false
                        clearEditContactForm()
                        showManageContactsDialog = true
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    // 删除确认对话框
    if (showDeleteConfirmDialog && contactToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmDialog = false
                contactToDelete = null
            },
            title = { Text("确认删除") },
            text = { 
                Text("您确定要删除联系人\"${contactToDelete!!.name}\"吗？此操作不可恢复。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            contactRepository.deleteContact(contactToDelete!!.id)
                            textToSpeechService?.speak("已删除联系人${contactToDelete!!.name}")
                            showDeleteConfirmDialog = false
                            contactToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        showDeleteConfirmDialog = false
                        contactToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
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
                    FamilyContactsCard(
                        contacts = contactsState.value,
                        phoneCallService = phoneCallService,
                        textToSpeechService = textToSpeechService ?: localTextToSpeechService,
                        onCallRequest = { name, phoneNumber ->
                            currentContactName = name
                            currentPhoneNumber = phoneNumber
                            showCallDialog = true
                        },
                        onAddContactClick = {
                            clearAddContactForm()
                            showAddContactDialog = true
                        },
                        onManageContacts = {
                            showManageContactsDialog = true
                            textToSpeechService?.speak("管理联系人")
                        }
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
private fun FamilyContactsCard(
    contacts: List<FamilyContact>,
    phoneCallService: PhoneCallService? = null,
    textToSpeechService: TextToSpeechService? = null,
    onCallRequest: (String, String) -> Unit,
    onAddContactClick: () -> Unit,
    onManageContacts: () -> Unit
) {
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
                    modifier = Modifier.clickable { onManageContacts() }
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
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 显示联系人
                items(contacts) { contact ->
                    ContactItem(
                        name = contact.name,
                        relation = contact.relation,
                        phoneNumber = contact.phoneNumber,
                        color = contact.getColor(),
                        onCallRequest = onCallRequest
                    )
                }
                
                // 添加联系人按钮
                item {
                    AddContactItem(onAddContactClick)
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    name: String,
    relation: String,
    phoneNumber: String,
    color: Color,
    onCallRequest: (String, String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        // 联系人头像
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color)
                .clickable { 
                    onCallRequest(name, phoneNumber)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
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
private fun AddContactItem(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        // 添加按钮
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50))
                .clickable { onClick() },
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
                        text = "已与 儿子、儿媳 共享",
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
                location = "儿子家",
                time = "中午 12:00"
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE),
                thickness = 1.dp
            )
            
            // 儿子生日聚会
            ActivityItem(
                day = "10",
                month = "4月",
                title = "儿子明远生日聚会",
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

@Composable
private fun ManageContactItem(
    contact: FamilyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 联系人头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(contact.getColor()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 联系人信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${contact.relation} · ${contact.phoneNumber}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 编辑按钮
        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "编辑",
                tint = Color(0xFF2196F3)
            )
        }
        
        // 删除按钮
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除",
                tint = Color(0xFFE53935)
            )
        }
    }
} 