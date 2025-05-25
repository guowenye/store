package com.smartshop.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 应用颜色定义
 */
object AppColors {
    // 主色调
    val Primary = Color(0xFF3F51B5)
    val PrimaryVariant = Color(0xFF303F9F)
    val Accent = Color(0xFF00BCD4)
    
    // 渐变色
    val Gradient1 = Color(0xFF512DA8)
    val Gradient2 = Color(0xFF2196F3)
    
    // 背景色
    val Background = Color(0xFF121212)
    val BackgroundVariant = Color(0xFF1E1E1E)
    
    // 文本颜色
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB3B3B3)
    
    // 边框和分隔线
    val Divider = Color(0xFF2A2A2A)
    
    // 状态颜色
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFF44336)
    val Warning = Color(0xFFFF9800)
    val Info = Color(0xFF2196F3)
}

// 主色系 - 科技蓝色系列
val CyberBlue = Color(0xFF00A8FF)
val CyberBlueLight = Color(0xFF61DDFF)
val CyberBlueDark = Color(0xFF0071CC)
val CyberBlueBackground = Color(0x1000A8FF)

// 辅助色系 - 霓虹紫色系列
val NeonPurple = Color(0xFFAE00FF)
val NeonPurpleLight = Color(0xFFE254FF)
val NeonPurpleDark = Color(0xFF7800CC)
val NeonPurpleBackground = Color(0x10AE00FF)

// 点缀色系 - 科技绿色系列
val TechGreen = Color(0xFF00FF94)
val TechGreenLight = Color(0xFF70FFBF)
val TechGreenDark = Color(0xFF00CC78)
val TechGreenBackground = Color(0x1000FF94)

// 语义色 - 错误/警告/成功
val ErrorRed = Color(0xFFFF3B30)
val ErrorRedLight = Color(0xFFFF6B67)
val ErrorRedDark = Color(0xFFCC1E00)
val WarningYellow = Color(0xFFFFD60A)
val SuccessGreen = Color(0xFF30D158)

// 背景色 - 暗色主题
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkBottomBar = Color(0xFF252525)
val DarkCardBackground = Color(0xFF2A2A2A)

// 背景色 - 亮色主题
val LightBackground = Color(0xFFF8F8F8)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEAEAEA)
val LightBottomBar = Color(0xFFEEEEEE)
val LightCardBackground = Color(0xFFFFFFFF)

// 文本色
val TextBlack = Color(0xFF121212)
val TextDarkGray = Color(0xFF4A4A4A)
val TextGray = Color(0xFF909090)
val TextLightGray = Color(0xFFBBBBBB)
val TextWhite = Color(0xFFF5F5F5)
val White = Color(0xFFFFFFFF)

// 渐变色
val GradientStart = CyberBlue
val GradientCenter = NeonPurple
val GradientEnd = TechGreen

// 毛玻璃效果蒙层
val GlassMorphismBackground = Color(0x22FFFFFF)
val DarkOverlay = Color(0x66000000)

// 应用状态色
val DownloadingColor = CyberBlue
val InstalledColor = TechGreen
val PausedColor = WarningYellow
val FailedColor = ErrorRed 