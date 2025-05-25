package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TextField
import androidx.wear.compose.material.TextFieldDefaults
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.smartshop.R
import com.smartshop.data.model.ReportReason
import com.smartshop.data.model.ReportType
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.ReportViewModel
import kotlin.math.roundToInt

/**
 * 举报页面
 * 允许用户举报不适当的内容
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = viewModel(),
    reportType: ReportType,
    targetId: String,
    targetName: String,
    onNavigateBack: () -> Unit
) {
    // 加载举报数据
    LaunchedEffect(Unit) {
        viewModel.initReport(reportType, targetId)
    }
    
    // 获取UI状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 滚动状态
    val scrollState = rememberScrollState()
    
    // SwipeToDismiss状态，用于支持手表的边缘手势返回
    val swipeState = rememberSwipeToDismissBoxState()
    
    // 获取应用上下文
    val context = LocalContext.current
    
    SwipeToDismissBox(
        state = swipeState,
        backgroundKey = Unit,
        onDismissed = { onNavigateBack() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground),
            timeText = {
                // 空实现，不显示系统时间
            },
            positionIndicator = {
                // 空实现，不显示系统位置指示器
            },
            vignette = {
                // 空实现，不显示系统晕影效果
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent { event ->
                        scrollState.scrollBy(event.verticalScrollPixels)
                        true
                    }
            ) {
                // 返回按钮
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppColors.BackgroundVariant.copy(alpha = 0.7f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.button_back),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // 主内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // 顶部留白，为返回按钮腾出空间
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // 标题
                    Text(
                        text = stringResource(R.string.report_title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 子标题 - 被举报内容
                    Text(
                        text = if (reportType == ReportType.APP) 
                            stringResource(R.string.report_app_subtitle, targetName)
                        else 
                            stringResource(R.string.report_comment_subtitle, targetName),
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 举报原因标题
                    Text(
                        text = stringResource(R.string.report_reason_title),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 举报原因选项
                    ReportReasonOptions(
                        selectedReason = uiState.selectedReason,
                        onReasonSelected = { viewModel.selectReason(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 举报描述标题
                    Text(
                        text = stringResource(R.string.report_description_title),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 举报描述输入框
                    TextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.report_description_hint),
                                fontSize = 12.sp,
                                color = AppColors.TextSecondary
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = AppColors.BackgroundVariant,
                            textColor = Color.White,
                            cursorColor = AppColors.Accent,
                            focusedIndicatorColor = AppColors.Accent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = if (uiState.showDescriptionError) Color.Red.copy(alpha = 0.7f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    
                    // 错误提示
                    if (uiState.showDescriptionError) {
                        Text(
                            text = stringResource(R.string.report_description_error),
                            color = Color.Red.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 提交按钮
                    Button(
                        onClick = { viewModel.submitReport() },
                        enabled = !uiState.isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppColors.Accent,
                            contentColor = Color.White,
                            disabledBackgroundColor = AppColors.Accent.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(text = stringResource(R.string.button_submit))
                        }
                    }
                    
                    // 底部留白
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 举报成功对话框
                AnimatedVisibility(
                    visible = uiState.isSubmitSuccess,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ReportSuccessDialog(
                        onClose = { onNavigateBack() }
                    )
                }
                
                // 举报失败对话框
                if (uiState.submitError.isNotEmpty()) {
                    ReportErrorDialog(
                        errorMessage = uiState.submitError,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

/**
 * 举报原因选项
 */
@Composable
fun ReportReasonOptions(
    selectedReason: ReportReason?,
    onReasonSelected: (ReportReason) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .padding(vertical = 8.dp)
    ) {
        ReportReason.values().forEach { reason ->
            ReportReasonItem(
                reason = reason,
                isSelected = reason == selectedReason,
                onSelected = { onReasonSelected(reason) }
            )
        }
    }
}

/**
 * 举报原因项
 */
@Composable
fun ReportReasonItem(
    reason: ReportReason,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = stringResource(
                when (reason) {
                    ReportReason.INAPPROPRIATE -> R.string.report_reason_inappropriate
                    ReportReason.SPAM -> R.string.report_reason_spam
                    ReportReason.VIOLENCE -> R.string.report_reason_violence
                    ReportReason.INFRINGES_RIGHTS -> R.string.report_reason_infringes_rights
                    ReportReason.MALWARE -> R.string.report_reason_malware
                    ReportReason.OTHER -> R.string.report_reason_other
                }
            ),
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

/**
 * 举报成功对话框
 */
@Composable
fun ReportSuccessDialog(
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(24.dp)
        ) {
            // 成功图标
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 标题
            Text(
                text = stringResource(R.string.report_success_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 描述
            Text(
                text = stringResource(R.string.report_success_message),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 关闭按钮
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppColors.Accent,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.button_close))
            }
        }
    }
}

/**
 * 举报错误对话框
 */
@Composable
fun ReportErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.BackgroundVariant)
                .padding(24.dp)
        ) {
            // 错误图标
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 标题
            Text(
                text = stringResource(R.string.report_error_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 错误信息
            Text(
                text = errorMessage,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 关闭按钮
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.button_close))
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun ReportScreenPreview() {
    SmartShopTheme {
        ReportScreen(
            reportType = ReportType.APP,
            targetId = "123",
            targetName = "测试应用",
            onNavigateBack = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=195dp,height=195dp,shape=circle",
    name = "圆形手表预览"
)
@Composable
fun ReportScreenRoundPreview() {
    SmartShopTheme {
        ReportScreen(
            reportType = ReportType.APP,
            targetId = "123",
            targetName = "测试应用",
            onNavigateBack = {}
        )
    }
} 