package com.baimsg.chat.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.baidu.mobstat.StatService
import com.baimsg.base.util.KvUtils
import com.baimsg.base.util.extensions.decodeData
import com.baimsg.base.util.extensions.encodeBase64
import com.baimsg.base.util.extensions.encodeDate
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivitySplashBinding
import com.baimsg.chat.util.extensions.*
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/19
 *
 **/
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val appViewModel by viewModels<AppViewModel>()

    private val loadDialog by lazy {
        MaterialDialog(this).cancelable(false).cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    override fun initView() {
        StatService.start(this)

        repeatOnLifecycleStarted {
            appViewModel.observeConfig.collectLatest {
                when (it) {
                    is Loading -> {
                        loadDialog.show()
                    }
                    is Success -> {
                        loadDialog.dismiss()
                        if (Constant.STATEMENT) {
                            verify()
                        } else {
                            MaterialDialog(this@SplashActivity).cancelable(false)
                                .cancelOnTouchOutside(false).show {
                                    title(R.string.statement)
                                    message(text = it().statement)
                                    negativeButton(R.string.disagree) { finish() }
                                    positiveButton(R.string.agree) {
                                        KvUtils.put(Constant.KEY_STATEMENT, true)
                                        verify()
                                    }
                                }
                        }
                    }
                    is Fail -> {
                        loadDialog.dismiss()
                        MaterialDialog(this@SplashActivity).cancelable(false)
                            .cancelOnTouchOutside(false).show {
                                title(text = "出错啦）：")
                                message(text = "${it.error.message}\n请检查您的网络连接是否正常")
                                negativeButton(R.string.quit) { finish() }
                                positiveButton(R.string.retry) {
                                    appViewModel.retry()
                                }
                            }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun verify() {
        if (appViewModel.stopUsing) {
            if (appViewModel.quit) {
                showError(appViewModel.noticeContent)
                finish()
                return
            } else {
                showWarning(appViewModel.noticeContent)
            }
        }
        val androidId = applicationContext.androidId()
        when {
            appViewModel.users.any { it.id == androidId } -> {
                if (Constant.NOTICE_VERSION < appViewModel.noticeVersion) {
                    MaterialDialog(this@SplashActivity).cancelable(false)
                        .cancelOnTouchOutside(false).show {
                            title(text = appViewModel.noticeTitle)
                            message(text = appViewModel.noticeContent)
                            if (appViewModel.noticeLink.isNotBlank()) {
                                positiveButton(R.string.go_now) {
                                    openWeb(appViewModel.noticeLink)
                                    finish()
                                }
                            } else {
                                negativeButton(R.string.not_again_hint) {
                                    KvUtils.put(Constant.KEY_NOTICE_VERSION,
                                        appViewModel.noticeVersion)
                                    nextActivity()
                                }
                                positiveButton(R.string.know) {
                                    nextActivity()
                                }
                            }
                        }
                } else {
                    nextActivity()
                }
            }
            appViewModel.debug -> {
                if (appViewModel.appKey.encodeDate(appViewModel.dataKey) == Constant.APP_KEY) {
                    nextActivity()
                } else {
                    verifyAppKey()
                }
            }
            else -> {
                MaterialDialog(this@SplashActivity).cancelable(false).cancelOnTouchOutside(false)
                    .show {
                        title(text = "设备未激活）：")
                        message(text = "神奇的卡密:\n${androidId.encodeBase64()}")
                        negativeButton(R.string.quit) { finish() }
                        positiveButton(R.string.copy) {
                            showSuccess("已复制卡密")
                            applicationContext.copy(androidId.encodeBase64())
                            finish()
                        }
                    }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun verifyAppKey() {
        MaterialDialog(this@SplashActivity).cancelable(false).cancelOnTouchOutside(false).show {
            input(hint = "请输入卡密") { materialDialog, charSequence ->
                materialDialog.dismiss()
                val appKey = charSequence.toString()
                if (appKey.decodeData(appViewModel.dataKey) == appViewModel.appKey) {
                    KvUtils.put(Constant.KEY_APP_KEY, appKey)
                    showSuccess("卡密使用成功！")
                    nextActivity()
                } else {
                    showError("该卡密已失效！")
                    verifyAppKey()
                }
            }
            negativeButton { finish() }
            positiveButton { }
        }
    }

    private fun nextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fragment_open_enter, R.anim.fragment_open_exit)
    }
}
