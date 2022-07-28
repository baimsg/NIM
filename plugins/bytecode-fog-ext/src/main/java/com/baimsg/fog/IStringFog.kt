package com.baimsg.fog

/**
 * Create by Baimsg on 2022/7/25
 * 如何加密和解密字符串的接口
 **/
interface IStringFog {
    /**
     * 使用特殊密钥加密数据.
     *
     * @param data The original data.
     * @param key Encrypt key.
     * @return The encrypted data.
     */
    fun encrypt(data: String, key: ByteArray): ByteArray

    /**
     * 通过特殊密钥将数据解密到源.
     *
     * @param data The encrypted data.
     * @param key Encrypt key.
     * @return The original data.
     */
    fun decrypt(data: ByteArray, key: ByteArray): String

    /**
     * 字符串是否应该加密.
     *
     * @param data The original data.
     * @return 如果要跳过此字符串，请返回 false.
     */
    fun shouldFog(data: String): Boolean
}