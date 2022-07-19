package com.baimsg.chat.fragment.login

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMLoginRecord

/**
 * Create by Baimsg on 2022/6/29
 * @param executionStatus 操作状态
 * @param allAppKeys 本地appKey列表
 * @param currentLoginRecord 当前账号信息
 * @param message 提示消息
 **/
data class LoginViewState(
    val executionStatus: ExecutionStatus,
    val allAppKeys: List<String>,
    val currentLoginRecord: NIMLoginRecord,
    val message: String
) {
    companion object {
        val EMPTY = LoginViewState(
            executionStatus = ExecutionStatus.UNKNOWN,
            allAppKeys = emptyList(),
            currentLoginRecord = NIMLoginRecord(),
            message = ""
        )
    }
}