package com.example.olderperson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.example.olderperson.ui.theme.FreshGreen

/**
 * 顶部栏
 */
@Composable
fun TopBar(
    title: String,
    onBackClick: () -> Unit = {},
    textToSpeechService: TextToSpeechService
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { 
                textToSpeechService.speak("返回")
                onBackClick() 
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 用户头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(FreshGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "用户头像",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 