package com.baimsg.chat.type

sealed class ErrorRouter {
    data class Unknown(
        val message: String = "",
        val status: ExecutionStatus = ExecutionStatus.SUCCESS
    ) : ErrorRouter() {
        fun isFail() = status == ExecutionStatus.FAIL
    }

    data class Login(
        val message: String = "",
        val status: ExecutionStatus = ExecutionStatus.SUCCESS
    ) : ErrorRouter() {
        fun isFail() = status == ExecutionStatus.FAIL
    }

    data class UserInfo(
        val message: String = "",
        val status: ExecutionStatus = ExecutionStatus.SUCCESS
    ) : ErrorRouter() {
        fun isFail() = status == ExecutionStatus.FAIL
    }

    data class Teams(
        val message: String = "",
        val status: ExecutionStatus = ExecutionStatus.SUCCESS
    ) : ErrorRouter() {
        fun isFail() = status == ExecutionStatus.FAIL
    }

    data class SearchUser(
        val message: String = "",
        val status: ExecutionStatus = ExecutionStatus.SUCCESS
    ) : ErrorRouter() {
        fun isFail() = status == ExecutionStatus.FAIL
    }
}