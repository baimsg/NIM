package com.baimsg.chat.fragment.friend

import androidx.lifecycle.ViewModel
import com.baimsg.chat.fragment.bulk.BulkData
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.model.Friend
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class FriendViewModel @Inject constructor() : ViewModel() {

    /**
     * 用户服务
     */
    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

    /**
     * 好友服务
     */
    private val friendService by lazy {
        NIMClient.getService(FriendService::class.java)
    }

    /**
     * 好友列表状态
     */
    private val _viewState by lazy {
        MutableStateFlow(FriendViewState.EMPTY)
    }

    /**
     * 用于监听的状态
     */
    val observeViewState: StateFlow<FriendViewState> = _viewState

    /**
     * 所有好友
     */
    val allAccounts: List<Friend>
        get() = _viewState.value.allFriends

    /**
     * 所有用户信息
     */
    val allUsers: List<NIMUserInfo>
        get() = _viewState.value.allUsers

    /**
     * 当前页码
     */
    private val page: Int
        get() = _viewState.value.page


    /**
     * 选择的好友列表
     */
    var selectBulks: MutableList<BulkData> = mutableListOf()
        private set


    /**
     * 加载好友列表
     */
    fun loadFriends() {
        _viewState.apply {
            value = FriendViewState.EMPTY.copy(allFriends = friendService.friends)
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
    private fun getUserInfo(limit: Int = 100) {
        val start = page * limit
        val end = start + limit
        val accounts = mutableListOf<String?>().apply {
            (start until end).forEachIndexed { _, i ->
                if (i in allAccounts.indices) add(allAccounts[i].account)
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

    /**
     * 更新选择中群聊
     */
    fun upCheckTeam(indices: IntArray) {
        selectBulks = mutableListOf<BulkData>().apply {
            indices.forEach { i ->
                val user = allUsers[i]
                add(BulkData(id = user.account, user.name))
            }
        }
    }
}