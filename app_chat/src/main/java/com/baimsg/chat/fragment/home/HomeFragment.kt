package com.baimsg.chat.fragment.home

import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FriendAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.type.ErrorRouter
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showShort
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val openLogin by lazy {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private val adapter by lazy {
        FriendAdapter()
    }

    override fun initView() {
        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                loginViewModel.loadGroupList()
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            this.adapter = this@HomeFragment.adapter
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                if (status.wontAutoLogin() || Constant.APP_KEY.isBlank() || Constant.ACCOUNT.isBlank()) openLogin
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.users.collectLatest {
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }
        }
        repeatOnLifecycleStarted {
            loginViewModel.executionStatus.collectLatest { error ->
                when (error) {
                    is ErrorRouter.UserInfo -> {
                        if (error.isFail()) showShort(error.message)
                    }
                    is ErrorRouter.Teams -> {
                        binding.srContent.isRefreshing = false
                        if (error.isFail()) showShort(error.message)
                    }
                }
            }
        }

    }


    override fun onPause() {
        super.onPause()
        binding.srContent.isRefreshing = false
    }


}