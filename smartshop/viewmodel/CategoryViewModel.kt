package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.App
import com.smartshop.data.model.Category
import com.smartshop.data.repository.AppRepository
import com.smartshop.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 分类页UI状态
 */
data class CategoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val categoryApps: List<App> = emptyList()
)

/**
 * 分类页ViewModel，负责加载和管理分类数据
 */
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    /**
     * 加载分类页数据
     * @param categoryId 分类ID，若为null则加载全部分类
     */
    fun loadData(categoryId: String? = null) {
        _uiState.update { 
            it.copy(
                isLoading = true,
                errorMessage = ""
            ) 
        }

        viewModelScope.launch {
            try {
                // 加载所有分类
                val categories = categoryRepository.getCategories()
                _uiState.update { it.copy(categories = categories) }
                
                // 如果指定了分类ID，则选择该分类
                if (categoryId != null) {
                    val category = categories.find { it.id == categoryId }
                    if (category != null) {
                        selectCategory(category)
                    } else {
                        throw Exception("分类不存在")
                    }
                } else if (categories.isNotEmpty()) {
                    // 否则默认选择第一个分类
                    selectCategory(categories.first())
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载分类数据失败，请重试"
                    ) 
                }
            }
        }
    }

    /**
     * 选择分类
     */
    fun selectCategory(category: Category) {
        // 如果已经选择了该分类，不做任何操作
        if (_uiState.value.selectedCategory?.id == category.id) {
            return
        }
        
        _uiState.update { 
            it.copy(
                selectedCategory = category,
                isRefreshing = true
            ) 
        }
        
        viewModelScope.launch {
            try {
                // 加载分类应用
                val apps = appRepository.getCategoryApps(category.id)
                _uiState.update { 
                    it.copy(
                        categoryApps = apps,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "加载分类应用失败，请重试",
                        isRefreshing = false
                    ) 
                }
            }
        }
    }

    /**
     * 刷新分类应用
     */
    fun refreshCategoryApps() {
        val selectedCategory = _uiState.value.selectedCategory ?: return
        
        _uiState.update { it.copy(isRefreshing = true) }
        
        viewModelScope.launch {
            try {
                val apps = appRepository.getCategoryApps(selectedCategory.id)
                _uiState.update { 
                    it.copy(
                        categoryApps = apps,
                        isRefreshing = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = e.message ?: "刷新应用失败，请重试",
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