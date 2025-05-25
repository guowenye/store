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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.smartshop.R
import com.smartshop.data.model.App
import com.smartshop.data.model.Category
import com.smartshop.ui.components.AppCard
import com.smartshop.ui.components.CategoryItem
import com.smartshop.ui.components.ErrorView
import com.smartshop.ui.components.LoadingView
import com.smartshop.ui.components.SearchBar
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.HomeViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import kotlinx.coroutines.delay

/**
 * 主页选项卡枚举
 */
enum class HomeTab {
    HOME, CATEGORIES, RANKING, PROFILE
}

/**
 * 主页屏幕
 * 应用展示和导航中心
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSearch: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToAllCategories: () -> Unit
) {
    // 界面状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 当前选中的标签页
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 加载数据
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    // SwipeToDismiss状态，用于支持手表的边缘手势返回
    val swipeState = rememberSwipeToDismissBoxState()
    
    SwipeToDismissBox(
        state = swipeState,
        backgroundKey = selectedTab,
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
                if (uiState.isLoading && !uiState.isRefreshing) {
                    LoadingView()
                }
                
                // 显示错误
                if (uiState.errorMessage.isNotEmpty() && uiState.featuredApps.isEmpty()) {
                    ErrorView(
                        message = uiState.errorMessage,
                        onRetry = { viewModel.loadData() }
                    )
                }
                
                // 内容区域
                if (!uiState.isLoading || uiState.isRefreshing || uiState.featuredApps.isNotEmpty()) {
                    when (selectedTab) {
                        HomeTab.HOME -> HomeTabContent(
                            uiState = uiState,
                            onRefresh = { viewModel.loadData(true) },
                            onAppClick = onNavigateToAppDetail,
                            onCategoryClick = onNavigateToCategory,
                            onSearchClick = onNavigateToSearch,
                            onSeeAllCategoriesClick = onNavigateToAllCategories,
                            onSeeAllFeaturedClick = onNavigateToRanking
                        )
                        HomeTab.CATEGORIES -> {
                            // 这里可以直接导航到分类页，或者显示分类内容
                            LaunchedEffect(Unit) {
                                onNavigateToAllCategories()
                            }
                        }
                        HomeTab.RANKING -> {
                            // 这里可以直接导航到排行页，或者显示排行内容
                            LaunchedEffect(Unit) {
                                onNavigateToRanking()
                            }
                        }
                        HomeTab.PROFILE -> {
                            // 这里可以直接导航到个人中心页，或者显示个人中心内容
                            LaunchedEffect(Unit) {
                                onNavigateToProfile()
                            }
                        }
                    }
                }
                
                // 底部导航栏
                BottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                
                // 下拉刷新指示器
                AnimatedVisibility(
                    visible = uiState.isRefreshing,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
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
 * 主页标签内容
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeTabContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onAppClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSeeAllCategoriesClick: () -> Unit,
    onSeeAllFeaturedClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // 下拉刷新自动完成
    LaunchedEffect(uiState.isRefreshing) {
        if (uiState.isRefreshing) {
            delay(2000) // 模拟刷新耗时
            onRefresh()
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .onRotaryScrollEvent { event ->
                scrollState.scrollBy(event.verticalScrollPixels)
                true
            },
        contentPadding = PaddingValues(bottom = 64.dp) // 为底部导航留出空间
    ) {
        item {
            // 搜索栏
            SearchBar(
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // 特色应用轮播
        if (uiState.featuredApps.isNotEmpty()) {
            item {
                FeaturedAppsCarousel(
                    apps = uiState.featuredApps,
                    onAppClick = onAppClick,
                    onSeeAllClick = onSeeAllFeaturedClick,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        // 分类区
        if (uiState.categories.isNotEmpty()) {
            item {
                CategorySection(
                    categories = uiState.categories,
                    onCategoryClick = onCategoryClick,
                    onSeeAllClick = onSeeAllCategoriesClick,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        // 新上架应用
        if (uiState.newApps.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.home_new_apps),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                AppsList(
                    apps = uiState.newApps,
                    onAppClick = onAppClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        
        // 推荐应用
        if (uiState.recommendedApps.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.home_recommended),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                AppsList(
                    apps = uiState.recommendedApps,
                    onAppClick = onAppClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * 特色应用轮播
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedAppsCarousel(
    apps: List<App>,
    onAppClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.home_featured),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.home_see_all),
                color = AppColors.Accent,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onSeeAllClick)
                    .padding(4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val pagerState = rememberPagerState()
        
        // 自动轮播
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % apps.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
        
        HorizontalPager(
            count = apps.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 16.dp)
        ) { page ->
            FeaturedAppItem(
                app = apps[page],
                onClick = { onAppClick(apps[page].id) },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = AppColors.Accent,
            inactiveColor = AppColors.Divider,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp)
        )
    }
}

/**
 * 特色应用项
 */
