package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.Comment
import com.smartshop.data.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 评论页UI状态
 */
data class CommentUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val comments: List<Comment> = emptyList(),
    val averageRating: Float = 0f,
    val totalComments: Int = 0,
    val isSubmitting: Boolean = false,
    val sortByLatest: Boolean = true
)

/**
 * 评论页ViewModel，负责管理评论列表数据
 */
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CommentUiState())
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()
    
    private var currentAppId: String = ""
    private var allComments: List<Comment> = emptyList()
    
    /**
     * 加载评论列表
     */
    fun loadComments(appId: String) {
        currentAppId = appId
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        
        viewModelScope.launch {
            try {
                // 加载评论列表
                allComments = commentRepository.getAppComments(appId)
                
                // 计算平均评分
                val avgRating = if (allComments.isNotEmpty()) {
                    allComments.map { it.rating }.average().toFloat()
                } else {
                    0f
                }
                
                // 更新UI状态
                updateCommentsList(allComments, avgRating)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载评论失败"
                    )
                }
            }
        }
    }
    
    /**
     * 提交评论
     */
    fun submitComment(appId: String, rating: Int, content: String) {
        _uiState.update { it.copy(isSubmitting = true) }
        
        viewModelScope.launch {
            try {
                // 提交评论
                val newComment = commentRepository.submitComment(appId, rating, content)
                
                // 将新评论添加到列表中
                val updatedComments = listOf(newComment) + allComments
                allComments = updatedComments
                
                // 重新计算平均评分
                val avgRating = updatedComments.map { it.rating }.average().toFloat()
                
                // 更新UI状态
                updateCommentsList(updatedComments, avgRating)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "提交评论失败"
                    )
                }
            }
        }
    }
    
    /**
     * 切换评论排序方式
     */
    fun toggleSortOrder() {
        val currentState = _uiState.value
        val newSortByLatest = !currentState.sortByLatest
        
        _uiState.update { it.copy(sortByLatest = newSortByLatest) }
        
        // 根据新的排序方式重新排序评论列表
        val sortedComments = sortComments(allComments, newSortByLatest)
        _uiState.update { it.copy(comments = sortedComments) }
    }
    
    /**
     * 按指定方式排序评论
     */
    private fun sortComments(comments: List<Comment>, byLatest: Boolean): List<Comment> {
        return if (byLatest) {
            // 按创建时间降序排序
            comments.sortedByDescending { it.createdAt }
        } else {
            // 按评分降序排序
            comments.sortedByDescending { it.rating }
        }
    }
    
    /**
     * 更新评论列表状态
     */
    private fun updateCommentsList(comments: List<Comment>, avgRating: Float) {
        val sortedComments = sortComments(comments, _uiState.value.sortByLatest)
        
        _uiState.update {
            it.copy(
                isLoading = false,
                isSubmitting = false,
                comments = sortedComments,
                averageRating = avgRating,
                totalComments = comments.size,
                errorMessage = ""
            )
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
} 