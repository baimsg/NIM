package com.baimsg.chat.fragment.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.encodeBase64
import com.baimsg.data.api.BaseEndpoints
import com.baimsg.data.api.NetConfig
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val baseEndpoints: BaseEndpoints
) : ViewModel() {
    val initFromRemote = handle["fromRemote"] ?: false

    val initUserInfo = handle["userInfo"] ?: NIMUserInfo()

    private val _viewState by lazy {
        MutableStateFlow(UserDetailViewState.EMPTY)
    }

    val observeViewState: StateFlow<UserDetailViewState> = _viewState

    /**
     * @param baseUrl API地址
     */
    fun getUserInfo(baseUrl: String, sign: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = value.copy(data = Loading())
                value = try {
                    val headers = mutableMapOf(
                        NetConfig.DYNAMIC_URL to baseUrl,
                        "auth" to "",
                        "appkey" to sign,
                    )
                    val fields = mutableMapOf(
                        "uid" to initUserInfo.account,
                        "sign" to sign,
                        "timestamp" to System.currentTimeMillis().toString(),
                    )
                    value.copy(
                        data = Success(
                            baseEndpoints.postUserDetail(
                                headers = headers,
                                fields = fields
                            )
                        )
                    )
                } catch (e: Exception) {
                    value.copy(data = Fail(e))
                }
            }

        }
    }

}