package com.baimsg.chat.app

import android.app.Application
import android.content.Context
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NIMClient.init(this, loginInfo(), options(this))
    }


    private fun loginInfo(): LoginInfo {
        return LoginInfo(
            "60975798",
            "4bc69983fd080918abbf06e4b01dce9f",
            "8d3e6faf3fb7ce8c678b2b2900cbb6c9"
        )
    }

    private fun options(context: Context): SDKOptions {
        val sDKOptions = SDKOptions()
        sDKOptions.sdkStorageRootPath = "${context.cacheDir.canonicalPath}/nim"
        sDKOptions.preloadAttach = true
        sDKOptions.appKey = "8d3e6faf3fb7ce8c678b2b2900cbb6c9"
        sDKOptions.sessionReadAck = true
        sDKOptions.animatedImageThumbnailEnabled = true
        sDKOptions.asyncInitSDK = true
        sDKOptions.reducedIM = false
        sDKOptions.checkManifestConfig = false
        sDKOptions.enableTeamMsgAck = true
        sDKOptions.shouldConsiderRevokedMessageUnreadCount = true
        sDKOptions.loginCustomTag = "登录自定义字段"
        return sDKOptions
    }


}