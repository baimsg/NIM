package com.baimsg.chat.fragment.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.bean.NIMTeam
import com.baimsg.chat.bean.NIMUserInfo
import com.baimsg.chat.bean.asTeam
import com.baimsg.chat.bean.asUser
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
internal class LoginViewModel constructor(val app: Application) :
    AndroidViewModel(app) {

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
     * 用户信息
     */
    val users by lazy {
        MutableStateFlow(listOf(NIMUserInfo()))
    }

    /**
     * 好友列表
     */
    var friends = emptyList<String?>()

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


    fun getUserInfo(page: Int = 0, limit: Int = 20) {
        val start = page * limit
        val end = start + limit
        val accounts = mutableListOf<String?>()
        (start until end).forEachIndexed { _, i ->
            if (i in friends.indices) accounts.add(friends[i])
        }
        NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(mUsers: List<NimUserInfo>?) {
                    viewModelScope.launch {
                        mUsers?.map { it.asUser() }?.let { users.emit(it) }
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
     * 加载好友列表
     */
    fun loadFriends() {
        viewModelScope.launch {
            friends = NIMClient.getService(FriendService::class.java).friendAccounts
            getUserInfo()
        }
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