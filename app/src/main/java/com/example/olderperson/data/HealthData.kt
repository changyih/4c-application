package com.example.olderperson.data

import java.util.Date

data class HealthData(
    val type: HealthDataType,
    val value: Float,
    val timestamp: Date = Date()
)

enum class HealthDataType {
    BLOOD_PRESSURE_SYSTOLIC,
    BLOOD_PRESSURE_DIASTOLIC,
    HEART_RATE,
    BODY_TEMPERATURE,
    BLOOD_OXYGEN
} 