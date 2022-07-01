package com.baimsg.chat.fragment.batch

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMTeam

data class BatchExecuteViewState(
    val allTaskAccounts: List<NIMTaskAccount>,
    val allTeam: List<NIMTeam>,
    val inviteRecord: Map<NIMTeam, Int>,
    val updateStatus: UpdateStatus
) {
    companion object {
        val EMPTY = BatchExecuteViewState(
            allTaskAccounts = emptyList(),
            allTeam = emptyList(),
            inviteRecord = emptyMap(),
            updateStatus = UpdateStatus.DEFAULT
        )
    }
}

data class TestView(
    val index: Int = 0,
    val name: String = "",
    val executionStatus: ExecutionStatus = ExecutionStatus.UNKNOWN
)