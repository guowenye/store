package com.smartshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartshop.data.model.ReportType
import com.smartshop.ui.AboutScreen
import com.smartshop.ui.AppDetailScreen
import com.smartshop.ui.CategoryScreen
import com.smartshop.ui.CommentScreen
import com.smartshop.ui.DownloadManagerScreen
import com.smartshop.ui.EmailVerificationScreen
import com.smartshop.ui.FavoriteScreen
import com.smartshop.ui.HomeScreen
import com.smartshop.ui.LoginScreen
import com.smartshop.ui.ProfileScreen
import com.smartshop.ui.RankingScreen
import com.smartshop.ui.RegisterScreen
import com.smartshop.ui.ReportScreen
import com.smartshop.ui.SearchScreen
import com.smartshop.ui.SettingsScreen
import com.smartshop.ui.SplashScreen
import com.smartshop.ui.theme.SmartShopTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用主Activity
 * 设置应用主题和导航结构
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartShopTheme {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = "splash") {
                    // 启动页
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateToHome = { navController.navigate("home") { popUpTo("splash") { inclusive = true } } }
                        )
                    }
                    
                    // 登录页
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = { navController.navigate("register") },
                            onNavigateToHome = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
                        )
                    }
                    
                    // 注册页
                    composable("register") {
                        RegisterScreen(
                            onNavigateToVerification = { email -> 
                                navController.navigate("email_verification/$email")
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    // 邮箱验证页
                    composable(
                        "email_verification/{email}",
                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        EmailVerificationScreen(
                            email = email,
                            onVerificationSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                            onSkip = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
                        )
                    }
                    
                    // 主页
                    composable("home") {
                        HomeScreen(
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToCategory = { categoryId, categoryName -> 
                                navController.navigate("category/$categoryId/$categoryName") 
                            },
                            onNavigateToRanking = { navController.navigate("ranking") },
                            onNavigateToAppDetail = { appId -> 
                                navController.navigate("app_detail/$appId") 
                            },
                            onNavigateToProfile = { navController.navigate("profile") }
                        )
                    }
                    
                    // 分类页
                    composable(
                        "category/{categoryId}/{categoryName}",
                        arguments = listOf(
                            navArgument("categoryId") { type = NavType.StringType },
                            navArgument("categoryName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        CategoryScreen(
                            categoryId = categoryId,
                            categoryName = categoryName,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToAppDetail = { appId -> 
                                navController.navigate("app_detail/$appId") 
                            }
                        )
                    }
                    
                    // 排行页
                    composable("ranking") {
                        RankingScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToAppDetail = { appId -> 
                                navController.navigate("app_detail/$appId") 
                            }
                        )
                    }
                    
                    // 搜索页
                    composable("search") {
                        SearchScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToAppDetail = { appId -> 
                                navController.navigate("app_detail/$appId") 
                            }
                        )
                    }
                    
                    // 应用详情页
                    composable(
                        "app_detail/{appId}",
                        arguments = listOf(navArgument("appId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val appId = backStackEntry.arguments?.getString("appId") ?: ""
                        AppDetailScreen(
                            appId = appId,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToComments = { appId, appName -> 
                                navController.navigate("comments/$appId/$appName") 
                            },
                            onNavigateToReport = { appId, appName ->
                                navController.navigate("report/app/$appId/$appName")
                            }
                        )
                    }
                    
                    // 评论页
                    composable(
                        "comments/{appId}/{appName}",
                        arguments = listOf(
                            navArgument("appId") { type = NavType.StringType },
                            navArgument("appName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val appId = backStackEntry.arguments?.getString("appId") ?: ""
                        val appName = backStackEntry.arguments?.getString("appName") ?: ""
                        CommentScreen(
                            appId = appId,
                            appName = appName,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToReport = { commentId, commentPreview ->
                                navController.navigate("report/comment/$commentId/$commentPreview")
                            }
                        )
                    }
                    
                    // 下载管理页
                    composable("download_manager") {
                        DownloadManagerScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    // 个人中心页
                    composable("profile") {
                        ProfileScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToFavorites = { navController.navigate("favorites") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToAbout = { navController.navigate("about") },
                            onNavigateToLogin = { 
                                navController.navigate("login") { 
                                    popUpTo("home") { inclusive = false } 
                                } 
                            }
                        )
                    }
                    
                    // 收藏列表页
                    composable("favorites") {
                        FavoriteScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToAppDetail = { appId -> 
                                navController.navigate("app_detail/$appId") 
                            }
                        )
                    }
                    
                    // 设置页
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    // 关于页
                    composable("about") {
                        AboutScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    
                    // 举报页
                    composable(
                        "report/{type}/{targetId}/{targetName}",
                        arguments = listOf(
                            navArgument("type") { type = NavType.StringType },
                            navArgument("targetId") { type = NavType.StringType },
                            navArgument("targetName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val type = backStackEntry.arguments?.getString("type") ?: "app"
                        val targetId = backStackEntry.arguments?.getString("targetId") ?: ""
                        val targetName = backStackEntry.arguments?.getString("targetName") ?: ""
                        
                        val reportType = if (type == "app") ReportType.APP else ReportType.COMMENT
                        
                        ReportScreen(
                            reportType = reportType,
                            targetId = targetId,
                            targetName = targetName,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
} 