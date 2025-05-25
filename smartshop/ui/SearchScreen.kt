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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TextField
import androidx.wear.compose.material.TextFieldDefaults
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.ui.components.EmptyView
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.components.SearchResultItem
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.SearchViewModel

/**
 * 搜索页面
 * 提供应用搜索功能
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit
) {
    // 界面状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 搜索关键词
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 焦点请求器
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // 自动聚焦搜索框
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.loadSearchHistory()
    }
    
    // 搜索处理
    val performSearch = {
        if (searchQuery.isNotEmpty()) {
            viewModel.search(searchQuery)
            keyboardController?.hide()
            focusManager.clearFocus()
        }
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
                // 搜索框
                SearchHeader(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = performSearch,
                    onClearQuery = { searchQuery = "" },
                    onBackClick = onNavigateBack,
                    focusRequester = focusRequester,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                
                // 内容区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp) // 为搜索框留出空间
                ) {
                    // 显示加载中
                    if (uiState.isLoading) {
                        LoadingView()
                    }
                    
                    // 显示错误
                    if (uiState.errorMessage.isNotEmpty() && !uiState.isLoading) {
                        ErrorView(
                            message = uiState.errorMessage,
                            onRetry = { viewModel.search(searchQuery) }
                        )
                    }
                    
                    // 搜索结果
                    if (!uiState.isLoading && uiState.errorMessage.isEmpty() && uiState.hasPerformedSearch) {
                        SearchResults(
                            apps = uiState.searchResults,
                            searchQuery = searchQuery,
                            onAppClick = onNavigateToAppDetail
                        )
                    }
                    
                    // 搜索历史
                    if (!uiState.isLoading && !uiState.hasPerformedSearch) {
                        SearchHistory(
                            history = uiState.searchHistory,
                            onHistoryItemClick = { 
                                searchQuery = it
                                viewModel.search(it)
                            },
                            onClearHistory = { viewModel.clearSearchHistory() }
                        )
                    }
                }
                
                // 刷新指示器
                AnimatedVisibility(
                    visible = uiState.isRefreshing,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 64.dp)
                            .size(24.dp),
                        color = AppColors.Accent,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
    
    // 保存搜索记录
    DisposableEffect(searchQuery) {
        onDispose {
            if (searchQuery.isNotEmpty() && uiState.hasPerformedSearch) {
                viewModel.addSearchHistory(searchQuery)
            }
        }
    }
}

/**
 * 搜索页面标题栏
 */
@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onBackClick: () -> Unit,
    focusRequester: FocusRequester,
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
            .padding(top = 8.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 返回按钮
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
            
            // 搜索输入框
            TextField(
                value = query,
                onValueChange = onQueryChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = AppColors.BackgroundVariant,
                    cursorColor = AppColors.Accent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_hint),
                        color = AppColors.TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = onClearQuery,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.button_clear),
                                tint = AppColors.TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .focusRequester(focusRequester)
            )
        }
    }
}

/**
 * 搜索结果
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResults(
    apps: List<App>,
    searchQuery: String,
    onAppClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    if (apps.isEmpty()) {
        EmptyView(
            message = stringResource(R.string.search_empty, searchQuery),
            icon = Icons.Default.Search,
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
            item {
                Text(
                    text = stringResource(R.string.search_results_for, searchQuery),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(apps) { app ->
                SearchResultItem(
                    app = app,
                    onClick = { onAppClick(app.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }
    }
}

/**
 * 搜索历史
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchHistory(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (history.isNotEmpty()) {
            // 标题和清除按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.search_history),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = stringResource(R.string.search_clear_history),
                    color = AppColors.Accent,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClearHistory)
                        .padding(4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 历史记录列表
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onRotaryScrollEvent { event ->
                        scrollState.scrollBy(event.verticalScrollPixels)
                        true
                    }
            ) {
                items(history) { item ->
                    HistoryItem(
                        query = item,
                        onClick = { onHistoryItemClick(item) }
                    )
                }
            }
        } else {
            // 无历史记录
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.search_no_history),
                        color = AppColors.TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 历史记录项
 */
@Composable
fun HistoryItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = query,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun SearchScreenPreview() {
    SmartShopTheme {
        SearchScreen(
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
fun SearchScreenRoundPreview() {
    SmartShopTheme {
        SearchScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
        )
    }
} 