package com.baimsg.chat.activity

import android.content.Intent
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.baidu.mobstat.StatService
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivitySplashBinding
import com.baimsg.chat.util.extensions.androidId
import com.baimsg.chat.util.extensions.copy
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
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

    private val viewModel by viewModels<AppViewModel>()

    private val loadDialog by lazy {
        MaterialDialog(this).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    override fun initView() {
        StatService.start(this)

        repeatOnLifecycleStarted {
            viewModel.observeConfig.collectLatest {
                when (it) {
                    is Loading -> {
                        loadDialog.show()
                    }
                    is Success -> {
                        loadDialog.dismiss()
                        if (Constant.STATEMENT) {
                            verify()
                        } else {
                            MaterialDialog(this@SplashActivity)
                                .cancelable(false)
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
                        MaterialDialog(this@SplashActivity)
                            .cancelable(false)
                            .cancelOnTouchOutside(false)
                            .show {
                                title(text = "出错啦）：")
                                message(text = "${it.error.message}\n请检查您的网络连接是否正常")
                                negativeButton(R.string.quit) { finish() }
                                positiveButton(R.string.retry) {
                                    viewModel.retry()
                                }
                            }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun verify() {
        val androidId = applicationContext.androidId()
        if (viewModel.users.any { it.id == androidId }) {

        } else {
            MaterialDialog(this@SplashActivity)
                .cancelable(false)
                .cancelOnTouchOutside(false)
                .show {
                    title(text = "设备未激活）：")
                    message(text = "神奇的卡密:$androidId")
                    negativeButton(R.string.quit) { finish() }
                    positiveButton(R.string.copy) {
                        applicationContext.copy(androidId)
                    }
                }
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
