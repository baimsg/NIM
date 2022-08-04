package com.baimsg.chat.type

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum

/**
 * Create by Baimsg on 2022/8/4
 *
 **/
enum class SessionType {
    GROUP,
    FRIEND;

    fun toType(): SessionTypeEnum = when (this) {
        GROUP -> SessionTypeEnum.Team
        else -> SessionTypeEnum.P2P
    }
}