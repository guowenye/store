package com.smartshop.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.smartshop.data.api.ApiService
import com.smartshop.data.api.HomeData
import com.smartshop.data.model.App
import com.smartshop.data.model.Category
import com.smartshop.data.model.Comment
import com.smartshop.data.model.Download
import com.smartshop.data.model.DownloadStatus
import com.smartshop.data.model.PagedResponse
import com.smartshop.data.model.RankingType
import com.smartshop.data.model.Report
import com.smartshop.data.model.ReportType
import com.smartshop.data.model.SearchResult
import com.smartshop.data.model.User
import com.smartshop.data.model.UserSettings
import com.smartshop.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 仓库实现类
 * 实现所有数据操作方法
 */
@Singleton
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : Repository {
    
    // 本地存储的共享偏好
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME, Context.MODE_PRIVATE
    )
    
    // 登录状态流
    private val _isLoggedIn = MutableStateFlow(sharedPreferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false))
    
    // 用户设置流
    private val _userSettings = MutableStateFlow(
        UserSettings(
            darkMode = sharedPreferences.getBoolean("dark_mode", true),
            notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true),
            autoUpdate = sharedPreferences.getBoolean("auto_update", true),
            downloadOverWifiOnly = sharedPreferences.getBoolean("download_over_wifi_only", true),
            language = sharedPreferences.getString("language", "zh_CN") ?: "zh_CN"
        )
    )
    
    // 下载列表
    private val _downloads = MutableStateFlow<List<Download>>(emptyList())
    
    // ===== 用户相关 =====
    
    override suspend fun login(email: String, password: String): Result<User> = try {
        val response = apiService.login(email, password)
        if (response.success && response.data != null) {
            // 保存登录状态和用户信息
            saveUserToken(response.data.id)
            _isLoggedIn.value = true
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "登录失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun register(username: String, email: String, password: String): Result<User> = try {
        val response = apiService.register(username, email, password)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "注册失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun verifyEmail(email: String, code: String): Result<Boolean> = try {
        val response = apiService.verifyEmail(email, code)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "验证失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getUserProfile(): Result<User> = try {
        if (!_isLoggedIn.value) {
            return Result.failure(Exception("用户未登录"))
        }
        
        val response = apiService.getUserProfile()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取用户信息失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override fun getUserSettings(): Flow<UserSettings> = flow {
        emit(_userSettings.value)
    }
    
    override suspend fun updateUserSettings(settings: UserSettings): Result<Boolean> = try {
        // 更新本地设置
        sharedPreferences.edit().apply {
            putBoolean("dark_mode", settings.darkMode)
            putBoolean("notifications_enabled", settings.notificationsEnabled)
            putBoolean("auto_update", settings.autoUpdate)
            putBoolean("download_over_wifi_only", settings.downloadOverWifiOnly)
            putString("language", settings.language)
            apply()
        }
        
        _userSettings.value = settings
        
        // 同步到服务器
        if (_isLoggedIn.value) {
            val settingsMap = mapOf(
                "darkMode" to settings.darkMode,
                "notificationsEnabled" to settings.notificationsEnabled,
                "autoUpdate" to settings.autoUpdate,
                "downloadOverWifiOnly" to settings.downloadOverWifiOnly,
                "language" to settings.language
            )
            
            val response = apiService.updateUserSettings(settingsMap)
            if (response.success) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.message ?: "更新设置失败"))
            }
        } else {
            Result.success(true)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun logout(): Result<Boolean> = try {
        // 清除本地登录状态
        sharedPreferences.edit().apply {
            remove(Constants.PREF_USER_TOKEN)
            remove(Constants.PREF_USER_ID)
            putBoolean(Constants.PREF_IS_LOGGED_IN, false)
            apply()
        }
        
        _isLoggedIn.value = false
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override fun isLoggedIn(): Flow<Boolean> = flow {
        emit(_isLoggedIn.value)
    }
    
    // ===== 应用相关 =====
    
    override suspend fun getHomeData(): Result<HomeData> = try {
        val response = apiService.getHomeData()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取首页数据失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getCategories(): Result<List<Category>> = try {
        val response = apiService.getCategories()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取分类失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getCategoryApps(categoryId: String, page: Int): Result<PagedResponse<App>> = try {
        val response = apiService.getCategoryApps(categoryId, page)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取分类应用失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getRankingApps(type: RankingType, page: Int): Result<PagedResponse<App>> = try {
        val response = apiService.getRankingApps(type, page)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取排行榜应用失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getAppDetail(appId: String): Result<App> = try {
        val response = apiService.getAppDetail(appId)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取应用详情失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getAppComments(appId: String, page: Int): Result<PagedResponse<Comment>> = try {
        val response = apiService.getAppComments(appId, page)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取评论失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun postComment(appId: String, rating: Int, content: String): Result<Comment> = try {
        if (!_isLoggedIn.value) {
            return Result.failure(Exception("用户未登录"))
        }
        
        val response = apiService.postComment(appId, rating, content)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "发表评论失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun searchApps(keyword: String, page: Int): Result<SearchResult> = try {
        val response = apiService.searchApps(keyword, page)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "搜索失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun toggleFavorite(appId: String, favorite: Boolean): Result<Boolean> = try {
        if (!_isLoggedIn.value) {
            return Result.failure(Exception("用户未登录"))
        }
        
        val response = apiService.toggleFavorite(appId, favorite)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "操作失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getFavoriteApps(page: Int): Result<PagedResponse<App>> = try {
        if (!_isLoggedIn.value) {
            return Result.failure(Exception("用户未登录"))
        }
        
        val response = apiService.getFavoriteApps(page)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "获取收藏失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun submitReport(
        reportType: ReportType, 
        targetId: String, 
        reason: String, 
        description: String
    ): Result<Report> = try {
        if (!_isLoggedIn.value) {
            return Result.failure(Exception("用户未登录"))
        }
        
        val reportTypeStr = if (reportType == ReportType.APP) "app" else "comment"
        val response = apiService.submitReport(reportTypeStr, targetId, reason, description)
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message ?: "提交举报失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    // ===== 下载相关 =====
    
    override suspend fun downloadApp(app: App): Result<Download> = withContext(Dispatchers.IO) {
        try {
            // 创建下载记录
            val downloadId = UUID.randomUUID().toString()
            val download = Download(
                id = downloadId,
                appId = app.id,
                appName = app.name,
                appIcon = app.icon,
                downloadUrl = app.downloadUrl,
                startTime = Date(),
                progress = 0f,
                status = DownloadStatus.PENDING,
                fileSize = app.size,
                downloadedSize = 0
            )
            
            // 添加到下载列表
            val currentDownloads = _downloads.value.toMutableList()
            currentDownloads.add(download)
            _downloads.value = currentDownloads
            
            // 模拟下载过程
            // 注意：实际应用中应使用DownloadManager或自定义下载服务
            simulateDownload(download)
            
            Result.success(download)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getDownloads(): Flow<List<Download>> = flow {
        emit(_downloads.value)
    }
    
    override fun getDownloadProgress(downloadId: String): Flow<Float> = flow {
        val download = _downloads.value.find { it.id == downloadId }
        if (download != null) {
            emit(download.progress)
        } else {
            emit(0f)
        }
    }
    
    override suspend fun pauseDownload(downloadId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == downloadId }
            
            if (index != -1) {
                val download = currentDownloads[index].copy(status = DownloadStatus.PAUSED)
                currentDownloads[index] = download
                _downloads.value = currentDownloads
                Result.success(true)
            } else {
                Result.failure(Exception("下载不存在"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resumeDownload(downloadId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == downloadId }
            
            if (index != -1) {
                val download = currentDownloads[index].copy(status = DownloadStatus.DOWNLOADING)
                currentDownloads[index] = download
                _downloads.value = currentDownloads
                
                // 继续下载
                simulateDownload(download)
                
                Result.success(true)
            } else {
                Result.failure(Exception("下载不存在"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelDownload(downloadId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == downloadId }
            
            if (index != -1) {
                val download = currentDownloads[index].copy(status = DownloadStatus.CANCELED)
                currentDownloads[index] = download
                _downloads.value = currentDownloads
                Result.success(true)
            } else {
                Result.failure(Exception("下载不存在"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun installApp(downloadId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == downloadId }
            
            if (index != -1) {
                val download = currentDownloads[index]
                
                if (download.status == DownloadStatus.COMPLETED) {
                    // 更新状态为安装中
                    val updatedDownload = download.copy(status = DownloadStatus.INSTALLING)
                    currentDownloads[index] = updatedDownload
                    _downloads.value = currentDownloads
                    
                    // 模拟安装过程
                    Thread.sleep(2000)
                    
                    // 更新状态为已安装
                    val installedDownload = updatedDownload.copy(status = DownloadStatus.INSTALLED)
                    currentDownloads[index] = installedDownload
                    _downloads.value = currentDownloads
                    
                    Result.success(true)
                } else {
                    Result.failure(Exception("下载未完成，无法安装"))
                }
            } else {
                Result.failure(Exception("下载不存在"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ===== 私有辅助方法 =====
    
    /**
     * 保存用户令牌
     */
    private fun saveUserToken(userId: String) {
        sharedPreferences.edit().apply {
            putString(Constants.PREF_USER_ID, userId)
            putBoolean(Constants.PREF_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * 模拟下载过程
     * 注意：这只是一个模拟实现，实际应用中应使用DownloadManager或自定义下载服务
     */
    private suspend fun simulateDownload(download: Download) = withContext(Dispatchers.IO) {
        try {
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == download.id }
            
            if (index != -1) {
                // 更新状态为下载中
                val updatedDownload = currentDownloads[index].copy(status = DownloadStatus.DOWNLOADING)
                currentDownloads[index] = updatedDownload
                _downloads.value = currentDownloads
                
                // 模拟下载进度
                for (i in 1..10) {
                    if (currentDownloads[index].status != DownloadStatus.DOWNLOADING) {
                        // 如果状态已经改变（如暂停或取消），则停止进度更新
                        break
                    }
                    
                    val progress = i / 10f
                    val downloadedSize = (download.fileSize * progress).toLong()
                    
                    val progressDownload = updatedDownload.copy(
                        progress = progress,
                        downloadedSize = downloadedSize
                    )
                    
                    currentDownloads[index] = progressDownload
                    _downloads.value = currentDownloads
                    
                    // 模拟网络延迟
                    Thread.sleep(500)
                }
                
                // 如果下载未被中断，则标记为完成
                if (currentDownloads[index].status == DownloadStatus.DOWNLOADING) {
                    val completedDownload = currentDownloads[index].copy(
                        status = DownloadStatus.COMPLETED,
                        progress = 1f,
                        downloadedSize = download.fileSize,
                        endTime = Date(),
                        filePath = "${getDownloadDirectory()}/${download.appName}.apk"
                    )
                    
                    currentDownloads[index] = completedDownload
                    _downloads.value = currentDownloads
                }
            }
        } catch (e: Exception) {
            // 处理模拟下载过程中的异常
            val currentDownloads = _downloads.value.toMutableList()
            val index = currentDownloads.indexOfFirst { it.id == download.id }
            
            if (index != -1) {
                val failedDownload = currentDownloads[index].copy(status = DownloadStatus.FAILED)
                currentDownloads[index] = failedDownload
                _downloads.value = currentDownloads
            }
        }
    }
    
    /**
     * 获取下载目录
     */
    private fun getDownloadDirectory(): String {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Constants.DOWNLOAD_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory.absolutePath
    }
} 