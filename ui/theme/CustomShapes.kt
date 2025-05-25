package com.smartshop.ui.theme

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 星形Shape
 */
object StarShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(createStarPath(size))
    }
    
    private fun createStarPath(size: Size): Path {
        val path = Path()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.width / 2
        val innerRadius = outerRadius * 0.4f
        val numPoints = 5
        
        val angleStep = 2 * PI / numPoints
        var angle = -PI / 2  // 从顶部开始
        
        // 移动到第一个点
        val startX = (centerX + cos(angle) * outerRadius).toFloat()
        val startY = (centerY + sin(angle) * outerRadius).toFloat()
        path.moveTo(startX, startY)
        
        // 绘制其余点
        for (i in 1 until numPoints * 2) {
            angle += angleStep / 2
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val x = (centerX + cos(angle) * radius).toFloat()
            val y = (centerY + sin(angle) * radius).toFloat()
            path.lineTo(x, y)
        }
        
        path.close()
        return path
    }
}

/**
 * 六边形Shape
 */
object HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(createHexagonPath(size))
    }
    
    private fun createHexagonPath(size: Size): Path {
        val path = Path()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(size.width, size.height) / 2
        val numPoints = 6
        
        val angleStep = 2 * PI / numPoints
        var angle = 0.0  // 从右侧开始
        
        // 移动到第一个点
        val startX = (centerX + cos(angle) * radius).toFloat()
        val startY = (centerY + sin(angle) * radius).toFloat()
        path.moveTo(startX, startY)
        
        // 绘制其余点
        for (i in 1 until numPoints) {
            angle += angleStep
            val x = (centerX + cos(angle) * radius).toFloat()
            val y = (centerY + sin(angle) * radius).toFloat()
            path.lineTo(x, y)
        }
        
        path.close()
        return path
    }
}

/**
 * 聊天气泡Shape
 */
object ChatBubbleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(createChatBubblePath(size, layoutDirection))
    }
    
    private fun createChatBubblePath(size: Size, layoutDirection: LayoutDirection): Path {
        val path = Path()
        val width = size.width
        val height = size.height
        val cornerRadius = minOf(width, height) * 0.2f
        val tailWidth = width * 0.1f
        val tailHeight = height * 0.2f
        
        val isRtl = layoutDirection == LayoutDirection.Rtl
        
        // 开始绘制气泡主体
        if (isRtl) {
            // 右下
            path.moveTo(width, height - cornerRadius)
            path.arcTo(
                Rect(width - 2 * cornerRadius, height - 2 * cornerRadius, width, height),
                0f, 90f, false
            )
            
            // 左下
            path.lineTo(cornerRadius, height)
            path.arcTo(
                Rect(0f, height - 2 * cornerRadius, 2 * cornerRadius, height),
                90f, 90f, false
            )
            
            // 左上
            path.lineTo(0f, cornerRadius)
            path.arcTo(
                Rect(0f, 0f, 2 * cornerRadius, 2 * cornerRadius),
                180f, 90f, false
            )
            
            // 右上
            path.lineTo(width - tailWidth - cornerRadius, 0f)
            path.arcTo(
                Rect(width - tailWidth - 2 * cornerRadius, 0f, width - tailWidth, 2 * cornerRadius),
                270f, 90f, false
            )
            
            // 气泡尾巴
            path.lineTo(width - tailWidth, tailHeight)
            path.lineTo(width, 0f)
            path.lineTo(width, height - cornerRadius)
        } else {
            // 左下
            path.moveTo(0f, height - cornerRadius)
            path.arcTo(
                Rect(0f, height - 2 * cornerRadius, 2 * cornerRadius, height),
                180f, -90f, false
            )
            
            // 右下
            path.lineTo(width - cornerRadius, height)
            path.arcTo(
                Rect(width - 2 * cornerRadius, height - 2 * cornerRadius, width, height),
                90f, -90f, false
            )
            
            // 右上
            path.lineTo(width, cornerRadius)
            path.arcTo(
                Rect(width - 2 * cornerRadius, 0f, width, 2 * cornerRadius),
                0f, -90f, false
            )
            
            // 左上
            path.lineTo(tailWidth + cornerRadius, 0f)
            path.arcTo(
                Rect(tailWidth, 0f, tailWidth + 2 * cornerRadius, 2 * cornerRadius),
                270f, -90f, false
            )
            
            // 气泡尾巴
            path.lineTo(tailWidth, tailHeight)
            path.lineTo(0f, 0f)
            path.lineTo(0f, height - cornerRadius)
        }
        
        path.close()
        return path
    }
} 