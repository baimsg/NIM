package com.baimsg.chat.fragment.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.api.BaseEndpoints
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

    init {
        _viewState.apply {
            value = value.copy(
                executionStatus = ExecutionStatus.LOADING,
                myFriend = friendService.isMyFriend(initAccount),
                inBlackList = friendService.isInBlackList(initAccount)
            )
            userService.fetchUserInfo(listOf(initAccount))
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(list: List<NimUserInfo>?) {
                        value = value.copy(
                            userInfo = list?.firstOrNull().asUser(),
                            executionStatus = ExecutionStatus.SUCCESS
                        )
                        recover()
                    }

                    override fun onFailed(code: Int) {
                        value = value.copy(
                            executionStatus = ExecutionStatus.FAIL
                        )
                        recover()
                    }

                    override fun onException(e: Throwable?) {
                        value = value.copy(
                            executionStatus = ExecutionStatus.FAIL
                        )
                        recover()
                    }
                })
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
}