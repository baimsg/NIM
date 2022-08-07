package com.baimsg.base.util.extensions

import android.util.Base64
import java.nio.charset.Charset

/**
 * Create by baimsg on 2022/3/17.
 * Email 1469010683@qq.com
 *
 **/

fun String?.orNA() = when (this.isNullOrEmpty()) {
    false -> this
    else -> "N/A"
}

fun String?.orBlank() = when (this == null) {
    false -> this
    else -> ""
}

fun List<String?>.interpunctize(interpunct: String = " ꞏ "): String = joinToString(interpunct)

fun String?.isNotNullAndNotBlank() = this != null && this.isNotBlank()

fun CharSequence.truncate(limit: Int, ellipsize: String = "..."): CharSequence {
    if (length > limit) {
        return substring(0, limit) + ellipsize
    }
    return this
}

/**
 * Transliteration map.
 *
 * Only for Turkmen and Cyrillic letters.
 */
private val transliterateMap = mapOf(
    // tk
    "Ä" to "A",
    "ç" to "ch",
    "Ç" to "CH",
    "ü" to "u",
    "Ü" to "U",
    "ý" to "y",
    "Ý" to "Y",
    "ş" to "sh",
    "Ş" to "SH",
    "ö" to "o",
    "Ö" to "O",
    "ň" to "n",
    "Ň" to "N",
    "Ž" to "Z",
    "ž" to "z",
    // ru
    "ё" to "yo",
    "й" to "j",
    "ц" to "c",
    "у" to "u",
    "к" to "k",
    "е" to "e",
    "н" to "n",
    "г" to "g",
    "ш" to "sh",
    "щ" to "sh",
    "з" to "z",
    "х" to "h",
    "ъ" to "",
    "ф" to "f",
    "ы" to "y",
    "в" to "v",
    "а" to "a",
    "п" to "p",
    "р" to "r",
    "о" to "o",
    "л" to "l",
    "д" to "d",
    "ж" to "zh",
    "э" to "e",
    "я" to "ya",
    "ч" to "ch",
    "с" to "s",
    "м" to "m",
    "и" to "i",
    "т" to "t",
    "ь" to "",
    "б" to "b",
    "ю" to "yu",
    "Ё" to "YO",
    "Й" to "J",
    "Ц" to "C",
    "У" to "U",
    "К" to "K",
    "Е" to "E",
    "Н" to "N",
    "Г" to "G",
    "Ш" to "SH",
    "Щ" to "SH",
    "З" to "Z",
    "Х" to "H",
    "Ъ" to "",
    "Ф" to "F",
    "Ы" to "Y",
    "В" to "V",
    "А" to "A",
    "П" to "P",
    "Р" to "R",
    "О" to "O",
    "Л" to "L",
    "Д" to "D",
    "Ж" to "ZH",
    "Э" to "E",
    "Я" to "YA",
    "Ч" to "CH",
    "С" to "S",
    "М" to "M",
    "И" to "I",
    "Т" to "T",
    "Ь" to "",
    "Б" to "B",
    "Ю" to "YU",
    "ä" to "a"
)

/**
 * Transliterate this string.
 * Example: "Bäşim däli ýaly" -> "Bashim dali yaly"
 */
fun String?.transliterate(): String = when (this != null) {
    true -> this.map { transliterateMap[it.toString()] ?: it.toString() }.joinToString("")
    else -> ""
}

/**
 * 是否是http开头
 */
fun String?.isUrl(): Boolean {
    return this?.let { str ->
        str.startsWith("https") || str.startsWith("http")
    } ?: false
}

// 字符是否是中文
fun Char.isChinese(): Boolean = this.toInt() in 0x4E00..0x29FA5

// 字符是否是换行符
fun Char.isLineFeed(): Boolean = this == '\n'

// 字符是否是英文
fun Char.isEnglish(): Boolean = this in 'a'..'z' || this in 'A'..'Z'

fun String.toPureEnglish(): String {
    val re = Regex("[^A-Za-z0-9-]")
    return re.replace(this, "")
}

fun String.isEnglish(): Boolean {
    val re = Regex("[^A-Za-z0-9-]")
    val filter = re.replace(this, "") // works

    return if (filter.isNotEmpty()) {
        filter.matches(Regex("^[a-zA-Z0-9-]*"))
    } else false
}

fun String.toMd5String(charset: Charset = Charsets.UTF_8): String =
    toByteArray(charset).toMd5String()

fun String.toMd5Bytes(charset: Charset = Charsets.UTF_8): ByteArray =
    toByteArray(charset).toMd5Bytes()

fun String.encodeBase64(charset: Charset = Charsets.UTF_8): String =
    Base64.encode(this.toByteArray(charset), Base64.DEFAULT).toString(charset)

fun String.decodeBase64(charset: Charset = Charsets.UTF_8): String =
    Base64.decode(this.toByteArray(charset), Base64.DEFAULT).toString(charset)