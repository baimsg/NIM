package com.baimsg.chat.fragment.scanning.account

import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/7/1
 * 扫描账号状态
 * @param account 当前账号
 * @param count 已完成次数
 * @param status 运行状态
 * @param updateStatus 新数据执行操作
 * @param newUsers 新的用户数据
 * @param allUser 所有数据用户
 **/
data class ScanningAccountViewState(
    val account: Long,
    val count: Long,
    val status: BatchStatus,
    val updateStatus: UpdateStatus,
    val newUsers: List<NIMUserInfo>,
    val allUser: List<NIMUserInfo>,
) {
    companion object {
        val EMPTY = ScanningAccountViewState(
            account = 0,
            count = 0,
            status = BatchStatus.UNKNOWN,
            updateStatus = UpdateStatus.DEFAULT,
            newUsers = emptyList(),
            allUser = emptyList()
        )
    }

    fun pause() = status == BatchStatus.PAUSE

    fun running() = status == BatchStatus.RUNNING

    fun unknown() = status == BatchStatus.UNKNOWN

    fun stop() = status == BatchStatus.STOP
}
