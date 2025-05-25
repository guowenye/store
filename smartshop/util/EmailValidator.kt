package com.smartshop.util

import android.util.Patterns

/**
 * 邮箱验证工具类
 */
object EmailValidator {
    // 常见邮箱域名白名单
    private val DOMAIN_WHITELIST = listOf(
        "gmail.com",
        "outlook.com",
        "hotmail.com",
        "yahoo.com",
        "163.com",
        "126.com",
        "qq.com",
        "foxmail.com",
        "sina.com",
        "sohu.com",
        "yeah.net",
        "139.com",
        "189.cn",
        "aliyun.com",
        "icloud.com"
    )
    
    /**
     * 验证邮箱格式是否有效
     */
    fun isValid(email: String): Boolean {
        if (email.isBlank()) return false
        
        // 使用Android提供的Pattern检查基本格式
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        
        // 检查邮箱后缀是否在白名单中
        val domain = email.substringAfter('@', "")
        if (domain.isBlank()) return false
        
        // 如果配置了白名单，则检查域名是否在白名单中
        return DOMAIN_WHITELIST.isEmpty() || DOMAIN_WHITELIST.any { domain.equals(it, ignoreCase = true) }
    }
    
    /**
     * 获取认证次数限制
     */
    fun getVerificationAttemptsLimit(): Int {
        return 3
    }
}