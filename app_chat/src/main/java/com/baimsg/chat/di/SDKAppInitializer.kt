package com.baimsg.chat.di

import android.app.Application
import android.content.Context
import com.baidu.mobstat.StatService
import com.baimsg.base.util.inititializer.AppInitializer
import com.baimsg.chat.BuildConfig
import com.baimsg.chat.Constant
import com.baimsg.chat.util.extensions.androidId
import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.model.entities.NIMLoginRecord
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SDKAppInitializer @Inject constructor(
    private val loginRecordDao: LoginRecordDao
) : AppInitializer, CoroutineScope by CoroutineScope(Dispatchers.Main) {

    override fun init(application: Application) {
        /**
         * mmkv
         */
        MMKV.initialize(application)
        StatService.setAuthorizedState(application, true)
        StatService.enableDeviceMac(application, true)
        StatService.setDebugOn(BuildConfig.DEBUG)
        StatService.setOn(application, StatService.EXCEPTION_LOG)
        Bugly.setUserId(application, application.androidId())
        Bugly.init(application, Constant.BUGLY_KEY, BuildConfig.DEBUG)
        initIM(application)
    }

    /**
     * IM
     */
    private fun initIM(context: Context) {
        runBlocking {
            val loginRecord = loginRecord()
            val loginInfo =
                Constant.getLoginInfo(loginRecord.account, loginRecord.token, loginRecord.appKey)
            val sdkOptions = SDKOptions().apply {
                sdkStorageRootPath = context.externalCacheDir?.canonicalPath
                    ?: context.getExternalFilesDir("nim")?.canonicalPath
                preloadAttach = true
                appKey = loginRecord.appKey
                sessionReadAck = true
                animatedImageThumbnailEnabled = true
                asyncInitSDK = true
                reducedIM = false
                checkManifestConfig = false
                enableTeamMsgAck = true
                shouldConsiderRevokedMessageUnreadCount = true
                loginCustomTag = "登录自定义字段"
            }
            NIMClient.config(context, loginInfo, sdkOptions)
        }
    }

    private suspend fun loginRecord(): NIMLoginRecord {
        return withContext(Dispatchers.IO) {
            loginRecordDao.used() ?: NIMLoginRecord()
        }
    }

}