@Composable
fun FeaturedAppItem(
    app: App,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.BackgroundVariant)
            .clickable(onClick = onClick)
    ) {
        // 应用封面图
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_placeholder),
            contentDescription = app.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // 渐变遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC000000)
                        ),
                        startY = 0f,
                        endY = 300f
                    )
                )
        )
        
        // 应用信息
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = app.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = app.developer,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // 评分
                RatingBar(
                    rating = app.rating,
                    modifier = Modifier.height(16.dp)
                )
                
                Text(
                    text = String.format("%.1f", app.rating),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                // 下载量
                Text(
                    text = formatDownloadCount(app.downloadCount),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * 格式化下载次数
 */
fun formatDownloadCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
        count >= 1_000 -> String.format("%.1fK", count / 1_000f)
        else -> count.toString()
    } + "次下载"
}

/**
 * 评分条
 */
@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val fullStars = rating.toInt()
        val partialStar = rating - fullStars
        
        repeat(5) { index ->
            val starColor = when {
                index < fullStars -> AppColors.Accent
                index == fullStars && partialStar > 0 -> AppColors.Accent.copy(alpha = partialStar)
                else -> AppColors.Divider
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (index < 4) 2.dp else 0.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Star,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 分类区
 */
@Composable
fun CategorySection(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.home_categories),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.home_see_all),
                color = AppColors.Accent,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onSeeAllClick)
                    .padding(4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 显示前4个分类作为网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(160.dp)
        ) {
            items(categories.take(4)) { category ->
                CategoryItem(
                    category = category,
                    onClick = { onCategoryClick(category.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 应用列表
 */
@Composable
fun AppsList(
    apps: List<App>,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier
    ) {
        items(apps) { app ->
            AppCard(
                app = app,
                onClick = { onAppClick(app.id) },
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
            )
        }
    }
}

/**
 * 小节标题
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/**
 * 底部导航栏
 */
@Composable
fun BottomNavigation(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xCC1A1A2E)
                    )
                )
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            NavigationItem(
                icon = Icons.Outlined.Home,
                label = stringResource(R.string.nav_home),
                selected = selectedTab == HomeTab.HOME,
                onClick = { onTabSelected(HomeTab.HOME) }
            )
            
            NavigationItem(
                icon = Icons.Outlined.Apps,
                label = stringResource(R.string.nav_categories),
                selected = selectedTab == HomeTab.CATEGORIES,
                onClick = { onTabSelected(HomeTab.CATEGORIES) }
            )
            
            NavigationItem(
                icon = Icons.Outlined.Leaderboard,
                label = stringResource(R.string.nav_ranking),
                selected = selectedTab == HomeTab.RANKING,
                onClick = { onTabSelected(HomeTab.RANKING) }
            )
            
            NavigationItem(
                icon = Icons.Outlined.AccountCircle,
                label = stringResource(R.string.nav_profile),
                selected = selectedTab == HomeTab.PROFILE,
                onClick = { onTabSelected(HomeTab.PROFILE) }
            )
        }
    }
}

/**
 * 导航项
 */
@Composable
fun NavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) AppColors.Accent else AppColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = label,
            color = if (selected) AppColors.Accent else AppColors.TextSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun HomeScreenPreview() {
    SmartShopTheme {
        HomeScreen(
            onNavigateToSearch = {},
            onNavigateToAppDetail = {},
            onNavigateToCategory = {},
            onNavigateToProfile = {},
            onNavigateToRanking = {},
            onNavigateToAllCategories = {}
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
fun HomeScreenRoundPreview() {
    SmartShopTheme {
        HomeScreen(
            onNavigateToSearch = {},
            onNavigateToAppDetail = {},
            onNavigateToCategory = {},
            onNavigateToProfile = {},
            onNavigateToRanking = {},
            onNavigateToAllCategories = {}
        )
    }
} 