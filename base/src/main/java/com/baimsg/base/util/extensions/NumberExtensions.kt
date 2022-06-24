package com.baimsg.base.util.extensions

import kotlin.math.pow

/**
 * Create by Baimsg on 2022/6/24
 *
 **/
fun Long.length(): Int {
    var length = 1
    while (this % 10.0.pow(length.toDouble()) != this.toDouble()) {
        length++
    }
    return length
}

fun Int.length(): Int {
    var length = 1
    while (this % 10.0.pow(length.toDouble()) != this.toDouble()) {
        length++
    }
    return length
}