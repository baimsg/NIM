package com.baimsg.chat.fragment.batch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.fragment.scanning.account.ScanningAccountViewState
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.BatchType
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.model.entities.NIMTaskAccount
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.uinfo.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class BatchExecuteViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val taskAccountDao: TaskAccountDao
) : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val friendService by lazy {
        NIMClient.getService(FriendService::class.java)
    }

    private val initAppKey = handle["appKey"] ?: ""

    private val _taskAccountViewState by lazy {
        MutableStateFlow(TaskAccountViewState.EMPTY)
    }

    val observeTaskAccountViewState: StateFlow<TaskAccountViewState> = _taskAccountViewState

    val allTaskAccounts: List<NIMTaskAccount>
        get() = _taskAccountViewState.value.allTaskAccounts

    val processedTaskAccounts: List<NIMTaskAccount>
        get() = allTaskAccounts.filter { !it.processed }

    private val _batchExecuteViewState by lazy {
        MutableStateFlow(BatchExecuteViewState.EMPTY)
    }

    val observeBatchExecuteViewState: StateFlow<BatchExecuteViewState> = _batchExecuteViewState

    val batchType: BatchType
        get() = _batchExecuteViewState.value.batchType

    val batchExecuteViewState: BatchExecuteViewState
        get() = _batchExecuteViewState.value

    init {
        loadAllAccount()
    }

    fun loadAllAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            _taskAccountViewState.apply {
                value = TaskAccountViewState.EMPTY
                value = value.copy(
                    allTaskAccounts = taskAccountDao.entriesByAppKey(appKey = initAppKey),
                    updateStatus = UpdateStatus.REFRESH
                )
            }
        }
    }

    fun deleteById(nimTaskAccount: NIMTaskAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.deleteById(nimTaskAccount.id)
            _taskAccountViewState.apply {
                value = value.copy(allTaskAccounts = allTaskAccounts.toMutableList().apply {
                    remove(nimTaskAccount)
                }, task = nimTaskAccount, updateStatus = UpdateStatus.REMOVE)
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.deleteByAppKey(initAppKey)
            _taskAccountViewState.apply {
                value = TaskAccountViewState.EMPTY
            }
        }
    }

    /**
     * 停止搜索
     */
    fun stop(batchType: BatchType = this.batchType) {
        _batchExecuteViewState.apply {
            value =
                BatchExecuteViewState.EMPTY.copy(batchType = batchType, status = BatchStatus.STOP)
        }
    }

    fun start(batchType: BatchType = this.batchType) {
        _batchExecuteViewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE, batchType = batchType)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, batchType = batchType)
                if (batchType == BatchType.FRIEND) addFriend()
            }
        }
    }

    private fun addFriend() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                delay(Constant.ADD_FRIEND_DELAY)
                _batchExecuteViewState.apply {
                    if (value.pause()) return@runBlocking
                    if (processedTaskAccounts.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    val nimTaskAccount = processedTaskAccounts[0]
                    nimTaskAccount.processed = true
                    val account = nimTaskAccount.account
                    _taskAccountViewState.apply {
                        value =
                            value.copy(task = nimTaskAccount, updateStatus = UpdateStatus.UPDATE)
                    }
                    friendService.addFriend(
                        AddFriendData(
                            account,
                            if (Constant.ADD_MODE) VerifyType.DIRECT_ADD else VerifyType.VERIFY_REQUEST,
                            Constant.ADD_VERIFY
                        )
                    ).setCallback(object : RequestCallback<Void> {
                        override fun onSuccess(a: Void?) {
                            viewModelScope.launch(Dispatchers.IO) {
                                _taskAccountViewState.apply {
                                    value = value.copy(
                                        allTaskAccounts = allTaskAccounts.toMutableList().apply {
                                            remove(nimTaskAccount)
                                        }, task = nimTaskAccount, updateStatus = UpdateStatus.REMOVE
                                    )
                                    taskAccountDao.deleteById(nimTaskAccount.id)
                                }
                                value =
                                    value.copy(message = "${nimTaskAccount.name}-$account -> 添加成功")
                                addFriend()
                            }
                        }

                        override fun onFailed(code: Int) {
                            value =
                                value.copy(message = "${nimTaskAccount.name}-$account -> 添加失败:$code")
                            addFriend()
                        }

                        override fun onException(e: Throwable?) {
                            value =
                                value.copy(message = "${nimTaskAccount.name}-$account -> 添加失败:${e?.message}")
                            addFriend()
                        }
                    })
                }
            }
        }
    }

//    fun addTeam(nimTeam: NIMTeam) {
//        _viewState.apply {
//            value = value.copy(allTeam = allTeam.toMutableList().apply {
//                add(nimTeam)
//            }, updateStatus = UpdateStatus.APPEND)
//        }
//    }


//
//    private var index = 0
//
//    fun startInvite() {
//        allTeam.forEachIndexed { index, nimTeam ->
//            invite(nimTeam.id)
//        }
//    }
//
//    fun invite(teamId: String) {
//        viewModelScope.launch {
//            delay(Constant.ADD_FRIEND_DELAY)
//            val all = allTaskAccounts
//            _testView.apply {
//                if (value.index > 200) return@launch
//                val nimTaskAccount = all[index]
//                index++
//                teamService.addMembersEx(teamId, listOf(nimTaskAccount.account), "快来进群", null)
//                    .setCallback(object : RequestCallback<List<String>> {
//                        override fun onSuccess(p0: List<String>?) {
//                            _testView.apply {
//                                value = value.copy(
//                                    index = value.index + 1,
//                                    name = p0?.toString() ?: "",
//                                    executionStatus = ExecutionStatus.SUCCESS
//                                )
//                            }
//                            invite(teamId)
//                        }
//
//                        override fun onFailed(p0: Int) {
//                            _testView.apply {
//                                value = value.copy(
//                                    name = "error:$p0 :${nimTaskAccount.name}-${nimTaskAccount.account}}",
//                                    executionStatus = ExecutionStatus.FAIL
//                                )
//                            }
//                            invite(teamId)
//                        }
//
//                        override fun onException(p0: Throwable?) {
//                            _testView.apply {
//                                value = value.copy(
//                                    name = "error:${p0?.message}:${nimTaskAccount.name}-${nimTaskAccount.account}}",
//                                    executionStatus = ExecutionStatus.FAIL
//                                )
//                            }
//                            invite(teamId)
//                        }
//                    })
//            }
//        }
//
//    }


}