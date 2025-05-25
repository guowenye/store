package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.App
import com.smartshop.data.model.Category
import com.smartshop.data.repository.AppRepository
import com.smartshop.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主页UI状态
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val featuredApps: List<App> = emptyList(),
    val newApps: List<App> = emptyList(),
    val recommendedApps: List<App> = emptyList(),
    val categories: List<Category> = emptyList()
)

/**
 * 主页ViewModel，负责加载和管理主页数据
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * 加载主页数据
     * @param isRefresh 是否为刷新操作
     */
    fun loadData(isRefresh: Boolean = false) {
        if (_uiState.value.isLoading && !isRefresh) return
        
        _uiState.update { 
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                errorMessage = ""
            ) 
        }

        viewModelScope.launch {
            try {
                // 并行加载数据
                val featuredAppsDeferred = viewModelScope.launch { loadFeaturedApps() }
                val newAppsDeferred = viewModelScope.launch { loadNewApps() }
                val recommendedAppsDeferred = viewModelScope.launch { loadRecommendedApps() }
                val categoriesDeferred = viewModelScope.launch { loadCategories() }

                // 等待所有数据加载完成
                featuredAppsDeferred.join()
                newAppsDeferred.join()
                recommendedAppsDeferred.join()
                categoriesDeferred.join()

                // 更新加载状态
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = e.message ?: "加载数据失败，请重试"
                    ) 
                }
            }
        }
    }

    /**
     * 加载特色应用
     */
    private suspend fun loadFeaturedApps() {
        try {
            val apps = appRepository.getFeaturedApps()
            _uiState.update { it.copy(featuredApps = apps) }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(errorMessage = e.message ?: "加载特色应用失败") 
            }
        }
    }

    /**
     * 加载新上架应用
     */
    private suspend fun loadNewApps() {
        try {
            val apps = appRepository.getNewApps()
            _uiState.update { it.copy(newApps = apps) }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(errorMessage = e.message ?: "加载新应用失败") 
            }
        }
    }

    /**
     * 加载推荐应用
     */
    private suspend fun loadRecommendedApps() {
        try {
            val apps = appRepository.getRecommendedApps()
            _uiState.update { it.copy(recommendedApps = apps) }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(errorMessage = e.message ?: "加载推荐应用失败") 
            }
        }
    }

    /**
     * 加载应用分类
     */
    private suspend fun loadCategories() {
        try {
            val categories = categoryRepository.getCategories()
            _uiState.update { it.copy(categories = categories) }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(errorMessage = e.message ?: "加载分类失败") 
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