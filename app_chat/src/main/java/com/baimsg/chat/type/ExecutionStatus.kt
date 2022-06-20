package com.baimsg.chat.type

enum class ExecutionStatus(val code: Int) {
    SUCCESS(200),//执行成功
    FAIL(404),//执行失败
    UNKNOWN(222),//没执行
    EMPTY(201)//没执行
}