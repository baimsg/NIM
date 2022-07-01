package com.baimsg.chat.fragment.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.base.util.extensions.logE
import com.baimsg.chat.Constant
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatchExecuteViewModel @Inject constructor(
    private val taskAccountDao: TaskAccountDao
) : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val _viewState by lazy {
        MutableStateFlow(BatchExecuteViewState.EMPTY)
    }

    val observeViewState: StateFlow<BatchExecuteViewState> = _viewState

    private val allTaskAccounts: List<NIMTaskAccount>
        get() = _viewState.value.allTaskAccounts

    private val allTeam: List<NIMTeam>
        get() = _viewState.value.allTeam


    private val _testView by lazy {
        MutableStateFlow(TestView())
    }

    val observeTestView: StateFlow<TestView> = _testView

    private val testView: TestView
        get() = _testView.value

    fun loadAllAccount(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = BatchExecuteViewState.EMPTY
                value = value.copy(
                    allTaskAccounts = taskAccountDao.entriesByAppKey(appKey),
                    updateStatus = UpdateStatus.REFRESH
                )
            }
        }
    }

    fun deleteById(nimTaskAccount: NIMTaskAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.deleteById(nimTaskAccount.id)
            _viewState.apply {
                value = value.copy(allTaskAccounts = allTaskAccounts.toMutableList().apply {
                    remove(nimTaskAccount)
                }, updateStatus = UpdateStatus.DEFAULT)
            }
        }
    }

    fun addTeam(nimTeam: NIMTeam) {
        _viewState.apply {
            value = value.copy(allTeam = allTeam.toMutableList().apply {
                add(nimTeam)
            }, updateStatus = UpdateStatus.APPEND)
        }
    }

    fun deleteByAppKey(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = BatchExecuteViewState.EMPTY
            }
            taskAccountDao.deleteByAppKey(appKey)
        }
    }


    private var index = 0

    fun startInvite() {
        allTeam.forEachIndexed { index, nimTeam ->
            invite(nimTeam.id)
        }
    }

    fun invite(teamId: String) {
        viewModelScope.launch {
            delay(Constant.ADD_FRIEND_DELAY)
            val all = allTaskAccounts
            _testView.apply {
                if (value.index > 200) return@launch
                val nimTaskAccount = all[index]
                index++
                teamService.addMembersEx(teamId, listOf(nimTaskAccount.account), "快来进群", null)
                    .setCallback(object : RequestCallback<List<String>> {
                        override fun onSuccess(p0: List<String>?) {
                            _testView.apply {
                                value = value.copy(
                                    index = value.index + 1,
                                    name = p0?.toString() ?: "",
                                    executionStatus = ExecutionStatus.SUCCESS
                                )
                            }
                            invite(teamId)
                        }

                        override fun onFailed(p0: Int) {
                            _testView.apply {
                                value = value.copy(
                                    name = "error:$p0 :${nimTaskAccount.name}-${nimTaskAccount.account}}",
                                    executionStatus = ExecutionStatus.FAIL
                                )
                            }
                            invite(teamId)
                        }

                        override fun onException(p0: Throwable?) {
                            _testView.apply {
                                value = value.copy(
                                    name = "error:${p0?.message}:${nimTaskAccount.name}-${nimTaskAccount.account}}",
                                    executionStatus = ExecutionStatus.FAIL
                                )
                            }
                            invite(teamId)
                        }
                    })
            }
        }

    }


}