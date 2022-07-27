package com.baimsg.fog.util

/**
 * Create by Baimsg on 2022/7/27
 *
 **/
object Log {
    var isDebug = false

    fun v(msg: String?) {
        if (isDebug) {
            println(msg)
        }
    }

    fun e(msg: String?) {
        if (isDebug) {
            System.err.println(msg)
        }
    }
}