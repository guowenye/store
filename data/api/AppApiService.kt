package com.smartshop.data.api

import com.smartshop.data.model.App
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 应用API接口
 */
interface AppApiService {
    /**
     * 获取特色应用
     */
    @GET("apps/featured")
    suspend fun getFeaturedApps(): Response<List<App>>
    
    /**
     * 获取新上架应用
     */
    @GET("apps/new")
    suspend fun getNewApps(): Response<List<App>>
    
    /**
     * 获取推荐应用
     */
    @GET("apps/recommended")
    suspend fun getRecommendedApps(): Response<List<App>>
    
    /**
     * 获取应用详情
     */
    @GET("apps/{appId}")
    suspend fun getAppDetail(@Path("appId") appId: String): Response<App>
    
    /**
     * 搜索应用
     */
    @GET("apps/search")
    suspend fun searchApps(@Query("keyword") keyword: String): Response<List<App>>
    
    /**
     * 获取分类应用
     */
    @GET("categories/{categoryId}/apps")
    suspend fun getCategoryApps(@Path("categoryId") categoryId: String): Response<List<App>>
} 