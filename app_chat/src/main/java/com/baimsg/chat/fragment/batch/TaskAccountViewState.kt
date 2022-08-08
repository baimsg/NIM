package com.baimsg.chat.fragment.batch

import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.BatchType
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMTeam

/**
 * @param allTaskAccounts 任务列表
 * @param updateStatus 更新状态
 */
data class TaskAccountViewState(
    val allTaskAccounts: List<NIMTaskAccount>,
    val task: NIMTaskAccount,
    val updateStatus: UpdateStatus
) {
    companion object {
        val EMPTY = TaskAccountViewState(
            allTaskAccounts = emptyList(),
            task = NIMTaskAccount(),
            updateStatus = UpdateStatus.DEFAULT
        )
    }
}


data class BatchExecuteViewState(
    val teams: List<NIMTeam>,
    val message: String,
    val batchType: BatchType,
    val status: BatchStatus,
) {
    companion object {
        val EMPTY =
            BatchExecuteViewState(
                teams = emptyList(),
                batchType = BatchType.UNKNOWN,
                message = "UNKNOWN",
                status = BatchStatus.UNKNOWN
            )
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

}