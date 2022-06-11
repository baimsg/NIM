package com.baimsg.base.util.extensions

import java.security.MessageDigest

/**
 * Create by Baimsg on 2022/3/29
 *
 **/

private val HEX_DIGITS = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
)

fun ByteArray.toMd5String(): String = toMd5Bytes().toHexString()

fun ByteArray.toMd5Bytes(): ByteArray {
    val digest: MessageDigest = MessageDigest.getInstance("MD5")
    digest.update(this)
    return digest.digest()
}

fun ByteArray.toHexString(): String {
    val sb = StringBuilder(this.size * 2)
    for (byte in this) {
        sb.append(HEX_DIGITS[byte.toInt() ushr 4 and 0xf])
        sb.append(HEX_DIGITS[byte.toInt() and 0xf])
    }
    return sb.toString()
}