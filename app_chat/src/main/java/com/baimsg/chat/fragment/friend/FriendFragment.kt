package com.baimsg.chat.fragment.friend

import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountSmallAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FooterTeamChatBinding
import com.baimsg.chat.databinding.FragmentFriendBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>(R.layout.fragment_friend) {

    private val friendViewModel by viewModels<FriendViewModel>()

    private val accountSmallAdapter by lazy {
        AccountSmallAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                friendViewModel.loadFriends()
            }
        }

        accountSmallAdapter.apply {
            loadMoreModule.isEnableLoadMore = true
            animationEnable = true
            adapterAnimation = AlphaInAnimation()

            loadMoreModule.setOnLoadMoreListener {
                friendViewModel.nextPage()
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountSmallAdapter
            val footerView = View.inflate(requireContext(), R.layout.footer_team_chat, null)
            FooterTeamChatBinding.bind(footerView).apply {
                accountSmallAdapter.setFooterView(footerView)
                tvCount.text = "${friendViewModel.allAccounts.size}个好友"
            }
            val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
            EmptyBaseBinding.bind(emptyView).apply {
                accountSmallAdapter.setEmptyView(emptyView)
                tvTip.text = "您还没有好友哦：）"
            }
        }

    }

    override fun initLiveData() {
        friendViewModel.loadFriends()

        repeatOnLifecycleStarted {
            friendViewModel.observeViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.SUCCESS -> {
                        accountSmallAdapter.addData(it.newUsers)
                        accountSmallAdapter.loadMoreModule.loadMoreComplete()
                    }
                    ExecutionStatus.FAIL -> {
                        accountSmallAdapter.loadMoreModule.loadMoreFail()
                    }
                    ExecutionStatus.EMPTY -> {
                        accountSmallAdapter.loadMoreModule.loadMoreEnd()
                    }
                    else -> Unit
                }
            }
        }

    }
}