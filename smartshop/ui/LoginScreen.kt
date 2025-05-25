package com.smartshop.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Checkbox
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TextField
import androidx.wear.compose.material.TextFieldDefaults
import com.smartshop.R
import com.smartshop.ui.components.Logo
import com.smartshop.ui.theme.AppColors
import com.smartshop.ui.theme.SmartShopTheme
import com.smartshop.viewmodel.LoginViewModel

/**
 * 登录页屏幕
 * 提供用户登录功能
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // 界面状态
    val uiState = viewModel.uiState
    
    // 输入状态
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    
    // 焦点管理
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    
    // 滚动状态
    val scrollState = rememberScrollState()
    
    // 背景渐变色
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A)
        )
    )
    
    // 登录成功自动导航
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onNavigateToHome()
        }
    }
    
    // 预填充保存的邮箱
    LaunchedEffect(Unit) {
        viewModel.getSavedEmail()?.let { savedEmail ->
            email = savedEmail
        }
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
                text = stringResource(R.string.login_title),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // 邮箱输入框
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.login_email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { 
                        focusManager.moveFocus(FocusDirection.Down)
                        passwordFocusRequester.requestFocus() 
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = AppColors.BackgroundVariant,
                    textColor = Color.White,
                    cursorColor = AppColors.Accent,
                    focusedIndicatorColor = AppColors.Accent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester)
                    .semantics { 
                        contentDescription = stringResource(R.string.login_email) 
                    }
            )
            
            // 密码输入框
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.login_password)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                            tint = AppColors.TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(email, password, rememberMe)
                        }
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = AppColors.BackgroundVariant,
                    textColor = Color.White,
                    cursorColor = AppColors.Accent,
                    focusedIndicatorColor = AppColors.Accent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
                    .semantics { 
                        contentDescription = stringResource(R.string.login_password) 
                    }
            )
            
            // 记住我选项
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    modifier = Modifier.semantics { 
                        contentDescription = stringResource(R.string.login_remember_me) 
                    }
                )
                Text(
                    text = stringResource(R.string.login_remember_me),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 登录按钮
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.login(email, password, rememberMe)
                },
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
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
                        text = stringResource(R.string.login_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 错误信息
            AnimatedVisibility(
                visible = uiState.errorMessage.isNotBlank(),
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 注册入口
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.login_no_account),
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp
                )
                
                Text(
                    text = stringResource(R.string.login_register),
                    color = AppColors.Accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .semantics { 
                            contentDescription = stringResource(R.string.login_register) 
                        }
                        .clip(CircleShape)
                        .padding(4.dp)
                        .clickable(onClick = onNavigateToRegister)
                )
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
fun LoginScreenPreview() {
    SmartShopTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToHome = {}
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
fun LoginScreenRoundPreview() {
    SmartShopTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToHome = {}
        )
    }
} 