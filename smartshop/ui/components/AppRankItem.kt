package com.smartshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.ui.formatDownloadCount
import com.smartshop.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 排行榜应用项组件
 */
@Composable
fun AppRankItem(
    app: App,
    ranking: Int,
    onClick: () -> Unit,
    showDownloadCount: Boolean = true,
    showRating: Boolean = false,
    showUpdateTime: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        // 排名
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when (ranking) {
                        1 -> Color(0xFFFFD700) // 金色
                        2 -> Color(0xFFC0C0C0) // 银色
                        3 -> Color(0xFFCD7F32) // 铜色
                        else -> AppColors.Divider
                    }
                )
        ) {
            Text(
                text = ranking.toString(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 应用图标
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppColors.Gradient1)
                .padding(2.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_placeholder),
                contentDescription = app.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 应用信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = app.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = app.developer,
                color = AppColors.TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 根据不同类型显示不同信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showDownloadCount) {
                    Text(
                        text = formatDownloadCount(app.downloadCount),
                        color = AppColors.TextSecondary,
                        fontSize = 10.sp
                    )
                }
                
                if (showRating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AppColors.Accent,
                        modifier = Modifier.size(12.dp)
                    )
                    
                    Text(
                        text = String.format("%.1f", app.rating),
                        color = AppColors.TextSecondary,
                        fontSize = 10.sp
                    )
                }
                
                if (showUpdateTime) {
                    Text(
                        text = formatUpdateTime(app.updatedAt),
                        color = AppColors.TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * 格式化更新时间
 */
@Composable
fun formatUpdateTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 3600000 -> "刚刚更新" // 1小时内
        diff < 86400000 -> "${diff / 3600000}小时前" // 24小时内
        diff < 2592000000 -> "${diff / 86400000}天前" // 30天内
        else -> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
} 