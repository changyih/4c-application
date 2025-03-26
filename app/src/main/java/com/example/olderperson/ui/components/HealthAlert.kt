package com.example.olderperson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.olderperson.R
import com.example.olderperson.data.HealthData
import com.example.olderperson.data.HealthDataType
import com.example.olderperson.ui.theme.Green500
import com.example.olderperson.ui.theme.Red500

data class HealthWarning(
    val type: HealthDataType,
    val message: String,
    val isUrgent: Boolean
)

@Composable
fun HealthAlertCard(
    healthData: Map<HealthDataType, HealthData>,
    modifier: Modifier = Modifier
) {
    val warnings = checkHealthWarnings(healthData)
    val hasWarnings = warnings.isNotEmpty()
    val isUrgent = warnings.any { it.isUrgent }
    
    val backgroundColor = when {
        isUrgent -> Red500.copy(alpha = 0.1f)
        hasWarnings -> MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
        else -> Green500.copy(alpha = 0.1f)
    }
    
    val borderColor = when {
        isUrgent -> Red500
        hasWarnings -> MaterialTheme.colorScheme.error
        else -> Green500
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
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
                text = stringResource(R.string.health_warning),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hasWarnings) {
                        warnings.forEach { warning ->
                            Text(
                                text = warning.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (warning.isUrgent) Red500 else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.normal_status),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Green500
                        )
                    }
                }
            }
        }
    }
}

fun checkHealthWarnings(healthData: Map<HealthDataType, HealthData>): List<HealthWarning> {
    val warnings = mutableListOf<HealthWarning>()
    
    // 检查心率
    healthData[HealthDataType.HEART_RATE]?.let { heartRateData ->
        when {
            heartRateData.value > 100 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.HEART_RATE,
                        message = "心率偏高 (${heartRateData.value.toInt()} 次/分钟)",
                        isUrgent = heartRateData.value > 120
                    )
                )
            }
            heartRateData.value < 60 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.HEART_RATE,
                        message = "心率偏低 (${heartRateData.value.toInt()} 次/分钟)",
                        isUrgent = heartRateData.value < 50
                    )
                )
            }
        }
    }
    
    // 检查血氧
    healthData[HealthDataType.BLOOD_OXYGEN]?.let { bloodOxygenData ->
        if (bloodOxygenData.value < 95) {
            warnings.add(
                HealthWarning(
                    type = HealthDataType.BLOOD_OXYGEN,
                    message = "血氧偏低 (${bloodOxygenData.value.toInt()}%)",
                    isUrgent = bloodOxygenData.value < 90
                )
            )
        }
    }
    
    // 检查血压
    val systolic = healthData[HealthDataType.BLOOD_PRESSURE_SYSTOLIC]?.value
    val diastolic = healthData[HealthDataType.BLOOD_PRESSURE_DIASTOLIC]?.value
    
    if (systolic != null && diastolic != null) {
        when {
            systolic > 140 || diastolic > 90 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.BLOOD_PRESSURE_SYSTOLIC,
                        message = "血压偏高 (${systolic.toInt()}/${diastolic.toInt()} mmHg)",
                        isUrgent = systolic > 160 || diastolic > 100
                    )
                )
            }
            systolic < 90 || diastolic < 60 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.BLOOD_PRESSURE_SYSTOLIC,
                        message = "血压偏低 (${systolic.toInt()}/${diastolic.toInt()} mmHg)",
                        isUrgent = systolic < 80 || diastolic < 50
                    )
                )
            }
        }
    }
    
    // 检查体温
    healthData[HealthDataType.BODY_TEMPERATURE]?.let { bodyTempData ->
        when {
            bodyTempData.value > 37.3 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.BODY_TEMPERATURE,
                        message = "体温偏高 (${bodyTempData.value} °C)",
                        isUrgent = bodyTempData.value > 38.5
                    )
                )
            }
            bodyTempData.value < 36.0 -> {
                warnings.add(
                    HealthWarning(
                        type = HealthDataType.BODY_TEMPERATURE,
                        message = "体温偏低 (${bodyTempData.value} °C)",
                        isUrgent = bodyTempData.value < 35.0
                    )
                )
            }
        }
    }
    
    return warnings
} 