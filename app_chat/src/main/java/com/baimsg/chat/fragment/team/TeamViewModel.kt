package com.baimsg.chat.fragment.team

import androidx.lifecycle.ViewModel
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMTeam
import com.baimsg.data.model.entities.asTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    val teams: List<NIMTeam> = _viewState.value.teams() ?: emptyList()

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

}