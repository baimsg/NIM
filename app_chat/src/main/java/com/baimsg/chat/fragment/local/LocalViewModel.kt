package com.baimsg.chat.fragment.local

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.verifySensitiveWords
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    /**
     * 可观察状态对象
     */
    val observeViewState: StateFlow<LocalViewState> = _viewState

    /**
     * 所有用户列表
     */
    val allAccounts: List<NIMUserInfo>
        get() = _viewState.value.allAccounts

    private val _localOperateViewState by lazy {
        MutableStateFlow(LocalOperateViewState.EMPTY)
    }

    val observeOperateViewState: StateFlow<LocalOperateViewState> = _localOperateViewState


    /**
     * 加载数据库数据
     */
    fun loadAllAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = LocalViewState.EMPTY.copy(executionStatus = ExecutionStatus.LOADING)
                val allAccounts = userInfoDao.entriesByAppKey(appKey = initAppKey)
                value = value.copy(
                    executionStatus = ExecutionStatus.SUCCESS,
                    allAccounts = allAccounts,
                    updateStatus = UpdateStatus.REFRESH
                )
                recover()
            }
        }
    }

    /**
     * 删除一条数据
     * @param nimUserInfo 删除对象
     */
    fun deleteAccountById(nimUserInfo: NIMUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            userInfoDao.deleteById(nimUserInfo.id)
            _viewState.apply {
                value = value.copy(allAccounts = allAccounts.toMutableList().apply {
                    remove(nimUserInfo)
                }, updateStatus = UpdateStatus.REMOVE)
            }
        }
    }

    /**
     * 删除全部数据
     */
    suspend fun deleteAllByAppKey() {
        withContext(Dispatchers.IO) {
            userInfoDao.deleteByAppKey(initAppKey)
            _viewState.apply {
                value = value.copy(allAccounts = emptyList(), updateStatus = UpdateStatus.REFRESH)
            }
        }
    }

    /**
     * 添加一条数据到任务
     * @param nimUserInfo 数据
     */
    fun addTask(nimUserInfo: NIMUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            _localOperateViewState.apply {
                try {
                    taskAccountDao.updateOrInsert(nimUserInfo.asTask())
                } catch (e: Exception) {

                }
            }

        }
    }

    /**
     * 全部添加到任务
     */
    suspend fun addTaskAll() {
        withContext(Dispatchers.IO) {
            val tasks = allAccounts.filter { !it.name.verifySensitiveWords() }.map { it.asTask() }
            taskAccountDao.updateOrInsert(tasks)
        }
    }

    private suspend fun recover() {
        delay(250)
        _viewState.apply {
            value = value.copy(executionStatus = ExecutionStatus.UNKNOWN)
        }
        _localOperateViewState.apply {
            value = value.copy(executionStatus = ExecutionStatus.UNKNOWN)
        }
    }

}