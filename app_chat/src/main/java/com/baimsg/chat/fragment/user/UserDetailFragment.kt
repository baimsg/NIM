package com.baimsg.chat.fragment.user

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.DialogInputParamBinding
import com.baimsg.chat.databinding.FragmentUserDetailBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
import com.baimsg.data.model.JSON
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import java.lang.StringBuilder

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
@AndroidEntryPoint
class UserDetailFragment : BaseFragment<FragmentUserDetailBinding>(R.layout.fragment_user_detail) {

    private val userDetailViewModel by viewModels<UserDetailViewModel>()

    override fun initView() {
        binding.pullLayout.apply {
            setActionListener {
                postDelayed({ finishActionRun(it) }, 250)
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvMore.setOnClickListener {
            inputParam()
        }

        binding.tvRetry.setOnClickListener {
            userDetailViewModel.loadData()
        }

        binding.scBlackList.apply {
            setOnCheckedChangeListener { _, _ ->
                this.isChecked = false
                showWarning("暂不支持")
            }
        }

        binding.tvDeleteFriend.setOnClickListener {
            showWarning("暂不支持")
        }

        binding.tvAccountInfo.setOnClickListener {
            if (userDetailViewModel.condition) {
                userDetailViewModel.loadInfo()
            } else {
                inputParam()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initLiveData() {
        repeatOnLifecycleStarted {
            userDetailViewModel.observeViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> {
                        binding.proLoading.show()
                        binding.groupMain.hide()
                        binding.tvRetry.hide()
                    }
                    ExecutionStatus.SUCCESS -> {
                        binding.proLoading.hide()
                        binding.groupMain.show()
                        val info = it.userInfo
                        binding.tvDeleteFriend.setText(if (it.myFriend) R.string.delete_friend else R.string.add_friend)
                        binding.ivAvatar.loadImage(info.avatar)
                        binding.tvName.text = info.name
                        binding.tvGender.text = info.genderEnum.message()
                        binding.tvAccount.text = "ID:${info.account}"
                        binding.tvSignature.text = info.signature ?: "没有个性签名哦！"
                    }
                    ExecutionStatus.FAIL -> {
                        binding.proLoading.hide()
                        binding.tvRetry.show()
                    }
                    else -> Unit
                }
            }
        }

        repeatOnLifecycleStarted {
            userDetailViewModel.observeInfoViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> binding.editUserInfo.setText(com.chad.library.R.string.brvah_loading)
                    ExecutionStatus.SUCCESS -> binding.editUserInfo.setText(it.info)
                    else -> Unit
                }
            }
        }
    }

    private fun inputParam() {
        val view = View.inflate(requireContext(), R.layout.dialog_input_param, null)
        DialogInputParamBinding.bind(view).apply {
            MaterialDialog(requireContext()).cancelOnTouchOutside(false).customView(view = view)
                .show {
                    editUrl.setText(userDetailViewModel.url)
                    editParam.setText(userDetailViewModel.forms.run {
                        StringBuilder().apply {
                            this@run.forEach { (s, s2) ->
                                append("$s=$s2&")
                            }
                        }
                    })
                    editUrl.showKeyboard(true)
                    negativeButton { }
                    positiveButton {
                        val url = editUrl.text?.toString()?.run {
                            substring(0, indexOf("/", 12) + 1)
                        }
                        if (url.isNullOrBlank()) {
                            showWarning("URL不合法")
                            return@positiveButton
                        }
                        val params: Map<String, String>? = editParam.text?.toString()?.run {
                            val forms = mutableMapOf<String, String>()
                            if (isNotBlank()) {
                                split("&").forEach { s ->
                                    if (s.contains("=")) {
                                        val values = s.split("=")
                                        forms[values[0]] = values[1]
                                    }
                                }
                            }
                            forms
                        }
                        if (params.isNullOrEmpty()) {
                            showWarning("参数不合法")
                            return@positiveButton
                        }
                        KvUtils.put(Constant.KEY_URL, url)
                        KvUtils.put(Constant.KEY_PARAM,
                            JSON.encodeToString(MapSerializer(String.serializer(),
                                String.serializer()), params))
                        userDetailViewModel.loadForms()
                    }
                }
        }

    }

}