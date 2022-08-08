package com.baimsg.chat.fragment.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
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
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
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

    fun test() {
        val you = "30925171"
        val mi = "25912729"
        val fanno = "378339350"
        val content = JSONObject().apply {
            put("id", 2)
            put(
                "content",
                "学习进行时】习近平总书记对航天事业发展高度重视。酒泉卫星发射中心是我国航天事业的发祥地之一，党的十八大以来，习近平总书记两次来到这里。新华社《学习进行时》原创品牌栏目“讲习所”今天推出文章，与您一同学习感悟。\n" +
                        "\n" +
                        "探索浩瀚宇宙，发展航天事业，建设航天强国，是中华民族不懈追求的航天梦。航天梦是强国梦的重要组成部分。\n" +
                        "\n" +
                        "党的十八大以来，习近平总书记多次视察卫星发射中心、会见航天员和航天工作者，多次作出重要指示，就我国航天事业发展作出一系列重要论述，充分体现了对航天事业的关心和重视。\n" +
                        "\n" +
                        "酒泉卫星发射中心是我国航天事业的发祥地之一，党的十八大以来，习近平总书记两次来到这里。"
            )
        }.toString()
        // 自定义消息配置选项
        val config = CustomNotificationConfig()
        val notification = CustomNotification().apply {
            fromAccount = currentAccount
            sessionId = you
            sessionType = SessionTypeEnum.P2P
            isSendToOnlineUserOnly = false
            this.config = config
            this.apnsText = content
            this.content = content
        }

        msgService.sendCustomNotification(notification)
            .setCallback(object : RequestCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    logE("操作成功")
                }

                override fun onFailed(p0: Int) {
                    logE("操作失败：$p0")
                }

                override fun onException(p0: Throwable?) {
                    logE("操作失败：${p0?.message}")
                }
            })
    }

    fun login(appKey: String, account: String, token: String) {
        _viewState.apply {
            value = value.copy(
                executionStatus = ExecutionStatus.UNKNOWN,
                currentLoginRecord = value.currentLoginRecord.copy(
                    appKey = appKey,
                    account = account,
                    token = token
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
                )
                    .setCallback(object : RequestCallback<LoginInfo> {
                        override fun onSuccess(param: LoginInfo) {
                            viewModelScope.launch(Dispatchers.IO) {
                                val id =
                                    "${currentAppKey}-${currentAccount}"
                                loginRecordDao.cancelUsed()
                                loginRecordDao.updateOrInsert(currentLoginRecord.run {
                                    val t = System.currentTimeMillis()
                                    if (loginRecordDao.countById(id) == 0) copy(
                                        id = id,
                                        createTIme = t,
                                        loginTime = t,
                                        used = true
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
    suspend fun accounts(appKey: String) =
        withContext(Dispatchers.IO) {
            loginRecordDao.entriesByAppKey(appKey)
        }

    /**
     * 切换账号
     * @param nimLoginRecord
     */
    fun switchAccount(nimLoginRecord: NIMLoginRecord) {
        _viewState.apply {
            value = value.copy(
                executionStatus = ExecutionStatus.UNKNOWN,
                currentLoginRecord = nimLoginRecord
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