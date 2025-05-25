package com.smartshop.data.api

import com.smartshop.data.model.AppVersion
import retrofit2.Response
import retrofit2.http.GET

/**
 * 版本API服务接口
 */
interface VersionApiService {
    /**
     * 获取最新版本信息
     */
    @GET("api/version/latest")
    suspend fun getLatestVersion(): Response<AppVersion>
} 