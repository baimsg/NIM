package com.baimsg.chat.fragment.local

import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMUserInfo

data class LocalViewState(
    val allAccounts: List<NIMUserInfo>,
    val updateStatus: UpdateStatus
) {
    companion object {
        val EMPTY = LocalViewState(allAccounts = emptyList(), UpdateStatus.DEFAULT)
    }
}