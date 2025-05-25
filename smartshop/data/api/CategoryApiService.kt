package com.smartshop.data.api

import com.smartshop.data.model.Category
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 分类API接口
 */
interface CategoryApiService {
    /**
     * 获取所有分类
     */
    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>
    
    /**
     * 获取分类详情
     */
    @GET("categories/{categoryId}")
    suspend fun getCategoryDetail(@Path("categoryId") categoryId: String): Response<Category>
} 