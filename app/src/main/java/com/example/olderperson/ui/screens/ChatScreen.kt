package com.example.olderperson.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.SpeechRecognitionService
import com.example.olderperson.service.TextToSpeechService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    textToSpeechService: TextToSpeechService,
    speechRecognitionService: SpeechRecognitionService,
    onRequestPermission: () -> Unit = {}
) {
    val TAG = "ChatScreen"
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var showVoiceHelp by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // 收集语音识别状态
    val isListening by speechRecognitionService.isListening.collectAsState()
    val recognizedText by speechRecognitionService.recognizedText.collectAsState()
    
    // 监听识别状态和结果
    LaunchedEffect(isListening, recognizedText) {
        Log.d(TAG, "识别状态: $isListening, 识别文本: $recognizedText")
        
        if (recognizedText.isNotEmpty()) {
            Log.d(TAG, "更新输入框文本: $recognizedText")
            inputText = recognizedText
        }
    }
    
    // 添加初始消息，只执行一次
    LaunchedEffect(Unit) {
        messages = listOf(
            ChatMessage(
                content = "您好！我是您的智能助手，有什么可以帮您的吗？",
                isUser = false,
                timestamp = System.currentTimeMillis()
            )
        )
    }
    
    // 显示语音帮助对话框
    if (showVoiceHelp) {
        AlertDialog(
            onDismissRequest = { showVoiceHelp = false },
            title = { Text("语音输入帮助") },
            text = { 
                Column {
                    Text("1. 点击左下角麦克风图标切换到语音模式")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. 长按按住说话按钮开始录音")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. 说话完毕后松开按钮")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("4. 识别到的文字会自动显示并可以发送")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("注意: 如果语音识别不工作，请确保已授予麦克风权限", color = Color.Red)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showVoiceHelp = false
                        onRequestPermission()
                    }
                ) {
                    Text("请求权限")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceHelp = false }) {
                    Text("我知道了")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "智慧伙伴",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 帮助按钮
                    IconButton(onClick = { showVoiceHelp = true }) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "语音帮助"
                        )
                    }
                    // 查看今日全部提醒按钮
                    TextButton(
                        onClick = { /* TODO: 实现查看全部提醒功能 */ }
                    ) {
                        Text("查看今日全部提醒")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 消息列表
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(messages.asReversed()) { message ->
                    ChatMessageItem(message)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 底部输入区域
            BottomInputArea(
                inputText = inputText,
                isVoiceMode = isVoiceMode,
                isListening = isListening,
                onInputTextChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotEmpty()) {
                        coroutineScope.launch {
                            addUserMessage(inputText, messages) { newMessages ->
                                messages = newMessages
                            }
                            inputText = ""
                        }
                    }
                },
                onVoiceModeToggle = {
                    isVoiceMode = !isVoiceMode
                    // 如果切换到语音模式，请求权限
                    if (!isVoiceMode) {
                        onRequestPermission()
                    }
                },
                onStartRecognition = { 
                    Log.d(TAG, "开始语音识别")
                    speechRecognitionService.startListening() 
                },
                onStopRecognition = { 
                    Log.d(TAG, "停止语音识别")
                    speechRecognitionService.stopListening() 
                    
                    // 停止识别后如果有文字，就发送消息
                    if (inputText.isNotEmpty()) {
                        Log.d(TAG, "识别到的文本: $inputText")
                        coroutineScope.launch {
                            addUserMessage(inputText, messages) { newMessages ->
                                messages = newMessages
                            }
                            inputText = ""
                        }
                    } else {
                        // 没有识别到文字，可能需要权限
                        onRequestPermission()
                    }
                }
            )
        }
    }
}

// 发送消息的函数，以便在多处复用
private suspend fun addUserMessage(
    text: String,
    currentMessages: List<ChatMessage>,
    updateMessages: (List<ChatMessage>) -> Unit
) {
    val userMessage = ChatMessage(
        content = text,
        isUser = true,
        timestamp = System.currentTimeMillis()
    )
    
    // 先添加用户消息
    updateMessages(currentMessages + userMessage)
    
    // 延迟一秒后添加助手回复
    delay(1000)
    
    val assistantMessage = ChatMessage(
        content = getAssistantResponse(text),
        isUser = false,
        timestamp = System.currentTimeMillis()
    )
    
    updateMessages(currentMessages + userMessage + assistantMessage)
}

// 根据用户输入生成助手响应
private fun getAssistantResponse(userInput: String): String {
    return when {
        userInput.contains("今天") && userInput.contains("活动") -> 
            "是的，您今天下午有一个社区活动：\n\n社区棋牌活动\n时间：下午3:00-5:00\n地点：小区活动中心\n\n需要我提前半小时提醒您吗？"
        
        userInput.contains("今天") && userInput.contains("天气") ->
            "今天天气晴朗，气温20-25℃，适合户外活动。建议您做好防晒措施。"
        
        userInput.contains("吃") || userInput.contains("饭") ->
            "根据您的健康状况，今天建议饮食清淡，可以多吃一些蔬菜和水果。您想了解今天的推荐菜单吗？"
        
        userInput.contains("药") || userInput.contains("吃药") ->
            "根据记录，您今天需要在饭后服用降压药和降糖药各一片。请记得按时服用。"
        
        userInput.contains("谢谢") ->
            "不客气，这是我的职责。有什么需要随时告诉我。"
        
        else -> "好的，我明白了。我会帮您处理这个问题。需要其他帮助吗？"
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // 时间戳
        Text(
            text = formatTimestamp(message.timestamp),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // 消息气泡
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = if (message.isUser) 16.dp else 0.dp,
                        topEnd = if (message.isUser) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    if (message.isUser) Color(0xFF2E7D32)
                    else Color(0xFFE8F5E9)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isUser) Color.White else Color.Black,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun BottomInputArea(
    inputText: String,
    isVoiceMode: Boolean,
    isListening: Boolean,
    onInputTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onVoiceModeToggle: () -> Unit,
    onStartRecognition: () -> Unit,
    onStopRecognition: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 语音/键盘切换按钮
        IconButton(
            onClick = onVoiceModeToggle,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32))
        ) {
            Icon(
                imageVector = if (isVoiceMode) Icons.Default.Keyboard else Icons.Default.Mic,
                contentDescription = if (isVoiceMode) "切换到键盘" else "切换到语音",
                tint = Color.White
            )
        }

        if (isVoiceMode) {
            // 语音输入按钮
            Button(
                onClick = { /* 不处理单击事件 */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { 
                                // 按下时开始录音
                                onStartRecognition()
                                // 等待释放
                                try {
                                    awaitRelease()
                                } finally {
                                    // 释放时停止录音
                                    onStopRecognition()
                                }
                            }
                        )
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) Color(0xFFB9F6CA) else Color(0xFFE8F5E9),
                    contentColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = if (isListening) "正在收听..." else "按住说话",
                    fontSize = 16.sp
                )
            }
            
            // 显示识别到的文本并允许发送
            if (inputText.isNotEmpty()) {
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32))
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "发送",
                        tint = Color.White
                    )
                }
            }
        } else {
            // 文本输入框
            TextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("在这里输入或按住说话") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFE8F5E9),
                    focusedContainerColor = Color(0xFFE8F5E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // 发送按钮
            if (inputText.isNotEmpty()) {
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32))
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "发送",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long
)

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
} 