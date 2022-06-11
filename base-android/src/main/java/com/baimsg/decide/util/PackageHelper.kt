package com.baimsg.decide.util

import android.content.Context

object PackageHelper {
    private var appVersionName: String? = null
    private var majorMinorVersion: String? = null
    private var majorVersion = -1
    private var minorVersion = -1
    private var fixVersion = -1

    /**
     * manifest 中的 versionName 字段
     */
    fun getAppVersion(context: Context): String {
        if (appVersionName == null) {
            val manager = context.packageManager
            try {
                val info = manager.getPackageInfo(context.packageName, 0)
                appVersionName = info.versionName
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return appVersionName ?: ""
    }

    /**
     * 获取 App 的主版本与次版本号。比如说 3.1.2 中的 3.1
     */
    fun getMajorMinorVersion(context: Context): String? {
        if (majorMinorVersion == null || majorMinorVersion == "") {
            majorMinorVersion = getMajorVersion(context).toString() + "." + getMinorVersion(context)
        }
        return majorMinorVersion
    }

    /**
     * 读取 App 的主版本号，例如 3.1.2，主版本号是 3
     */
    private fun getMajorVersion(context: Context): Int {
        if (majorVersion == -1) {
            val appVersion = getAppVersion(context)
            val parts = appVersion.split("\\.".toRegex()).toTypedArray()
            if (parts.isNotEmpty()) {
                majorVersion = parts[0].toInt()
            }
        }
        return majorVersion
    }

    /**
     * 读取 App 的次版本号，例如 3.1.2，次版本号是 1
     */
    private fun getMinorVersion(context: Context): Int {
        if (minorVersion == -1) {
            val appVersion = getAppVersion(context)
            val parts = appVersion.split("\\.".toRegex()).toTypedArray()
            if (parts.size >= 2) {
                minorVersion = parts[1].toInt()
            }
        }
        return minorVersion
    }

    /**
     * 读取 App 的修正版本号，例如 3.1.2，修正版本号是 2
     */
    fun getFixVersion(context: Context): Int {
        if (fixVersion == -1) {
            val appVersion = getAppVersion(context)
            val parts = appVersion.split("\\.".toRegex()).toTypedArray()
            if (parts.size >= 3) {
                fixVersion = parts[2].toInt()
            }
        }
        return fixVersion
    }
}