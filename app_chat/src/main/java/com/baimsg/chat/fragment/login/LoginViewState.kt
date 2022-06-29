package com.baimsg.chat.fragment.login

import com.baimsg.chat.type.ExecutionStatus

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
data class LoginViewState(
    val executionStatus: ExecutionStatus,
    val message: String
) {
    companion object {
        val EMPTY = LoginViewState(executionStatus = ExecutionStatus.UNKNOWN, message = "")
    }
}