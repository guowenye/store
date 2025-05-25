package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置页UI状态
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isDarkMode: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val cacheSize: String = "0 MB",
    val isClearingCache: Boolean = false,
    val showCacheClearedMessage: Boolean = false
)

/**
 * 设置页ViewModel，负责管理应用设置
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    /**
     * 加载设置
     */
    fun loadSettings() {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        
        viewModelScope.launch {
            try {
                // 获取主题设置
                val isDarkMode = settingsRepository.isDarkModeEnabled()
                
                // 获取通知设置
                val isNotificationsEnabled = settingsRepository.isNotificationsEnabled()
                
                // 获取缓存大小
                val cacheSize = settingsRepository.getCacheSize()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDarkMode = isDarkMode,
                        isNotificationsEnabled = isNotificationsEnabled,
                        cacheSize = cacheSize
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载设置失败"
                    )
                }
            }
        }
    }
    
    /**
     * 切换深色模式
     */
    fun toggleDarkMode() {
        val currentDarkMode = _uiState.value.isDarkMode
        
        viewModelScope.launch {
            try {
                // 切换主题设置
                settingsRepository.setDarkModeEnabled(!currentDarkMode)
                
                // 更新UI状态
                _uiState.update {
                    it.copy(isDarkMode = !currentDarkMode)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "切换主题失败")
                }
            }
        }
    }
    
    /**
     * 切换通知设置
     */
    fun toggleNotifications() {
        val currentNotificationSetting = _uiState.value.isNotificationsEnabled
        
        viewModelScope.launch {
            try {
                // 切换通知设置
                settingsRepository.setNotificationsEnabled(!currentNotificationSetting)
                
                // 更新UI状态
                _uiState.update {
                    it.copy(isNotificationsEnabled = !currentNotificationSetting)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "切换通知设置失败")
                }
            }
        }
    }
    
    /**
     * 清除缓存
     */
    fun clearCache() {
        _uiState.update { it.copy(isClearingCache = true) }
        
        viewModelScope.launch {
            try {
                // 清除缓存
                settingsRepository.clearCache()
                
                // 模拟延迟，提供更好的用户体验
                delay(1000)
                
                // 获取清除后的缓存大小
                val newCacheSize = settingsRepository.getCacheSize()
                
                // 更新UI状态
                _uiState.update {
                    it.copy(
                        isClearingCache = false,
                        cacheSize = newCacheSize,
                        showCacheClearedMessage = true
                    )
                }
                
                // 3秒后自动关闭成功提示
                delay(3000)
                dismissCacheClearedMessage()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isClearingCache = false,
                        errorMessage = e.message ?: "清除缓存失败"
                    )
                }
            }
        }
    }
    
    /**
     * 关闭缓存清除成功提示
     */
    fun dismissCacheClearedMessage() {
        _uiState.update { it.copy(showCacheClearedMessage = false) }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
} 