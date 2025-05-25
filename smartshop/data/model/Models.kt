package com.smartshop.data.model

import java.util.Date

/**
 * 用户数据模型
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatar: String?,
    val isVerified: Boolean = false,
    val registerDate: Date,
    val favoriteApps: List<String> = emptyList(),
    val installedApps: List<String> = emptyList(),
    val settings: UserSettings = UserSettings()
)

/**
 * 用户设置
 */
data class UserSettings(
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val autoUpdate: Boolean = true,
    val downloadOverWifiOnly: Boolean = true,
    val language: String = "zh_CN"
)

/**
 * 应用分类
 */
data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val appsCount: Int
)

/**
 * 应用信息
 */
data class App(
    val id: String,
    val name: String,
    val developer: String,
    val version: String,
    val icon: String,
    val screenshots: List<String>,
    val description: String,
    val shortDescription: String,
    val size: Long, // 字节数
    val categoryId: String,
    val rating: Float,
    val downloadCount: Int,
    val price: Float = 0f,
    val isFree: Boolean = true,
    val releaseDate: Date,
    val lastUpdateDate: Date,
    val permissions: List<Permission> = emptyList(),
    val tags: List<String> = emptyList(),
    val compatibleDevices: List<String> = emptyList(),
    val minWearOsVersion: String,
    val downloadUrl: String,
    val isInstalled: Boolean = false,
    val isFavorite: Boolean = false
)

/**
 * 应用所需的权限
 */
data class Permission(
    val id: String,
    val name: String,
    val description: String,
    val isRequired: Boolean,
    val group: PermissionGroup
)

/**
 * 权限分组
 */
enum class PermissionGroup {
    LOCATION,
    STORAGE,
    CAMERA,
    MICROPHONE,
    SENSORS,
    NOTIFICATIONS,
    CONTACTS,
    PHONE,
    SMS,
    CALENDAR,
    ACTIVITY_RECOGNITION,
    OTHER
}

/**
 * 应用评论
 */
data class Comment(
    val id: String,
    val appId: String,
    val userId: String,
    val username: String,
    val userAvatar: String?,
    val rating: Int, // 1-5
    val content: String,
    val date: Date,
    val likes: Int,
    val dislikes: Int,
    val developerResponse: DeveloperResponse? = null
)

/**
 * 开发者回复
 */
data class DeveloperResponse(
    val content: String,
    val date: Date
)

/**
 * 下载记录
 */
data class Download(
    val id: String,
    val appId: String,
    val appName: String,
    val appIcon: String,
    val downloadUrl: String,
    val startTime: Date,
    val endTime: Date? = null,
    val progress: Float = 0f, // 0.0 - 1.0
    val status: DownloadStatus,
    val filePath: String? = null,
    val fileSize: Long,
    val downloadedSize: Long = 0
)

/**
 * 下载状态
 */
enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELED,
    INSTALLING,
    INSTALLED
}

/**
 * 举报类型
 */
enum class ReportType {
    APP,
    COMMENT
}

/**
 * 举报内容
 */
data class Report(
    val id: String,
    val reportType: ReportType,
    val targetId: String,
    val userId: String,
    val reason: String,
    val description: String,
    val date: Date,
    val status: ReportStatus = ReportStatus.PENDING
)

/**
 * 举报状态
 */
enum class ReportStatus {
    PENDING,
    REVIEWING,
    RESOLVED,
    REJECTED
}

/**
 * 应用轮播图
 */
data class Banner(
    val id: String,
    val appId: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val startDate: Date,
    val endDate: Date
)

/**
 * 排名类型
 */
enum class RankingType {
    HOT,
    NEW,
    RATING
}

/**
 * 搜索结果
 */
data class SearchResult(
    val apps: List<App>,
    val totalCount: Int,
    val searchTerm: String,
    val categories: List<Category> = emptyList(),
    val suggestions: List<String> = emptyList()
)

/**
 * API响应包装
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int? = null
)

/**
 * 分页数据
 */
data class PagedResponse<T>(
    val items: List<T>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
) 