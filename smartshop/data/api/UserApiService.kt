package com.smartshop.data.api

import com.smartshop.data.model.User
import com.smartshop.data.model.LoginRequest
import com.smartshop.data.model.LoginResponse
import com.smartshop.data.model.RegisterRequest
import com.smartshop.data.model.RegisterResponse
import com.smartshop.data.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 用户API服务接口
 */
interface UserApiService {
    /**
     * 用户登录
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    /**
     * 用户注册
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    /**
     * 邮箱验证
     */
    @POST("api/auth/verify-email")
    suspend fun verifyEmail(
        @Header("Authorization") token: String,
        @Body request: VerifyEmailRequest
    ): Response<User>
    
    /**
     * 获取用户信息
     */
    @GET("api/user/profile")
    suspend fun getUserInfo(@Header("Authorization") token: String): Response<User>
    
    /**
     * 退出登录
     */
    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
} 