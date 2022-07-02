package com.baimsg.chat.fragment.local

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.verifySensitiveWords
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@HiltViewModel
class LocalViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val userInfoDao: UserInfoDao,
    private val taskAccountDao: TaskAccountDao
) : ViewModel() {
    private val initAppKey = handle["appKey"] ?: ""

    private val _viewState by lazy {
        MutableStateFlow(LocalViewState.EMPTY)
    }

    val observeViewState: StateFlow<LocalViewState> = _viewState

    val allAccounts: List<NIMUserInfo>
        get() = _viewState.value.allAccounts

    init {
        loadAllAccount()
    }

    /**
     * 加载数据库列表
     */
    fun loadAllAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = value.copy(
                    allAccounts = userInfoDao.entriesByAppKey(appKey = initAppKey),
                    updateStatus = UpdateStatus.REFRESH
                )
            }
        }
    }

    /**
     * 单个删除
     * @param nimUserInfo 删除对象
     */
    fun deleteAccountById(nimUserInfo: NIMUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = value.copy(allAccounts = allAccounts.toMutableList().apply {
                    remove(nimUserInfo)
                }, updateStatus = UpdateStatus.REMOVE)
            }
            userInfoDao.deleteById(nimUserInfo.id)
        }
    }

    /**
     * 全部删除
     */
    fun deleteAllByAppKey() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = value.copy(allAccounts = emptyList(), updateStatus = UpdateStatus.REFRESH)
            }
            userInfoDao.deleteByAppKey(initAppKey)
        }
    }

    /**
     * 全部加入任务
     */
    fun addTaskAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = allAccounts.filter { !it.name.verifySensitiveWords() }.map { it.asTask() }
            taskAccountDao.updateOrInsert(tasks)
        }
    }


    /**
     * 添加到任务
     * @param nimUserInfo
     */
    fun addTask(nimUserInfo: NIMUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.updateOrInsert(nimUserInfo.asTask())
        }
    }


}