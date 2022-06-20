package com.baimsg.chat.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.baimsg.chat.Constant
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
@HiltAndroidApp
class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        NIMClient.init(this, loginInfo(), options(this))
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun loginInfo(): LoginInfo {
        return LoginInfo(Constant.ACCOUNT, Constant.TOKEN)
    }

    private fun options(context: Context): SDKOptions {
        val sDKOptions = SDKOptions()
        sDKOptions.sdkStorageRootPath = "${context.cacheDir.canonicalPath}/nim"
        sDKOptions.preloadAttach = true
        sDKOptions.appKey = Constant.APP_KEY
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