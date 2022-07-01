package com.baimsg.chat.fragment.scanning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.length
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asUser
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@HiltViewModel
class ScanningAccountViewModel @Inject constructor(
    private val userInfoDao: UserInfoDao
) :
    ViewModel() {

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

    /**
     * 搜索参数
     */
    private val _viewState by lazy {
        MutableStateFlow(ScanningAccountViewState.EMPTY)
    }

    val observeViewState: StateFlow<ScanningAccountViewState> = _viewState

    val allUser: List<NIMUserInfo>
        get() = _viewState.value.allUser

    /**
     * 更新输入内容
     * @param account 输入的内容
     */
    fun updateAccount(account: Long) {
        _viewState.apply {
            if (value.isDestroy()) value = ScanningAccountViewState.EMPTY.copy(account = account)
        }
    }

    /**
     * 停止搜索
     */
    fun stopSearchAccount() {
        _viewState.apply {
            value = ScanningAccountViewState.EMPTY.copy(status = BatchStatus.STOP)
        }
    }

    /**
     * 开始或暂停搜索
     */
    fun searchAccount() {
        _viewState.apply {
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
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                if (value.count >= Constant.SEARCH_COUNT || value.pause()) {
                    value = value.copy(status = BatchStatus.PAUSE, update = false)
                    return@launch
                }
                val accounts = mutableListOf<String>().apply {
                    (0..149).forEach { index ->
                        val id = Constant.run {
                            "$SEARCH_PREFIX%0${SEARCH_COUNT.length()}d".format(account + index)
                        }
                        add(id)
                        logE(id)
                        value = value.copy(count = value.count + 1, update = false)
                    }
                }
                userService.fetchUserInfo(accounts)
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

}