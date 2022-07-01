package com.baimsg.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baimsg.data.model.base.BaseEntity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "nim_task_account")
data class NIMTaskAccount(
    @PrimaryKey
    val id: String = "",
    val appKey: String = "",
    val account: String = "",
    val name: String = "",
    val avatar: String? = null,
    val createTime: Long = 0
) : BaseEntity, java.io.Serializable {

    override fun getIdentifier(): String = id
}

fun NIMUserInfo.asTask(): NIMTaskAccount = run {
    NIMTaskAccount(
        id = id,
        appKey = appKey,
        account = account,
        name = name,
        avatar = avatar,
        createTime = System.currentTimeMillis()
    )
}