package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.smartshop.data.model.Category
import com.smartshop.ui.components.AppCard
import com.smartshop.ui.components.CategoryChip
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

/**
 * 分类页面
 * 展示应用分类列表和分类应用
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = viewModel(),
    categoryId: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit
) {
    // 界面状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 加载数据
    LaunchedEffect(categoryId) {
        viewModel.loadData(categoryId)
    }
    
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 显示加载中
                if (uiState.isLoading) {
                    LoadingView()
                }
                
                // 显示错误
                if (uiState.errorMessage.isNotEmpty() && !uiState.isLoading) {
                    ErrorView(
                        message = uiState.errorMessage,
                        onRetry = { viewModel.loadData(categoryId) }
                    )
                }
                
                // 内容区域
                if (!uiState.isLoading && uiState.errorMessage.isEmpty()) {
                    CategoryContent(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        apps = uiState.categoryApps,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onAppClick = onNavigateToAppDetail,
                        onBackClick = onNavigateBack
                    )
                }
                
                // 标题栏
                CategoryHeader(
                    title = uiState.selectedCategory?.name ?: stringResource(R.string.categories_title),
                    onBackClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                
                // 刷新指示器
                AnimatedVisibility(
                    visible = uiState.isRefreshing,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 56.dp)
                            .size(24.dp),
                        color = AppColors.Accent,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

/**
 * 分类页面标题栏
 */
@Composable
fun CategoryHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF1A1A2E).copy(alpha = 0.9f),
                        Color(0xFF1A1A2E).copy(alpha = 0.7f),
                        Color(0xFF1A1A2E).copy(alpha = 0.0f)
                    )
                )
            )
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AppColors.BackgroundVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.button_back),
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            
            // 平衡布局的空间
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}

/**
 * 分类内容
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryContent(
    categories: List<Category>,
    selectedCategory: Category?,
    apps: List<App>,
    onCategorySelect: (Category) -> Unit,
    onAppClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val categoriesListState = rememberLazyListState()
    
    // 页面主内容
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp) // 为标题栏留出空间
    ) {
        // 分类标签行
        if (categories.isNotEmpty()) {
            LazyRow(
                state = categoriesListState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = category.id == selectedCategory?.id,
                        onClick = { onCategorySelect(category) }
                    )
                }
            }
        }
        
        // 分类应用列表
        if (apps.isEmpty() && selectedCategory != null) {
            EmptyView(
                message = stringResource(R.string.category_empty),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent { event ->
                        scrollState.scrollBy(event.verticalScrollPixels)
                        true
                    }
            ) {
                items(apps) { app ->
                    AppCard(
                        app = app,
                        onClick = { onAppClick(app.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
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
fun CategoryScreenPreview() {
    SmartShopTheme {
        CategoryScreen(
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
fun CategoryScreenRoundPreview() {
    SmartShopTheme {
        CategoryScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
        )
    }
} 