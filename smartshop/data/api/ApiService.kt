package com.smartshop.data.api

import com.smartshop.data.model.ApiResponse
import com.smartshop.data.model.App
import com.smartshop.data.model.Banner
import com.smartshop.data.model.Category
import com.smartshop.data.model.Comment
import com.smartshop.data.model.PagedResponse
import com.smartshop.data.model.RankingType
import com.smartshop.data.model.Report
import com.smartshop.data.model.SearchResult
import com.smartshop.data.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Smart应用商店API服务接口
 * 定义所有网络请求方法
 */
interface ApiService {
    
    // ===== 用户相关接口 =====
    
    /**
     * 用户登录
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ApiResponse<User>
    
    /**
     * 用户注册
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ApiResponse<User>
    
    /**
     * 验证邮箱验证码
     */
    @FormUrlEncoded
    @POST("user/verify-email")
    suspend fun verifyEmail(
        @Field("email") email: String,
        @Field("code") code: String
    ): ApiResponse<Boolean>
    
    /**
     * 获取当前用户信息
     */
    @GET("user/profile")
    suspend fun getUserProfile(): ApiResponse<User>
    
    /**
     * 更新用户设置
     */
    @PUT("user/settings")
    suspend fun updateUserSettings(@Body settings: Map<String, Any>): ApiResponse<Boolean>
    
    // ===== 应用相关接口 =====
    
    /**
     * 获取首页数据
     */
    @GET("home")
    suspend fun getHomeData(): ApiResponse<HomeData>
    
    /**
     * 获取所有分类
     */
    @GET("categories")
    suspend fun getCategories(): ApiResponse<List<Category>>
    
    /**
     * 获取分类下的应用列表
     */
    @GET("categories/{categoryId}/apps")
    suspend fun getCategoryApps(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PagedResponse<App>>
    
    /**
     * 获取排行榜应用
     */
    @GET("ranking")
    suspend fun getRankingApps(
        @Query("type") type: RankingType,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PagedResponse<App>>
    
    /**
     * 获取应用详情
     */
    @GET("apps/{appId}")
    suspend fun getAppDetail(@Path("appId") appId: String): ApiResponse<App>
    
    /**
     * 获取应用评论
     */
    @GET("apps/{appId}/comments")
    suspend fun getAppComments(
        @Path("appId") appId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PagedResponse<Comment>>
    
    /**
     * 发表评论
     */
    @FormUrlEncoded
    @POST("apps/{appId}/comments")
    suspend fun postComment(
        @Path("appId") appId: String,
        @Field("rating") rating: Int,
        @Field("content") content: String
    ): ApiResponse<Comment>
    
    /**
     * 搜索应用
     */
    @GET("search")
    suspend fun searchApps(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<SearchResult>
    
    /**
     * 收藏/取消收藏应用
     */
    @POST("apps/{appId}/favorite")
    suspend fun toggleFavorite(
        @Path("appId") appId: String,
        @Query("favorite") favorite: Boolean
    ): ApiResponse<Boolean>
    
    /**
     * 获取收藏的应用列表
     */
    @GET("user/favorites")
    suspend fun getFavoriteApps(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PagedResponse<App>>
    
    /**
     * 提交举报
     */
    @FormUrlEncoded
    @POST("report")
    suspend fun submitReport(
        @Field("reportType") reportType: String,
        @Field("targetId") targetId: String,
        @Field("reason") reason: String,
        @Field("description") description: String
    ): ApiResponse<Report>
}

/**
 * 首页数据结构
 */
data class HomeData(
    val banners: List<Banner>,
    val recommendedApps: List<App>,
    val newApps: List<App>,
    val popularCategories: List<Category>,
    val promotionApps: List<App>
) 