package com.baimsg.chat.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.baimsg.base.util.inititializer.AppInitializers
import com.baimsg.chat.Constant
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import com.umeng.commonsdk.UMConfigure
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
@HiltAndroidApp
class ChatApp : Application() {

    @Inject
    lateinit var appInitializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        appInitializers.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}