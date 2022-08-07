package com.baimsg.chat.type


/**
 * 用于列表操作的类型枚举
 */
enum class UpdateStatus {
    DEFAULT, //默认操作
    REFRESH, //刷新数据
    UPDATE, //更新内容
    APPEND, //追加内容
    REMOVE, //移除内容
    CLEAN //清空内容
}