package com.baimsg.chat.fragment.local

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMUserInfo

data class LocalViewState(
    val executionStatus: ExecutionStatus,
    val allAccounts: List<NIMUserInfo>,
    val updateStatus: UpdateStatus
) {
    companion object {
        val EMPTY = LocalViewState(
            executionStatus = ExecutionStatus.UNKNOWN,
            allAccounts = emptyList(),
            updateStatus = UpdateStatus.DEFAULT
        )
    }
}

data class LocalOperateViewState(
    val executionStatus: ExecutionStatus,
    val tip: String
) {
    companion object {
        val EMPTY = LocalOperateViewState(executionStatus = ExecutionStatus.UNKNOWN, "")
    }
}