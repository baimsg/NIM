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

//    val searchUserViewState by lazy {
//        MutableStateFlow(SearchUserViewState())
//    }



    val account by lazy {
        MutableStateFlow<String?>(null)
    }

    val users by lazy {
        MutableStateFlow(listOf(NIMUserInfo()))
    }

    fun updateAccount(newAccount: String?) {
        viewModelScope.launch {
            account.emit(newAccount)
        }
    }

    fun searchUser() {
        viewModelScope.launch {

        }
        account.value?.toLong()?.let { searchUser(it) }
    }

    private fun searchUser(account: Long) {
        var index = 0
        if (index >= Constant.SEARCH_COUNT) {
            return
        }
        val accounts = mutableListOf<String>().apply {
            (0..149).forEach { _ ->
                index++
                add("${account + index}")
            }
        }

        NIMClient.getService(UserService::class.java).fetchUserInfo(accounts)
            .setCallback(object : RequestCallback<List<NimUserInfo>> {
                override fun onSuccess(mUsers: List<NimUserInfo>?) {
                    viewModelScope.launch {
                        mUsers?.map { it.asUser() }?.let { users.emit(it) }
                    }
                    searchUser(account + index)
                }

                override fun onFailed(code: Int) {
                    viewModelScope.launch {

                    }
                }

                override fun onException(e: Throwable?) {

                }
            })
    }

}