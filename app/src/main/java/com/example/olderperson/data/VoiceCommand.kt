package com.example.olderperson.data

data class VoiceCommand(
    val text: String,
    val type: CommandType
)

enum class CommandType {
    HEALTH_CHECK,
    EMERGENCY,
    WEATHER,
    REMINDER,
    UNKNOWN
} 