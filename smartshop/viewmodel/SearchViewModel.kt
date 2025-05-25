package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.App
import com.smartshop.data.repository.AppRepository
import com.smartshop.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 搜索页UI状态
 */
data class SearchUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val searchResults: List<App> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val hasPerformedSearch: Boolean = false
)

/**
 * 搜索页ViewModel，负责加载和管理搜索数据
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    companion object {
        private const val MAX_HISTORY_ITEMS = 10
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /**
     * 执行搜索
     */
    fun search(query: String) {
        if (query.isEmpty()) return
        
        _uiState.update { 
            it.copy(
                isLoading = true,
                errorMessage = "",
                hasPerformedSearch = true
            ) 
        }

        viewModelScope.launch {
            try {
                val apps = appRepository.searchApps(query)
                _uiState.update { 
                    it.copy(
                        searchResults = apps,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "搜索失败，请重试",
                        isLoading = false
                    ) 
                }
            }
        }
    }

    /**
     * 加载搜索历史
     */
    fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                val history = preferencesRepository.getSearchHistory()
                _uiState.update { it.copy(searchHistory = history) }
            } catch (e: Exception) {
                // 加载历史记录失败，但不显示错误
                _uiState.update { it.copy(searchHistory = emptyList()) }
            }
        }
    }

    /**
     * 添加搜索历史
     */
    fun addSearchHistory(query: String) {
        if (query.isEmpty()) return
        
        viewModelScope.launch {
            try {
                // 获取当前历史记录
                val currentHistory = _uiState.value.searchHistory.toMutableList()
                
                // 如果已存在相同查询，先移除它
                currentHistory.remove(query)
                
                // 添加新查询到列表开头
                currentHistory.add(0, query)
                
                // 保持历史记录不超过最大限制
                val newHistory = currentHistory.take(MAX_HISTORY_ITEMS)
                
                // 更新状态
                _uiState.update { it.copy(searchHistory = newHistory) }
                
                // 保存到持久化存储
                preferencesRepository.saveSearchHistory(newHistory)
            } catch (e: Exception) {
                // 保存历史记录失败，但不显示错误
            }
        }
    }

    /**
     * 清除搜索历史
     */
    fun clearSearchHistory() {
        viewModelScope.launch {
            try {
                preferencesRepository.clearSearchHistory()
                _uiState.update { it.copy(searchHistory = emptyList()) }
            } catch (e: Exception) {
                // 清除历史记录失败，但不显示错误
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