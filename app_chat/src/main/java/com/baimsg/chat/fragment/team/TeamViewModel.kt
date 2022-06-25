package com.baimsg.chat.fragment.team

import androidx.lifecycle.ViewModel
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.asTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum
import com.netease.nimlib.sdk.team.model.CreateTeamResult
import com.netease.nimlib.sdk.team.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(

) : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    val viewState by lazy {
        MutableStateFlow(TeamViewState.EMPTY)
    }

    init {
        loadTeams()
    }

    /**
     * 加载群群列表
     */
    fun loadTeams() {
        viewState.apply {
            value = value.copy(teams = Loading())
        }
        teamService.queryTeamList()
            .setCallback(object : RequestCallback<List<Team>> {
                override fun onSuccess(teams: List<Team>) {
                    viewState.apply {
                        value = value.copy(
                            teams = Success(teams.map { it.asTeam() })
                        )
                    }
                }

                override fun onFailed(code: Int) {
                    viewState.apply {
                        value = value.copy(
                            teams = Fail(Throwable("错误码$code"))
                        )
                    }
                }

                override fun onException(e: Throwable?) {
                    viewState.apply {
                        value = value.copy(
                            teams = Fail(e ?: Throwable("未知错误"))
                        )
                    }
                }
            })
    }

    fun addMembers(
        teamId: String,
        accounts: List<String>,
        msg: String? = null
    ) {
        teamService.addMembersEx(teamId, accounts, msg, null)
            .setCallback(object : RequestCallback<List<String>> {
                override fun onSuccess(list: List<String>?) {
                    list
                }

                override fun onFailed(code: Int) {
                    code
                }

                override fun onException(e: Throwable?) {
                    e
                }
            })
    }

    /**
     * 创建一个群组
     *
     * @param fields         群组预设资料, key为数据字段，value对对应的值，该值类型必须和field中定义的fieldType一致。
     * @param type           要创建的群组类型
     * @param postscript     邀请入群的附言。如果是创建临时群，该参数无效
     * @param members        邀请加入的成员帐号列表
     * @return InvocationFuture 可以设置回调函数，如果成功，参数为创建的群组资料
     */
    suspend fun createTeam(
        fields: Map<TeamFieldEnum, Serializable> = mapOf(
            TeamFieldEnum.Name to "群聊",
            TeamFieldEnum.Introduce to "欢迎大家加群",
            TeamFieldEnum.VerifyType to VerifyTypeEnum.Apply
        ),
        type: TeamTypeEnum = TeamTypeEnum.Normal,
        postscript: String = "",
        members: List<String> = emptyList()
    ) {
        teamService.createTeam(fields, type, postscript, members)
            .setCallback(object : RequestCallback<CreateTeamResult> {
                override fun onSuccess(teamResult: CreateTeamResult?) {

                }

                override fun onFailed(p0: Int) {

                }

                override fun onException(p0: Throwable?) {

                }
            })
    }
}