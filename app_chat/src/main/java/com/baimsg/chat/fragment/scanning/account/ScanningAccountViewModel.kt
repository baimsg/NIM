package com.baimsg.chat.fragment.scanning.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.getId
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@HiltViewModel
class ScanningAccountViewModel @Inject constructor(
    private val userInfoDao: UserInfoDao
) : ViewModel() {

    private val userService by lazy {
        NIMClient.getService(UserService::class.java)
    }

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
            if (value.unknown()) value = ScanningAccountViewState.EMPTY.copy(account = account)
        }
    }

    /**
     * 清空已扫描到数据
     */
    fun cleanSearchAccount() {
        _viewState.apply {
            value = value.copy(allUser = emptyList(), updateStatus = UpdateStatus.CLEAN)
        }
    }

    /**
     * 清空已扫描到数据
     */
    fun stopSearchAccount() {
        _viewState.apply {
            value = ScanningAccountViewState.EMPTY
        }
    }

    /**
     * 开始或暂停搜索
     */
    fun searchAccount() {
        _viewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE, updateStatus = UpdateStatus.DEFAULT)
            } else {
                value =
                    value.copy(status = BatchStatus.RUNNING, updateStatus = UpdateStatus.DEFAULT)
                searchAccount(account = value.account)
            }
        }
    }

    /**
     * 循环搜索用户
     */
    private fun searchAccount(account: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                val searchCount = Constant.SEARCH_COUNT
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (value.count >= searchCount) {
                        value = value.copy(
                            status = BatchStatus.STOP,
                            updateStatus = UpdateStatus.DEFAULT
                        )
                        return@runBlocking
                    }
                    val accounts = mutableListOf<String>().apply {
                        (0..149).forEach { index ->
                            if (value.count >= searchCount) return@forEach
                            add((account + index).getId())
                            logE((account + index).getId())
                            value = value.copy(
                                count = value.count + 1,
                                updateStatus = UpdateStatus.DEFAULT
                            )
                        }
                    }
                    userService.fetchUserInfo(accounts)
                        .setCallback(object : RequestCallback<List<NimUserInfo>> {
                            override fun onSuccess(mUsers: List<NimUserInfo>?) {
                                val newUser = mUsers?.map { it.asUser() }
                                value = value.copy(
                                    account = account + accounts.size,
                                    newUsers = newUser ?: emptyList(),
                                    updateStatus = UpdateStatus.APPEND,
                                    allUser = value.allUser.toMutableList().apply {
                                        if (newUser != null) {
                                            addAll(newUser)
                                        }
                                    }
                                )
                                searchAccount(account + accounts.size)
                            }

                            override fun onFailed(code: Int) {
                                searchAccount(account)
                            }

                            override fun onException(e: Throwable?) {
                                searchAccount(account)
                            }
                        })
                }
            }
        }
    }

    /**
     * 保存到数据库
     * @param appKey 当前登录的key
     */
    suspend fun saveDatabase(appKey: String) {
        withContext(Dispatchers.IO) {
            userInfoDao.updateOrInsert(allUser.map {
                it.copy(
                    id = "$appKey-${it.account}",
                    appKey = appKey
                )
            })
        }
    }

}