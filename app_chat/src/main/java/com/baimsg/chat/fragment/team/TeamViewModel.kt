package com.baimsg.chat.fragment.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.fragment.team.create.CreateTeamViewState
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMTeam
import com.baimsg.data.model.entities.asTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import com.netease.nimlib.sdk.team.model.CreateTeamResult
import com.netease.nimlib.sdk.team.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(

) : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val _viewState by lazy {
        MutableStateFlow(TeamViewState.EMPTY)
    }

    val observeViewState: StateFlow<TeamViewState> = _viewState

    val allTeam: List<NIMTeam>?
        get() = _viewState.value.teams.invoke()

    private val _createTeamState by lazy {
        MutableStateFlow(CreateTeamViewState.EMPTY)
    }

    val observeCreateTeamState: StateFlow<CreateTeamViewState> = _createTeamState

    private val fields: Map<TeamFieldEnum, Serializable>
        get() = _createTeamState.value.fields

    init {
        loadTeams()
    }

    /**
     * 加载群群列表
     */
    fun loadTeams() {
        _viewState.apply {
            value = value.copy(teams = Loading())
            teamService.queryTeamList()
                .setCallback(object : RequestCallback<List<Team>> {
                    override fun onSuccess(teams: List<Team>) {
                        value = value.copy(
                            teams = Success(teams.map { it.asTeam() })
                        )
                    }

                    override fun onFailed(code: Int) {
                        value = value.copy(
                            teams = Fail(Throwable("错误码$code"))
                        )
                    }

                    override fun onException(e: Throwable?) {
                        value = value.copy(
                            teams = Fail(e ?: Throwable("未知错误"))
                        )
                    }
                })
        }
    }

    /**
     * 中缀函数改变群聊参数
     * @param value 参数值
     */
    infix fun TeamFieldEnum.set(value: Serializable) {
        _createTeamState.apply {
            this.value = this.value.copy(
                fields = fields.toMutableMap().apply {
                    put(this@set, value)
                }
            )
        }
    }

    operator fun plus(limit: Int) {
        _createTeamState.apply {
            this.value = this.value.copy(
                limit = limit
            )
        }
    }

    fun stopBatchCreateTeam() {
        _createTeamState.apply {
            value = CreateTeamViewState.EMPTY
        }
    }

    fun startBatchCreateTeam() {
        _createTeamState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE)
            } else {
                value = value.copy(status = BatchStatus.RUNNING)
                batchCreateTeam()
            }
        }
    }

    /**
     * 批量创建群
     */
    private fun batchCreateTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                delay(Constant.DELAY)
                _createTeamState.apply {
                    if (value.pause()) return@runBlocking
                    value = value.copy(status = BatchStatus.RUNNING)
                    if (value.index >= value.limit) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                }
                teamService.createTeam(fields, TeamTypeEnum.Advanced, "", listOf())
                    .setCallback(object : RequestCallback<CreateTeamResult> {
                        override fun onSuccess(result: CreateTeamResult?) {
                            _createTeamState.apply {
                                value = value.copy(message = "创建成功", index = value.index + 1)
                            }
                            batchCreateTeam()
                        }

                        override fun onFailed(code: Int) {
                            _createTeamState.apply {
                                value = value.copy(message = "创建失败「$code」")
                            }
                            batchCreateTeam()
                        }

                        override fun onException(e: Throwable?) {
                            _createTeamState.apply {
                                value = value.copy(message = "创建失败「${e?.message}」")
                            }
                            batchCreateTeam()
                        }
                    })
            }
        }
    }


    fun dismissTeam(teamId: String) {
        teamService.dismissTeam(teamId)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {

                }

                override fun onFailed(code: Int) {
                }

                override fun onException(exception: Throwable) {
                }
            })

    }


}