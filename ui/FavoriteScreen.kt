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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.FavoriteApp
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.FavoriteViewModel
import kotlin.math.roundToInt

/**
 * 收藏列表页面
 * 展示用户收藏的应用列表
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit
) {
    // 加载收藏列表
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 取消收藏确认对话框状态
    var showUnfavoriteDialog by remember { mutableStateOf<FavoriteApp?>(null) }
    
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
                if (!uiState.isLoading && uiState.errorMessage.isEmpty() && uiState.favorites.isNotEmpty()) {
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
                            onRetry = { viewModel.loadFavorites() }
                        )
                    }
                    
                    uiState.favorites.isEmpty() -> {
                        EmptyView(
                            message = stringResource(R.string.favorite_empty),
                            icon = Icons.Default.Favorite
                        )
                    }
                    
                    else -> {
                        FavoriteContent(
                            favorites = uiState.favorites,
                            onAppClick = onNavigateToAppDetail,
                            onUnfavoriteClick = { showUnfavoriteDialog = it },
                            listState = listState
                        )
                    }
                }
                
                // 取消收藏确认对话框
                showUnfavoriteDialog?.let { app ->
                    UnfavoriteConfirmDialog(
                        app = app,
                        onConfirm = {
                            viewModel.unfavoriteApp(app.id)
                            showUnfavoriteDialog = null
                        },
                        onDismiss = { showUnfavoriteDialog = null }
                    )
                }
            }
        }
    }
}

/**
 * 收藏列表内容
 */
@Composable
fun FavoriteContent(
    favorites: List<FavoriteApp>,
    onAppClick: (String) -> Unit,
    onUnfavoriteClick: (FavoriteApp) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp) // 为返回按钮留出空间
    ) {
        // 标题
        Text(
            text = stringResource(R.string.favorite_title),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        // 收藏列表
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(favorites) { app ->
                FavoriteItem(
                    app = app,
                    onClick = { onAppClick(app.id) },
                    onUnfavoriteClick = { onUnfavoriteClick(app) }
                )
            }
            
            // 底部留白
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * 收藏应用项
 */
@Composable
fun FavoriteItem(
    app: FavoriteApp,
    onClick: () -> Unit,
    onUnfavoriteClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        // 应用图标
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColors.Gradient1)
                .padding(2.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_placeholder),
                contentDescription = app.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 应用信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 应用名称
            Text(
                text = app.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 开发者
            Text(
                text = app.developer,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 评分
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Accent,
                    modifier = Modifier.size(12.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = String.format("%.1f", app.rating),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        
        // 取消收藏按钮
        IconButton(
            onClick = onUnfavoriteClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.button_unfavorite),
                tint = Color.Red.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * 取消收藏确认对话框
 */
@Composable
fun UnfavoriteConfirmDialog(
    app: FavoriteApp,
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
                text = stringResource(R.string.favorite_unfavorite_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 确认信息
            Text(
                text = stringResource(R.string.favorite_unfavorite_message, app.name),
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
fun FavoriteScreenPreview() {
    SmartShopTheme {
        FavoriteScreen(
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
fun FavoriteScreenRoundPreview() {
    SmartShopTheme {
        FavoriteScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
        )
    }
} 