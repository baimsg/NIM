package com.baimsg.chat.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.model.entities.NIMLoginRecord
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
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

    private val _loginInfo by lazy {
        MutableStateFlow(NIMLoginRecord())
    }

    private val _viewState by lazy {
        MutableStateFlow(LoginViewState.EMPTY)
    }

    val observeViewState: StateFlow<LoginViewState> = _viewState

    private val _statusCode by lazy {
        MutableStateFlow(StatusCode.INVALID)
    }

    val observerStatusCode: StateFlow<StatusCode> = _statusCode

    private val _userInfo by lazy {
        MutableStateFlow(NIMUserInfo())
    }

    val observeUserInfo: StateFlow<NIMUserInfo> = _userInfo

    init {
        //初始化用户信息
        viewModelScope.launch {
            _loginInfo.value = getLoginInfo()
        }

    }

    suspend fun getLoginInfo() =
        withContext(Dispatchers.IO) { loginRecordDao.used().firstOrNull() ?: NIMLoginRecord() }

    suspend fun appKeys() = withContext(Dispatchers.IO) {
        loginRecordDao.appKeys()
    }

    suspend fun accounts(appKey: String) =
        withContext(Dispatchers.IO) {
            loginRecordDao.entriesByAppKey(appKey)
        }


    /**
     * 更新 appKey
     * @param appKey
     */
    fun upDateAppKey(appKey: String) {
        _loginInfo.apply {
            value = value.copy(appKey = appKey)
        }
    }

    /**
     * 更新 account
     * @param account
     */
    fun updateAccount(account: String) {
        _loginInfo.apply {
            value = value.copy(account = account)
        }
    }

    /**
     * 更新 token
     * @param token
     */
    fun updateToken(token: String) {
        _loginInfo.apply {
            value = value.copy(token = token)
        }
    }

    /**
     * 更新登录账号并登录
     * @param loginInfo
     */
    fun updateLoginInfo(loginInfo: NIMLoginRecord) {
        _loginInfo.value = loginInfo
        login()
    }

    /**
     * 登录账号
     */
    fun login() {
        _userInfo.value = NIMUserInfo()
        _viewState.value = LoginViewState.EMPTY
        val loginInfo = _loginInfo.value
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
        _userInfo.value = NIMUserInfo()
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
        userService.fetchUserInfo(listOf(_loginInfo.value.account))
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(users: List<NimUserInfo>?) {
                    _userInfo.value = users?.firstOrNull().asUser()
                }

                override fun onFailed(code: Int) {}

                override fun onException(e: Throwable?) {}
            })
    }

}