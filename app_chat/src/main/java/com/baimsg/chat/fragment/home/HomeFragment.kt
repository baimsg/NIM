package com.baimsg.chat.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FriendAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FooterHomeBinding
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.databinding.HeaderHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showInfo
import com.chad.library.adapter.base.animation.AlphaInAnimation
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val openLogin by lazy {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(tough = true))
    }

    private val friendAdapter by lazy {
        FriendAdapter()
    }

    private val loadMoreModule by lazy {
        friendAdapter.loadMoreModule
    }

    private val dialog by lazy {
        MaterialDialog(requireContext()).cancelable(true).cancelOnTouchOutside(false)
    }

    private var tvFriendCount: TextView? = null


    override fun initView() {
        binding.ivAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchUserFragment)
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                loginViewModel.loadFriends()
            }
        }

        binding.ryContent.apply {
            friendAdapter.animationEnable = true
            friendAdapter.adapterAnimation = AlphaInAnimation()

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

            val footerView = View.inflate(requireContext(), R.layout.footer_home, null)
            FooterHomeBinding.bind(footerView).apply {
                friendAdapter.setFooterView(footerView)
                this@HomeFragment.tvFriendCount = this.tvFriendCount
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            adapter = friendAdapter
        }
    }

    override fun initLiveData() {
        loadMoreModule.isEnableLoadMore = true

        loadMoreModule.setOnLoadMoreListener {
            loginViewModel.nextPageUserInfo()
        }

        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                Constant.apply {
                    if (status.wontAutoLogin() || APP_KEY.isBlank() || ACCOUNT.isBlank() || TOKEN.isBlank()) openLogin
                }
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.friendViewState.collectLatest {
                binding.srContent.isRefreshing = false
                val users = it.users
                when (it.executionStatus) {
                    ExecutionStatus.FAIL -> {
                        binding.proLoading.hide()
                        dialog.show {
                            title(text = "获取好友信息失败")
                            message(text = "请尝试重新获取数据！")
                            negativeButton(text = "重试") {
                                loginViewModel.loadFriends()
                            }
                        }
                        loadMoreModule.loadMoreFail()
                    }
                    ExecutionStatus.SUCCESS -> {
                        binding.proLoading.hide()
                        if (loginViewModel.friendPage == 0) {
                            friendAdapter.setList(users)
                        } else {
                            friendAdapter.addData(users)
                        }
                        loadMoreModule.loadMoreComplete()
                    }
                    ExecutionStatus.UNKNOWN -> {
                        binding.proLoading.show()
                    }
                    ExecutionStatus.EMPTY -> {
                        binding.proLoading.hide()
                        loadMoreModule.loadMoreEnd(true)
                    }
                }
                friendAdapter.notifyDataSetChanged()
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.friends.collectLatest {
                tvFriendCount?.text = "${it.size}个好友"
            }
        }

    }

    override fun onPause() {
        super.onPause()
        binding.srContent.isRefreshing = false
    }

}