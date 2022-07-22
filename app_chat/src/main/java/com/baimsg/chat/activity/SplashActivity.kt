package com.baimsg.chat.activity

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivitySplashBinding
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            loadDialog.show()
            viewModel.getBaseConfig()
            val base = viewModel.getBaseConfig()
            loadDialog.dismiss()

            when (base) {
                is Success -> {
                    if (base().id != Constant.ID) {
                        MaterialDialog(this@SplashActivity)
                            .cancelable(false)
                            .cancelOnTouchOutside(false).show {
                                input(
                                    hint = "id",
                                    waitForPositiveButton = false
                                ) { materialDialog, charSequence ->
                                    val isValid = charSequence.toString() == base().id
                                    materialDialog.setActionButtonEnabled(
                                        WhichButton.POSITIVE,
                                        isValid
                                    )
                                    KvUtils.put(Constant.KEY_ID, charSequence.toString())
                                }
                                negativeButton { finish() }
                                positiveButton()
                            }
                    }
                }
                is Fail -> {
                    MaterialDialog(this@SplashActivity)
                        .cancelable(false)
                        .cancelOnTouchOutside(false).show {
                            title(text = "网络异常")
                            message(text = "你的网络好像已经丢失了:)")
                            negativeButton { finish() }
                            positiveButton(R.string.retry) {
                            }
                        }
                }
                else -> {}
            }

        }
    }
}