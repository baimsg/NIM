package com.baimsg.chat.util.extensions

import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.team.constant.*

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
fun StatusCode.message(): String = when (this) {
    StatusCode.LOGINING -> "正在登录"
    StatusCode.SYNCING -> "正在同步数据"
    StatusCode.LOGINED -> "登录成功"
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

fun VerifyTypeEnum.message() = when (this) {
    VerifyTypeEnum.Free -> "允许任何人加入"
    VerifyTypeEnum.Apply -> "需要身份验证"
    else -> "不允许任何人加入"
}

fun TeamBeInviteModeEnum.message() = when (this) {
    TeamBeInviteModeEnum.NoAuth -> "不需要同意"
    else -> "需要对方同意"
}

fun TeamInviteModeEnum.message() = when (this) {
    TeamInviteModeEnum.Manager -> "仅管理"
    else -> "任何人"
}

fun TeamUpdateModeEnum.message() = when (this) {
    TeamUpdateModeEnum.Manager -> "仅管理"
    else -> "任何人"
}

fun TeamAllMuteModeEnum.message() = when (this) {
    TeamAllMuteModeEnum.MuteALL -> "全体禁言"
    TeamAllMuteModeEnum.MuteNormal -> "默认禁言"
    else -> "取消禁言"
}


