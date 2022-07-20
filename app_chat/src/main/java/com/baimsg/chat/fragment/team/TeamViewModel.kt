package com.baimsg.chat.fragment.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.ExecutionStatus
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
        MutableStateFlow(CreateTeamState.EMPTY)
    }

    val observeCreateTeamState: StateFlow<CreateTeamState> = _createTeamState

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

    fun batchCreateTeam(
        sum: Int,
        fields: MutableMap<TeamFieldEnum, Serializable?>,
        type: TeamTypeEnum,
        postscript: String?,
        members: List<String>
    ) {
        viewModelScope.launch {
            val name = fields[TeamFieldEnum.Name]
            repeat(sum) {
                _createTeamState.apply {
                    value =
                        value.copy(
                            executionStatus = ExecutionStatus.UNKNOWN,
                            name = if (it != 0) "$name$it" else name.toString()
                        )
                }
                createTeam(
                    fields = fields.apply {
                        if (it != 0) put(TeamFieldEnum.Name, "$name$it")
                    },
                    type = type,
                    postscript = postscript,
                    members = members
                )
                delay(1500)
            }
            _createTeamState.apply {
                value = value.copy(executionStatus = ExecutionStatus.EMPTY)
            }
        }

    }

    fun createTeam(
        fields: Map<TeamFieldEnum, Serializable?>,
        type: TeamTypeEnum,
        postscript: String?,
        members: List<String>
    ) {
        teamService.createTeam(fields, type, postscript, members)
            .setCallback(object : RequestCallback<CreateTeamResult> {
                override fun onSuccess(result: CreateTeamResult?) {
                    _createTeamState.apply {
                        value = value.copy(executionStatus = ExecutionStatus.SUCCESS)
                    }
                }

                override fun onFailed(code: Int) {
                    _createTeamState.apply {
                        value = value.copy(
                            executionStatus = ExecutionStatus.FAIL,
                            message = "错误码[$code]"
                        )
                    }
                }

                override fun onException(e: Throwable?) {
                    _createTeamState.apply {
                        value = value.copy(
                            executionStatus = ExecutionStatus.FAIL, message = "[${e?.message}]"
                        )
                    }
                }
            })
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