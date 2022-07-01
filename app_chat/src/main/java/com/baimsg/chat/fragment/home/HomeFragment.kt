package com.baimsg.chat.fragment.home

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val openLogin by lazy {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(hard = true))
    }

    override fun initView() {
        binding.ivAdd.setOnClickListener {
            showInfo("unknown 该功能待开发")
        }

        binding.vNewFriend.setOnClickListener {
            showInfo("unknown 该功能待开发")
        }

        binding.vFriendList.setOnClickListener {
            showInfo("unknown 该功能待开发")
        }

        binding.vTeamChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_teamFragment)
        }

        binding.vScanning.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanningAccountFragment)
        }

        binding.vBatchExe.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBatchExecuteFragment(
                        appKey = loginViewModel.getLoginInfo().appKey
                    )
                )
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.observerStatusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                lifecycleScope.launch(Dispatchers.Main) {
                    val loginInfo = loginViewModel.getLoginInfo()
                    if (status.wontAutoLogin() || loginInfo.mustEmpty() || loginInfo.appKeyEmpty()) openLogin
                }
            }
        }

    }

}