package com.baimsg.chat.fragment.team.create

import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.entities.NIMTeam
import com.netease.nimlib.sdk.team.constant.*
import java.io.Serializable

/**
 * Create by Baimsg on 2022/7/21
 * 创建群状态参数
 * @param status 运行状态
 * @param index 当前执行下标
 * @param limit 创建数量
 * @param message 执行结果提示
 * @param team 创建成功后群信息
 * @param fields 提交参数
 **/
data class CreateTeamViewState(
    val status: BatchStatus,
    val index: Int,
    val limit: Int,
    val message: String,
    val team: NIMTeam,
    val fields: MutableMap<TeamFieldEnum, Serializable>
) {
    companion object {
        val EMPTY =
            CreateTeamViewState(
                status = BatchStatus.UNKNOWN,
                index = 0,
                limit = 1,
                message = "",
                team = NIMTeam(),
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