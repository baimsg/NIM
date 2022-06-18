package com.baimsg.chat.bean

import com.netease.nimlib.sdk.friend.model.Friend

data class NIMFriend(
    val account: String = "",
    val alias: String = "",
    val extension: MutableMap<String, Any>? = null,
    val serverExtension: String? = null
)

fun Friend?.asFriend(): NIMFriend =
    this?.run {
        NIMFriend(
            account = account,
            alias = alias,
            extension = extension,
            serverExtension = serverExtension
        )
    } ?: NIMFriend()