package com.baimsg.chat.fragment.team.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable
import javax.inject.Inject
import kotlin.random.Random

/**
 * Create by Baimsg on 2022/7/21
 *
 **/
@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val _teamInfo by lazy {
        MutableStateFlow(handle["teamInfo"] ?: NIMTeam())
    }

    val observeTeamInfo: StateFlow<NIMTeam> = _teamInfo

    val announcement: String?
        get() = _teamInfo.value.announcement

    val id: String
        get() = _teamInfo.value.id

    val introduce: String?
        get() = _teamInfo.value.introduce

    private val _dismissTeamViewState by lazy {
        MutableStateFlow(DismissTeamViewState.EMPTY)
    }

    val observeDismissTeamViewState: StateFlow<DismissTeamViewState> = _dismissTeamViewState

    infix fun TeamFieldEnum.set(value: Serializable) {
        teamService.updateTeam(_teamInfo.value.id, this, value)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    this@set up value
                }

                override fun onFailed(p0: Int) {
                    this@set.updateFail()
                }

                override fun onException(p0: Throwable?) {
                    this@set.updateFail()
                }
            })

    }

    private fun TeamFieldEnum.updateFail() {
        _teamInfo.apply {
            when (this@updateFail) {
                TeamFieldEnum.BeInviteMode, TeamFieldEnum.TeamUpdateMode, TeamFieldEnum.InviteMode -> {
                    value = value.copy(extension = Random(10000000).toString())
                }
                else -> Unit
            }
        }

    }

    private infix fun TeamFieldEnum.up(value: Serializable) {
        _teamInfo.apply {
            when (this@up) {
                TeamFieldEnum.Announcement -> {
                    this.value = this.value.copy(announcement = value.toString())
                }
                TeamFieldEnum.Introduce -> {
                    this.value = this.value.copy(introduce = value.toString())
                }
                TeamFieldEnum.Name -> {
                    this.value = this.value.copy(name = value.toString())
                }
                TeamFieldEnum.VerifyType -> {
                    this.value = this.value.copy(verifyType = value as VerifyTypeEnum)
                }
                TeamFieldEnum.BeInviteMode -> {
                    this.value = this.value.copy(teamBeInviteMode = value as TeamBeInviteModeEnum)
                }
                TeamFieldEnum.InviteMode -> {
                    this.value = this.value.copy(teamInviteMode = value as TeamInviteModeEnum)
                }
                TeamFieldEnum.TeamUpdateMode -> {
                    this.value = this.value.copy(teamUpdateMode = value as TeamUpdateModeEnum)
                }
                else -> Unit
            }
        }
    }

    /**
     * 开启或关闭全体禁言
     * @param isMute 是否禁言
     */
    fun allMute(isMute: Boolean) {
        _teamInfo.apply {
            teamService.muteAllTeamMember(value.id, isMute).setCallback(object :
                RequestCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    value = value.copy(allMute = isMute, muteMode = muteMode(isMute))
                }

                override fun onFailed(p0: Int) {
                    value = value.copy(extension = Random(10000000).toString())
                }

                override fun onException(p0: Throwable?) {
                    value = value.copy(extension = Random(10000000).toString())
                }

            })
        }
    }

    /**
     * 获取禁言枚举类型
     * @param isMute 是否禁言
     */
    private fun muteMode(isMute: Boolean) =
        if (isMute) TeamAllMuteModeEnum.MuteALL else TeamAllMuteModeEnum.Cancel


    /**
     * 解散群聊
     */
    fun dismissTeam() {
        _dismissTeamViewState.apply {
            value = DismissTeamViewState.EMPTY
            teamService.dismissTeam(id)
                .setCallback(object : RequestCallback<Void?> {
                    override fun onSuccess(param: Void?) {
                        value = value.copy(
                            info = _teamInfo.value,
                            executionStatus = ExecutionStatus.SUCCESS
                        )
                    }

                    override fun onFailed(code: Int) {
                        value = value.copy(
                            info = _teamInfo.value,
                            message = "解散群聊失败「$code」",
                            executionStatus = ExecutionStatus.FAIL
                        )
                    }

                    override fun onException(exception: Throwable) {
                        value = value.copy(
                            info = _teamInfo.value,
                            message = "解散群聊失败「${exception.message}」",
                            executionStatus = ExecutionStatus.FAIL
                        )
                    }
                })
        }


    }
}