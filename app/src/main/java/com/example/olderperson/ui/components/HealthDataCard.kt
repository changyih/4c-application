package com.example.olderperson.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olderperson.R
import com.example.olderperson.data.HealthData
import com.example.olderperson.data.HealthDataType
import com.example.olderperson.ui.theme.*

@Composable
fun HealthDataCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    color: Color,
    normalRange: ClosedFloatingPointRange<Float> = 0f..100f,
    currentValue: Float = 0f,
    history: List<Float> = emptyList()
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Gray100
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = DarkText
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LightText
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 简易图表
            if (history.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        
                        // 绘制正常范围区域
                        val rangeMin = normalRange.start / 200f * canvasHeight
                        val rangeMax = normalRange.endInclusive / 200f * canvasHeight
                        
                        // 绘制图表路径
                        if (history.size > 1) {
                            val path = Path()
                            val pointWidth = canvasWidth / (history.size - 1)
                            
                            // 移动到第一个点
                            val firstY = canvasHeight - (history[0] / 200f * canvasHeight)
                            path.moveTo(0f, firstY)
                            
                            // 连接其余的点
                            for (i in 1 until history.size) {
                                val x = i * pointWidth
                                val y = canvasHeight - (history[i] / 200f * canvasHeight)
                                path.lineTo(x, y)
                            }
                            
                            // 绘制路径
                            drawPath(
                                path = path,
                                color = color,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        
                        // 绘制当前点
                        val currentY = canvasHeight - (currentValue / 200f * canvasHeight)
                        drawCircle(
                            color = color,
                            radius = 4.dp.toPx(),
                            center = Offset(canvasWidth, currentY)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthDataSection(
    healthData: Map<HealthDataType, HealthData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.health_data_title),
            style = MaterialTheme.typography.headlineSmall,
            color = DarkText,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 心率卡片
            val heartRate = healthData[HealthDataType.HEART_RATE]
            HealthDataCard(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                title = stringResource(R.string.heart_rate),
                value = heartRate?.value?.toInt()?.toString() ?: "0",
                unit = stringResource(R.string.bpm_unit),
                color = Red500,
                normalRange = 60f..100f,
                currentValue = heartRate?.value ?: 0f,
                history = listOf(75f, 78f, 72f, 80f, 76f)
            )
            
            // 血氧卡片
            val bloodOxygen = healthData[HealthDataType.BLOOD_OXYGEN]
            HealthDataCard(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                title = stringResource(R.string.blood_oxygen),
                value = bloodOxygen?.value?.toInt()?.toString() ?: "0",
                unit = stringResource(R.string.percent_unit),
                color = PurpleMain,
                normalRange = 95f..100f,
                currentValue = bloodOxygen?.value ?: 0f,
                history = listOf(98f, 97f, 99f, 98f, 98f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 血压卡片
            val systolic = healthData[HealthDataType.BLOOD_PRESSURE_SYSTOLIC]
            val diastolic = healthData[HealthDataType.BLOOD_PRESSURE_DIASTOLIC]
            HealthDataCard(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                title = stringResource(R.string.blood_pressure),
                value = "${systolic?.value?.toInt() ?: 0}/${diastolic?.value?.toInt() ?: 0}",
                unit = stringResource(R.string.mmhg_unit),
                color = OrangeMain,
                normalRange = 90f..140f,
                currentValue = systolic?.value ?: 0f,
                history = listOf(120f, 118f, 122f, 119f, 121f)
            )
            
            // 体温卡片
            val bodyTemp = healthData[HealthDataType.BODY_TEMPERATURE]
            HealthDataCard(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                title = "体温",
                value = bodyTemp?.value?.toString() ?: "0",
                unit = "°C",
                color = Green500,
                normalRange = 36f..37.2f,
                currentValue = bodyTemp?.value ?: 0f,
                history = listOf(36.5f, 36.6f, 36.7f, 36.5f, 36.6f)
            )
        }
    }
} 