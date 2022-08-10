package com.baimsg.base.util


import okhttp3.internal.and
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Create by Baimsg on 2022/8/10
 *
 **/
object SafeUtils {

    fun decode(key: String, body: String): ByteArray {
        return try {
            val data = loweringData(body)
            val iv = data.copyOfRange(0, 16)
            val input = data.copyOfRange(16, data.size)
            val parseKeys = parseKey(key.toByteArray(Charsets.UTF_8))
            val ivParameterSpec = IvParameterSpec(iv)
            val secretKeySpec = SecretKeySpec(parseKeys[0], "AES")
            val instance = Cipher.getInstance("AES/CFB/NoPadding")
            instance.init(2, secretKeySpec, ivParameterSpec)
            val bytes = instance.doFinal(input)
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    fun encode(key: String, body: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/CFB/NoPadding")
            val parseKeys = parseKey(key.toByteArray(charset("UTF-8")))
            cipher.init(1, SecretKeySpec(parseKeys[0], "AES"), IvParameterSpec(parseKeys[1]))
            val ivs = cipher.iv
            val bytes = cipher.doFinal(body.toByteArray(Charsets.UTF_8))
            val dest = ByteArray(ivs.size + bytes.size)
            System.arraycopy(ivs, 0, dest, 0, ivs.size)
            System.arraycopy(bytes, 0, dest, ivs.size, bytes.size)
            val sb = StringBuilder()
            for (byte in dest) {
                val hexString = Integer.toHexString(byte and 255)
                sb.append(if (hexString.length == 1) "0$hexString" else hexString)
            }
            sb.toString().uppercase(Locale.getDefault())
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun loweringData(str: String): ByteArray {
        val length = str.length
        if (length % 2 == 1) {
            return ByteArray(0)
        }
        val size = length / 2
        val bytes = ByteArray(size)
        for (index in 0 until size) {
            val startIndex = index * 2
            bytes[index] = str.substring(startIndex, startIndex + 2).toInt(16).toByte()
        }
        return bytes
    }

    /**
     * 解析密钥
     * @param keys 密钥字节数组
     */
    private fun parseKey(
        keys: ByteArray?
    ): List
    <ByteArray> {
        val digest = MessageDigest.getInstance("md5")
        var high = 32
        val highBytes = ByteArray(high)
        var low = 16
        val lowBytes = ByteArray(low)
        val data = mutableListOf(highBytes, lowBytes)
        if (keys == null) {
            return data
        }
        var input = ByteArray(0)
        var a = 0
        var b = 0
        var c = 0
        while (true) {
            if (high == 0 && low == 0) {
                break
            }
            digest.reset()
            if (a > 0) {
                digest.update(input)
            }
            digest.update(keys)
            input = digest.digest()
            var e = 0
            input.let { inp ->
                if (high > 0) {
                    while (high != 0 && e != inp.size) {
                        highBytes[b] = inp[e]
                        high--
                        e++
                        b++
                    }
                }
                if (low > 0 && e != inp.size) {
                    while (low != 0 && e != inp.size) {
                        lowBytes[c] = inp[e]
                        low--
                        e++
                        c++
                    }
                }
            }
            a++
        }
        input.apply {
            forEachIndexed { index, _ ->
                set(index, 0)
            }
        }
        return data
    }


}