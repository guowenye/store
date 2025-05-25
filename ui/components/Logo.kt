package com.smartshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.smartshop.ui.theme.AppColors

/**
 * 应用Logo组件
 */
@Composable
fun Logo(
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    // 霓虹渐变背景
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            AppColors.Gradient1,
            AppColors.Gradient2
        )
    )
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ShoppingCart,
            contentDescription = "Smart应用商店",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
} 