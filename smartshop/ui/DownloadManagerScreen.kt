package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.LinearProgressIndicator
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Tab
import androidx.wear.compose.material.TabDefaults
import androidx.wear.compose.material.TabRow
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.DownloadItem
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.formatFileSize
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.DownloadManagerViewModel
import kotlin.math.roundToInt

/**
 * 下载管理页面
 * 展示和管理应用下载任务
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun DownloadManagerScreen(
    viewModel: DownloadManagerViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit
) {
    // 加载下载任务列表
    LaunchedEffect(Unit) {
        viewModel.loadDownloads()
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 选中的标签页索引
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // 删除确认对话框状态
    var showDeleteDialog by remember { mutableStateOf<DownloadItem?>(null) }
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 滚动状态
    val listState = rememberLazyListState()
    
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
                if (!uiState.isLoading && uiState.errorMessage.isEmpty()) {
                    PositionIndicator(
                        scrollState = listState
                    )
                }
            },
            vignette = {
                // 空实现，不显示系统晕影效果
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent { event ->
                        val lazyListState = listState
                        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                        val firstVisibleItemScrollOffset = lazyListState.firstVisibleItemScrollOffset
                        
                        // 处理旋钮滚动
                        lazyListState.scrollToItem(
                            firstVisibleItemIndex,
                            firstVisibleItemScrollOffset - event.verticalScrollPixels.roundToInt()
                        )
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
                            onRetry = { viewModel.loadDownloads() }
                        )
                    }
                    
                    uiState.downloadingItems.isEmpty() && uiState.completedItems.isEmpty() -> {
                        EmptyView(
                            message = stringResource(R.string.download_empty),
                            icon = Icons.Default.Download
                        )
                    }
                    
                    else -> {
                        DownloadContent(
                            downloadingItems = uiState.downloadingItems,
                            completedItems = uiState.completedItems,
                            selectedTabIndex = selectedTabIndex,
                            onTabChange = { selectedTabIndex = it },
                            onPauseClick = { viewModel.pauseDownload(it.id) },
                            onResumeClick = { viewModel.resumeDownload(it.id) },
                            onCancelClick = { showDeleteDialog = it },
                            onDeleteClick = { showDeleteDialog = it },
                            onInstallClick = { viewModel.installApp(it.id) },
                            onOpenClick = { viewModel.openApp(it.packageName) },
                            onAppClick = { onNavigateToAppDetail(it.id) },
                            listState = listState
                        )
                    }
                }
                
                // 删除确认对话框
                showDeleteDialog?.let { downloadItem ->
                    DeleteConfirmDialog(
                        downloadItem = downloadItem,
                        onConfirm = {
                            viewModel.deleteDownload(downloadItem.id)
                            showDeleteDialog = null
                        },
                        onDismiss = { showDeleteDialog = null }
                    )
                }
            }
        }
    }
}

/**
 * 下载管理内容
 */
@Composable
fun DownloadContent(
    downloadingItems: List<DownloadItem>,
    completedItems: List<DownloadItem>,
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit,
    onPauseClick: (DownloadItem) -> Unit,
    onResumeClick: (DownloadItem) -> Unit,
    onCancelClick: (DownloadItem) -> Unit,
    onDeleteClick: (DownloadItem) -> Unit,
    onInstallClick: (DownloadItem) -> Unit,
    onOpenClick: (DownloadItem) -> Unit,
    onAppClick: (DownloadItem) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp) // 为返回按钮留出空间
    ) {
        // 标签页
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
            contentColor = AppColors.Accent,
            indicator = { tabPositions ->
                TabDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = AppColors.Accent
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // 下载中标签
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabChange(0) },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.download_downloading),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 已完成标签
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabChange(1) },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.download_completed),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // 内容区域
        when (selectedTabIndex) {
            0 -> { // 下载中
                if (downloadingItems.isEmpty()) {
                    EmptyView(
                        message = stringResource(R.string.download_no_downloading),
                        icon = Icons.Default.Download,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DownloadList(
                        items = downloadingItems,
                        isDownloading = true,
                        onPauseClick = onPauseClick,
                        onResumeClick = onResumeClick,
                        onCancelClick = onCancelClick,
                        onDeleteClick = onDeleteClick,
                        onInstallClick = onInstallClick,
                        onOpenClick = onOpenClick,
                        onAppClick = onAppClick,
                        listState = listState
                    )
                }
            }
            
            1 -> { // 已完成
                if (completedItems.isEmpty()) {
                    EmptyView(
                        message = stringResource(R.string.download_no_completed),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DownloadList(
                        items = completedItems,
                        isDownloading = false,
                        onPauseClick = onPauseClick,
                        onResumeClick = onResumeClick,
                        onCancelClick = onCancelClick,
                        onDeleteClick = onDeleteClick,
                        onInstallClick = onInstallClick,
                        onOpenClick = onOpenClick,
                        onAppClick = onAppClick,
                        listState = listState
                    )
                }
            }
        }
    }
}

