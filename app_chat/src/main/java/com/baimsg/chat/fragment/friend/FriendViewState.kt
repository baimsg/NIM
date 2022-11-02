package com.baimsg.chat.fragment.friend

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMUserInfo
import com.netease.nimlib.sdk.friend.model.Friend

/**
 * Create by Baimsg on 2022/6/20
 *
 * 好友数据状态
 * @param executionStatus 执行结果
 * @param allFriends 所有好友
 * @param allUsers 所有用户信息
 **/
data class FriendViewState(
    val executionStatus: ExecutionStatus,
    val allFriends: List<Friend>,
    val allUsers: List<NIMUserInfo>
) {
    companion object {
        val EMPTY =
            FriendViewState(
                executionStatus = ExecutionStatus.UNKNOWN,
                allFriends = emptyList(),
                allUsers = emptyList()
            )
    }
}