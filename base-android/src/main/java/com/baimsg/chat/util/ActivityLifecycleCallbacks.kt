package com.baimsg.chat.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 *
 * create by baimsg 2021/11/24
 * Email 1469010683@qq.com
 *
 **/
abstract class ActivityLifecycleCallbacks(private val mTargetActivity: Activity) :
    Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (activity === mTargetActivity) {
            mTargetActivity.application.unregisterActivityLifecycleCallbacks(this)
            onTargetActivityDestroyed()
        }
    }

    protected abstract fun onTargetActivityDestroyed()
}