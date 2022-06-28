package com.baimsg.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baimsg.data.model.base.BaseEntity
import kotlinx.serialization.Serializable

/**
 * Create by Baimsg on 2022/6/28
 * 登录记录
 * @param id 主键 appKey+account
 * @param appKey 应用密钥
 * @param account 账号
 * @param token NIM校验token
 * @param createTIme 创建事件
 * @param loginTime 登录时间
 **/
@Serializable
@Entity(tableName = "nim_login_record")
data class NIMLoginRecord(
    @PrimaryKey
    val id: String = "",
    val appKey: String = "96e60d1d45c959069333ad8308b5799b",
    val account: String = "",
    val token: String = "",
    val createTIme: Long = 0,
    val loginTime: Long = 0,
    val used: Boolean = false
) : BaseEntity, java.io.Serializable {
    override fun getIdentifier(): String = id
}