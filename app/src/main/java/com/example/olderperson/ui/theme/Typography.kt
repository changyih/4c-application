package com.example.olderperson.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 全局字体大小配置类
object FontSizeConfig {
    private var _fontSize = mutableStateOf(1.0f) // 1.0为默认大小
    val fontSize = _fontSize
    
    fun setFontSize(size: Float) {
        _fontSize.value = size
    }
    
    // 根据当前字体大小比例获取实际尺寸
    fun scaledSp(baseSp: Int): Int {
        return (baseSp * fontSize.value).toInt()
    }

    // 提供一个函数来获取按比例缩放的TextStyle
    fun getScaledTextStyle(
        baseStyle: TextStyle,
        scaleFactor: Float = fontSize.value
    ): TextStyle {
        return baseStyle.copy(
            fontSize = (baseStyle.fontSize.value * scaleFactor).sp
        )
    }
}

// 基础Typography - 这是标准大小
val BaseTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// 提供一个函数来获取缩放后的Typography
fun getScaledTypography(): Typography {
    val scaleFactor = FontSizeConfig.fontSize.value
    
    return Typography(
        bodyLarge = FontSizeConfig.getScaledTextStyle(BaseTypography.bodyLarge),
        bodyMedium = FontSizeConfig.getScaledTextStyle(BaseTypography.bodyMedium),
        bodySmall = FontSizeConfig.getScaledTextStyle(BaseTypography.bodySmall),
        titleLarge = FontSizeConfig.getScaledTextStyle(BaseTypography.titleLarge),
        titleMedium = FontSizeConfig.getScaledTextStyle(BaseTypography.titleMedium),
        titleSmall = FontSizeConfig.getScaledTextStyle(BaseTypography.titleSmall),
        labelLarge = FontSizeConfig.getScaledTextStyle(BaseTypography.labelLarge),
        labelMedium = FontSizeConfig.getScaledTextStyle(BaseTypography.labelMedium),
        labelSmall = FontSizeConfig.getScaledTextStyle(BaseTypography.labelSmall)
    )
} 