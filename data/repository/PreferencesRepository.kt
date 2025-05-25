package com.smartshop.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 为Context类扩展dataStore属性
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * 偏好设置仓库，管理应用设置和搜索历史
 */
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val NOTIFICATION_ENABLED_KEY = stringPreferencesKey("notification_enabled")
        private const val HISTORY_SEPARATOR = "|||" // 用于分隔历史记录项的特殊字符串
    }

    /**
     * 获取搜索历史
     */
    suspend fun getSearchHistory(): List<String> {
        val historyString = context.dataStore.data
            .map { preferences ->
                preferences[SEARCH_HISTORY_KEY] ?: ""
            }
            .first()
        
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(HISTORY_SEPARATOR)
        }
    }

    /**
     * 保存搜索历史
     */
    suspend fun saveSearchHistory(history: List<String>) {
        val historyString = history.joinToString(HISTORY_SEPARATOR)
        context.dataStore.edit { preferences ->
            preferences[SEARCH_HISTORY_KEY] = historyString
        }
    }

    /**
     * 清除搜索历史
     */
    suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }

    /**
     * 获取主题设置
     */
    suspend fun getTheme(): String {
        return context.dataStore.data
            .map { preferences ->
                preferences[THEME_KEY] ?: "dark" // 默认暗色主题
            }
            .first()
    }

    /**
     * 保存主题设置
     */
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }

    /**
     * 获取通知设置
     */
    suspend fun getNotificationEnabled(): Boolean {
        return context.dataStore.data
            .map { preferences ->
                (preferences[NOTIFICATION_ENABLED_KEY] ?: "true") == "true"
            }
            .first()
    }

    /**
     * 保存通知设置
     */
    suspend fun saveNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled.toString()
        }
    }
} 