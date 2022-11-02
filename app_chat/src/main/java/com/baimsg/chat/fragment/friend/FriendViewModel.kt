package com.baimsg.chat.fragment.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.fragment.bulk.BulkData
import com.baimsg.chat.fragment.bulk.BulkType
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    val allUsers: List<NIMUserInfo>
        get() = _viewState.value.allUsers

    val loading: Boolean
        get() = _viewState.value.executionStatus == ExecutionStatus.LOADING

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
            value = FriendViewState.EMPTY.copy(executionStatus = ExecutionStatus.LOADING)
            val allFriends = friendService.friends ?: return
            value = value.copy(
                allFriends = allFriends,
                executionStatus = ExecutionStatus.LOADING
            )
            val allUsers = userService.getUserInfoList(allFriends.map { it.account })
            if (allUsers.isNullOrEmpty()) {
                value = value.copy(executionStatus = ExecutionStatus.SUCCESS)
                recover()
                return
            }
            if (allFriends.size == allUsers.size) {
                value = value.copy(
                    allUsers = allUsers.map { it.asUser() },
                    executionStatus = ExecutionStatus.SUCCESS
                )
                recover()
                return
            }
        }
        getUserInfo(page = 0)
    }


    /**
     * 获取好友列表信息
     * @param limit 返回数量
     */
    private fun getUserInfo(page: Int, limit: Int = 15) {
        _viewState.apply {
            val start = page * limit
            val end = start + limit
            val accounts = mutableListOf<String>().apply {
                (start until end).forEachIndexed { _, i ->
                    if (i in allAccounts.indices) add(allAccounts[i].account)
                }
            }
            if (accounts.isEmpty()) {
                value = value.copy(executionStatus = ExecutionStatus.SUCCESS)
                recover()
                return
            }
            userService.fetchUserInfo(accounts)
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(mUsers: List<NimUserInfo>?) {
                        val users = mUsers?.map { it.asUser() } ?: emptyList()
                        value = value.copy(
                            allUsers = value.allUsers.toMutableList().apply {
                                addAll(users)
                            }
                        )
                        getUserInfo(page = page + 1)
                    }

                    override fun onFailed(code: Int) {
                        getUserInfo(page = page)
                    }

                    override fun onException(e: Throwable?) {
                        getUserInfo(page = page)
                    }
                })
        }
    }

    /**
     * 恢复默认状态
     */
    private fun recover() {
        viewModelScope.launch {
            delay(250)
            _viewState.apply {
                value = value.copy(executionStatus = ExecutionStatus.UNKNOWN)
            }
        }
    }

    /**
     * 更新选择中
     * @param indices 选中的位置数组
     * @param bulkType 操作类型
     */
    fun upSelectBulks(indices: IntArray, bulkType: BulkType) {
        selectBulks = mutableListOf<BulkData>().apply {
            indices.forEach { i ->
                val user = allUsers[i]
                add(BulkData(id = user.account, user.name, bulkType))
            }
        }
    }
}