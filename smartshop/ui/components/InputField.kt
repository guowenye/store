package com.smartshop.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.IconButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TextField
import androidx.wear.compose.material.TextFieldDefaults
import com.smartshop.ui.theme.AppColors

/**
 * 通用输入字段组件
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val visualTransformation = when {
        isPassword && !passwordVisible -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = AppColors.TextSecondary
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                        tint = AppColors.TextSecondary
                    )
                }
            }
        } else null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = AppColors.BackgroundVariant,
            textColor = Color.White,
            cursorColor = AppColors.Accent,
            focusedIndicatorColor = if (isError) Color.Red else AppColors.Accent,
            unfocusedIndicatorColor = if (isError) Color.Red.copy(alpha = 0.5f) else Color.Transparent
        ),
        isError = isError,
        singleLine = true,
        enabled = enabled,
        modifier = modifier.semantics { 
            contentDescription = label 
        }
    )
    
    if (isError && errorMessage.isNotBlank()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}