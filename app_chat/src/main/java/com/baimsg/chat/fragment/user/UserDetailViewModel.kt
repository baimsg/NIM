package com.baimsg.chat.fragment.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.toMd5String
import com.baimsg.chat.Constant
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.api.BaseEndpoints
import com.baimsg.data.api.NetConfig
import com.baimsg.data.model.JSON
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val baseEndpoints: BaseEndpoints,
) : ViewModel() {

    private val initAccount = handle["account"] ?: "100000"

    private val _viewState by lazy {
        MutableStateFlow(UserDetailViewState.EMPTY)
    }

    val observeViewState: StateFlow<UserDetailViewState> = _viewState

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

    private val friendService by lazy {
        NIMClient.getService(FriendService::class.java)
    }

    private val _infoViewState by lazy {
        MutableStateFlow(UserInfoViewState.EMPTY)
    }

    val observeInfoViewState: StateFlow<UserInfoViewState> = _infoViewState

    val url: String
        get() = _infoViewState.value.url

    val forms: Map<String, String>
        get() = _infoViewState.value.forms

    val headers: Map<String, String>
        get() = _infoViewState.value.headers

    val condition: Boolean
        get() = url.isNotBlank() && forms.isNotEmpty()

    init {
        loadForms()
        loadData()
    }

    /**
     * 加载数据
     */
    fun loadData() {
        _viewState.apply {
            value = value.copy(executionStatus = ExecutionStatus.LOADING,
                myFriend = friendService.isMyFriend(initAccount),
                inBlackList = friendService.isInBlackList(initAccount))
            userService.fetchUserInfo(listOf(initAccount))
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(list: List<NimUserInfo>?) {
                        value = value.copy(userInfo = list?.firstOrNull().asUser(),
                            executionStatus = ExecutionStatus.SUCCESS)
                        recover()
                    }

                    override fun onFailed(code: Int) {
                        value = value.copy(executionStatus = ExecutionStatus.FAIL)
                        recover()
                    }

                    override fun onException(e: Throwable?) {
                        value = value.copy(executionStatus = ExecutionStatus.FAIL)
                        recover()
                    }
                })
        }
    }

    fun loadInfo() {
        viewModelScope.launch {
            _infoViewState.apply {
                value = value.copy(executionStatus = ExecutionStatus.LOADING)
                val headers = headers.toMutableMap().apply {
                    put(NetConfig.DYNAMIC_URL, url)
                }
                val forms = forms.toMutableMap().apply {
                    val time = System.currentTimeMillis()
                    remove("signature")
                    remove("account")
                    remove("uid")
                    remove("user_id")
                    remove("userid")
                    remove("time")
                    remove("timestamp")
                    put("timestamp", "$time")
                    put("time", "$time")
                    put("account", initAccount)
                    put("uid", initAccount)
                    put("user_id", initAccount)
                    put("userid", initAccount)
                    put("str", time.verify())
                    val sb = StringBuffer()
                    entries.sortedWith(compareBy { it.key }).forEach { (key, value) ->
                        if (sb.isNotEmpty()) sb.append("&")
                        sb.append("$key=$value")
                    }
                    put("signature", "${Constant.START_KEY}$sb${Constant.END_KEY}".toMd5String())
                }
                value = try {
                    value.copy(executionStatus = ExecutionStatus.SUCCESS,
                        info = JSONObject(baseEndpoints.postUserDetail(headers = headers,
                            forms)).toString())
                } catch (e: Exception) {
                    value.copy(executionStatus = ExecutionStatus.SUCCESS,
                        info = e.message ?: "未知异常")
                }
            }
        }
    }

    fun loadForms() {
        _infoViewState.apply {
            var forms: Map<String, String> = emptyMap()
            val param = Constant.PARAM
            if (param.isNotBlank()) {
                forms =
                    JSON.decodeFromString(MapSerializer(String.serializer(), String.serializer()),
                        param)
            }
            var headers: Map<String, String> = emptyMap()
            val s = Constant.HEADERS
            if (s.isNotBlank()) {
                headers =
                    JSON.decodeFromString(MapSerializer(String.serializer(), String.serializer()),
                        s)
            }
            value = value.copy(url = Constant.URL, forms = forms, headers = headers)
        }
    }

    private fun recover() {
        viewModelScope.launch {
            delay(250)
            _viewState.apply {
                value = value.copy(executionStatus = ExecutionStatus.UNKNOWN)
            }
        }
    }

    private fun Long.verify(): String {
        val concat = Constant.START_KEY + Constant.END_KEY
        val substring = concat.substring(8, concat.length - 8)
        return "$this$substring".toMd5String().lowercase(Locale.getDefault())
    }
}