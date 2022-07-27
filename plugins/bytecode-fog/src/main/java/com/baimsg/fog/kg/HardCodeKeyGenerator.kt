package com.baimsg.fog.kg

import com.baimsg.fog.ext.IKeyGenerator
import java.nio.charset.StandardCharsets

/**
 * Create by Baimsg on 2022/7/27
 *
 **/
class HardCodeKeyGenerator(private val keys: ByteArray) : IKeyGenerator {

    constructor(key: String) : this(key.toByteArray(StandardCharsets.UTF_8))

    override fun generate(text: String): ByteArray {
        return keys
    }

}