package com.example.olderperson.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.Primary

/**
 * 城市选择器组件
 * 
 * @param currentCity 当前选择的城市
 * @param cityList 可选城市列表
 * @param onCitySelected 城市选择回调
 * @param textToSpeechService 文字转语音服务，用于播报选择的城市
 */
@Composable
fun CitySelector(
    currentCity: String,
    cityList: List<String>,
    onCitySelected: (String) -> Unit,
    textToSpeechService: TextToSpeechService? = null
) {
    var showCityDialog by remember { mutableStateOf(false) }
    
    // 城市选择器按钮
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { showCityDialog = true }
    ) {
        Text(
            text = currentCity,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "选择城市"
        )
    }
    
    // 城市选择对话框
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("选择城市") },
            text = {
                Column {
                    cityList.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (city != currentCity) {
                                        onCitySelected(city)
                                        // 播报切换城市的语音提示
                                        textToSpeechService?.speak("正在切换到${city}天气")
                                    }
                                    showCityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (city == currentCity) Primary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = city,
                                fontSize = 16.sp,
                                fontWeight = if (city == currentCity) FontWeight.Bold else FontWeight.Normal,
                                color = if (city == currentCity) Primary else Color.Black
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
} 