package com.baimsg.chat.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.model.JSON
import com.baimsg.data.model.entities.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

    private val _viewState by lazy {
        MutableStateFlow(LoginViewState.EMPTY)
    }

    val observeViewState: StateFlow<LoginViewState> = _viewState

    val allAppKeys: List<String>
        get() = _viewState.value.allAppKeys

    val currentLoginRecord: NIMLoginRecord
        get() = _viewState.value.currentLoginRecord

    val currentAppKey: String
        get() = _viewState.value.currentLoginRecord.appKey

    val currentAccount: String
        get() = _viewState.value.currentLoginRecord.account

    val currentToken: String
        get() = _viewState.value.currentLoginRecord.token


    private val _statusCode by lazy {
        MutableStateFlow(StatusCode.INVALID)
    }

    val observerStatusCode: StateFlow<StatusCode> = _statusCode

    private val _userInfo by lazy {
        MutableStateFlow(NIMUserInfo())
    }

    val observeUserInfo: StateFlow<NIMUserInfo> = _userInfo

    init {
        refreshData()
    }

    private val msgService by lazy {
        NIMClient.getService(MsgService::class.java)
    }

    var a = 1;
    var id = 8165033568
    fun test() {
        NIMClient.getService(TeamService::class.java).queryMemberList("$id")
            .setCallback(object : RequestCallback<List<TeamMember>> {
                override fun onSuccess(result: List<TeamMember>?) {
                    result?.forEachIndexed { index, teamMember ->
                        logE("result=${teamMember.account}")
                    }
                    if (result.isNullOrEmpty()) logE("null")
//                    logE("result=${JSON.encodeToString(NIMTeam.serializer(), result.asTeam())}")
//                    next()
                }

                override fun onFailed(code: Int) {
                    logE("code=$code")
                    next()
                }

                override fun onException(exception: Throwable?) {
                    logE("exception=$exception")
                    next()
                }
            })

    }

    fun next() {
        if (a > 10000000) {
            logE(id)
            a = 0
        } else {
            test()
        }
    }

    fun login(appKey: String, account: String, token: String) {
        _viewState.apply {
            value = value.copy(
                executionStatus = ExecutionStatus.UNKNOWN,
                currentLoginRecord = value.currentLoginRecord.copy(
                    appKey = appKey, account = account, token = token
                )
            )
        }
        login()
    }

    /**
     * 登录账号
     */
    private fun login() {
        _userInfo.value = NIMUserInfo()
        when {
            currentLoginRecord.appKeyEmpty() -> {
                _viewState.apply {
                    value = value.copy(executionStatus = ExecutionStatus.FAIL, message = "appKey为空")
                }
            }
            currentLoginRecord.mustEmpty() -> {
                _viewState.apply {
                    value = value.copy(executionStatus = ExecutionStatus.FAIL, message = "账号或密码为空")
                }
            }
            else -> {
                authService.login(
                    Constant.getLoginInfo(currentAccount, currentToken, currentAppKey)
                ).setCallback(object : RequestCallback<LoginInfo> {
                    override fun onSuccess(param: LoginInfo) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val id = "${currentAppKey}-${currentAccount}"
                            loginRecordDao.cancelUsed()
                            loginRecordDao.updateOrInsert(currentLoginRecord.run {
                                val t = System.currentTimeMillis()
                                if (loginRecordDao.countById(id) == 0) copy(
                                    id = id, createTIme = t, loginTime = t, used = true
                                ) else copy(loginTime = t, used = true)
                            })
                            _viewState.apply {
                                value = value.copy(executionStatus = ExecutionStatus.SUCCESS)
                            }
                            refreshData()
                        }
                    }

                    override fun onFailed(code: Int) = Unit
                    override fun onException(exception: Throwable) = Unit
                })
            }
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginRecordDao.updateOrInsert(currentLoginRecord.copy(used = false))
            _userInfo.value = NIMUserInfo()
            authService.logout()
            _statusCode.value = StatusCode.UNLOGIN
            refreshData()
        }

    }

    /**
     * 退出软件
     */
    fun exit() {
        authService.exit()
    }

    /**
     * 更新登录状态
     * @param statusCode 状态
     */
    fun updateStatusCode(statusCode: StatusCode) {
        _statusCode.value = statusCode
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        userService.fetchUserInfo(listOf(currentLoginRecord.account))
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(users: List<NimUserInfo>?) {
                    _userInfo.value = users?.firstOrNull().asUser()
                }

                override fun onFailed(code: Int) = Unit
                override fun onException(e: Throwable?) = Unit
            })
    }

    /**
     * 根据id删除数据
     * @param id
     */
    fun deleteById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRecordDao.deleteById(id = id)
            refreshData()
        }
    }

    /**
     * 根据appKey删除数据
     * @param appKey
     */
    fun deleteByAppKey(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginRecordDao.deleteByAppKey(appKey = appKey)
            refreshData()
        }
    }

    /**
     * 获取账号列表
     */
    suspend fun accounts(appKey: String) = withContext(Dispatchers.IO) {
        loginRecordDao.entriesByAppKey(appKey)
    }

    /**
     * 切换账号
     * @param nimLoginRecord
     */
    fun switchAccount(nimLoginRecord: NIMLoginRecord) {
        _viewState.apply {
            value = value.copy(
                executionStatus = ExecutionStatus.UNKNOWN, currentLoginRecord = nimLoginRecord
            )
        }
        login()
    }

    private fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = LoginViewState.EMPTY.copy(
                    allAppKeys = loginRecordDao.appKeys(),
                    currentLoginRecord = loginRecordDao.used() ?: NIMLoginRecord()
                )
            }
        }
    }

}