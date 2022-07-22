package com.baimsg.chat.util

import com.baimsg.base.util.extensions.length
import com.baimsg.chat.Constant

/**
 * 检查字符串是否包含敏感词
 */
fun String.verifySensitiveWords(): Boolean {
    Constant.ADD_FILTERS.forEachIndexed { _, s ->
        if (this.contains(s)) {
            return true
        }
    }
    return false
}

/**
 * 获取计算后的account
 */
fun Long.getId(): String {
    val searchCount = Constant.SEARCH_COUNT
    val searchPrefix = Constant.SEARCH_PREFIX
    return if (Constant.AUTO_FILL)
        "$searchPrefix%0${searchCount.length()}d".format(this)
    else "${searchPrefix}$this"
}