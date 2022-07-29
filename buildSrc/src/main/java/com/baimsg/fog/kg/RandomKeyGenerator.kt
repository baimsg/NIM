package com.baimsg.fog.kg

import com.baimsg.fog.IKeyGenerator
import java.io.Serializable
import java.security.SecureRandom

/**
 * Create by Baimsg on 2022/7/27
 * 随机生成密钥
 **/
class RandomKeyGenerator @JvmOverloads constructor(private val mLength: Int = 8) : IKeyGenerator,
    Serializable {
    private val mSecureRandom: SecureRandom = SecureRandom()

    override fun generate(text: String): ByteArray {
        val key = ByteArray(mLength)
        mSecureRandom.nextBytes(key)
        return key
    }
}