package com.baimsg.chat.type

/**
 * 耗时操作状态类型枚举
 */
enum class ExecutionStatus(val code: Int) {
    UNKNOWN(222),//未知状态
    LOADING(888),//正在加载
    SUCCESS(200),//操作成功
    FAIL(404),//操作失败
    EMPTY(201)//没有数据
}