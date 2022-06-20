package com.baimsg.chat.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.fragment.home.FriendViewState
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMTeam
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asTeam
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
internal class LoginViewModel constructor() :
    ViewModel() {

    /**
     * 登录状态
     */
    val statusCode by lazy {
        MutableStateFlow(StatusCode.INVALID)
    }

    /**
     * 用户信息
     */
    val userInfo by lazy {
        MutableStateFlow(NIMUserInfo())
    }

    /**
     * 好友用户信息
     */
    val friendViewState by lazy {
        MutableStateFlow(FriendViewState.EMPTY)
    }

    /**
     * 当前页码
     */
    var friendPage = 0

    /**
     * 好友列表
     */
    val friends by lazy {
        MutableStateFlow(emptyList<String?>())
    }

    /**
     * 群列表
     */
    val teams by lazy {
        MutableStateFlow(emptyList<NIMTeam>())
    }

    private val pending by lazy {
        MutableSharedFlow<LoginAction>()
    }

    init {
        viewModelScope.launch {
            statusCode.collectLatest { statusCode ->
                if (statusCode == StatusCode.LOGINED) {
                    loadUserInfo()
                    loadFriends()
                }
            }
        }

        viewModelScope.launch {
            pending.collectLatest { action ->
                when (action) {
                    is LoginAction.UpdateStatusCode -> {
                        statusCode.value = action.statusCode
                    }
                    is LoginAction.UpdateUserInfo -> {
                        userInfo.value = action.nimUserInfo
                    }
                }
            }
        }
    }

    fun submit(action: LoginAction) {
        viewModelScope.launch {
            pending.emit(action)
        }
    }

    /**
     * 加载好友列表
     */
    fun loadFriends() {
        /**
         * 重制页码
         */
        friendPage = 0
        friends.value = NIMClient.getService(FriendService::class.java).friendAccounts
        getUserInfo()
    }

    fun nextPageUserInfo() {
        friendPage++
        getUserInfo()
    }

    /**
     * 获取好友列表信息
     * @param limit 返回数量
     */
    private fun getUserInfo(limit: Int = 20) {
        friendViewState.value = FriendViewState.EMPTY
        val start = friendPage * limit
        val end = start + limit
        val accounts = mutableListOf<String?>()
        (start until end).forEachIndexed { _, i ->
            if (i in friends.value.indices) accounts.add(friends.value[i])
        }
        if (accounts.isEmpty()) {
            friendViewState.value =
                FriendViewState.EMPTY.copy(executionStatus = ExecutionStatus.EMPTY)
            return
        }
        NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(mUsers: List<NimUserInfo>?) {
                    val users = mUsers?.map { it.asUser() } ?: emptyList()
                    friendViewState.value = FriendViewState(
                        executionStatus = ExecutionStatus.SUCCESS,
                        users = users
                    )
                }

                override fun onFailed(code: Int) {
                    friendViewState.value =
                        FriendViewState.EMPTY.copy(executionStatus = ExecutionStatus.FAIL)
                }

                override fun onException(e: Throwable?) {
                    friendViewState.value =
                        FriendViewState.EMPTY.copy(executionStatus = ExecutionStatus.FAIL)
                }
            })
    }


    /**
     * 加载群列表
     */
    fun loadGroupList() {
        NIMClient.getService(TeamService::class.java).queryTeamList()
            .setCallback(object : RequestCallback<List<Team>> {
                override fun onSuccess(mTeams: List<Team>?) {
                    viewModelScope.launch {
                        mTeams?.run {
                            teams.emit(this.map { it.asTeam() })
                        }
                    }
                }

                override fun onFailed(code: Int) {
                    viewModelScope.launch {

                    }
                }

                override fun onException(e: Throwable?) {

                }
            })
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        NIMClient.getService(UserService::class.java).fetchUserInfo(listOf(Constant.ACCOUNT))
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(users: List<NimUserInfo>?) {
                    viewModelScope.launch {
                        userInfo.emit(users?.firstOrNull().asUser())
                    }
                }

                override fun onFailed(code: Int) {

                }

                override fun onException(e: Throwable?) {

                }
            })
    }


}