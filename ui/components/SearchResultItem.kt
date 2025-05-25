package com.smartshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.ui.formatDownloadCount
import com.smartshop.ui.theme.AppColors

/**
 * 搜索结果项组件
 */
@Composable
fun SearchResultItem(
    app: App,
    onClick: () -> Unit,
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
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 应用信息
        Column(modifier = Modifier.weight(1f)) {
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
                overflow = TextOverflow.Ellipsis
            )
            
            // 评分和下载信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // 评分
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Accent,
                    modifier = Modifier.size(12.dp)
                )
                
                Text(
                    text = String.format("%.1f", app.rating),
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 2.dp)
                )
                
                Text(
                    text = " • ",
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp
                )
                
                // 下载量
                Text(
                    text = formatDownloadCount(app.downloadCount),
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
} 