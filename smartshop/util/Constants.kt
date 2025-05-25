package com.smartshop.util

/**
 * 应用全局常量
 */
object Constants {
    // API相关
    const val API_BASE_URL = "https://api.smartshop.com/"
    const val API_TIMEOUT = 30L
    
    // 缓存相关
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    const val CACHE_MAX_AGE = 60 * 60 * 24 * 7 // 一周
    
    // 分页加载
    const val DEFAULT_PAGE_SIZE = 20
    
    // 共享偏好设置
    const val PREF_NAME = "smart_shop_prefs"
    const val PREF_USER_TOKEN = "user_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_LANGUAGE = "language"
    
    // 下载相关
    const val DOWNLOAD_DIRECTORY = "SmartShop/downloads"
    
    // 文件Provider授权
    const val FILE_PROVIDER_AUTHORITY = "com.smartshop.fileprovider"
    
    // 通知
    const val NOTIFICATION_CHANNEL_ID = "download_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Downloads"
    const val NOTIFICATION_ID_DOWNLOAD = 1001
    
    // 屏幕尺寸分类
    const val SCREEN_SIZE_SMALL = 180 // dp
    const val SCREEN_SIZE_MEDIUM = 200 // dp
    const val SCREEN_SIZE_LARGE = 220 // dp
    
    // 隐私政策和用户协议
    const val PRIVACY_POLICY_URL = "https://smartshop.com/privacy"
    const val TERMS_OF_SERVICE_URL = "https://smartshop.com/terms"
    
    // 应用版本
    const val APP_VERSION = "1.0.0"
} 