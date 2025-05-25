package com.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.model.Report
import com.smartshop.data.model.ReportReason
import com.smartshop.data.model.ReportType
import com.smartshop.data.repository.ReportRepository
import com.smartshop.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 举报页面UI状态
 */
data class ReportUiState(
    val reportType: ReportType? = null,
    val targetId: String = "",
    val selectedReason: ReportReason? = null,
    val description: String = "",
    val showDescriptionError: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val submitError: String = ""
)

/**
 * 举报页面ViewModel，负责管理举报表单和提交举报
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()
    
    /**
     * 初始化举报数据
     */
    fun initReport(reportType: ReportType, targetId: String) {
        _uiState.update {
            it.copy(
                reportType = reportType,
                targetId = targetId
            )
        }
    }
    
    /**
     * 选择举报原因
     */
    fun selectReason(reason: ReportReason) {
        _uiState.update {
            it.copy(
                selectedReason = reason
            )
        }
    }
    
    /**
     * 更新举报描述
     */
    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                showDescriptionError = false
            )
        }
    }
    
    /**
     * 验证表单
     */
    private fun validateForm(): Boolean {
        val state = _uiState.value
        
        // 验证是否选择了举报原因
        if (state.selectedReason == null) {
            return false
        }
        
        // 验证描述是否为空（当选择"其他"原因时，必须填写描述）
        if (state.selectedReason == ReportReason.OTHER && state.description.isBlank()) {
            _uiState.update {
                it.copy(showDescriptionError = true)
            }
            return false
        }
        
        return true
    }
    
    /**
     * 提交举报
     */
    fun submitReport() {
        // 验证表单
        if (!validateForm()) {
            return
        }
        
        // 设置提交中状态
        _uiState.update {
            it.copy(isSubmitting = true, submitError = "")
        }
        
        viewModelScope.launch {
            try {
                // 获取当前用户ID
                val userId = userRepository.getCurrentUserId()
                
                // 创建举报数据
                val report = Report(
                    reportType = _uiState.value.reportType!!,
                    targetId = _uiState.value.targetId,
                    reason = _uiState.value.selectedReason!!,
                    description = _uiState.value.description,
                    userId = userId
                )
                
                // 提交举报
                reportRepository.submitReport(report)
                
                // 更新UI状态为提交成功
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSubmitSuccess = true
                    )
                }
                
                // 3秒后自动关闭成功提示
                delay(3000)
                _uiState.update {
                    it.copy(isSubmitSuccess = false)
                }
                
            } catch (e: Exception) {
                // 更新UI状态为提交失败
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitError = e.message ?: "提交举报失败"
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.update {
            it.copy(submitError = "")
        }
    }
} 