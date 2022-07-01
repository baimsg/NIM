package com.baimsg.chat.util

import com.baimsg.chat.Constant

fun String.verifySensitiveWords(): Boolean {
    Constant.ADD_FILTERS.forEachIndexed { _, s ->
        if (this.contains(s)) {
            return true
        }
    }
    return false
}
