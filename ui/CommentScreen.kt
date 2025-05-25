package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Sort
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
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FloatingActionButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedTextField
import androidx.wear.compose.material.OutlinedTextFieldDefaults
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.Comment
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.CommentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 评论列表页面
 * 展示应用的评论列表并支持发表评论
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun CommentScreen(
    appId: String,
    appName: String,
    viewModel: CommentViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    // 加载评论列表
    LaunchedEffect(appId) {
        viewModel.loadComments(appId)
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 评论编辑弹窗状态
    var showCommentDialog by remember { mutableStateOf(false) }
    
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
                // 空实现，不显示系统滚动指示器
            },
            vignette = {
                // 空实现，不显示系统晕影效果
            },
            floatingActionButton = {
                // 添加评论按钮
                if (!uiState.isLoading && uiState.errorMessage.isEmpty() && !showCommentDialog) {
                    FloatingActionButton(
                        onClick = { showCommentDialog = true },
                        modifier = Modifier.padding(bottom = 8.dp),
                        backgroundColor = AppColors.Accent,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.comment_add),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
                            onRetry = { viewModel.loadComments(appId) }
                        )
                    }
                    
                    uiState.comments.isEmpty() -> {
                        EmptyView(
                            message = stringResource(R.string.comment_empty),
                            icon = Icons.Default.Comment
                        )
                    }
                    
                    else -> {
                        CommentContent(
                            appName = appName,
                            comments = uiState.comments,
                            averageRating = uiState.averageRating,
                            totalComments = uiState.totalComments,
                            onSortClick = { viewModel.toggleSortOrder() },
                            listState = listState
                        )
                    }
                }
                
                // 评论编辑弹窗
                if (showCommentDialog) {
                    CommentDialog(
                        onDismiss = { showCommentDialog = false },
                        onSubmit = { rating, content ->
                            viewModel.submitComment(appId, rating, content)
                            showCommentDialog = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 评论内容
 */
@Composable
fun CommentContent(
    appName: String,
    comments: List<Comment>,
    averageRating: Float,
    totalComments: Int,
    onSortClick: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp) // 为返回按钮留出空间
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 标题和评分汇总
            item {
                CommentHeader(
                    appName = appName,
                    averageRating = averageRating,
                    totalComments = totalComments,
                    onSortClick = onSortClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 评论列表
            items(comments) { comment ->
                CommentItem(comment = comment)
            }
            
            // 底部留白，为悬浮按钮腾出空间
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

/**
 * 评论页头部
 */
@Composable
fun CommentHeader(
    appName: String,
    averageRating: Float,
    totalComments: Int,
    onSortClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 应用名称
        Text(
            text = appName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 平均评分展示
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 评分
            Text(
                text = String.format("%.1f", averageRating),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 星级展示
            RatingBar(
                rating = averageRating,
                starSize = 16.dp
            )
        }
        
        // 评论总数
        Text(
            text = stringResource(R.string.comment_total_count, totalComments),
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 排序按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.BackgroundVariant)
                    .clickable(onClick = onSortClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = stringResource(R.string.comment_sort),
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = stringResource(R.string.comment_sort),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 评论项
 */
@Composable
fun CommentItem(comment: Comment) {
    // 日期格式化
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val formattedDate = remember(comment.createdAt) {
        dateFormat.format(Date(comment.createdAt))
    }
    
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
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            // 日期
            Text(
                text = formattedDate,
                color = AppColors.TextSecondary,
                fontSize = 10.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 评分
        RatingBar(
            rating = comment.rating.toFloat(),
            starSize = 12.dp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 评论内容
        Text(
            text = comment.content,
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 星级评分显示组件
 */
@Composable
fun RatingBar(
    rating: Float,
    starSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(5) { index ->
            val starAlpha = when {
                index < rating.toInt() -> 1f
                index == rating.toInt() && rating % 1 > 0 -> rating % 1
                else -> 0.3f
            }
            
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AppColors.Accent.copy(alpha = starAlpha),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

/**
 * 星级评分选择组件
 */
@Composable
fun RatingSelector(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    starSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.comment_rating_star, index + 1),
                tint = if (index < rating) AppColors.Accent else AppColors.TextSecondary.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRatingChange(index + 1) }
                    .padding(4.dp)
            )
        }
    }
}

/**
 * 评论编辑弹窗
 */
@Composable
fun CommentDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var content by remember { mutableStateOf("") }
    var isSubmitEnabled by remember { mutableStateOf(false) }
    
    // 检查评论是否有效
    LaunchedEffect(rating, content) {
        isSubmitEnabled = rating > 0 && content.isNotBlank()
    }
    
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
                text = stringResource(R.string.comment_write),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 评分选择
            Text(
                text = stringResource(R.string.comment_rating),
                color = Color.White,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            RatingSelector(
                rating = rating,
                onRatingChange = { rating = it },
                starSize = 24.dp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 评论内容输入
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.comment_content)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Accent,
                    unfocusedBorderColor = AppColors.TextSecondary,
                    focusedLabelColor = AppColors.Accent,
                    unfocusedLabelColor = AppColors.TextSecondary,
                    cursorColor = AppColors.Accent,
                    textColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                
                // 提交按钮
                Button(
                    onClick = { onSubmit(rating, content) },
                    enabled = isSubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.Accent,
                        contentColor = Color.White,
                        disabledBackgroundColor = AppColors.Accent.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_submit))
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
fun CommentScreenPreview() {
    SmartShopTheme {
        CommentScreen(
            appId = "1",
            appName = "示例应用",
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
fun CommentScreenRoundPreview() {
    SmartShopTheme {
        CommentScreen(
            appId = "1",
            appName = "示例应用",
            onNavigateBack = {}
        )
    }
} 