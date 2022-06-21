package com.baimsg.chat.fragment.search

import com.baimsg.chat.type.BatchStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/6/21
 *
 **/
data class SearchViewState(
    val account: Long,
    val count: Long,
    val status: BatchStatus,
    val update: Boolean,
    val users: List<NIMUserInfo>,
    val allUser: List<NIMUserInfo>,
) {
    companion object {
        val EMPTY = SearchViewState(
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

    fun isDestroy() = status == BatchStatus.STOP
}