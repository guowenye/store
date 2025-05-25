package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

/**
 * 设置页面
 * 允许用户调整应用的各种设置
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    // 加载设置
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 清除缓存确认对话框状态
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
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
                // 空实现，不显示系统滚动指示器
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
                when {
                    uiState.isLoading -> {
                        LoadingView()
                    }
                    
                    uiState.errorMessage.isNotEmpty() -> {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { viewModel.loadSettings() }
                        )
                    }
                    
                    else -> {
                        SettingsContent(
                            isDarkMode = uiState.isDarkMode,
                            isNotificationsEnabled = uiState.isNotificationsEnabled,
                            cacheSize = uiState.cacheSize,
                            isClearingCache = uiState.isClearingCache,
                            onDarkModeChange = { viewModel.toggleDarkMode() },
                            onNotificationsChange = { viewModel.toggleNotifications() },
                            onClearCacheClick = { showClearCacheDialog = true },
                            scrollState = scrollState
                        )
                    }
                }
                
                // 清除缓存确认对话框
                if (showClearCacheDialog) {
                    ClearCacheConfirmDialog(
                        onConfirm = {
                            viewModel.clearCache()
                            showClearCacheDialog = false
                        },
                        onDismiss = { showClearCacheDialog = false }
                    )
                }
                
                // 清除缓存成功提示
                AnimatedVisibility(
                    visible = uiState.showCacheClearedMessage,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CacheClearedMessage(
                        onDismiss = { viewModel.dismissCacheClearedMessage() }
                    )
                }
            }
        }
    }
}

/**
 * 设置内容
 */
@Composable
fun SettingsContent(
    isDarkMode: Boolean,
    isNotificationsEnabled: Boolean,
    cacheSize: String,
    isClearingCache: Boolean,
    onDarkModeChange: () -> Unit,
    onNotificationsChange: () -> Unit,
    onClearCacheClick: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
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
            text = stringResource(R.string.settings_title),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 暗色模式设置
        SettingsSwitch(
            icon = Icons.Default.DarkMode,
            title = stringResource(R.string.settings_dark_mode),
            description = stringResource(R.string.settings_dark_mode_desc),
            isChecked = isDarkMode,
            onCheckedChange = onDarkModeChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 通知设置
        SettingsSwitch(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.settings_notifications),
            description = stringResource(R.string.settings_notifications_desc),
            isChecked = isNotificationsEnabled,
            onCheckedChange = onNotificationsChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 缓存管理
        SettingsAction(
            icon = Icons.Default.Storage,
            title = stringResource(R.string.settings_cache),
            description = stringResource(R.string.settings_cache_desc, cacheSize),
            actionContent = {
                Button(
                    onClick = onClearCacheClick,
                    enabled = !isClearingCache,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.BackgroundVariant,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(width = 100.dp, height = 32.dp)
                ) {
                    if (isClearingCache) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.button_clear),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        )
        
        // 底部留白
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 设置项带开关
 */
@Composable
fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onCheckedChange)
            .padding(16.dp)
    ) {
        // 图标
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.Accent,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 标题和描述
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 开关
        Switch(
            checked = isChecked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppColors.Accent,
                checkedTrackColor = AppColors.Accent.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}

/**
 * 设置项带操作按钮
 */
@Composable
fun SettingsAction(
    icon: ImageVector,
    title: String,
    description: String,
    actionContent: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .padding(16.dp)
    ) {
        // 图标
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.Accent,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 标题和描述
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 操作按钮
        actionContent()
    }
}

/**
 * 清除缓存确认对话框
 */
@Composable
fun ClearCacheConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(16.dp)
        ) {
            // 警告图标
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 标题
            Text(
                text = stringResource(R.string.settings_clear_cache_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 确认信息
            Text(
                text = stringResource(R.string.settings_clear_cache_message),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 按钮区
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 取消按钮
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_cancel))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 确认按钮
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.Accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_confirm))
                }
            }
        }
    }
}

/**
 * 清除缓存成功提示
 */
@Composable
fun CacheClearedMessage(
    onDismiss: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // 半透明背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(onClick = onDismiss)
        )
        
        // 提示内容
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(24.dp)
        ) {
            // 成功图标
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 提示文字
            Text(
                text = stringResource(R.string.settings_cache_cleared),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun SettingsScreenPreview() {
    SmartShopTheme {
        SettingsScreen(
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
fun SettingsScreenRoundPreview() {
    SmartShopTheme {
        SettingsScreen(
            onNavigateBack = {}
        )
    }
} 