package com.baimsg.chat.fragment.bulk

import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.ExecutionStatus
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import java.io.Serializable

/**
 * Create by Baimsg on 2022/7/22
 *
 **/
data class BulkViewState(
    val executionStatus: ExecutionStatus,
    val bulkData: BulkData,
    val message: String,
    val status: BatchStatus,
    val tip: String,
) {
    companion object {
        val EMPTY = BulkViewState(executionStatus = ExecutionStatus.UNKNOWN,
            bulkData = BulkData("", "", BulkType.TeamSendMessage),
            message = "",
            status = BatchStatus.UNKNOWN,
            tip = "")
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

    fun unknown() = status == BatchStatus.UNKNOWN

    fun stop() = status == BatchStatus.STOP
}

/**
 *
 * @param id 操作id
 * @param name 操作名称
 * @param bulkType 操作类型
 */
@kotlinx.serialization.Serializable
data class BulkData(val id: String, val name: String, val bulkType: BulkType) : Serializable

/**
 * 操作类型
 */
enum class BulkType {
    TeamSendMessage, FriendSendMessage, TeamDelete, FriendDelete, ForcedOffline
}

internal fun BulkType.toSessionTypeEnum(): SessionTypeEnum = when (this) {
    BulkType.TeamSendMessage -> SessionTypeEnum.Team
    BulkType.FriendSendMessage -> SessionTypeEnum.P2P
    else -> SessionTypeEnum.Ysf
}
