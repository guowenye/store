package com.smartshop.data.model

/**
 * 应用分类数据模型
 */
data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val appCount: Int,
    val color: Long,
    val sortOrder: Int
) 