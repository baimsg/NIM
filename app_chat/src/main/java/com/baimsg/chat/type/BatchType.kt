package com.baimsg.chat.type

/**
 * 批量操作的类型
 */
enum class BatchType {
    UNKNOWN, //未知状态
    ADD_FRIEND, //加好友
    INVITE_TO_TEAM, //邀请进群
    SEND_MESSAGE, //发送消息
    SEND_NOTIFICATION //发送通知
}