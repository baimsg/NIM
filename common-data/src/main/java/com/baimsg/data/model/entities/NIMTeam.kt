package com.baimsg.data.model.entities

import com.netease.nimlib.sdk.team.constant.*
import com.netease.nimlib.sdk.team.model.Team

data class NIMTeam(
    val id: String? = null,
    val name: String? = null,
    val icon: String? = null,
    val type: TeamTypeEnum = TeamTypeEnum.Normal,
    val announcement: String? = null,//通知
    val introduce: String? = null,//介绍
    val creator: String? = null,
    val memberCount: Int = 0,
    val memberLimit: Int = 0,
    val verifyType: VerifyTypeEnum = VerifyTypeEnum.Free,
    val createTime: Long = 0,
    val myTeam: Boolean = false,
    val extension: String? = null,
    val extServer: String? = null,
    val messageNotifyType: TeamMessageNotifyTypeEnum = TeamMessageNotifyTypeEnum.All,
    val teamInviteMode: TeamInviteModeEnum = TeamInviteModeEnum.All,
    val teamBeInviteMode: TeamBeInviteModeEnum = TeamBeInviteModeEnum.NoAuth,
    val teamUpdateMode: TeamUpdateModeEnum = TeamUpdateModeEnum.All,
    val teamExtensionUpdateMode: TeamExtensionUpdateModeEnum = TeamExtensionUpdateModeEnum.All,
    val allMute: Boolean = false,
    val muteMode: TeamAllMuteModeEnum = TeamAllMuteModeEnum.MuteNormal,
)

fun Team?.asTeam(): NIMTeam =
    this?.run {
        NIMTeam(
            id = id,
            name = name,
            icon = icon,
            type = type,
            announcement = announcement,
            introduce = introduce,
            creator = creator,
            memberCount = memberCount,
            memberLimit = memberLimit,
            verifyType = verifyType,
            createTime = createTime,
            myTeam = isMyTeam,
            extension = extension,
            extServer = extServer,
            messageNotifyType = messageNotifyType,
            teamInviteMode = teamInviteMode,
            teamBeInviteMode = teamBeInviteMode,
            teamUpdateMode = teamUpdateMode,
            teamExtensionUpdateMode = teamExtensionUpdateMode,
            allMute = isAllMute,
            muteMode = muteMode
        )
    } ?: NIMTeam()