package com.baimsg.chat.util.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager
import com.baimsg.base.util.KvUtils
import com.baimsg.base.util.extensions.logD
import com.baimsg.base.util.extensions.toMd5String
import java.util.*

fun Context.copy(message: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("simple text", message)
    clipboard.setPrimaryClip(clip)
}

@SuppressLint("HardwareIds")
fun Context.androidId(): String {
    val localId = KvUtils.getString("KEY_ANDROID_ID", "")
    if (localId.isNotBlank()) {
        return localId
    }
    val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    val tmDevice = imei()
    val macAd = mac()
    var serialNum = ""
    try {
        val c = Class.forName("android.os.SystemProperties")
        val get = c.getMethod("get", String::class.java, String::class.java)
        serialNum = get.invoke(c, "ro.serialno", "unknown") as String
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    if (androidId.isNullOrBlank() && tmDevice.isBlank() && macAd.isBlank() && serialNum.isBlank()) {
        val uuid = UUID.randomUUID().toString().toMd5String()
        KvUtils.put("KEY_ANDROID_ID", uuid)
        return uuid
    }
    val deviceUuid = UUID(
        androidId.hashCode().toLong(),
        (tmDevice.hashCode() or macAd.hashCode() or serialNum.hashCode()).toLong()
    ).toString().toMd5String()
    KvUtils.put("KEY_ANDROID_ID", deviceUuid)
    return deviceUuid
}

@SuppressLint("MissingPermission")
fun Context.imei(): String {
    return if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
            Manifest.permission.READ_PHONE_STATE,
            packageName
        )
    ) {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            logD("尝试获取imei")
            tm.imei
        } catch (e: Exception) {
            try {
                logD("获取IMEI失败,尝试获取MEID")
                tm.meid
            } catch (e: Exception) {
                try {
                    logD("获取meId失败,尝试获取deviceId")
                    tm.deviceId
                } catch (e: Exception) {
                    ""
                }
            }
        }
    } else ""
}

@SuppressLint("MissingPermission")
fun Context.mac(): String {
    return try {
        val manager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val macAddress = manager.connectionInfo.macAddress
        if (macAddress.isNullOrBlank()) throw NullPointerException()
        else macAddress
    } catch (e: Exception) {
        ""
    }
}

