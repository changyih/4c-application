package com.example.olderperson.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService

/**
 * 区域标题组件，带有语音播报功能
 */
@Composable
fun SectionTitle(
    title: String,
    textToSpeechService: TextToSpeechService,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "语音播报",
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .clickable { textToSpeechService.speak(title) }
        )
    }
} 