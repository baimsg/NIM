package com.baimsg.chat.fragment.user

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
data class UserDetailViewState(
    val executionStatus: ExecutionStatus,
    val myFriend: Boolean,
    val inBlackList: Boolean,
    val message: String,
    val userInfo: NIMUserInfo,
) {
    companion object {
        val EMPTY = UserDetailViewState(executionStatus = ExecutionStatus.UNKNOWN,
            myFriend = false,
            inBlackList = false,
            message = "",
            userInfo = NIMUserInfo())
    }

    val loading: Boolean
        get() = executionStatus == ExecutionStatus.LOADING

    val fail: Boolean
        get() = executionStatus == ExecutionStatus.FAIL
}

data class UserInfoViewState(
    val executionStatus: ExecutionStatus,
    val url: String,
    val forms: Map<String, String>,
    val headers: Map<String, String>,
    val info: String,
) {
    companion object {
        val EMPTY = UserInfoViewState(executionStatus = ExecutionStatus.UNKNOWN,
            url = "",
            forms = emptyMap(),
            headers = emptyMap(),
            info = "")
    }
}