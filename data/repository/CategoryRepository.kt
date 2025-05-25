package com.smartshop.data.repository

import android.content.Context
import com.smartshop.data.api.CategoryApiService
import com.smartshop.data.model.Category
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 分类数据仓库，管理应用分类数据
 */
@Singleton
class CategoryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryApiService: CategoryApiService
) {
    /**
     * 获取所有分类
     */
    suspend fun getCategories(): List<Category> {
        // 模拟网络请求
        delay(500)
        return getDummyCategories()
    }
    
    /**
     * 获取分类详情
     */
    suspend fun getCategoryDetail(categoryId: String): Category {
        // 模拟网络请求
        delay(300)
        return getDummyCategories().firstOrNull { it.id == categoryId }
            ?: throw Exception("分类不存在")
    }
    
    /**
     * 模拟数据 - 实际应用中应该从API获取
     */
    private fun getDummyCategories(): List<Category> {
        return listOf(
            Category(
                id = "health",
                name = "健康",
                icon = "ic_health",
                appCount = 25,
                color = 0xFF4CAF50,
                sortOrder = 1
            ),
            Category(
                id = "fitness",
                name = "健身",
                icon = "ic_fitness",
                appCount = 18,
                color = 0xFFF44336,
                sortOrder = 2
            ),
            Category(
                id = "tools",
                name = "工具",
                icon = "ic_tools",
                appCount = 42,
                color = 0xFF2196F3,
                sortOrder = 3
            ),
            Category(
                id = "productivity",
                name = "效率",
                icon = "ic_productivity",
                appCount = 30,
                color = 0xFFFF9800,
                sortOrder = 4
            ),
            Category(
                id = "music",
                name = "音乐",
                icon = "ic_music",
                appCount = 15,
                color = 0xFF9C27B0,
                sortOrder = 5
            ),
            Category(
                id = "games",
                name = "游戏",
                icon = "ic_games",
                appCount = 35,
                color = 0xFF3F51B5,
                sortOrder = 6
            ),
            Category(
                id = "education",
                name = "教育",
                icon = "ic_education",
                appCount = 20,
                color = 0xFFE91E63,
                sortOrder = 7
            ),
            Category(
                id = "weather",
                name = "天气",
                icon = "ic_weather",
                appCount = 8,
                color = 0xFF00BCD4,
                sortOrder = 8
            )
        )
    }
} 