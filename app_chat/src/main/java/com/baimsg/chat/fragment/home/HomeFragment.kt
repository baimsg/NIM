package com.baimsg.chat.fragment.home

import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FriendAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.databinding.HeaderHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showInfo
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val openLogin by lazy {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private val friendAdapter by lazy {
        FriendAdapter()
    }

    override fun initView() {
        binding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFriendFragment)
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                loginViewModel.loadFriends()
            }
        }

        binding.ryContent.apply {
            val headerView = View.inflate(requireContext(), R.layout.header_home, null)
            HeaderHomeBinding.bind(headerView).apply {
                friendAdapter.setHeaderView(headerView)
                vNewFriend.setOnClickListener {
                    showInfo("unknown 该功能待开发")
                }
                vTeamChat.setOnClickListener {
                    showInfo("unknown 该功能待开发")
                }
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            adapter = friendAdapter
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                Constant.apply {
                    if (status.wontAutoLogin() || APP_KEY.isBlank() || ACCOUNT.isBlank() || TOKEN.isBlank()) openLogin
                }
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.users.collectLatest {
                binding.srContent.isRefreshing = false
                friendAdapter.setList(it)
                friendAdapter.notifyDataSetChanged()
            }
        }


    }

    override fun onPause() {
        super.onPause()
        binding.srContent.isRefreshing = false
    }


}