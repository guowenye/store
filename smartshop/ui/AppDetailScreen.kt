package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.data.model.Comment
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.formatDownloadCount
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.AppDetailViewModel

/**
 * 应用详情页面
 * 展示应用的详细信息
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppDetailScreen(
    appId: String,
    viewModel: AppDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToComments: (String) -> Unit,
    onNavigateToScreenshot: (String, Int) -> Unit
) {
    // 加载应用详情
    LaunchedEffect(appId) {
        viewModel.loadAppDetail(appId)
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
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
                            onRetry = { viewModel.loadAppDetail(appId) }
                        )
                    }
                    
                    uiState.app != null -> {
                        AppDetailContent(
                            app = uiState.app!!,
                            comments = uiState.comments,
                            isFavorite = uiState.isFavorite,
                            downloadState = uiState.downloadState,
                            downloadProgress = uiState.downloadProgress,
                            onFavoriteClick = { viewModel.toggleFavorite() },
                            onDownloadClick = { viewModel.toggleDownload() },
                            onShareClick = { /* 分享功能 */ },
                            onScreenshotClick = { index ->
                                onNavigateToScreenshot(appId, index)
                            },
                            onViewAllCommentsClick = {
                                onNavigateToComments(appId)
                            },
                            scrollState = scrollState
                        )
                    }
                    
                    else -> {
                        EmptyView(
                            message = stringResource(R.string.error_loading_app_details),
                            icon = Icons.Default.Image
                        )
                    }
                }
            }
        }
    }
}

/**
 * 应用详情内容
 */
