package com.baimsg.chat.fragment.scanning.account

import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
data class ScanningAccountViewState(
    val account: Long,
    val count: Long,
    val status: BatchStatus,
    val update: Boolean,
    val users: List<NIMUserInfo>,
    val allUser: List<NIMUserInfo>,
) {
    companion object {
        val EMPTY = ScanningAccountViewState(
            account = 0,
            count = 0,
            status = BatchStatus.UNKNOWN,
            update = false,
            users = emptyList(),
            allUser = emptyList()
        )
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

    fun isDestroy() = status == BatchStatus.STOP || status == BatchStatus.UNKNOWN
}
