package com.baimsg.chat.fragment.friend

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/6/20
 *
 * 好友数据状态
 * @param executionStatus 执行结果
 * @param allAccounts 所有好友账号
 * @param page 页码
 * @param allUsers 所有用户信息
 * @param newUsers 新的用户信息
 **/
data class FriendViewState(
    val executionStatus: ExecutionStatus,
    val allAccounts: List<String>,
    val page: Int,
    val allUsers: List<NIMUserInfo>,
    val newUsers: List<NIMUserInfo>
) {
    companion object {
        val EMPTY =
            FriendViewState(
                executionStatus = ExecutionStatus.UNKNOWN,
                allAccounts = emptyList(),
                page = 0,
                allUsers = emptyList(),
                newUsers = emptyList()
            )
    }
}