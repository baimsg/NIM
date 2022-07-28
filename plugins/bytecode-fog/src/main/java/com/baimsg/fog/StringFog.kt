package com.baimsg.fog

import com.baimsg.fog.xor.StringFogImpl
import java.io.Serializable

/**
 * Create by Baimsg on 2022/7/28
 *
 **/
class StringFog : Serializable {
    companion object {
        fun decrypt(data: ByteArray, key: ByteArray): String = StringFogImpl().decrypt(data, key)
    }
}