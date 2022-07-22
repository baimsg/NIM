package com.baimsg.chat.fragment.team.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum
import com.netease.nimlib.sdk.team.model.CreateTeamResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/22
 *
 **/
@HiltViewModel
class CreateTeamViewModel @Inject constructor() : ViewModel() {

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val _viewState by lazy {
        MutableStateFlow(CreateTeamViewState.EMPTY)
    }

    val observeViewState: StateFlow<CreateTeamViewState> = _viewState

    private val fields: Map<TeamFieldEnum, Serializable>
        get() = _viewState.value.fields

    /**
     * 中缀函数改变群聊参数
     * @param value 参数值
     */
    infix fun TeamFieldEnum.set(value: Serializable) {
        _viewState.apply {
            this.value = this.value.copy(
                fields = fields.toMutableMap().apply {
                    put(this@set, value)
                }
            )
        }
    }

    operator fun plus(limit: Int) {
        _viewState.apply {
            this.value = this.value.copy(
                limit = limit
            )
        }
    }

    fun stopBatchCreateTeam() {
        _viewState.apply {
            value = CreateTeamViewState.EMPTY
        }
    }

    fun startBatchCreateTeam() {
        _viewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, message = "")
                batchCreateTeam()
            }
        }
    }

    /**
     * 批量创建群
     */
    private fun batchCreateTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    value = value.copy(status = BatchStatus.RUNNING)
                    if (value.index >= value.limit) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                }
                delay(Constant.DELAY)
                teamService.createTeam(fields, TeamTypeEnum.Advanced, "", listOf())
                    .setCallback(object : RequestCallback<CreateTeamResult> {
                        override fun onSuccess(result: CreateTeamResult?) {
                            _viewState.apply {
                                value = value.copy(message = "创建成功", index = value.index + 1)
                            }
                            batchCreateTeam()
                        }

                        override fun onFailed(code: Int) {
                            _viewState.apply {
                                value = value.copy(message = "创建失败「$code」")
                            }
                            batchCreateTeam()
                        }

                        override fun onException(e: Throwable?) {
                            _viewState.apply {
                                value = value.copy(message = "创建失败「${e?.message}」")
                            }
                            batchCreateTeam()
                        }
                    })
            }
        }
    }
}