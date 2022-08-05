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

    const val KEY_STATEMENT = "key_statement"

    const val KEY_FILTER = "key_filter"

    const val KEY_DELAY = "key_delay"

    const val KEY_SEARCH_COUNT = "key_search_count"

    const val KEY_SEARCH_PREFIX = "key_search_prefix"

    const val KEY_AUTO_FILL = "key_auto_fill"

    const val KEY_ADD_DIRECT = "key_add_direct"

    const val KEY_ADD_FRIEND_DESCRIBE = "key_add_friend_describe"

    const val KEY_TEAM_LIMIT = "key_team_limit"

    const val KEY_TEAM_DESCRIBE = "key_team_describe"

    const val BUGLY_KEY = "84f02b15ed"

    val STATEMENT: Boolean
        get() = KvUtils.getBoolean(KEY_STATEMENT, false)

    val SEARCH_COUNT: Long
        get() = KvUtils.getLong(KEY_SEARCH_COUNT, 100000)

    val TEAM_LIMIT: Long
        get() = KvUtils.getLong(KEY_TEAM_LIMIT, 200)

    val DELAY: Long
        get() = KvUtils.getLong(KEY_DELAY, 1000)

    val SEARCH_PREFIX: String
        get() = KvUtils.getString(KEY_SEARCH_PREFIX, "659")

    val ADD_FRIEND_DESCRIBE: String
        get() = KvUtils.getString(KEY_ADD_FRIEND_DESCRIBE, "你好！")

    val TEAM_DESCRIBE: String
        get() = KvUtils.getString(KEY_TEAM_DESCRIBE, "快来进群聊天")

    private val FILTER: String
        get() = KvUtils.getString(KEY_FILTER, "")

    val ADD_DIRECT: Boolean
        get() = KvUtils.getBoolean(KEY_ADD_DIRECT, true)

    val AUTO_FILL: Boolean
        get() = KvUtils.getBoolean(KEY_AUTO_FILL, true)

    val ADD_FILTERS: List<String>
        get() = if (FILTER.isNotBlank()) DEFAULT_JSON_FORMAT.decodeFromString(
            ListSerializer(String.serializer()),
            FILTER
        )
        else emptyList()

    fun getChannel(): String = "default"

    fun appKeyRemark(appKey: String): String = KvUtils.getString(appKey, "无备注")

}