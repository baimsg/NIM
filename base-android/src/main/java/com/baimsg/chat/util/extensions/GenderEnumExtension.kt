package com.baimsg.chat.util.extensions

import com.netease.nimlib.sdk.uinfo.constant.GenderEnum

/**
 * Create by Baimsg on 2022/8/9
 *
 **/
fun GenderEnum.message(): String = when (this) {
    GenderEnum.MALE -> "男生"
    GenderEnum.FEMALE -> "女生"
    else -> "保密"
}