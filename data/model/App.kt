package com.smartshop.data.model

/**
 * 应用数据模型
 */
data class App(
    val id: String,
    val name: String,
    val packageName: String,
    val version: String,
    val versionCode: Int,
    val size: Long,
    val icon: String,
    val developer: String,
    val description: String,
    val category: String,
    val screenshots: List<String>,
    val downloadCount: Int,
    val rating: Float,
    val status: Int,
    val apkUrl: String,
    val updatedAt: Long
) 