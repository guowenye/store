package com.smartshop.data.repository

import android.content.Context
import com.smartshop.R
import com.smartshop.data.api.AppApiService
import com.smartshop.data.model.App
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 应用数据仓库，管理应用列表、详情等数据
 */
@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appApiService: AppApiService
) {
    /**
     * 获取特色应用列表
     */
    suspend fun getFeaturedApps(): List<App> {
        // 这里为了演示，模拟网络请求，返回模拟数据
        // 实际应用中应该调用API获取真实数据
        delay(1000)
        return getDummyApps().shuffled().take(5)
    }
    
    /**
     * 获取新上架应用列表
     */
    suspend fun getNewApps(): List<App> {
        // 模拟网络请求
        delay(800)
        return getDummyApps().sortedByDescending { it.updatedAt }.take(10)
    }
    
    /**
     * 获取推荐应用列表
     */
    suspend fun getRecommendedApps(): List<App> {
        // 模拟网络请求
        delay(1200)
        return getDummyApps().sortedByDescending { it.rating }.take(10)
    }
    
    /**
     * 获取应用详情
     */
    suspend fun getAppDetail(appId: String): App {
        // 模拟网络请求
        delay(500)
        return getDummyApps().firstOrNull { it.id == appId }
            ?: throw Exception("应用不存在")
    }
    
    /**
     * 搜索应用
     */
    suspend fun searchApps(keyword: String): List<App> {
        // 模拟网络请求
        delay(700)
        return getDummyApps().filter { 
            it.name.contains(keyword, ignoreCase = true) || 
            it.developer.contains(keyword, ignoreCase = true) ||
            it.description.contains(keyword, ignoreCase = true)
        }
    }
    
    /**
     * 获取分类应用
     */
    suspend fun getCategoryApps(categoryId: String): List<App> {
        // 模拟网络请求
        delay(900)
        return getDummyApps().filter { it.category == categoryId }
    }
    
    /**
     * 模拟数据 - 实际应用中应该从API获取
     */
    private fun getDummyApps(): List<App> {
        return listOf(
            App(
                id = "1",
                name = "健康监测",
                packageName = "com.health.monitor",
                version = "1.2.0",
                versionCode = 120,
                size = 15_000_000,
                icon = "health_icon",
                developer = "健康科技",
                description = "全面的健康监测应用，支持心率、睡眠、运动等多种健康数据记录与分析。",
                category = "health",
                screenshots = listOf("health_1", "health_2", "health_3"),
                downloadCount = 250000,
                rating = 4.5f,
                status = 1,
                apkUrl = "https://example.com/apps/health_monitor.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 2
            ),
            App(
                id = "2",
                name = "运动追踪",
                packageName = "com.fitness.tracker",
                version = "2.1.5",
                versionCode = 215,
                size = 22_000_000,
                icon = "fitness_icon",
                developer = "运动助手",
                description = "专业的运动追踪应用，支持跑步、骑行、游泳等多种运动模式，精准记录运动数据。",
                category = "fitness",
                screenshots = listOf("fitness_1", "fitness_2", "fitness_3"),
                downloadCount = 1_500_000,
                rating = 4.7f,
                status = 1,
                apkUrl = "https://example.com/apps/fitness_tracker.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 5
            ),
            App(
                id = "3",
                name = "睡眠管家",
                packageName = "com.sleep.master",
                version = "1.0.2",
                versionCode = 102,
                size = 10_000_000,
                icon = "sleep_icon",
                developer = "健康科技",
                description = "智能睡眠监测应用，通过分析睡眠数据，提供个性化的睡眠改善建议。",
                category = "health",
                screenshots = listOf("sleep_1", "sleep_2", "sleep_3"),
                downloadCount = 800000,
                rating = 4.3f,
                status = 1,
                apkUrl = "https://example.com/apps/sleep_master.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 10
            ),
            App(
                id = "4",
                name = "天气预报",
                packageName = "com.weather.forecast",
                version = "3.5.1",
                versionCode = 351,
                size = 18_000_000,
                icon = "weather_icon",
                developer = "天气通",
                description = "精准的天气预报应用，支持全球天气查询，提供实时天气、未来预报、空气质量等信息。",
                category = "tools",
                screenshots = listOf("weather_1", "weather_2", "weather_3"),
                downloadCount = 5_000_000,
                rating = 4.8f,
                status = 1,
                apkUrl = "https://example.com/apps/weather_forecast.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 1
            ),
            App(
                id = "5",
                name = "计步器",
                packageName = "com.step.counter",
                version = "1.3.0",
                versionCode = 130,
                size = 8_000_000,
                icon = "step_icon",
                developer = "运动助手",
                description = "简单易用的计步器应用，自动记录每日步数，支持设定目标和查看历史数据。",
                category = "fitness",
                screenshots = listOf("step_1", "step_2", "step_3"),
                downloadCount = 3_000_000,
                rating = 4.4f,
                status = 1,
                apkUrl = "https://example.com/apps/step_counter.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 15
            ),
            App(
                id = "6",
                name = "音乐播放器",
                packageName = "com.music.player",
                version = "2.7.3",
                versionCode = 273,
                size = 25_000_000,
                icon = "music_icon",
                developer = "音乐世界",
                description = "高品质音乐播放应用，支持多种音频格式，提供均衡器、音效调节等专业功能。",
                category = "music",
                screenshots = listOf("music_1", "music_2", "music_3"),
                downloadCount = 10_000_000,
                rating = 4.6f,
                status = 1,
                apkUrl = "https://example.com/apps/music_player.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 7
            ),
            App(
                id = "7",
                name = "任务清单",
                packageName = "com.task.list",
                version = "1.5.2",
                versionCode = 152,
                size = 12_000_000,
                icon = "task_icon",
                developer = "效率工具",
                description = "简洁高效的任务管理应用，帮助您合理规划时间，提高工作效率。",
                category = "productivity",
                screenshots = listOf("task_1", "task_2", "task_3"),
                downloadCount = 2_000_000,
                rating = 4.2f,
                status = 1,
                apkUrl = "https://example.com/apps/task_list.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 20
            ),
            App(
                id = "8",
                name = "计算器",
                packageName = "com.calculator.pro",
                version = "2.0.1",
                versionCode = 201,
                size = 5_000_000,
                icon = "calculator_icon",
                developer = "工具箱",
                description = "功能强大的计算器应用，支持科学计算、单位换算、汇率转换等多种功能。",
                category = "tools",
                screenshots = listOf("calculator_1", "calculator_2", "calculator_3"),
                downloadCount = 8_000_000,
                rating = 4.5f,
                status = 1,
                apkUrl = "https://example.com/apps/calculator_pro.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 25
            ),
            App(
                id = "9",
                name = "备忘录",
                packageName = "com.note.pad",
                version = "1.8.0",
                versionCode = 180,
                size = 15_000_000,
                icon = "note_icon",
                developer = "效率工具",
                description = "简洁易用的备忘录应用，支持文字、图片、语音等多种记录方式，随时随地记录灵感。",
                category = "productivity",
                screenshots = listOf("note_1", "note_2", "note_3"),
                downloadCount = 4_000_000,
                rating = 4.4f,
                status = 1,
                apkUrl = "https://example.com/apps/note_pad.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 12
            ),
            App(
                id = "10",
                name = "水分提醒",
                packageName = "com.water.reminder",
                version = "1.1.1",
                versionCode = 111,
                size = 7_000_000,
                icon = "water_icon",
                developer = "健康科技",
                description = "智能饮水提醒应用，根据个人情况制定饮水计划，帮助保持身体水分平衡。",
                category = "health",
                screenshots = listOf("water_1", "water_2", "water_3"),
                downloadCount = 1_200_000,
                rating = 4.1f,
                status = 1,
                apkUrl = "https://example.com/apps/water_reminder.apk",
                updatedAt = System.currentTimeMillis() - 86400000 * 30
            )
        )
    }
} 