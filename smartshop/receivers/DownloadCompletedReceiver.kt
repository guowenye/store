package com.smartshop.receivers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 下载完成广播接收器
 * 监听系统下载管理器的下载完成事件
 */
@AndroidEntryPoint
class DownloadCompletedReceiver : BroadcastReceiver() {
    
    /**
     * 接收下载完成广播
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId != -1L) {
                CoroutineScope(Dispatchers.Main).launch {
                    // 处理下载完成事件
                    handleDownloadComplete(context, downloadId)
                }
            }
        }
    }
    
    /**
     * 处理下载完成事件
     */
    private suspend fun handleDownloadComplete(context: Context, downloadId: Long) {
        // 获取下载信息
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        
        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIndex)
            
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // 下载成功，可以显示安装提示或自动安装
                showInstallNotification(context, downloadId)
            }
        }
        cursor.close()
    }
    
    /**
     * 显示安装通知
     */
    private fun showInstallNotification(context: Context, downloadId: Long) {
        // 实际应用中，这里应该创建一个通知，让用户点击进行安装
        // 或者自动打开APK安装界面
    }
    
    companion object {
        /**
         * 注册广播接收器
         */
        fun register(context: Context): DownloadCompletedReceiver {
            val receiver = DownloadCompletedReceiver()
            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            context.registerReceiver(receiver, filter)
            return receiver
        }
        
        /**
         * 注销广播接收器
         */
        fun unregister(context: Context, receiver: DownloadCompletedReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
} 