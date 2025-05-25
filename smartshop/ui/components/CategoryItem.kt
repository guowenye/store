package com.smartshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.smartshop.R
import com.smartshop.data.model.Category
import com.smartshop.ui.theme.AppColors

/**
 * 分类项组件
 */
@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 获取分类颜色并转换为Color
    val categoryColor = Color(category.color.toInt())
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            categoryColor,
            categoryColor.copy(alpha = 0.7f)
        )
    )
    
    Card(
        onClick = onClick,
        modifier = modifier.size(72.dp),
        colors = CardDefaults.cardColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(gradientBrush)
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // 分类图标
                Icon(
                    imageVector = Icons.Filled.Spa, // 这里使用临时图标，实际应根据分类图标动态加载
                    contentDescription = category.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                
                // 分类名称
                Text(
                    text = category.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
} 