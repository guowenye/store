package com.smartshop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Smart应用商店应用入口类
 * 初始化全局组件和依赖注入
 */
@HiltAndroidApp
class SmartShopApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 全局初始化逻辑
    }
} 