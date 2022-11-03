package com.baimsg.chat.fragment.bulk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.Constant
import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.JSON
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig
import com.netease.nimlib.sdk.team.TeamService
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
    handle: SavedStateHandle,
) : ViewModel() {

    private val msgService by lazy {
        NIMClient.getService(MsgService::class.java)
    }

    private val friendService by lazy {
        NIMClient.getService(FriendService::class.java)
    }

    private val teamService by lazy {
        NIMClient.getService(TeamService::class.java)
    }

    private val allBulk: MutableList<BulkData> by lazy {
        JSON.decodeFromString(ListSerializer(BulkData.serializer()), handle["bulks"] ?: "")
            .toMutableList()
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
        startBulk()
    }

    fun startBulk() {
        _viewState.apply {
            if (value.running()) {
                value = value.copy(status = BatchStatus.PAUSE)
            } else {
                value = value.copy(status = BatchStatus.RUNNING, tip = "")
                when (allBulk.firstOrNull()?.bulkType) {
                    BulkType.FriendSendMessage, BulkType.TeamSendMessage -> send()
                    BulkType.FriendDelete -> deleteFriend()
                    BulkType.TeamDelete -> deleteTeam()
                    else -> forcedOffline()
                }
            }
        }
    }

    /**
     * 强制下线
     */
    fun forcedOffline() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (allBulk.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    delay(Constant.DELAY)
                    val bulkData = allBulk[0]
                    allBulk.removeAt(0)
                    val notification = CustomNotification().apply {
                        sessionId = bulkData.id
                        sessionType = SessionTypeEnum.P2P
                        isSendToOnlineUserOnly = false
                        val json = JSONObject().apply {
                            put("id", 1)
                            put("data", JSONObject().apply { put("islogin", 0) })
                            put("isapp", 0)
                            put("isclear", 0)
                            put("islogin", 0)
                            put("issup", 1)
                        }
                        config = CustomNotificationConfig().apply {
                            enablePush = true
                            enableUnreadCount = true
                        }
                        apnsText = json.toString()
                        content = json.toString()
                    }
                    value = value.copy(bulkData = bulkData, status = BatchStatus.RUNNING, tip = "")
                    msgService.sendCustomNotification(notification)
                        .setCallback(object : RequestCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                value = value.copy(tip = "下线成功")
                                forcedOffline()
                            }

                            override fun onFailed(code: Int) {
                                value = value.copy(tip = "下线失败「$code」")
                                forcedOffline()
                            }

                            override fun onException(e: Throwable?) {
                                value = value.copy(tip = "下线失败「${e?.message}」")
                                forcedOffline()
                            }
                        })
                }
            }
        }
    }

    /**
     * 发送消息
     */
    private fun send() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (allBulk.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    delay(Constant.DELAY)
                    val bulkData = allBulk[0]
                    allBulk.removeAt(0)
                    val textMessage = MessageBuilder.createTextMessage(bulkData.id,
                        bulkData.bulkType.toSessionTypeEnum(),
                        _viewState.value.message)
                    value = value.copy(bulkData = bulkData, status = BatchStatus.RUNNING, tip = "")
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

    /**
     * 删除好友
     */
    private fun deleteFriend() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (allBulk.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    delay(Constant.DELAY)
                    val bulkData = allBulk[0]
                    allBulk.removeAt(0)
                    value = value.copy(bulkData = bulkData, status = BatchStatus.RUNNING, tip = "")
                    friendService.deleteFriend(bulkData.id, true)
                        .setCallback(object : RequestCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                value = value.copy(tip = "删除成功")
                                send()
                            }

                            override fun onFailed(code: Int) {
                                value = value.copy(tip = "删除失败「$code」")
                                send()
                            }

                            override fun onException(e: Throwable?) {
                                value = value.copy(tip = "删除失败「${e?.message}」")
                                send()
                            }
                        })
                }
            }
        }
    }

    /**
     * 删除好友
     */
    private fun deleteTeam() {
        viewModelScope.launch(Dispatchers.IO) {
            runBlocking {
                _viewState.apply {
                    if (value.pause()) return@runBlocking
                    if (allBulk.isEmpty()) {
                        value = value.copy(status = BatchStatus.STOP)
                        return@runBlocking
                    }
                    delay(Constant.DELAY)
                    val bulkData = allBulk[0]
                    allBulk.removeAt(0)
                    value = value.copy(bulkData = bulkData, status = BatchStatus.RUNNING, tip = "")
                    teamService.dismissTeam(bulkData.id)
                        .setCallback(object : RequestCallback<Void> {
                            override fun onSuccess(p0: Void?) {
                                value = value.copy(tip = "解散成功")
                                send()
                            }

                            override fun onFailed(code: Int) {
                                value = value.copy(tip = "解散失败「$code」")
                                send()
                            }

                            override fun onException(e: Throwable?) {
                                value = value.copy(tip = "解散失败「${e?.message}」")
                                send()
                            }
                        })
                }
            }
        }
    }

}