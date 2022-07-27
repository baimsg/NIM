package com.baimsg.fog.xor

import com.baimsg.fog.IStringFog
import java.nio.charset.StandardCharsets
import kotlin.experimental.xor

/**
 * Create by Baimsg on 2022/7/27
 * StringFog xor encrypt and decrypt implementation
 **/
class StringFogImpl : IStringFog {
    override fun encrypt(data: String, key: ByteArray): ByteArray {
        return xor(data.toByteArray(StandardCharsets.UTF_8), key)
    }

    override fun decrypt(data: ByteArray, key: ByteArray): String {
        return String(xor(data, key), StandardCharsets.UTF_8)
    }

    override fun shouldFog(data: String): Boolean {
        return true
    }

    companion object {
        private fun xor(data: ByteArray, key: ByteArray): ByteArray {
            val len = data.size
            val lenKey = key.size
            var i = 0
            var j = 0
            while (i < len) {
                if (j >= lenKey) {
                    j = 0
                }
                data[i] = (data[i] xor key[j])
                i++
                j++
            }
            return data
        }
    }
}
