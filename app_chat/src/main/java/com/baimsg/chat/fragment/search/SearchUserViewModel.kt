package com.baimsg.chat.fragment.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchUserViewModel : ViewModel() {

    /**
     * 开始值
     */
    val account by lazy {
        MutableStateFlow<String?>(null)
    }

    /**
     * 用户列表
     */
    val users by lazy {
        MutableStateFlow(mutableListOf(NIMUserInfo()))
    }

    /**
     * 用户列表
     */
    val allUsers by lazy {
        MutableStateFlow(mutableListOf(NIMUserInfo()))
    }

    /**
     * 运行中
     */
    val running by lazy {
        MutableStateFlow(false)
    }

    /**
     * 运行次数
     */
    val index by lazy {
        MutableStateFlow(0L)
    }

    /**
     * 当前值
     */
    val currentIndex by lazy {
        MutableStateFlow(0L)
    }

    /**
     * 更新输入内容
     * @param newAccount 输入的内容
     */
    fun updateAccount(newAccount: String?) {
        index.value = 0
        running.value = false
        account.value = newAccount
    }

    fun stopSearchUser() {
        running.value = false
    }

    fun searchUser() {
        running.value = true
        account.value?.toLong()?.let { searchUser(it) }
    }

    private fun searchUser(account: Long) {
        if (index.value >= Constant.SEARCH_COUNT || !running.value) {
            running.value = false
            index.value = 0
            return
        }
        val accounts = mutableListOf<String>().apply {
            (0..149).forEach { _ ->
                index.value = index.value + 1
                add("${Constant.SEARCH_PREFIX}${account + index.value}")
            }
        }

        NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(mUsers: List<NimUserInfo>?) {
                    val newUser = mUsers?.map { it.asUser() }
                    val oldUser = mutableListOf<NIMUserInfo>().apply {
                        addAll(allUsers.value.filter { it.loaded })
                        if (newUser != null) {
                            addAll(newUser)
                        }
                    }
                    allUsers.value = oldUser
                    newUser?.toMutableList()?.let {
                        users.value = it
                    }
                    searchUser(account + index.value)
                }

                override fun onFailed(code: Int) {
                    searchUser(account + index.value)
                }

                override fun onException(e: Throwable?) {
                    searchUser(account + index.value)
                }
            })
    }

}