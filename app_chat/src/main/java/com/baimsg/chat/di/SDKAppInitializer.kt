package com.baimsg.chat.di

import android.app.Application
import android.content.Context
import android.view.Gravity
import com.baimsg.base.util.inititializer.AppInitializer
import com.baimsg.chat.Constant
import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.model.entities.NIMLoginRecord
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import com.umeng.commonsdk.UMConfigure
import es.dmoral.toasty.Toasty
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
        initUmeng(application)
        Bugly.init(application, Constant.BUGLY_KEY, false)
        initIM(application)

    }

    /**
     * 初始化Umeng
     */
    private fun initUmeng(context: Context) {
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

    /**
     * IM
     */
    private fun initIM(context: Context) {
        runBlocking {
            val loginRecord = loginRecord()
            val loginInfo = LoginInfo(loginRecord.account, loginRecord.token)
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