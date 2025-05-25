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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.wear.compose.material.Tab
import androidx.wear.compose.material.TabRow
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.ui.components.AppCard
import com.smartshop.ui.components.AppRankItem
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.RankingViewModel

/**
 * 排行榜类型
 */
enum class RankingType {
    POPULAR, NEW, RATING
}

/**
 * 排行页面
 * 展示热门应用、新上架应用和好评应用排行
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RankingScreen(
    viewModel: RankingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit
) {
    // 界面状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 当前选中的排行类型
    var selectedRankingType by remember { mutableStateOf(RankingType.POPULAR) }
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 加载数据
    LaunchedEffect(selectedRankingType) {
        when (selectedRankingType) {
            RankingType.POPULAR -> viewModel.loadPopularApps()
            RankingType.NEW -> viewModel.loadNewApps()
            RankingType.RATING -> viewModel.loadTopRatedApps()
        }
    }
    
    // SwipeToDismiss状态，用于支持手表的边缘手势返回
    val swipeState = rememberSwipeToDismissBoxState()
    
    SwipeToDismissBox(
        state = swipeState,
        backgroundKey = selectedRankingType,
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
                // 标题栏
                RankingHeader(
                    title = stringResource(R.string.ranking_title),
                    onBackClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                
                // 选项卡
                RankingTabs(
                    selectedRankingType = selectedRankingType,
                    onTabSelected = { selectedRankingType = it },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 56.dp)
                )
                
                // 显示加载中
                if (uiState.isLoading) {
                    LoadingView()
                }
                
                // 显示错误
                if (uiState.errorMessage.isNotEmpty() && !uiState.isLoading) {
                    ErrorView(
                        message = uiState.errorMessage,
                        onRetry = {
                            when (selectedRankingType) {
                                RankingType.POPULAR -> viewModel.loadPopularApps()
                                RankingType.NEW -> viewModel.loadNewApps()
                                RankingType.RATING -> viewModel.loadTopRatedApps()
                            }
                        }
                    )
                }
                
                // 内容区域
                if (!uiState.isLoading && uiState.errorMessage.isEmpty()) {
                    val apps = when (selectedRankingType) {
                        RankingType.POPULAR -> uiState.popularApps
                        RankingType.NEW -> uiState.newApps
                        RankingType.RATING -> uiState.topRatedApps
                    }
                    
                    RankingContent(
                        apps = apps,
                        rankingType = selectedRankingType,
                        onAppClick = onNavigateToAppDetail,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 104.dp) // 为标题栏和选项卡留出空间
                    )
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
                            .padding(top = 104.dp)
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
 * 排行页面标题栏
 */
@Composable
fun RankingHeader(
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
 * 排行标签页
 */
@Composable
fun RankingTabs(
    selectedRankingType: RankingType,
    onTabSelected: (RankingType) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedRankingType.ordinal,
        indicator = { tabPositions ->
            // 不显示默认指示器
        },
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        RankingType.values().forEach { rankingType ->
            val isSelected = selectedRankingType == rankingType
            
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(rankingType) },
                modifier = Modifier.padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .background(
                            color = if (isSelected) AppColors.Accent.copy(alpha = 0.2f) else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (rankingType) {
                            RankingType.POPULAR -> stringResource(R.string.ranking_popular)
                            RankingType.NEW -> stringResource(R.string.ranking_new)
                            RankingType.RATING -> stringResource(R.string.ranking_rating)
                        },
                        color = if (isSelected) AppColors.Accent else Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * 排行内容
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RankingContent(
    apps: List<App>,
    rankingType: RankingType,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .onRotaryScrollEvent { event ->
                scrollState.scrollBy(event.verticalScrollPixels)
                true
            }
    ) {
        itemsIndexed(apps) { index, app ->
            AppRankItem(
                app = app,
                ranking = index + 1,
                onClick = { onAppClick(app.id) },
                showDownloadCount = rankingType == RankingType.POPULAR,
                showRating = rankingType == RankingType.RATING,
                showUpdateTime = rankingType == RankingType.NEW,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
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
fun RankingScreenPreview() {
    SmartShopTheme {
        RankingScreen(
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
fun RankingScreenRoundPreview() {
    SmartShopTheme {
        RankingScreen(
            onNavigateBack = {},
            onNavigateToAppDetail = {}
        )
    }
} 