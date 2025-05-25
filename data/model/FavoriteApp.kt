package com.smartshop.data.model

/**
 * 收藏应用数据模型
 * 包含了收藏应用的基本信息
 */
data class FavoriteApp(
    val id: String,
    val name: String,
    val packageName: String,
    val developer: String,
    val icon: String,
    val rating: Float,
    val favoriteTime: Long
) 