package com.baimsg.chat.fragment.my

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentMyBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        binding.vDatabase.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigate(
                    MyFragmentDirections.actionMyFragmentToLocalFragment(
                        appKey = loginViewModel.getLoginInfo().appKey
                    )
                )
            }
        }

        binding.vAbout.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_aboutFragment)
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.observeUserInfo.collectLatest {
                it.apply {
                    binding.proLoading.show(!loaded)
                    if (loaded) {
                        binding.tvName.text = name
                        binding.tvSignature.text = signature ?: "没有个性签名哦！"
                        binding.tvAccount.text = "账号:${account}"
                        binding.ivAvatar.loadImage(avatar)
                    } else {
                        loginViewModel.loadUserInfo()
                    }
                }
            }
        }

    }


}