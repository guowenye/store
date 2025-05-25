package com.smartshop.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 应用形状系统
 * 为Wear OS设备适配的圆角和形状
 */
val Shapes = Shapes(
    // 小组件的圆角，如按钮、小卡片
    small = RoundedCornerShape(8.dp),
    
    // 中型组件的圆角，如普通卡片、对话框
    medium = RoundedCornerShape(12.dp),
    
    // 大型组件的圆角，如全屏卡片、底部表单
    large = RoundedCornerShape(16.dp),
    
    // 特大组件的圆角，如大型模态框
    extraLarge = RoundedCornerShape(24.dp)
)

// 圆形形状，用于圆形按钮或头像
val CircularShape = CircleShape

// 应用特有的胶囊形状，用于标签和特殊按钮
val CapsuleShape = RoundedCornerShape(50) 