package com.baimsg.chat.fragment.bulk

import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.ExecutionStatus
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
    val tip: String
) {
    companion object {
        val EMPTY =
            BulkViewState(
                executionStatus = ExecutionStatus.UNKNOWN,
                bulkData = BulkData("", ""),
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

@kotlinx.serialization.Serializable
data class BulkData(val id: String, val name: String) : Serializable