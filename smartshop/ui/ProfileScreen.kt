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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.layout.ContentScale
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.ProfileViewModel
import kotlin.math.roundToInt

/**
 * 个人中心页面
 * 显示用户信息和各种设置入口
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit
) {
    // 加载用户信息
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 退出登录确认对话框状态
    var showLogoutDialog by remember { mutableStateOf(false) }
    
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
                            onRetry = { viewModel.loadUserProfile() }
                        )
                    }
                    
                    uiState.user != null -> {
                        ProfileContent(
                            username = uiState.user!!.username,
                            email = uiState.user!!.email,
                            isVerified = uiState.user!!.isVerified,
                            favoriteCount = uiState.favoriteCount,
                            onFavoritesClick = onNavigateToFavorites,
                            onSettingsClick = onNavigateToSettings,
                            onAboutClick = onNavigateToAbout,
                            onLogoutClick = { showLogoutDialog = true },
                            scrollState = scrollState
                        )
                    }
                    
                    else -> {
                        EmptyView(
                            message = stringResource(R.string.profile_not_logged_in),
                            icon = Icons.Default.Person
                        )
                    }
                }
                
                // 退出登录确认对话框
                if (showLogoutDialog) {
                    LogoutConfirmDialog(
                        onConfirm = {
                            viewModel.logout()
                            onLogout()
                            showLogoutDialog = false
                        },
                        onDismiss = { showLogoutDialog = false }
                    )
                }
            }
        }
    }
}

/**
 * 个人中心内容
 */
@Composable
fun ProfileContent(
    username: String,
    email: String,
    isVerified: Boolean,
    favoriteCount: Int,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
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
        
        // 用户头像和信息
        UserHeader(
            username = username,
            email = email,
            isVerified = isVerified
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 收藏入口
        MenuOption(
            icon = Icons.Default.Favorite,
            title = stringResource(R.string.profile_favorites),
            subtitle = stringResource(R.string.profile_favorites_count, favoriteCount),
            onClick = onFavoritesClick
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 设置入口
        MenuOption(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.profile_settings),
            subtitle = stringResource(R.string.profile_settings_desc),
            onClick = onSettingsClick
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 关于入口
        MenuOption(
            icon = Icons.Default.Info,
            title = stringResource(R.string.profile_about),
            subtitle = stringResource(R.string.profile_about_desc),
            onClick = onAboutClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 退出登录按钮
        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red.copy(alpha = 0.7f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = stringResource(R.string.profile_logout),
                fontSize = 14.sp
            )
        }
        
        // 底部留白
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 用户头像和信息
 */
@Composable
fun UserHeader(
    username: String,
    email: String,
    isVerified: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 用户头像
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AppColors.Gradient1,
                            AppColors.Gradient2
                        )
                    )
                )
                .padding(2.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 用户名
        Text(
            text = username,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 邮箱
        Text(
            text = email,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 验证状态
        Text(
            text = stringResource(
                if (isVerified) R.string.profile_verified else R.string.profile_not_verified
            ),
            color = if (isVerified) Color.Green else Color.Yellow,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 菜单选项
 */
@Composable
fun MenuOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 图标
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = AppColors.Accent,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 标题和副标题
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
                text = subtitle,
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
        }
        
        // 箭头
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * 退出登录确认对话框
 */
@Composable
fun LogoutConfirmDialog(
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
            // 标题
            Text(
                text = stringResource(R.string.profile_logout_confirm_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 确认信息
            Text(
                text = stringResource(R.string.profile_logout_confirm_message),
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
                        backgroundColor = Color.Red.copy(alpha = 0.7f),
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun ProfileScreenPreview() {
    SmartShopTheme {
        ProfileScreen(
            onNavigateBack = {},
            onNavigateToFavorites = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {},
            onLogout = {}
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
fun ProfileScreenRoundPreview() {
    SmartShopTheme {
        ProfileScreen(
            onNavigateBack = {},
            onNavigateToFavorites = {},
            onNavigateToSettings = {},
            onNavigateToAbout = {},
            onLogout = {}
        )
    }
} 