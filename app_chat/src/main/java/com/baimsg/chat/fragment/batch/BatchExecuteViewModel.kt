package com.baimsg.chat.fragment.batch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.BatchType
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMTeam
import com.baimsg.data.model.entities.asTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
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

    private val processedTaskAccounts: List<NIMTaskAccount>
        get() = allTaskAccounts.filter { !it.processed }

    private val _batchExecuteViewState by lazy {
        MutableStateFlow(BatchExecuteViewState.EMPTY)
    }

    val observeBatchExecuteViewState: StateFlow<BatchExecuteViewState> = _batchExecuteViewState

    val batchType: BatchType
        get() = _batchExecuteViewState.value.batchType

    val batchExecuteViewState: BatchExecuteViewState
        get() = _batchExecuteViewState.value

    private val teams: List<NIMTeam>
        get() = _batchExecuteViewState.value.teams

    val allTeam: MutableList<NIMTeam> = mutableListOf()

    init {
        loadAllAccount()
        teamService.queryTeamList()
            .setCallback(object : RequestCallback<List<Team>> {
                override fun onSuccess(teams: List<Team>) {
                    allTeam.addAll(teams.map { it.asTeam() })
                }

                override fun onFailed(code: Int) {
                }

                override fun onException(e: Throwable?) {
                }
            })
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
     * 更新选择的群
     * @param indices 选择序号集合
     */
    fun updateTeam(indices: IntArray) {
        val teams = mutableListOf<NIMTeam>().apply {
            indices.forEachIndexed { _, i ->
                add(allTeam[i])
            }
        }
        _batchExecuteViewState.apply {
            value = value.copy(teams = teams)
        }
        start(BatchType.TEAM)
    }

    /**
     * 停止批量操作
     */
    fun stop() {
        _batchExecuteViewState.apply {
            value =
                BatchExecuteViewState.EMPTY.copy(
                    batchType = BatchType.UNKNOWN,
                    status = BatchStatus.STOP
                )
        }
    }

    /**
     * 开始批量操作
     * @param batchType 操作类型
     */
    fun start(batchType: BatchType = this.batchType) {
        _batchExecuteViewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE, batchType = batchType)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, batchType = batchType)
                if (batchType == BatchType.FRIEND) addFriend() else addMember()
            }
        }
    }

    /**
     * 添加好友
     */
    private fun addFriend() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                delay(Constant.DELAY)
                _batchExecuteViewState.apply {
                    if (value.pause()) return@runBlocking
                    if (processedTaskAccounts.isEmpty()) {
                        value = value.copy(batchType = BatchType.UNKNOWN, status = BatchStatus.STOP)
                        value =
                            value.copy(message = "END OF RUN")
                        return@runBlocking
                    }
                    val nimTaskAccount = processedTaskAccounts[0]
                    nimTaskAccount.processed = true
                    val account = nimTaskAccount.account
                    _taskAccountViewState.apply {
                        value =
                            value.copy(task = nimTaskAccount, updateStatus = UpdateStatus.UPDATE)
                    }
                    friendService.ackAddFriendRequest(
                        account,
                        true
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

    /**
     * 邀请进群
     */
    private fun addMember() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                delay(Constant.DELAY)
                _batchExecuteViewState.apply {
                    val teamLimit = Constant.TEAM_LIMIT
                    if (value.pause()) return@runBlocking
                    if (processedTaskAccounts.isEmpty() || teams.isEmpty()) {
                        value = value.copy(batchType = BatchType.UNKNOWN, status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    val nimTaskAccount = processedTaskAccounts[0]
                    nimTaskAccount.processed = true
                    val account = nimTaskAccount.account
                    _taskAccountViewState.apply {
                        value =
                            value.copy(task = nimTaskAccount, updateStatus = UpdateStatus.UPDATE)
                    }
                    val team = teams[0]
                    val teamId = team.id
                    val head =
                        "(${team.memberCount}/$teamLimit)${team.name}-${nimTaskAccount.name}-$account ->"
                    if (team.memberCount >= teamLimit) {
                        value = value.copy(teams = teams.toMutableList().apply {
                            remove(team)
                        })
                        value =
                            value.copy(message = "$head 到达上限")
                        addMember()
                        return@runBlocking
                    }
                    teamService.addMembersEx(teamId, listOf(account), Constant.TEAM_DESCRIBE, null)
                        .setCallback(object : RequestCallback<List<String>> {
                            override fun onSuccess(accounts: List<String>?) {
                                viewModelScope.launch(Dispatchers.IO) {
                                    _taskAccountViewState.apply {
                                        value = value.copy(
                                            allTaskAccounts = allTaskAccounts.toMutableList()
                                                .apply {
                                                    remove(nimTaskAccount)
                                                },
                                            task = nimTaskAccount,
                                            updateStatus = UpdateStatus.REMOVE
                                        )
                                        taskAccountDao.deleteById(nimTaskAccount.id)
                                    }
                                    value =
                                        value.copy(message = "$head 邀请成功")
                                    team.memberCount += 1
                                    addMember()
                                }
                            }

                            override fun onFailed(code: Int) {
                                if (code == 802) {
                                    value = value.copy(teams = teams.toMutableList().apply {
                                        remove(team)
                                    })
                                    value =
                                        value.copy(message = "$head 没有权限")
                                } else {
                                    value =
                                        value.copy(message = "$head 邀请失败:$code")
                                }
                                addMember()
                            }

                            override fun onException(e: Throwable?) {
                                value =
                                    value.copy(message = "$head 邀请失败:${e?.message}")
                                addMember()
                            }
                        })
                }
            }
        }
    }

}