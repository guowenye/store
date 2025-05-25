package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.FavoriteApp
import com.smartshop.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 收藏列表页UI状态
 */
data class FavoriteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val favorites: List<FavoriteApp> = emptyList()
)

/**
 * 收藏列表页ViewModel，负责加载和管理收藏应用列表
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()
    
    /**
     * 加载收藏列表
     */
    fun loadFavorites() {
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        
        viewModelScope.launch {
            try {
                // 获取收藏应用列表
                val favorites = favoriteRepository.getUserFavorites()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favorites = favorites
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载收藏列表失败"
                    )
                }
            }
        }
    }
    
    /**
     * 取消收藏应用
     */
    fun unfavoriteApp(appId: String) {
        viewModelScope.launch {
            try {
                // 取消收藏
                favoriteRepository.removeFavorite(appId)
                
                // 更新UI状态，从列表中移除该应用
                _uiState.update { currentState ->
                    currentState.copy(
                        favorites = currentState.favorites.filter { it.id != appId }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "取消收藏失败"
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