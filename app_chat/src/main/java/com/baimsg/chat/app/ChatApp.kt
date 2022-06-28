package com.baimsg.chat.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.baimsg.chat.Constant
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import com.umeng.commonsdk.UMConfigure
import dagger.hilt.android.HiltAndroidApp

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
@HiltAndroidApp
class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        /**
         * mmkv
         */
        MMKV.initialize(this)
        initUmeng(this)
        Bugly.init(applicationContext, Constant.BUGLY_KEY, false)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    companion object {
        /**
         * IM
         */
//        fun initIM(context: Context) {
//            NIMClient.init(context, loginInfo(), options(context))
//        }

//        private fun loginInfo(): LoginInfo {
//            return LoginInfo(Constant.ACCOUNT, Constant.TOKEN)
//        }
//
//        private fun options(context: Context): SDKOptions {
//            val sDKOptions = SDKOptions()
//            sDKOptions.sdkStorageRootPath = "${context.cacheDir.canonicalPath}/nim"
//            sDKOptions.preloadAttach = true
//            sDKOptions.appKey = Constant.APP_KEY
//            sDKOptions.sessionReadAck = true
//            sDKOptions.animatedImageThumbnailEnabled = true
//            sDKOptions.asyncInitSDK = true
//            sDKOptions.reducedIM = false
//            sDKOptions.checkManifestConfig = false
//            sDKOptions.enableTeamMsgAck = true
//            sDKOptions.shouldConsiderRevokedMessageUnreadCount = true
//            sDKOptions.loginCustomTag = "登录自定义字段"
//            return sDKOptions
//        }

        /**
         * 初始化Umeng
         */
        fun initUmeng(context: Context) {
            UMConfigure.preInit(
                context,
                Constant.UMENG_APP_KEY,
                Constant.getChannel()
            )
            UMConfigure.init(
                context,
                Constant.UMENG_APP_KEY,
                Constant.getChannel(),
                UMConfigure.DEVICE_TYPE_PHONE,
                ""
            )
        }

    }


}