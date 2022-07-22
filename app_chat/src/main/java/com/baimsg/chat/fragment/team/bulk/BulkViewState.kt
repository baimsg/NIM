package com.baimsg.chat.fragment.team.bulk

import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMTeam

/**
 * Create by Baimsg on 2022/7/22
 *
 **/
data class BulkViewState(
    val executionStatus: ExecutionStatus,
    val team: NIMTeam,
    val message: String,
    val status: BatchStatus,
    val tip: String
) {
    companion object {
        val EMPTY =
            BulkViewState(
                executionStatus = ExecutionStatus.UNKNOWN,
                team = NIMTeam(),
                message = "",
                status = BatchStatus.UNKNOWN,
                tip = ""
            )
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

    fun unknown() = status == BatchStatus.UNKNOWN

    fun stop() = status == BatchStatus.STOP
}