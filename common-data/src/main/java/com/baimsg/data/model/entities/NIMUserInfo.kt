package com.baimsg.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baimsg.data.model.base.BaseEntity
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
@Serializable
@Entity(tableName = "nim_user_info")
data class NIMUserInfo(
    @PrimaryKey
    val account: String = "",
    val name: String = "",
    val avatar: String? = null,
    val signature: String? = null,
    val genderEnum: GenderEnum = GenderEnum.UNKNOWN,
    val email: String? = null,
    val birthday: String? = null,
    val mobile: String? = null,
    val extension: String? = null,
    val extensionMap: Map<String, String>? = mutableMapOf(),
    val loaded: Boolean = false
) : BaseEntity {
    override fun getIdentifier(): String = account
}

fun NimUserInfo?.asUser(): NIMUserInfo =
    this?.run {
        NIMUserInfo(
            account = account,
            name = name,
            avatar = avatar,
            signature = signature,
            genderEnum = genderEnum,
            email = email,
            birthday = birthday,
            mobile = mobile,
            extension = extension,
            extensionMap = mutableMapOf<String, String>().apply {
                extensionMap?.forEach { (key, value) ->
                    put(key, value.toString())
                }
            },
            loaded = true
        )
    } ?: NIMUserInfo()