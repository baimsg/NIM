package com.baimsg.chat.fragment.team.bulk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.JSON
import com.baimsg.data.model.entities.NIMTeam
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject


/**
 * Create by Baimsg on 2022/7/22
 *
 **/
@HiltViewModel
class BulkViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    private val msgService by lazy {
        NIMClient.getService(MsgService::class.java)
    }

    private val allTeam: MutableList<NIMTeam> = JSON.decodeFromString(
        ListSerializer(NIMTeam.serializer()),
        handle["teams"] ?: ""
    ).toMutableList()

    private val _viewState by lazy {
        MutableStateFlow(BulkViewState.EMPTY)
    }

    val observeViewState: StateFlow<BulkViewState> = _viewState

    fun setMessage(msg: String) {
        _viewState.apply {
            value = value.copy(message = msg)
        }
        startSend()
    }

    fun startSend() {
        _viewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, tip = "")
                send()
            }
        }
    }

    private fun send() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (allTeam.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    delay(Constant.DELAY)
                    val team = allTeam[0]
                    allTeam.removeAt(0)
                    val textMessage = MessageBuilder.createTextMessage(
                        team.id,
                        SessionTypeEnum.Team,
                        _viewState.value.message
                    )
                    value = value.copy(team = team, status = BatchStatus.RUNNING)
                    msgService.sendMessage(textMessage, false)
                        .setCallback(object : RequestCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                value = value.copy(tip = "发送成功")
                                send()
                            }

                            override fun onFailed(code: Int) {
                                value = value.copy(tip = "发送失败「$code」")
                                send()
                            }

                            override fun onException(e: Throwable?) {
                                value = value.copy(tip = "发送失败「${e?.message}」")
                                send()
                            }
                        })
                }
            }
        }
    }
}