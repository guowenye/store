package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.RegisterRequest
import com.smartshop.data.repository.UserRepository
import com.smartshop.util.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 注册页UI状态
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistrationSuccess: Boolean = false,
    val usernameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val generalError: String = ""
)

/**
 * 注册页ViewModel，负责处理注册逻辑和UI状态
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * 用户注册
     */
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // 重置错误信息
        resetErrors()
        
        // 验证输入
        if (!validateInputs(username, email, password, confirmPassword)) {
            return
        }

        // 开始注册
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 创建注册请求
                val registerRequest = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )

                // 调用注册API
                val response = userRepository.register(registerRequest)
                
                if (response != null) {
                    // 保存用户登录信息
                    userRepository.saveUserLogin(response.user, response.token)
                    
                    // 注册成功
                    _uiState.update { it.copy(
                        isLoading = false,
                        isRegistrationSuccess = true
                    ) }
                } else {
                    // 注册失败
                    _uiState.update { it.copy(
                        isLoading = false,
                        generalError = "注册失败，请稍后重试"
                    ) }
                }
            } catch (e: Exception) {
                // 处理错误
                _uiState.update { it.copy(
                    isLoading = false,
                    generalError = e.message ?: "注册失败，请稍后重试"
                ) }
            }
        }
    }

    /**
     * 验证输入
     */
    private fun validateInputs(
        username: String, 
        email: String, 
        password: String, 
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // 验证用户名
        if (username.isBlank()) {
            _uiState.update { it.copy(usernameError = "用户名不能为空") }
            isValid = false
        } else if (username.length < 3) {
            _uiState.update { it.copy(usernameError = "用户名至少需要3个字符") }
            isValid = false
        }

        // 验证邮箱
        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "邮箱不能为空") }
            isValid = false
        } else if (!EmailValidator.isValid(email)) {
            _uiState.update { it.copy(emailError = "请输入有效的邮箱地址，并确保使用常见邮箱服务") }
            isValid = false
        }

        // 验证密码
        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "密码不能为空") }
            isValid = false
        } else if (password.length < 6) {
            _uiState.update { it.copy(passwordError = "密码至少需要6个字符") }
            isValid = false
        }

        // 验证确认密码
        if (confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = "请确认密码") }
            isValid = false
        } else if (password != confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "两次输入的密码不一致") }
            isValid = false
        }

        return isValid
    }

    /**
     * 重置错误信息
     */
    private fun resetErrors() {
        _uiState.update { it.copy(
            usernameError = "",
            emailError = "",
            passwordError = "",
            confirmPasswordError = "",
            generalError = ""
        ) }
    }

    /**
     * 清除一般性错误
     */
    fun clearGeneralError() {
        _uiState.update { it.copy(generalError = "") }
    }
} 