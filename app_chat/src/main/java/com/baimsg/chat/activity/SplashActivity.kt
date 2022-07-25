package com.baimsg.chat.activity

import android.content.Intent
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivitySplashBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.setFullScreen
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
        repeatOnLifecycleStarted {
            viewModel.observeBaseConfig.collectLatest {
                when (it) {
                    is Loading -> {
                        loadDialog.show()
                    }
                    is Success -> {
                        loadDialog.dismiss()
                        val id = it().id
                        if (id != Constant.ID) {
                            verifyKey()
                        } else {
                            nextActivity()
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

    private fun verifyKey() {
        MaterialDialog(this@SplashActivity)
            .cancelable(false)
            .cancelOnTouchOutside(false)
            .show {
                input(
                    hint = "请输入id"
                ) { _, charSequence ->
                    if (charSequence.toString() == viewModel.verifyKey) {
                        KvUtils.put(Constant.KEY_ID, charSequence.toString())
                        nextActivity()
                    } else {
                        verifyKey()
                    }
                }
                negativeButton(R.string.quit) { finish() }
                positiveButton()
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
