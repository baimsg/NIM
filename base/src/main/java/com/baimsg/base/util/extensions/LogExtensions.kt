package com.baimsg.base.util.extensions

import android.util.Log

/**
 * Create by baimsg on 2022/3/17.
 * Email 1469010683@qq.com
 *
 **/
enum class LogLevel {
    DEBUG, INFO, ERR;
}

@JvmField
var msDebugAble = true

fun Any.logD(vararg param: Any) {
    logWithLevel(this, LogLevel.DEBUG, param.asList())
}

fun Any.logI(vararg param: Any) {
    logWithLevel(this, LogLevel.INFO, param.asList())
}

fun Any.logE(vararg param: Any) {
    logWithLevel(this, LogLevel.ERR, param.asList())
}

private fun logWithLevel(obj: Any, level: LogLevel, param: List<Any?>) {
    if (msDebugAble || level == LogLevel.ERR) {
        val builder = StringBuilder()
        param.forEach {
            builder.append(it.toString() + " | ")
        }
        when (level) {
            LogLevel.DEBUG -> {
                Log.d(obj.javaClass.name, builder.toString())
            }
            LogLevel.INFO -> {
                Log.i(obj.javaClass.name, builder.toString())
            }
            LogLevel.ERR -> {
                Log.e(obj.javaClass.name, builder.toString())
            }
        }

    }
}