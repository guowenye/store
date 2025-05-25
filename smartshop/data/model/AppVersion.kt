package com.smartshop.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 应用版本数据模型
 */
@JsonClass(generateAdapter = true)
data class AppVersion(
    @Json(name = "version_name") val versionName: String,
    @Json(name = "version_code") val versionCode: Int,
    @Json(name = "release_date") val releaseDate: Long,
    @Json(name = "download_url") val downloadUrl: String,
    @Json(name = "force_update") val forceUpdate: Boolean = false,
    @Json(name = "update_description") val updateDescription: String = "",
    @Json(name = "min_android_version") val minAndroidVersion: Int = 19 // 对应Android 4.4
)