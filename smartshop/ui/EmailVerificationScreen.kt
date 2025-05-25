package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.AlertDialog
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.smartshop.R
import com.smartshop.ui.components.Logo
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.util.EmailValidator
import com.smartshop.viewmodel.EmailVerificationViewModel
import kotlinx.coroutines.delay

/**
 * 邮箱验证页屏幕
 * 提供邮箱验证码输入和验证功能
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailVerificationScreen(
    email: String,
    viewModel: EmailVerificationViewModel = viewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // 界面状态
    val uiState by viewModel.uiState.collectAsState()
    
    // 验证码输入状态
    val codeLength = 6
    var code by rememberSaveable { mutableStateOf("") }
    val focusRequesters = remember { List(codeLength) { FocusRequester() } }
    
    // 显示跳过确认对话框
    var showSkipDialog by remember { mutableStateOf(false) }
    
    // 焦点管理
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // 滚动状态
    val scrollState = rememberScrollState()
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 设置邮箱和发送验证码
    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
        viewModel.sendVerificationCode()
    }
    
    // 验证成功自动导航
    LaunchedEffect(uiState.isVerificationSuccess) {
        if (uiState.isVerificationSuccess) {
            delay(1000) // 显示成功状态一会
            onNavigateToHome()
        }
    }
    
    // 显示跳过确认对话框
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = {
                Text(
                    text = "跳过验证？",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            content = {
                Text(
                    text = "邮箱未验证将无法发表评论和上传应用",
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipDialog = false
                        onNavigateToHome()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.Gradient1
                    )
                ) {
                    Text("确认跳过")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSkipDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray
                    )
                ) {
                    Text("继续验证")
                }
            }
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
                .onRotaryScrollEvent { event ->
                    scrollState.scrollBy(event.verticalScrollPixels)
                    true
                }
        ) {
            // Logo
            Spacer(modifier = Modifier.height(16.dp))
            Logo(size = 64.dp)
            
            // 标题
            Text(
                text = stringResource(R.string.verification_title),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // 副标题
            Text(
                text = stringResource(R.string.verification_subtitle),
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            // 邮箱显示
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = AppColors.Accent,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = email,
                    color = AppColors.Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 验证码输入
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.verification_code),
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until codeLength) {
                        OtpCell(
                            value = if (i < code.length) code[i].toString() else "",
                            isFocused = i == code.length,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty()) {
                                    // 删除操作
                                    if (code.isNotEmpty()) {
                                        code = code.dropLast(1)
                                        if (i > 0) {
                                            focusRequesters[i - 1].requestFocus()
                                        }
                                    }
                                } else {
                                    // 只允许数字输入
                                    val filteredValue = newValue.filter { it.isDigit() }
                                    if (filteredValue.isNotEmpty()) {
                                        val newCode = code + filteredValue.last()
                                        code = newCode.take(codeLength)
                                        
                                        // 移动到下一格或提交
                                        if (code.length < codeLength && i < codeLength - 1) {
                                            focusRequesters[i + 1].requestFocus()
                                        } else if (code.length == codeLength) {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            // 自动提交验证码
                                            viewModel.verifyEmail(code)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesters[i])
                                .onKeyEvent { event ->
                                    if (event.key == Key.Backspace && code.isNotEmpty() && i == code.length) {
                                        code = code.dropLast(1)
                                        if (i > 0) {
                                            focusRequesters[i - 1].requestFocus()
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                        )
                    }
                }
            }
            
            // 剩余尝试次数
            Text(
                text = stringResource(
                    R.string.verification_remaining_attempts,
                    EmailValidator.getVerificationAttemptsLimit() - uiState.verificationAttempts
                ),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 验证按钮
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.verifyEmail(code)
                },
                enabled = !uiState.isLoading && code.length == codeLength,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppColors.Gradient1,
                    contentColor = Color.White,
                    disabledBackgroundColor = AppColors.Gradient1.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.verification_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 重发验证码
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (uiState.resendCooldown > 0) {
                    Text(
                        text = stringResource(
                            R.string.verification_countdown,
                            uiState.resendCooldown
                        ),
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.verification_resend),
                        color = AppColors.Accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                enabled = !uiState.isLoading,
                                onClick = { viewModel.sendVerificationCode() }
                            )
                            .padding(4.dp)
                    )
                }
            }
            
            // 跳过验证
            Text(
                text = stringResource(R.string.verification_skip),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(CircleShape)
                    .clickable { showSkipDialog = true }
                    .padding(4.dp)
            )
            
            // 错误信息
            AnimatedVisibility(
                visible = uiState.errorMessage.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colors.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            
            // 成功信息
            AnimatedVisibility(
                visible = uiState.isVerificationSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = stringResource(R.string.verification_success),
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * 验证码输入单元格
 */
@Composable
fun OtpCell(
    value: String,
    isFocused: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isFocused) AppColors.Accent else AppColors.Divider
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.BackgroundVariant)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
    ) {
        BasicTextField(
            value = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            ),
            onValueChange = { textFieldValue ->
                onValueChange(textFieldValue.text)
            },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            cursorBrush = SolidColor(AppColors.Accent),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
        
        if (value.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = if (isFocused) AppColors.Accent else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "方形手表预览"
)
@Composable
fun EmailVerificationScreenPreview() {
    SmartShopTheme {
        EmailVerificationScreen(
            email = "user@example.com",
            onNavigateToHome = {},
            onNavigateToLogin = {}
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
fun EmailVerificationScreenRoundPreview() {
    SmartShopTheme {
        EmailVerificationScreen(
            email = "user@example.com",
            onNavigateToHome = {},
            onNavigateToLogin = {}
        )
    }
} 