package com.smartshop.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 用户数据模型
 */
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "verification_attempts") val verificationAttempts: Int = 0,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @Json(name = "status") val status: Int = STATUS_NORMAL,
    @Json(name = "role") val role: Int = ROLE_USER
) {
    companion object {
        // 账号状态
        const val STATUS_NORMAL = 0 // 正常
        const val STATUS_BANNED = 1 // 封禁
        
        // 用户角色
        const val ROLE_USER = 0 // 普通用户
        const val ROLE_OPERATOR = 1 // 运营
        const val ROLE_REVIEWER = 2 // 审核
        const val ROLE_ADMIN = 3 // 管理员
    }
}

/**
 * 登录请求
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "remember_me") val rememberMe: Boolean = false
)

/**
 * 登录响应
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token") val token: String,
    @Json(name = "user") val user: User
)

/**
 * 注册请求
 */
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "confirm_password") val confirmPassword: String
)

/**
 * 注册响应
 */
@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "token") val token: String,
    @Json(name = "user") val user: User,
    @Json(name = "verification_code_sent") val verificationCodeSent: Boolean
)

/**
 * 邮箱验证请求
 */
@JsonClass(generateAdapter = true)
data class VerifyEmailRequest(
    @Json(name = "verification_code") val verificationCode: String
) 