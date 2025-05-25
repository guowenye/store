package com.smartshop.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smartshop.data.api.UserApiService
import com.smartshop.data.model.LoginRequest
import com.smartshop.data.model.LoginResponse
import com.smartshop.data.model.RegisterRequest
import com.smartshop.data.model.RegisterResponse
import com.smartshop.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 用户DataStore
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * 用户数据仓库，管理用户登录状态、账号信息等
 */
@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userApiService: UserApiService
) {
    companion object {
        // 键值定义
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_TOKEN = stringPreferencesKey("user_token")
        private val KEY_IS_VERIFIED = booleanPreferencesKey("is_verified")
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求信息
     * @return 登录响应，包含用户信息和token
     */
    suspend fun login(loginRequest: LoginRequest): LoginResponse? {
        try {
            val response = userApiService.login(loginRequest)
            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 检查用户是否已登录
     */
    fun isUserLoggedIn(): Flow<Boolean> {
        return context.userDataStore.data.map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }
    }

    /**
     * 获取保存的邮箱地址
     */
    fun getSavedEmail(): Flow<String?> {
        return context.userDataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }
    }

    /**
     * 获取当前登录用户信息
     */
    fun getCurrentUser(): Flow<User?> {
        return context.userDataStore.data.map { preferences ->
            val userId = preferences[KEY_USER_ID]
            val userName = preferences[KEY_USER_NAME]
            val userEmail = preferences[KEY_USER_EMAIL]
            val isVerified = preferences[KEY_IS_VERIFIED] ?: false
            
            if (userId != null && userName != null && userEmail != null) {
                User(
                    id = userId,
                    username = userName,
                    email = userEmail,
                    isVerified = isVerified
                )
            } else {
                null
            }
        }
    }

    /**
     * 保存用户登录信息
     */
    suspend fun saveUserLogin(user: User, token: String) {
        context.userDataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_USER_ID] = user.id
            preferences[KEY_USER_NAME] = user.username
            preferences[KEY_USER_EMAIL] = user.email
            preferences[KEY_USER_TOKEN] = token
            preferences[KEY_IS_VERIFIED] = user.isVerified
        }
    }

    /**
     * 更新用户验证状态
     */
    suspend fun updateVerificationStatus(isVerified: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[KEY_IS_VERIFIED] = isVerified
        }
    }

    /**
     * 用户登出，清除登录信息
     */
    suspend fun logout() {
        context.userDataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = false
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_TOKEN)
            preferences.remove(KEY_IS_VERIFIED)
        }
    }

    /**
     * 检查用户认证状态
     */
    suspend fun refreshUserInfo(): User? {
        try {
            // 从本地获取token
            val token = context.userDataStore.data.map { preferences ->
                preferences[KEY_USER_TOKEN]
            }.firstOrNull() ?: return null

            // 调用API获取最新用户信息
            val response = userApiService.getUserInfo("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                // 更新本地存储的用户信息
                saveUserLogin(user, token)
                return user
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 用户注册
     * @param registerRequest 注册请求信息
     * @return 注册响应，包含用户信息和token
     */
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse? {
        try {
            val response = userApiService.register(registerRequest)
            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            return null
        }
    }
} 