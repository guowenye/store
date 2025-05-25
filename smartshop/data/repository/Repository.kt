package com.smartshop.data.repository

import com.smartshop.data.api.HomeData
import com.smartshop.data.model.App
import com.smartshop.data.model.Category
import com.smartshop.data.model.Comment
import com.smartshop.data.model.Download
import com.smartshop.data.model.PagedResponse
import com.smartshop.data.model.RankingType
import com.smartshop.data.model.Report
import com.smartshop.data.model.ReportType
import com.smartshop.data.model.SearchResult
import com.smartshop.data.model.User
import com.smartshop.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * 应用数据仓库接口
 * 定义所有数据操作方法
 */
interface Repository {
    
    // ===== 用户相关 =====
    
    /**
     * 用户登录
     */
    suspend fun login(email: String, password: String): Result<User>
    
    /**
     * 用户注册
     */
    suspend fun register(username: String, email: String, password: String): Result<User>
    
    /**
     * 验证邮箱验证码
     */
    suspend fun verifyEmail(email: String, code: String): Result<Boolean>
    
    /**
     * 获取当前用户信息
     */
    suspend fun getUserProfile(): Result<User>
    
    /**
     * 获取用户设置
     */
    fun getUserSettings(): Flow<UserSettings>
    
    /**
     * 更新用户设置
     */
    suspend fun updateUserSettings(settings: UserSettings): Result<Boolean>
    
    /**
     * 退出登录
     */
    suspend fun logout(): Result<Boolean>
    
    /**
     * 检查登录状态
     */
    fun isLoggedIn(): Flow<Boolean>
    
    // ===== 应用相关 =====
    
    /**
     * 获取首页数据
     */
    suspend fun getHomeData(): Result<HomeData>
    
    /**
     * 获取所有分类
     */
    suspend fun getCategories(): Result<List<Category>>
    
    /**
     * 获取分类下的应用列表
     */
    suspend fun getCategoryApps(categoryId: String, page: Int): Result<PagedResponse<App>>
    
    /**
     * 获取排行榜应用
     */
    suspend fun getRankingApps(type: RankingType, page: Int): Result<PagedResponse<App>>
    
    /**
     * 获取应用详情
     */
    suspend fun getAppDetail(appId: String): Result<App>
    
    /**
     * 获取应用评论
     */
    suspend fun getAppComments(appId: String, page: Int): Result<PagedResponse<Comment>>
    
    /**
     * 发表评论
     */
    suspend fun postComment(appId: String, rating: Int, content: String): Result<Comment>
    
    /**
     * 搜索应用
     */
    suspend fun searchApps(keyword: String, page: Int): Result<SearchResult>
    
    /**
     * 收藏/取消收藏应用
     */
    suspend fun toggleFavorite(appId: String, favorite: Boolean): Result<Boolean>
    
    /**
     * 获取收藏的应用列表
     */
    suspend fun getFavoriteApps(page: Int): Result<PagedResponse<App>>
    
    /**
     * 提交举报
     */
    suspend fun submitReport(
        reportType: ReportType,
        targetId: String,
        reason: String,
        description: String
    ): Result<Report>
    
    // ===== 下载相关 =====
    
    /**
     * 开始下载应用
     */
    suspend fun downloadApp(app: App): Result<Download>
    
    /**
     * 获取下载列表
     */
    fun getDownloads(): Flow<List<Download>>
    
    /**
     * 获取下载进度
     */
    fun getDownloadProgress(downloadId: String): Flow<Float>
    
    /**
     * 暂停下载
     */
    suspend fun pauseDownload(downloadId: String): Result<Boolean>
    
    /**
     * 恢复下载
     */
    suspend fun resumeDownload(downloadId: String): Result<Boolean>
    
    /**
     * 取消下载
     */
    suspend fun cancelDownload(downloadId: String): Result<Boolean>
    
    /**
     * 安装应用
     */
    suspend fun installApp(downloadId: String): Result<Boolean>
} 