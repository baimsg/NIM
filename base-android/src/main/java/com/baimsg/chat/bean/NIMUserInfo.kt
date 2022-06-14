package com.baimsg.chat.bean

import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
data class NIMUserInfo(
    private val account: String = "",
    private val name: String = "",
    private val avatar: String = "",
    private val signature: String = "",
    private val genderEnum: GenderEnum = GenderEnum.UNKNOWN,
    private val email: String = "",
    private val birthday: String = "",
    private val mobile: String = "",
    private val extension: String = "",
    private val ExtensionMap: MutableMap<String, Any> = mutableMapOf(),
) : NimUserInfo {

    override fun getAccount(): String = account

    override fun getName(): String = name

    override fun getAvatar(): String = avatar

    override fun getSignature(): String = signature

    override fun getGenderEnum(): GenderEnum = genderEnum

    override fun getEmail(): String = email

    override fun getBirthday(): String = birthday

    override fun getMobile(): String = mobile

    override fun getExtension(): String = extension

    override fun getExtensionMap(): MutableMap<String, Any> = ExtensionMap
}