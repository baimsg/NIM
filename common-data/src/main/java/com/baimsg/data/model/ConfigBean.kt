package com.baimsg.data.model

import kotlinx.serialization.Serializable

/**
 * Create by Baimsg on 2022/8/8
 * @param stopUsing 停止使用
 * @param debug 测试环境
 * @param noticeVersion 通知版本
 * @param noticeTitle 通知标题
 * @param noticeContent 通知内容
 * @param noticeLink 通知链接地址
 * @param statement 服务声明
 **/
@Serializable
data class ConfigBean(
    val stopUsing: Boolean,
    val debug: Boolean,
    val noticeVersion: Int,
    val noticeTitle: String,
    val noticeContent: String,
    val noticeLink: String,
    val statement: String,
    val users: List<User>
)

@Serializable
data class User(
    val remark: String,
    val id: String
)