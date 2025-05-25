package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.App
import com.smartshop.data.model.Comment
import com.smartshop.data.repository.AppRepository
import com.smartshop.data.repository.CommentRepository
import com.smartshop.data.repository.DownloadRepository
import com.smartshop.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 应用详情页UI状态
 */
data class AppDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val app: App? = null,
    val comments: List<Comment> = emptyList(),
    val isFavorite: Boolean = false,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED,
    val downloadProgress: Float = 0f
)

/**
 * 应用详情页ViewModel，负责加载和管理应用详情数据
 */
@HiltViewModel
class AppDetailViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val commentRepository: CommentRepository,
    private val favoriteRepository: FavoriteRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    /**
     * 下载状态枚举
     */
    enum class DownloadState {
        NOT_DOWNLOADED, // 未下载
        DOWNLOADING,    // 下载中
        DOWNLOADED,     // 已下载但未安装
        INSTALLED       // 已安装
    }
    
    private val _uiState = MutableStateFlow(AppDetailUiState())
    val uiState: StateFlow<AppDetailUiState> = _uiState.asStateFlow()
    
    private var currentAppId: String = ""
    
    /**
     * 加载应用详情
     */
    fun loadAppDetail(appId: String) {
        currentAppId = appId
        _uiState.update { it.copy(isLoading = true, errorMessage = "") }
        
        viewModelScope.launch {
            try {
                // 加载应用信息
                val app = appRepository.getAppDetail(appId)
                
                // 加载评论
                val comments = commentRepository.getAppComments(appId)
                
                // 检查是否收藏
                val isFavorite = favoriteRepository.isAppFavorite(appId)
                
                // 检查下载状态
                val downloadState = downloadRepository.getAppDownloadState(appId)
                val downloadProgress = downloadRepository.getAppDownloadProgress(appId)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        app = app,
                        comments = comments,
                        isFavorite = isFavorite,
                        downloadState = mapDownloadState(downloadState),
                        downloadProgress = downloadProgress
                    )
                }
                
                // 监听下载进度
                observeDownloadProgress(appId)
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "加载应用详情失败"
                    )
                }
            }
        }
    }
    
    /**
     * 监听下载进度
     */
    private fun observeDownloadProgress(appId: String) {
        viewModelScope.launch {
            downloadRepository.observeDownloadProgress(appId).collect { progress ->
                _uiState.update {
                    it.copy(
                        downloadProgress = progress,
                        downloadState = if (progress >= 1f) DownloadState.DOWNLOADED else DownloadState.DOWNLOADING
                    )
                }
            }
        }
    }
    
    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        val currentState = _uiState.value
        val app = currentState.app ?: return
        val newFavoriteState = !currentState.isFavorite
        
        _uiState.update { it.copy(isFavorite = newFavoriteState) }
        
        viewModelScope.launch {
            try {
                if (newFavoriteState) {
                    favoriteRepository.addFavorite(app.id)
                } else {
                    favoriteRepository.removeFavorite(app.id)
                }
            } catch (e: Exception) {
                // 操作失败，恢复原状态
                _uiState.update { it.copy(isFavorite = currentState.isFavorite) }
            }
        }
    }
    
    /**
     * 切换下载状态
     */
    fun toggleDownload() {
        val currentState = _uiState.value
        val app = currentState.app ?: return
        
        when (currentState.downloadState) {
            DownloadState.NOT_DOWNLOADED -> {
                // 开始下载
                _uiState.update {
                    it.copy(
                        downloadState = DownloadState.DOWNLOADING,
                        downloadProgress = 0f
                    )
                }
                
                viewModelScope.launch {
                    try {
                        downloadRepository.downloadApp(app)
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(
                                downloadState = DownloadState.NOT_DOWNLOADED,
                                errorMessage = "下载失败: ${e.message}"
                            )
                        }
                    }
                }
            }
            
            DownloadState.DOWNLOADING -> {
                // 暂停下载
                viewModelScope.launch {
                    downloadRepository.cancelDownload(app.id)
                    _uiState.update { it.copy(downloadState = DownloadState.NOT_DOWNLOADED) }
                }
            }
            
            DownloadState.DOWNLOADED -> {
                // 安装应用
                viewModelScope.launch {
                    try {
                        downloadRepository.installApp(app.id)
                        _uiState.update { it.copy(downloadState = DownloadState.INSTALLED) }
                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(errorMessage = "安装失败: ${e.message}")
                        }
                    }
                }
            }
            
            DownloadState.INSTALLED -> {
                // 打开应用
                viewModelScope.launch {
                    downloadRepository.openApp(app.packageName)
                }
            }
        }
    }
    
    /**
     * 将仓库下载状态映射为UI状态
     */
    private fun mapDownloadState(repoState: Int): DownloadState {
        return when (repoState) {
            0 -> DownloadState.NOT_DOWNLOADED
            1 -> DownloadState.DOWNLOADING
            2 -> DownloadState.DOWNLOADED
            3 -> DownloadState.INSTALLED
            else -> DownloadState.NOT_DOWNLOADED
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = "") }
    }
} 