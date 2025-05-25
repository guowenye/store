package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.App
import com.smartshop.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 排行页UI状态
 */
data class RankingUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val popularApps: List<App> = emptyList(),
    val newApps: List<App> = emptyList(),
    val topRatedApps: List<App> = emptyList()
)

/**
 * 排行页ViewModel，负责加载和管理排行榜数据
 */
@HiltViewModel
class RankingViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    /**
     * 加载热门应用
     */
    fun loadPopularApps() {
        if (_uiState.value.isLoading && _uiState.value.popularApps.isEmpty()) return
        
        _uiState.update { 
            it.copy(
                isLoading = it.popularApps.isEmpty(),
                isRefreshing = it.popularApps.isNotEmpty(),
                errorMessage = ""
            ) 
        }

        viewModelScope.launch {
            try {
                // 获取热门应用（按下载量排序）
                val apps = appRepository.getFeaturedApps().sortedByDescending { it.downloadCount }
                _uiState.update { 
                    it.copy(
                        popularApps = apps,
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "加载热门应用失败，请重试",
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            }
        }
    }

    /**
     * 加载新上架应用
     */
    fun loadNewApps() {
        if (_uiState.value.isLoading && _uiState.value.newApps.isEmpty()) return
        
        _uiState.update { 
            it.copy(
                isLoading = it.newApps.isEmpty(),
                isRefreshing = it.newApps.isNotEmpty(),
                errorMessage = ""
            ) 
        }

        viewModelScope.launch {
            try {
                // 获取新上架应用（按更新时间排序）
                val apps = appRepository.getNewApps().sortedByDescending { it.updatedAt }
                _uiState.update { 
                    it.copy(
                        newApps = apps,
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "加载新上架应用失败，请重试",
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            }
        }
    }

    /**
     * 加载好评应用
     */
    fun loadTopRatedApps() {
        if (_uiState.value.isLoading && _uiState.value.topRatedApps.isEmpty()) return
        
        _uiState.update { 
            it.copy(
                isLoading = it.topRatedApps.isEmpty(),
                isRefreshing = it.topRatedApps.isNotEmpty(),
                errorMessage = ""
            ) 
        }

        viewModelScope.launch {
            try {
                // 获取好评应用（按评分排序）
                val apps = appRepository.getRecommendedApps().sortedByDescending { it.rating }
                _uiState.update { 
                    it.copy(
                        topRatedApps = apps,
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "加载好评应用失败，请重试",
                        isLoading = false,
                        isRefreshing = false
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