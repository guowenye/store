package com.smartshop.data.model

/**
 * 举报类型
 */
enum class ReportType {
    APP,        // 举报应用
    COMMENT     // 举报评论
}

/**
 * 举报原因
 */
enum class ReportReason {
    INAPPROPRIATE,    // 不适当内容
    SPAM,             // 垃圾信息
    VIOLENCE,         // 暴力内容
    INFRINGES_RIGHTS, // 侵犯权益
    MALWARE,          // 恶意软件
    OTHER             // 其他原因
}

/**
 * 举报数据模型
 */
data class Report(
    val id: String = "",
    val reportType: ReportType,
    val targetId: String,
    val reason: ReportReason,
    val description: String,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 