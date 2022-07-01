package com.baimsg.chat

import com.baimsg.base.util.KvUtils
import com.baimsg.data.model.DEFAULT_JSON_FORMAT
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
object Constant {

    const val KEY_SEARCH_COUNT = "key_search_count"

    const val KEY_TEAM_LIMIT = "key_team_limit"

    const val KEY_ADD_MODE = "key_add_mode"

    const val KEY_ADD_FRIEND_DELAY = "key_add_friend_delay"

    const val KEY_SEARCH_PREFIX = "key_search_prefix"

    const val KEY_ADD_VERIFY = "key_key_add_verify"

    const val KEY_ADD_FILTER = "key_add_filter"

    const val UMENG_APP_KEY = "62b2829b88ccdf4b7ea53d64"

    const val BUGLY_KEY = "10ced88958"


    val SEARCH_COUNT: Long
        get() = KvUtils.getLong(KEY_SEARCH_COUNT, 100000)

    val TEAM_LIMIT: Long
        get() = KvUtils.getLong(KEY_TEAM_LIMIT, 200)

    val ADD_FRIEND_DELAY: Long
        get() = KvUtils.getLong(KEY_ADD_FRIEND_DELAY, 1000)

    val SEARCH_PREFIX: String
        get() = KvUtils.getString(KEY_SEARCH_PREFIX, "659")

    val ADD_VERIFY: String
        get() = KvUtils.getString(KEY_ADD_VERIFY, "你好！")

    val ADD_FILTER: String
        get() = KvUtils.getString(KEY_ADD_FILTER, "")

    val ADD_MODE: Boolean
        get() = KvUtils.getBoolean(KEY_ADD_MODE, true)

    val ADD_FILTERS: List<String>
        get() = if (ADD_FILTER.isNotBlank()) DEFAULT_JSON_FORMAT.decodeFromString(
            ListSerializer(String.serializer()),
            ADD_FILTER
        )
        else emptyList()

    fun getChannel(): String = "default"

}