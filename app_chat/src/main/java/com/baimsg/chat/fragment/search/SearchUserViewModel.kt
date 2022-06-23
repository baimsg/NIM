package com.baimsg.chat.fragment.search

import androidx.lifecycle.ViewModel
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.coroutines.flow.MutableStateFlow

class SearchUserViewModel : ViewModel() {

    /**
     * 搜索参数
     */
    val searchViewState by lazy {
        MutableStateFlow(SearchViewState.EMPTY)
    }

    /**
     * 加好友参数
     */
    val addFriendViewState by lazy {
        MutableStateFlow(AddFriendViewState.EMPTY)
    }

    /**
     * 更新输入内容
     * @param account 输入的内容
     */
    fun updateAccount(account: Long) {
        searchViewState.apply {
            if (value.isDestroy()) value = SearchViewState.EMPTY.copy(account = account)
        }
    }


    private fun String.verified(): Boolean {
        var verified = true
        Constant.ADD_FILTERS.forEachIndexed { _, s ->
            if (this.contains(s)) {
                verified = false
                return@forEachIndexed
            }
        }
        return verified
    }

    /**
     *
     */
    fun addFriend() {
        searchViewState.apply {
            addFriend(value.allUser[0])
        }
    }

    private fun addFriend(nimUserInfo: NIMUserInfo) {
        addFriendViewState.apply {
            if (nimUserInfo.name.verified()) {
                NIMClient.getService(FriendService::class.java).addFriend(
                    AddFriendData(
                        nimUserInfo.account,
                        VerifyType.DIRECT_ADD,
                        Constant.KEY_ADD_VERIFY
                    )
                ).setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        addFriendViewState.value =
                            AddFriendViewState(index = value.index + 1, "添加成功", nimUserInfo)
                        next()
                    }

                    override fun onFailed(code: Int) {
                        addFriendViewState.value =
                            AddFriendViewState(index = value.index + 1, "添加失败：$code", nimUserInfo)
                        next()
                    }

                    override fun onException(e: Throwable?) {
                        addFriendViewState.value =
                            AddFriendViewState(index = value.index + 1, "添加失败：$e", nimUserInfo)
                        next()
                    }
                })
            } else {
                value =
                    AddFriendViewState(index = value.index + 1, "敏感词过滤", nimUserInfo)
                next()
            }
        }
    }

    private fun next() {
        val allUser = searchViewState.value.allUser
        val index = addFriendViewState.value.index
        if (index in allUser.indices) addFriend(allUser[index])
    }

    /**
     * 停止搜索
     */
    fun stopSearchUser() {
        searchViewState.apply {
            value = SearchViewState.EMPTY.copy(status = BatchStatus.STOP)
        }
    }

    /**
     * 开始或暂停搜索
     */
    fun searchUser() {
        searchViewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE, update = false)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, update = false)
                searchUser(account = value.account)
            }
        }
    }

    /**
     * 循环搜索用户
     */
    private fun searchUser(account: Long) {
        searchViewState.apply {
            if (value.count >= Constant.SEARCH_COUNT || value.pause()) {
                return
            }
            val accounts = mutableListOf<String>().apply {
                (0..149).forEach { index ->
                    val id =
                        "${Constant.SEARCH_PREFIX}%0${Constant.SEARCH_COUNT.toString().length}d".format(
                            account + index
                        )
                    add(id)
//                    logE("id=$id ,count=${value.count}")
                    value = value.copy(count = value.count + 1, update = false)
                }
            }
            NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(mUsers: List<NimUserInfo>?) {
                        val newUser = mUsers?.map { it.asUser() }
                        value = value.copy(
                            account = account + 150,
                            users = newUser ?: emptyList(),
                            update = true,
                            allUser = value.allUser.toMutableList().apply {
                                if (newUser != null) {
                                    addAll(newUser)
                                }
                            }
                        )
                        searchUser(account + 150)
                    }

                    override fun onFailed(code: Int) {
                        searchUser(account)
                    }

                    override fun onException(e: Throwable?) {
                        searchUser(account)
                    }
                })
        }

    }

}