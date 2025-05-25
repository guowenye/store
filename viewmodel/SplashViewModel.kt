package com.smartshop.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartshop.data.repository.UserRepository
import com.smartshop.data.repository.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 启动页的ViewModel，负责检查登录状态和应用版本
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val versionRepository: VersionRepository
) : AndroidViewModel(application) {

    // 登录状态
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    // 应用版本
    private val _appVersion = mutableStateOf("")
    val appVersion: State<String> = _appVersion

    // 是否有新版本
    private val _hasNewVersion = mutableStateOf(false)
    val hasNewVersion: State<Boolean> = _hasNewVersion

    /**
     * 检查用户登录状态
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            userRepository.isUserLoggedIn()
                .catch { _isLoggedIn.value = false }
                .collect { isLoggedIn ->
                    _isLoggedIn.value = isLoggedIn
                }
        }
    }

    /**
     * 获取当前应用版本号
     */
    fun checkAppVersion() {
        try {
            // 获取本地版本号
            val packageInfo = application.packageManager.getPackageInfo(
                application.packageName, 0
            )
            val versionName = packageInfo.versionName
            _appVersion.value = versionName

            // 检查服务器最新版本
            checkForUpdates(versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            _appVersion.value = "未知"
        }
    }

    /**
     * 检查应用更新
     */
    private fun checkForUpdates(currentVersion: String) {
        viewModelScope.launch {
            versionRepository.checkForUpdates(currentVersion)
                .catch { _hasNewVersion.value = false }
                .collect { hasUpdate ->
                    _hasNewVersion.value = hasUpdate
                }
        }
    }
} 