@Composable
fun AppDetailContent(
    app: App,
    comments: List<Comment>,
    isFavorite: Boolean,
    downloadState: AppDetailViewModel.DownloadState,
    downloadProgress: Float,
    onFavoriteClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit,
    onScreenshotClick: (Int) -> Unit,
    onViewAllCommentsClick: () -> Unit,
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
        
        // 应用基本信息
        AppHeader(
            app = app,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onShareClick = onShareClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 下载按钮
        DownloadButton(
            downloadState = downloadState,
            downloadProgress = downloadProgress,
            onClick = onDownloadClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 应用截图
        AppScreenshots(
            screenshots = app.screenshots,
            onScreenshotClick = onScreenshotClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 应用描述
        AppDescription(description = app.description)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 评论区
        if (comments.isNotEmpty()) {
            AppComments(
                comments = comments,
                onViewAllClick = onViewAllCommentsClick
            )
        }
        
        // 底部留白
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 应用头部信息
 */
@Composable
fun AppHeader(
    app: App,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 应用图标
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AppColors.Gradient1)
                .padding(2.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.app_placeholder),
                contentDescription = app.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 应用名称
        Text(
            text = app.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 开发者
        Text(
            text = app.developer,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 评分和下载信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 评分
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AppColors.Accent,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = String.format("%.1f", app.rating),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
            
            Text(
                text = " • ",
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
            
            // 下载量
            Text(
                text = formatDownloadCount(app.downloadCount),
                color = Color.White,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 收藏和分享按钮
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 收藏按钮
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppColors.BackgroundVariant)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.button_unfavorite else R.string.button_favorite
                    ),
                    tint = if (isFavorite) AppColors.Accent else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 分享按钮
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppColors.BackgroundVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.button_share),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 下载按钮
 */
@Composable
fun DownloadButton(
    downloadState: AppDetailViewModel.DownloadState,
    downloadProgress: Float,
    onClick: () -> Unit
) {
    val buttonColors = when (downloadState) {
        AppDetailViewModel.DownloadState.NOT_DOWNLOADED -> ButtonDefaults.buttonColors(
            backgroundColor = AppColors.Accent,
            contentColor = Color.White
        )
        AppDetailViewModel.DownloadState.DOWNLOADING -> ButtonDefaults.buttonColors(
            backgroundColor = AppColors.BackgroundVariant,
            contentColor = Color.White
        )
        AppDetailViewModel.DownloadState.DOWNLOADED -> ButtonDefaults.buttonColors(
            backgroundColor = Color.Green.copy(alpha = 0.7f),
            contentColor = Color.White
        )
        AppDetailViewModel.DownloadState.INSTALLED -> ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray,
            contentColor = Color.White
        )
    }
    
    val buttonText = when (downloadState) {
        AppDetailViewModel.DownloadState.NOT_DOWNLOADED -> stringResource(R.string.button_download)
        AppDetailViewModel.DownloadState.DOWNLOADING -> stringResource(R.string.button_installing)
        AppDetailViewModel.DownloadState.DOWNLOADED -> stringResource(R.string.button_install)
        AppDetailViewModel.DownloadState.INSTALLED -> stringResource(R.string.button_open)
    }
    
    Button(
        onClick = onClick,
        colors = buttonColors,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        if (downloadState == AppDetailViewModel.DownloadState.DOWNLOADING) {
            CircularProgressIndicator(
                progress = downloadProgress,
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Icon(
                imageVector = when (downloadState) {
                    AppDetailViewModel.DownloadState.NOT_DOWNLOADED -> Icons.Default.Download
                    AppDetailViewModel.DownloadState.DOWNLOADING -> Icons.Default.Download
                    AppDetailViewModel.DownloadState.DOWNLOADED -> Icons.Default.Download
                    AppDetailViewModel.DownloadState.INSTALLED -> Icons.Default.Download
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = buttonText,
            fontSize = 14.sp
        )
    }
}

/**
 * 应用截图
 */
@Composable
fun AppScreenshots(
    screenshots: List<String>,
    onScreenshotClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.app_screenshots),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (screenshots.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.BackgroundVariant)
            ) {
                Text(
                    text = stringResource(R.string.app_no_screenshots),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(screenshots.indices.toList()) { index ->
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.BackgroundVariant)
                            .clickable { onScreenshotClick(index) }
                    ) {
                        // 在实际应用中，这里应该使用Coil或类似库加载网络图片
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.screenshot_placeholder),
                            contentDescription = stringResource(R.string.app_screenshot_number, index + 1),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 应用描述
 */
@Composable
fun AppDescription(description: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.app_description),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 描述内容
        Text(
            text = description,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        
        // 展开/收起按钮
        if (description.length > 150) {
            Text(
                text = stringResource(if (expanded) R.string.app_description_collapse else R.string.app_description_expand),
                color = AppColors.Accent,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(top = 4.dp, bottom = 4.dp)
                    .align(Alignment.End),
            )
        }
    }
}

/**
 * 应用评论区
 */
@Composable
fun AppComments(
    comments: List<Comment>,
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 标题和查看全部
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.app_comments),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.home_see_all),
                color = AppColors.Accent,
                fontSize = 12.sp,
                modifier = Modifier.clickable(onClick = onViewAllClick)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 评论列表
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 只显示前2条评论
            comments.take(2).forEach { comment ->
                CommentItem(comment = comment)
            }
        }
    }
}

/**
 * 评论项
 */
@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .padding(12.dp)
    ) {
        // 用户信息和评分
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户名
            Text(
                text = comment.userId, // 实际应用中应显示用户名
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            
            // 评分
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < comment.rating) AppColors.Accent else AppColors.TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 评论内容
        Text(
            text = comment.content,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun AppDetailScreenPreview() {
    SmartShopTheme {
        AppDetailScreen(
            appId = "1",
            onNavigateBack = {},
            onNavigateToComments = {},
            onNavigateToScreenshot = { _, _ -> }
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
fun AppDetailScreenRoundPreview() {
    SmartShopTheme {
        AppDetailScreen(
            appId = "1",
            onNavigateBack = {},
            onNavigateToComments = {},
            onNavigateToScreenshot = { _, _ -> }
        )
    }
} 