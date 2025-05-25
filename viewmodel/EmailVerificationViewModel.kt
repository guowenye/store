package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.VerifyEmailRequest
import com.smartshop.data.repository.UserRepository
import com.smartshop.util.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 邮箱验证页UI状态
 */
data class EmailVerificationUiState(
    val isLoading: Boolean = false,
    val isVerificationSuccess: Boolean = false,
    val email: String = "",
    val verificationAttempts: Int = 0,
    val resendCooldown: Int = 0,
    val errorMessage: String = ""
)

/**
 * 邮箱验证页ViewModel，负责处理验证逻辑和UI状态
 */
@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private val resendCooldownSeconds = 60 // 重发冷却时间（秒）

    /**
     * 设置要验证的邮箱
     */
    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    /**
     * 发送验证码
     */
    fun sendVerificationCode() {
        if (_uiState.value.isLoading || _uiState.value.resendCooldown > 0) {
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = "") }

        viewModelScope.launch {
            try {
                // 这里假设已经在注册过程中发送了验证码，这里只是模拟重发
                // 实际场景中需要调用API重新发送验证码
                delay(1000) // 模拟网络请求
                
                // 更新UI状态，开始倒计时
                _uiState.update { it.copy(isLoading = false) }
                startResendCooldown()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "发送验证码失败，请稍后重试"
                    )
                }
            }
        }
    }

    /**
     * 验证邮箱
     */
    fun verifyEmail(code: String) {
        if (_uiState.value.isLoading) {
            return
        }

        // 检查验证尝试次数
        if (_uiState.value.verificationAttempts >= EmailValidator.getVerificationAttemptsLimit()) {
            _uiState.update { 
                it.copy(
                    errorMessage = "验证尝试次数已用完，请稍后重试或联系客服"
                )
            }
            return
        }

        // 验证码格式检查
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _uiState.update { 
                it.copy(
                    errorMessage = "请输入6位数字验证码"
                )
            }
            return
        }

        _uiState.update { 
            it.copy(
                isLoading = true,
                errorMessage = "",
                verificationAttempts = it.verificationAttempts + 1
            )
        }

        viewModelScope.launch {
            try {
                // 创建验证请求
                val request = VerifyEmailRequest(
                    verificationCode = code
                )

                // 调用API进行验证
                // 这里为简化演示，假设验证码为123456时验证成功，其他情况失败
                // 实际场景中应该调用真实的API进行验证
                if (code == "123456") {
                    delay(1000) // 模拟网络请求
                    
                    // 更新用户验证状态
                    userRepository.updateVerificationStatus(true)
                    
                    // 更新UI状态
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isVerificationSuccess = true
                        )
                    }
                } else {
                    delay(1000) // 模拟网络请求
                    
                    // 验证失败
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "验证码错误，请重新输入"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "验证失败，请稍后重试"
                    )
                }
            }
        }
    }

    /**
     * 启动重发冷却倒计时
     */
    private fun startResendCooldown() {
        countdownJob?.cancel()
        
        _uiState.update { it.copy(resendCooldown = resendCooldownSeconds) }
        
        countdownJob = viewModelScope.launch {
            for (i in resendCooldownSeconds downTo 1) {
                _uiState.update { it.copy(resendCooldown = i) }
                delay(1000)
            }
            _uiState.update { it.copy(resendCooldown = 0) }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
} 