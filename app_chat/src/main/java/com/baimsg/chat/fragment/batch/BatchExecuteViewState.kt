package com.baimsg.chat.fragment.batch

import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMTaskAccount

data class BatchExecuteViewState(
    val allTaskAccounts: List<NIMTaskAccount>,
    val updateStatus: UpdateStatus
) {
    companion object {
        val EMPTY = BatchExecuteViewState(
            allTaskAccounts = emptyList(),
            updateStatus = UpdateStatus.DEFAULT
        )
    }
}