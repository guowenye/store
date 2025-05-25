package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.User
import com.smartshop.data.repository.FavoriteRepository
import com.smartshop.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 个人中心页UI状态
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val user: User? = null,
    val favoriteCount: Int = 0
)

/**
 * 个人中心页ViewModel，负责加载和管理用户信息
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    /**
     * 加载用户个人资料
     */
    fun loadUserProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        
        viewModelScope.launch {
            try {
                // 检查用户是否登录
                if (!userRepository.isLoggedIn()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = null
                        )
                    }
                    return@launch
                }
                
                // 加载用户信息
                val user = userRepository.getCurrentUser()
                
                // 加载收藏数量
                val favoriteCount = favoriteRepository.getUserFavoriteCount()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                        favoriteCount = favoriteCount
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载用户信息失败"
                    )
                }
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                
                // 清除状态
                _uiState.update {
                    it.copy(
                        user = null,
                        favoriteCount = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "退出登录失败"
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
} 