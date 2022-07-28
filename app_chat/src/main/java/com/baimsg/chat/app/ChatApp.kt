package com.baimsg.chat.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.baimsg.base.util.inititializer.AppInitializers
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