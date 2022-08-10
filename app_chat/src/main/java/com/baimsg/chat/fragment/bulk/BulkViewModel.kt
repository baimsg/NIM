package com.baimsg.chat.fragment.bulk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.JSON
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import org.json.JSONObject
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

    private val sessionType by lazy {
        handle["sessionType"] ?: SessionTypeEnum.Team
    }

    private val allTeam: MutableList<BulkData> by lazy {
        JSON.decodeFromString(
            ListSerializer(BulkData.serializer()),
            handle["bulks"] ?: ""
        ).toMutableList()
    }

    private val _viewState by lazy {
        MutableStateFlow(BulkViewState.EMPTY)
    }

    val observeViewState: StateFlow<BulkViewState> = _viewState

    val message: String
        get() = _viewState.value.message

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
                if (sessionType == SessionTypeEnum.Ysf)
                    forcedOffline()
                else
                    send()
            }
        }
    }

    fun forcedOffline() {
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
                    val notification = CustomNotification()
                    notification.sessionId = team.id
                    notification.sessionType = SessionTypeEnum.P2P
                    val json = JSONObject().apply {
                        put("id", 1)
                        put("data", JSONObject().apply { put("islogin", 0) })
                        put("isapp", 0)
                        put("isclear", 0)
                        put("islogin", 0)
                        put("issup", 1)
                    }
                    notification.isSendToOnlineUserOnly = false
                    val config = CustomNotificationConfig()
                    config.enablePush = true
                    config.enableUnreadCount = true
                    notification.config = config
                    notification.apnsText = json.toString()
                    notification.content = json.toString()
                    value = value.copy(bulkData = team, status = BatchStatus.RUNNING)
                    NIMClient.getService(MsgService::class.java)
                        .sendCustomNotification(notification)
                        .setCallback(object : RequestCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                value = value.copy(tip = "发送下线成功")
                                forcedOffline()
                            }

                            override fun onFailed(code: Int) {
                                value = value.copy(tip = "发送下线失败「$code」")
                                forcedOffline()
                            }

                            override fun onException(e: Throwable?) {
                                value = value.copy(tip = "发送下线失败「${e?.message}」")
                                forcedOffline()
                            }
                        })
                }
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
                        sessionType,
                        _viewState.value.message
                    )
                    value = value.copy(bulkData = team, status = BatchStatus.RUNNING)
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