package com.smartshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.Typography
import androidx.wear.compose.material.darkColorPalette

// 应用颜色主题
private val DarkColorPalette = darkColorPalette(
    primary = AppColors.Primary,
    primaryVariant = AppColors.PrimaryVariant,
    secondary = AppColors.Accent,
    background = AppColors.Background,
    surface = AppColors.BackgroundVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = AppColors.Error,
    onError = Color.White
)

// 自定义颜色
object AppColors {
    val Accent = Color(0xFF2AECFF)              // 霓虹青色强调
    val Gradient1 = Color(0xFF58B7FF)           // 渐变起始色
    val Gradient2 = Color(0xFF7F5AF0)           // 渐变中间色
    val Gradient3 = Color(0xFF2AECFF)           // 渐变结束色
    val Background = Color(0xFF121212)          // 主背景
    val BackgroundVariant = Color(0xFF1E1E1E)   // 次要背景
    val DarkSurface = Color(0xFF252525)         // 深色表面
    val TextPrimary = Color.White               // 主要文本
    val TextSecondary = Color(0xFFAAAAAA)       // 次要文本
    val Divider = Color(0xFF2A2A2A)             // 分隔线
    val Primary = Color(0xFF58B7FF)              // 主要颜色
    val PrimaryVariant = Color(0xFF7F5AF0)        // 主要变体颜色
    val Error = Color(0xFFFF5757)                // 错误颜色
}

// 暗色主题配色方案（默认）
private val DarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    onPrimary = White,
    primaryContainer = CyberBlueDark,
    onPrimaryContainer = CyberBlueLight,
    secondary = NeonPurple,
    onSecondary = White,
    secondaryContainer = NeonPurpleDark,
    onSecondaryContainer = NeonPurpleLight,
    tertiary = TechGreen,
    onTertiary = White,
    tertiaryContainer = TechGreenDark,
    onTertiaryContainer = TechGreenLight,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedDark,
    onErrorContainer = ErrorRedLight,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGray
)

// 亮色主题配色方案（备选）
private val LightColorScheme = lightColorScheme(
    primary = CyberBlue,
    onPrimary = White,
    primaryContainer = CyberBlueLight,
    onPrimaryContainer = CyberBlueDark,
    secondary = NeonPurple,
    onSecondary = White,
    secondaryContainer = NeonPurpleLight,
    onSecondaryContainer = NeonPurpleDark,
    tertiary = TechGreen,
    onTertiary = White,
    tertiaryContainer = TechGreenLight,
    onTertiaryContainer = TechGreenDark,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRedDark,
    background = LightBackground,
    onBackground = TextBlack,
    surface = LightSurface,
    onSurface = TextBlack,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextDarkGray
)

// 自定义主题数据
data class SmartShopThemeConfig(
    val isDarkTheme: Boolean,
    val isHighContrast: Boolean = false,
    val isRoundScreen: Boolean = true
)

// 创建CompositionLocal，用于在组合树中共享主题配置
val LocalSmartShopThemeConfig = compositionLocalOf { 
    SmartShopThemeConfig(isDarkTheme = true)
}

/**
 * Smart应用商店主题
 */
@Composable
fun SmartShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 动态颜色可用于Android 12+
    dynamicColor: Boolean = false,
    // 高对比度模式
    highContrast: Boolean = false,
    // 是否为圆形屏幕（默认为圆形，适配大多数智能手表）
    roundScreen: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    // 创建主题配置
    val themeConfig = SmartShopThemeConfig(
        isDarkTheme = darkTheme,
        isHighContrast = highContrast,
        isRoundScreen = roundScreen
    )
    
    CompositionLocalProvider(LocalSmartShopThemeConfig provides themeConfig) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}