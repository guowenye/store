package com.smartshop.data.repository

import com.smartshop.data.api.VersionApiService
import com.smartshop.data.model.AppVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 版本信息仓库，负责检查应用更新
 */
@Singleton
class VersionRepository @Inject constructor(
    private val versionApiService: VersionApiService
) {
    /**
     * 检查是否有新版本
     * @param currentVersion 当前应用版本
     * @return 是否有更新版本
     */
    fun checkForUpdates(currentVersion: String): Flow<Boolean> = flow {
        try {
            val response = versionApiService.getLatestVersion()
            if (response.isSuccessful && response.body() != null) {
                val latestVersion = response.body()!!
                // 比较版本号
                val hasUpdate = compareVersions(currentVersion, latestVersion.versionName)
                emit(hasUpdate)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            emit(false)
        }
    }

    /**
     * 获取最新版本信息
     */
    fun getLatestVersionInfo(): Flow<AppVersion?> = flow {
        try {
            val response = versionApiService.getLatestVersion()
            if (response.isSuccessful && response.body() != null) {
                emit(response.body())
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    /**
     * 比较版本号
     * @param currentVersion 当前版本 (如: "1.0.0")
     * @param latestVersion 最新版本 (如: "1.0.1")
     * @return 如果最新版本大于当前版本返回true
     */
    private fun compareVersions(currentVersion: String, latestVersion: String): Boolean {
        val current = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val latest = latestVersion.split(".").map { it.toIntOrNull() ?: 0 }
        
        // 确保两个列表长度相同，不足的用0补充
        val maxLength = maxOf(current.size, latest.size)
        val normalizedCurrent = current.padEnd(maxLength)
        val normalizedLatest = latest.padEnd(maxLength)
        
        // 逐个比较版本号的各部分
        for (i in 0 until maxLength) {
            when {
                normalizedLatest[i] > normalizedCurrent[i] -> return true
                normalizedLatest[i] < normalizedCurrent[i] -> return false
            }
        }
        
        // 完全相同，没有更新
        return false
    }

    /**
     * 将列表扩展到指定长度
     */
    private fun List<Int>.padEnd(length: Int): List<Int> {
        if (this.size >= length) return this
        return this + List(length - this.size) { 0 }
    }
} 