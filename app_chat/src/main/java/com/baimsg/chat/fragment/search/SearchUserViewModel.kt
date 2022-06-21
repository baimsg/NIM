package com.baimsg.chat.fragment.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
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
     * 搜索参数
     */
    val searchViewState by lazy {
        MutableStateFlow(SearchViewState.EMPTY)
    }


    /**
     * 更新输入内容
     * @param account 输入的内容
     */
    fun updateAccount(account: Long) {
        searchViewState.apply {
            value = SearchViewState.EMPTY.copy(account = account)
        }
    }

    fun stopSearchUser() {
        searchViewState.apply {
            value = SearchViewState.EMPTY.copy(status = BatchStatus.STOP)
        }
    }

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

    private fun searchUser(account: Long) {
        searchViewState.apply {
            if (value.count >= Constant.SEARCH_COUNT || value.pause()) {
                return
            }

            val accounts = mutableListOf<String>().apply {
                (0..149).forEach { _ ->
                    value = value.copy(count = value.count + 1, update = false)
                    add("${Constant.SEARCH_PREFIX}${account + value.count}")
                }
            }

            NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(mUsers: List<NimUserInfo>?) {
                        val newUser = mUsers?.map { it.asUser() }
                        value = value.copy(
                            account = account + value.count,
                            users = newUser ?: emptyList(),
                            update = true,
                            allUser = value.allUser.toMutableList().apply {
                                if (newUser != null) {
                                    addAll(newUser)
                                }
                            }
                        )

                        searchUser(account + value.count)
                    }

                    override fun onFailed(code: Int) {
                        searchUser(account + value.count)
                    }

                    override fun onException(e: Throwable?) {
                        searchUser(account + value.count)
                    }
                })
        }
    }

}