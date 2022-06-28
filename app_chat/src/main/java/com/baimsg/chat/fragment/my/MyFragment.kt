package com.baimsg.chat.fragment.my

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentMyBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.dp2px
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showWarning
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
@AndroidEntryPoint
class MyFragment : BaseFragment<FragmentMyBinding>(R.layout.fragment_my) {
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun initView() {

        binding.vSetting.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_settingFragment)
        }

        binding.vAbout.setOnClickListener {
            showWarning("此版本为定制版本")
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.userInfo.collectLatest {
                it.apply {
                    if (loaded) {
                        binding.tvName.text = name

                        binding.tvSignature.text = signature ?: "没有个性签名哦！"

                        binding.tvAccount.text = "账号:${account}"

                        Glide.with(this@MyFragment).load(avatar).apply(
                            RequestOptions()
                                .transform(
                                    CenterCrop(),
                                    RoundedCorners(requireContext().dp2px(88.0f).toInt())
                                )
                        ).into(binding.ivAvatar)
                    }
                }
            }
        }

    }


}