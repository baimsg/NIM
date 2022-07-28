package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 * 生成器用于生成安全密钥
 **/
 interface IKeyGenerator {
    /**
     * 生成安全密钥.
     *
     * @param text 内容文本将被加密.
     * @return 用于加密的安全密钥.
     */
    fun generate(text: String): ByteArray
}