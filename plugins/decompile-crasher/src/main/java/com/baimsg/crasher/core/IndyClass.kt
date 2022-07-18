package com.baimsg.crasher.core

/**
 * Created by anatawa12 on 2018/09/09.
 */
data class IndyClass(
        val classPath: String,
        val method: String,
        val field: String
) {
    companion object {
        val default = IndyClass(
                "com/anatawa12/tools/lib/A",
                "m", "m"
        )
    }
}

