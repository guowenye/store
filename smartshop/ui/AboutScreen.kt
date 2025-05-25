package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import kotlin.math.roundToInt

/**
 * 关于页面
 * 展示应用版本信息、开发者信息和使用条款等内容
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    appVersion: String = "1.0.0",
    buildNumber: String = "1"
) {
    // 使用条款对话框状态
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 滚动状态
    val scrollState = rememberScrollState()
    
    // SwipeToDismiss状态，用于支持手表的边缘手势返回
    val swipeState = rememberSwipeToDismissBoxState()
    
    // 获取应用上下文
    val context = LocalContext.current
    
    SwipeToDismissBox(
        state = swipeState,
        backgroundKey = Unit,
        onDismissed = { onNavigateBack() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground),
            timeText = {
                // 空实现，不显示系统时间
            },
            positionIndicator = {
                // 空实现，不显示系统位置指示器
            },
            vignette = {
                // 空实现，不显示系统晕影效果
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent { event ->
                        scrollState.scrollBy(event.verticalScrollPixels)
                        true
                    }
            ) {
                // 返回按钮
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.BackgroundVariant.copy(alpha = 0.7f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.button_back),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // 主内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // 顶部留白，为返回按钮腾出空间
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // 标题
                    Text(
                        text = stringResource(R.string.about_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 应用Logo
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF3A4BD8),
                                            Color(0xFF8F45ED)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo),
                                contentDescription = stringResource(R.string.app_logo_content_description),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 应用名称
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 版本信息
                    Text(
                        text = stringResource(R.string.about_version_info, appVersion, buildNumber),
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 关于信息
                    AboutInfoItem(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.about_description),
                        content = stringResource(R.string.about_description_content)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 开发者信息
                    AboutInfoItem(
                        icon = Icons.Default.Code,
                        title = stringResource(R.string.about_developer),
                        content = stringResource(R.string.app_developer)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 联系邮箱
                    AboutInfoItem(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.about_contact),
                        content = stringResource(R.string.about_contact_email)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 使用条款
                    AboutClickableItem(
                        icon = Icons.Default.Description,
                        title = stringResource(R.string.about_terms),
                        onClick = { showTermsDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 隐私政策
                    AboutClickableItem(
                        icon = Icons.Default.Security,
                        title = stringResource(R.string.about_privacy),
                        onClick = { showPrivacyDialog = true }
                    )
                    
                    // 底部留白
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 使用条款对话框
                if (showTermsDialog) {
                    TermsDialog(
                        onDismiss = { showTermsDialog = false }
                    )
                }
                
                // 隐私政策对话框
                if (showPrivacyDialog) {
                    PrivacyDialog(
                        onDismiss = { showPrivacyDialog = false }
                    )
                }
            }
        }
    }
}

/**
 * 关于信息项
 */
@Composable
fun AboutInfoItem(
    icon: ImageVector,
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .padding(16.dp)
    ) {
        // 标题栏
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.Accent,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 标题
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 内容
        Text(
            text = content,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

/**
 * 关于可点击项
 */
@Composable
fun AboutClickableItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // 图标
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.Accent,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 标题
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 使用条款对话框
 */
@Composable
fun TermsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题
            Text(
                text = stringResource(R.string.about_terms),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 内容
            Text(
                text = stringResource(R.string.about_terms_content),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 关闭按钮
            androidx.wear.compose.material.Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.button_close))
            }
        }
    }
}

/**
 * 隐私政策对话框
 */
@Composable
fun PrivacyDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题
            Text(
                text = stringResource(R.string.about_privacy),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 内容
            Text(
                text = stringResource(R.string.about_privacy_content),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 关闭按钮
            androidx.wear.compose.material.Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.button_close))
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun AboutScreenPreview() {
    SmartShopTheme {
        AboutScreen(
            onNavigateBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=195dp,height=195dp,shape=circle",
    name = "圆形手表预览"
)
@Composable
fun AboutScreenRoundPreview() {
    SmartShopTheme {
        AboutScreen(
            onNavigateBack = {}
        )
    }
} 