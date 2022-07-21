package com.baimsg.chat.fragment.team.create

import com.baimsg.chat.type.BatchStatus
import com.netease.nimlib.sdk.team.constant.*
import java.io.Serializable

/**
 * Create by Baimsg on 2022/7/21
 *
 **/
data class CreateTeamViewState(
    val status: BatchStatus,
    val index: Int,
    val limit: Int,
    val message: String,
    val fields: MutableMap<TeamFieldEnum, Serializable>
) {
    companion object {
        val EMPTY =
            CreateTeamViewState(
                status = BatchStatus.UNKNOWN,
                index = 0,
                limit = 1,
                message = "",
                fields = mutableMapOf(
                    TeamFieldEnum.Announcement to "",
                    TeamFieldEnum.Introduce to "",
                    TeamFieldEnum.Name to "群聊",
                    TeamFieldEnum.VerifyType to VerifyTypeEnum.Apply,
                    TeamFieldEnum.BeInviteMode to TeamBeInviteModeEnum.NoAuth,
                    TeamFieldEnum.InviteMode to TeamInviteModeEnum.Manager,
                    TeamFieldEnum.TeamUpdateMode to TeamUpdateModeEnum.Manager,
                    TeamFieldEnum.AllMute to TeamAllMuteModeEnum.MuteALL,
                )
            )
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

    fun unknown() = status == BatchStatus.UNKNOWN

    fun stop() = status == BatchStatus.STOP

}