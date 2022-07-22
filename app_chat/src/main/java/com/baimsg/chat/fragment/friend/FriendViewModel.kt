package com.baimsg.chat.fragment.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendViewModel @Inject constructor() : ViewModel() {

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

    private val friendService by lazy {
        NIMClient.getService(FriendService::class.java)
    }

    private val _viewState by lazy {
        MutableStateFlow(FriendViewState.EMPTY)
    }

    val observeViewState: StateFlow<FriendViewState> = _viewState

    val allAccounts: List<String> = _viewState.value.allAccounts

    private val page: Int = _viewState.value.page

    /**
     * 加载好友列表
     */
    fun loadFriends() {
        _viewState.apply {
            value = FriendViewState.EMPTY.copy(allAccounts = friendService.friendAccounts)
        }
        getUserInfo()
    }

    /**
     * 下一页数据
     */
    fun nextPage() {
        _viewState.apply {
            value = value.copy(executionStatus = ExecutionStatus.UNKNOWN, page = value.page + 1)
        }
        getUserInfo()
    }

    /**
     * 获取好友列表信息
     * @param limit 返回数量
     */
    private fun getUserInfo(limit: Int = 20) {
        val start = page * limit
        val end = start + limit
        val accounts = mutableListOf<String?>().apply {
            (start until end).forEachIndexed { _, i ->
                if (i in allAccounts.indices) add(allAccounts[i])
            }
        }

        if (accounts.isEmpty()) {
            _viewState.apply {
                value = value.copy(executionStatus = ExecutionStatus.EMPTY)
            }
            return
        }
        userService.fetchUserInfo(accounts)
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(mUsers: List<NimUserInfo>?) {
                    val users = mUsers?.map { it.asUser() } ?: emptyList()
                    _viewState.apply {
                        value = value.copy(
                            executionStatus = ExecutionStatus.SUCCESS,
                            allUsers = value.allUsers.toMutableList().apply {
                                addAll(users)
                            },
                            newUsers = users
                        )
                    }
                }

                override fun onFailed(code: Int) {
                    _viewState.apply {
                        value = value.copy(executionStatus = ExecutionStatus.FAIL)
                    }
                }

                override fun onException(e: Throwable?) {
                    _viewState.apply {
                        value = value.copy(executionStatus = ExecutionStatus.FAIL)
                    }
                }
            })
    }


}