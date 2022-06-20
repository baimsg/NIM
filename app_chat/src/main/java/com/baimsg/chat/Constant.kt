package com.baimsg.chat

import com.baimsg.base.util.KvUtils

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
object Constant {
    const val KEY_ACCOUNT = "account"

    const val KEY_APP_KEY = "appKey"

    const val KEY_SEARCH_COUNT = "key_search_count"

    const val KEY_SEARCH_PREFIX = "key_search_prefix"

    const val KEY_TOKEN = "token"

    val ACCOUNT: String
        get() = KvUtils.getString(KEY_ACCOUNT, "")

    val APP_KEY: String
        get() = KvUtils.getString(KEY_APP_KEY, "")

    val TOKEN: String
        get() = KvUtils.getString(KEY_TOKEN, "")

    val SEARCH_COUNT: Long
        get() = KvUtils.getLong(KEY_SEARCH_COUNT, 100000)

    val SEARCH_PREFIX: String
        get() = KvUtils.getString(KEY_SEARCH_PREFIX, "659")
}