/**
 * 下载列表
 */
@Composable
fun DownloadList(
    items: List<DownloadItem>,
    isDownloading: Boolean,
    onPauseClick: (DownloadItem) -> Unit,
    onResumeClick: (DownloadItem) -> Unit,
    onCancelClick: (DownloadItem) -> Unit,
    onDeleteClick: (DownloadItem) -> Unit,
    onInstallClick: (DownloadItem) -> Unit,
    onOpenClick: (DownloadItem) -> Unit,
    onAppClick: (DownloadItem) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            DownloadItem(
                downloadItem = item,
                isDownloading = isDownloading,
                onPauseClick = { onPauseClick(item) },
                onResumeClick = { onResumeClick(item) },
                onCancelClick = { onCancelClick(item) },
                onDeleteClick = { onDeleteClick(item) },
                onInstallClick = { onInstallClick(item) },
                onOpenClick = { onOpenClick(item) },
                onClick = { onAppClick(item) }
            )
        }
        
        // 底部留白
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 下载项
 */
@Composable
fun DownloadItem(
    downloadItem: DownloadItem,
    isDownloading: Boolean,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onInstallClick: () -> Unit,
    onOpenClick: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // 应用信息行
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 应用图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.Gradient1)
                    .padding(2.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.app_placeholder),
                    contentDescription = downloadItem.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 应用名称和状态
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = downloadItem.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // 状态或大小信息
                Text(
                    text = when {
                        isDownloading -> {
                            if (downloadItem.isPaused) {
                                stringResource(R.string.download_paused)
                            } else {
                                stringResource(
                                    R.string.download_progress_info,
                                    formatFileSize(downloadItem.downloadedBytes),
                                    formatFileSize(downloadItem.totalBytes)
                                )
                            }
                        }
                        downloadItem.isInstalled -> stringResource(R.string.download_installed)
                        else -> formatFileSize(downloadItem.totalBytes)
                    },
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 操作按钮
            if (isDownloading) {
                // 下载中状态的操作按钮
                if (downloadItem.isPaused) {
                    IconButton(
                        onClick = onResumeClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.download_resume),
                            tint = AppColors.Accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = stringResource(R.string.download_pause),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = onCancelClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.download_cancel),
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // 已完成状态的操作按钮
                Button(
                    onClick = if (downloadItem.isInstalled) onOpenClick else onInstallClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (downloadItem.isInstalled) Color.Green.copy(alpha = 0.8f) else AppColors.Accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(28.dp)
                        .width(60.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (downloadItem.isInstalled) R.string.button_open else R.string.button_install
                        ),
                        fontSize = 10.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.download_delete),
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        
        // 进度条（仅在下载中且未暂停时显示）
        if (isDownloading && !downloadItem.isPaused) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // 动画进度值
            val animatedProgress by animateFloatAsState(
                targetValue = downloadItem.progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )
            
            LinearProgressIndicator(
                progress = animatedProgress,
                color = AppColors.Accent,
                backgroundColor = AppColors.BackgroundVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 下载速度和剩余时间
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.download_speed,
                        formatFileSize(downloadItem.speedBytesPerSecond)
                    ),
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp
                )
                
                Text(
                    text = stringResource(
                        R.string.download_remaining_time,
                        formatRemainingTime(downloadItem.remainingTimeMillis)
                    ),
                    color = AppColors.TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * 删除确认对话框
 */
@Composable
fun DeleteConfirmDialog(
    downloadItem: DownloadItem,
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
                text = stringResource(R.string.download_delete_confirm_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 确认信息
            Text(
                text = stringResource(
                    R.string.download_delete_confirm_message,
                    downloadItem.name
                ),
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
                        backgroundColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_delete))
                }
            }
        }
    }
}

/**
 * 格式化剩余时间
 */
fun formatRemainingTime(millis: Long): String {
    if (millis <= 0) return "0s"
    
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun DownloadManagerScreenPreview() {
    SmartShopTheme {
        DownloadManagerScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
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
fun DownloadManagerScreenRoundPreview() {
    SmartShopTheme {
        DownloadManagerScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
        )
    }
} 