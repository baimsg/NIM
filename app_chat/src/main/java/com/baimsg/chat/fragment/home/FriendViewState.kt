package com.baimsg.chat.fragment.home

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
data class FriendViewState(
    val executionStatus: ExecutionStatus,
    val users: List<NIMUserInfo>
) {

    companion object {
         val EMPTY =
            FriendViewState(executionStatus = ExecutionStatus.UNKNOWN, users = emptyList())
    }
}