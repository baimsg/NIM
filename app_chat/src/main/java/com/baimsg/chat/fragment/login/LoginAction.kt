package com.baimsg.chat.fragment.login

import com.baimsg.chat.bean.NIMUserInfo
import com.netease.nimlib.sdk.StatusCode

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
internal sealed class LoginAction {
    data class UpdateStatusCode(val statusCode: StatusCode) : LoginAction()
    data class UpdateUserInfo(val nimUserInfo: NIMUserInfo) : LoginAction()
}