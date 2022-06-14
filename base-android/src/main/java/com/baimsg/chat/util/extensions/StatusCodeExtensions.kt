package com.baimsg.chat.util.extensions

import com.netease.nimlib.sdk.StatusCode

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
fun StatusCode.message(): String = when (this) {
    StatusCode.LOGINING -> "正在登录中"
    StatusCode.SYNCING -> "正在同步数据"
    StatusCode.LOGINED -> "已成功登录"
    StatusCode.UNLOGIN -> "未登录/登录失败"
    StatusCode.NET_BROKEN -> "网络连接已断开"
    StatusCode.CONNECTING -> "正在连接服务器"
    StatusCode.KICKOUT -> "已在其他设备登录"
    StatusCode.KICK_BY_OTHER_CLIENT -> "已被其他设备强制下线"
    StatusCode.FORBIDDEN -> "已被服务器禁止登录"
    StatusCode.VER_ERROR -> "客户端版本错误"
    StatusCode.PWD_ERROR -> "用户名或密码错误"
    StatusCode.DATA_UPGRADE -> "数据库需要迁移到加密状态"
    else -> "未定义"
}
