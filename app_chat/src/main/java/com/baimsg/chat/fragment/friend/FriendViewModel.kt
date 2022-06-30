package com.baimsg.chat.fragment.friend

import androidx.lifecycle.ViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.coroutines.flow.MutableStateFlow

class FriendViewModel : ViewModel() {

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
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
        userService.fetchUserInfo(accounts)
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


}