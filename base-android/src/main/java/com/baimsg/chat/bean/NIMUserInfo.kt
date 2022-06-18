package com.baimsg.chat.bean

import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
class NIMUserInfo(
    private val account: String = "",
    private val name: String = "",
    private val avatar: String? = null,
    private val signature: String? = null,
    private val genderEnum: GenderEnum = GenderEnum.UNKNOWN,
    private val email: String? = null,
    private val birthday: String? = null,
    private val mobile: String? = null,
    private val extension: String? = null,
    private val ExtensionMap: MutableMap<String, Any>? = mutableMapOf(),
    val loaded: Boolean = false
) : NimUserInfo {

    override fun getAccount(): String = account

    override fun getName(): String = name

    override fun getAvatar(): String? = avatar

    override fun getSignature(): String? = signature

    override fun getGenderEnum(): GenderEnum = genderEnum

    override fun getEmail(): String? = email

    override fun getBirthday(): String? = birthday

    override fun getMobile(): String? = mobile

    override fun getExtension(): String? = extension

    override fun getExtensionMap(): MutableMap<String, Any>? = ExtensionMap
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
            ExtensionMap = extensionMap,
            loaded = true
        )
    } ?: NIMUserInfo()