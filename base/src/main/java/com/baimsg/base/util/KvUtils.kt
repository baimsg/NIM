package com.baimsg.base.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * Create by Baimsg on 2022/2/23
 *
 **/
object KvUtils {

    val mmkv: MMKV by lazy {
        MMKV.defaultMMKV()
    }

    /**
     * 写入数据
     * @param key 键
     * @param value 值
     * @return 写入结果
     */
    fun put(key: String, value: Any): Boolean {
        return when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            else -> false
        }
    }

    fun <T : Parcelable> put(key: String, t: T): Boolean {
        return mmkv.encode(key, t)
    }

    fun put(key: String, sets: Set<String>): Boolean {
        return mmkv.encode(key, sets)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return mmkv.decodeDouble(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0F): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    fun getByteArray(key: String, defaultValue: ByteArray = byteArrayOf()): ByteArray {
        return mmkv.decodeBytes(key) ?: defaultValue
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }

    inline fun <reified T : Parcelable> getParcelable(key: String): T? {
        return mmkv.decodeParcelable(key, T::class.java)
    }

    fun getStringSet(key: String): Set<String> {
        return mmkv.decodeStringSet(key, setOf()) ?: setOf()
    }

    fun removeKey(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv.clearAll()
    }

}