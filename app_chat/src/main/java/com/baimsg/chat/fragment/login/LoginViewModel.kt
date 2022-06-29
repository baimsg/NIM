package com.baimsg.chat.fragment.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.fragment.home.FriendViewState
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.showError
import com.baimsg.chat.util.extensions.showSuccess
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.model.entities.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginRecordDao: LoginRecordDao
) : ViewModel() {

    private val authService by lazy {
        NIMClient.getService(AuthService::class.java)
    }

    private val _loginInfo by lazy {
        MutableStateFlow(NIMLoginRecord())
    }

    private val _viewState by lazy {
        MutableStateFlow(LoginViewState.EMPTY)
    }

    val observeViewState: StateFlow<LoginViewState> = _viewState

    init {
        viewModelScope.launch {
            _loginInfo.value = getLoginInfo()
        }
    }

    suspend fun getLoginInfo() =
        withContext(Dispatchers.IO) { loginRecordDao.used().firstOrNull() ?: NIMLoginRecord() }

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
                if (statusCode == StatusCode.LOGINED && !userInfo.value.loaded) {
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
     * 更新 appKey
     */
    fun upDateAppKey(appKey: String) {
        _loginInfo.apply {
            value = value.copy(appKey = appKey)
        }
    }

    /**
     * 更新 account
     */
    fun updateAccount(account: String) {
        _loginInfo.apply {
            value = value.copy(account = account)
        }
    }

    /**
     * 更新 token
     */
    fun updateToken(token: String) {
        _loginInfo.apply {
            value = value.copy(token = token)
        }
    }

    /**
     * 登录账号
     */
    fun login(loginInfo: NIMLoginRecord = _loginInfo.value) {
        _viewState.value = LoginViewState.EMPTY
        when {
            loginInfo.appKeyEmpty() -> {
                _viewState.apply {
                    value = value.copy(executionStatus = ExecutionStatus.FAIL, message = "appKey为空")
                }
            }
            loginInfo.mustEmpty() -> {
                _viewState.apply {
                    value = value.copy(executionStatus = ExecutionStatus.FAIL, message = "账号或密码为空")
                }
            }
            else -> {
                authService.login(LoginInfo(loginInfo.account, loginInfo.token, loginInfo.appKey))
                    .setCallback(object : RequestCallback<LoginInfo> {
                        override fun onSuccess(param: LoginInfo) {
                            viewModelScope.launch(Dispatchers.IO) {
                                val id = "${loginInfo.appKey}-${loginInfo.account}"
                                loginRecordDao.cancelUsed()
                                loginRecordDao.updateOrInsert(loginInfo.run {
                                    val t = System.currentTimeMillis()
                                    if (loginRecordDao.countById(id) == 0) copy(
                                        id = "$appKey-$account",
                                        createTIme = t,
                                        loginTime = t,
                                        used = true
                                    ) else copy(loginTime = t, used = true)
                                })
                            }
                            _viewState.apply {
                                value = value.copy(executionStatus = ExecutionStatus.SUCCESS)
                                value = LoginViewState.EMPTY
                            }
                        }

                        override fun onFailed(code: Int) {
                        }

                        override fun onException(exception: Throwable) {
                        }
                    })
            }
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        authService.logout()
    }

    /**
     * 退出软件
     */
    fun exit() {
        authService.exit()
    }

    /**
     * 加载好友列表
     */
    fun loadFriends() {
        /**
         * 重制页码
         */
        friendPage = 0
        val list = NIMClient.getService(FriendService::class.java)?.friendAccounts
        if (list == null) {
            friendViewState.value =
                FriendViewState.EMPTY.copy(executionStatus = ExecutionStatus.FAIL)
        } else {
            friends.value = list
            getUserInfo()
        }
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
        NIMClient.getService(UserService::class.java).fetchUserInfo(listOf(""))
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