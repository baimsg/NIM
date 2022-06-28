package com.baimsg.base.util.inititializer

import android.app.Application

/**
 * Create by baimsg on 2022/4/1.
 * Email 1469010683@qq.com
 *
 **/
class AppInitializers(private vararg val initializers: AppInitializer) : AppInitializer {
    override fun init(application: Application) {
        initializers.forEach {
            it.init(application)
        }
    }
}

interface AppInitializer {
    fun init(application: Application)
}