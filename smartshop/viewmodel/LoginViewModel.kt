package com.smartshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.LoginRequest
import com.smartshop.data.repository.UserRepository
import com.smartshop.util.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页UI状态
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String = ""
)

/**
 * 登录页ViewModel，负责处理登录逻辑和UI状态
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // UI状态
    var uiState by mutableStateOf(LoginUiState())
        private set

    /**
     * 用户登录
     */
    fun login(email: String, password: String, rememberMe: Boolean) {
        // 验证输入
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "邮箱和密码不能为空")
            return
        }

        if (!EmailValidator.isValid(email)) {
            uiState = uiState.copy(errorMessage = "请输入有效的邮箱地址")
            return
        }

        // 开始登录
        uiState = uiState.copy(isLoading = true, errorMessage = "")

        viewModelScope.launch {
            try {
                // 创建登录请求
                val loginRequest = LoginRequest(
                    email = email,
                    password = password,
                    rememberMe = rememberMe
                )

                // 调用登录API
                val response = userRepository.login(loginRequest)
                
                if (response != null) {
                    // 保存用户登录信息
                    userRepository.saveUserLogin(response.user, response.token)
                    
                    // 登录成功
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        errorMessage = ""
                    )
                } else {
                    // 登录失败
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccess = false,
                        errorMessage = "登录失败，请检查邮箱和密码"
                    )
                }
            } catch (e: Exception) {
                // 处理错误
                uiState = uiState.copy(
                    isLoading = false,
                    isLoginSuccess = false,
                    errorMessage = e.message ?: "登录失败，请稍后重试"
                )
            }
        }
    }

    /**
     * 获取保存的邮箱地址（如果有）
     */
    suspend fun getSavedEmail(): String? {
        return userRepository.getSavedEmail().firstOrNull()
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        uiState = uiState.copy(errorMessage = "")
    }
} 