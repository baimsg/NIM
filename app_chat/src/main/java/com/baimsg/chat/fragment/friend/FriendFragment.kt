package com.baimsg.chat.fragment.friend

import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.afollestad.materialdialogs.list.toggleAllItemsChecked
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountSmallAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FooterTeamChatBinding
import com.baimsg.chat.databinding.FragmentFriendBinding
import com.baimsg.chat.fragment.bulk.BulkData
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
import com.baimsg.data.model.JSON
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.builtins.ListSerializer

@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>(R.layout.fragment_friend) {

    private val friendViewModel by viewModels<FriendViewModel>()

    private val accountSmallAdapter by lazy {
        AccountSmallAdapter()
    }

    private lateinit var tvCount: TextView

    override fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener { friendViewModel.loadFriends() }
        }

        binding.tvMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("群发消息", "删除所有好友")) { dialog, index, _ ->
                    dialog.dismiss()
                    if (friendViewModel.allAccounts.isEmpty()) {
                        showWarning("您还没有好友哦：）")
                        return@listItems
                    }
                    when (index) {
                        0 -> {
                            MaterialDialog(
                                requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)
                            ).show {
                                message(text = "快捷操作&emsp;<a href=\"\">全部</a>") {
                                    html {
                                        toggleAllItemsChecked()
                                    }
                                }
                                listItemsMultiChoice(
                                    items = friendViewModel.allUsers.map { it.name + "-" + it.account },
                                ) { _, indices, _ ->
                                    friendViewModel.upSelectBulks(indices)
                                    findNavController().navigate(
                                        FriendFragmentDirections.actionFriendFragmentToBulkFragment(
                                            bulks = JSON.encodeToString(
                                                ListSerializer(BulkData.serializer()),
                                                friendViewModel.selectBulks
                                            ),
                                            sessionType = SessionTypeEnum.P2P
                                        )
                                    )
                                }
                                negativeButton()
                                positiveButton()
                            }
                        }
                        else -> {
                            showInfo("unknown 该功能待开发")
                        }
                    }
                }
                negativeButton()
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountSmallAdapter
            val footerView = View.inflate(requireContext(), R.layout.footer_team_chat, null)
            FooterTeamChatBinding.bind(footerView).apply {
                accountSmallAdapter.setFooterView(footerView)
                this@FriendFragment.tvCount = this.tvCount
            }
            val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
            EmptyBaseBinding.bind(emptyView).apply {
                accountSmallAdapter.setEmptyView(emptyView)
                tvTip.text = "您还没有好友哦：）"
            }
        }

        accountSmallAdapter.apply {
            animationEnable = true
            adapterAnimation = AlphaInAnimation()
        }

    }

    override fun initLiveData() {
        friendViewModel.loadFriends()

        repeatOnLifecycleStarted {
            friendViewModel.observeViewState.collectLatest {
                tvCount.text = "${friendViewModel.allAccounts.size}个好友"
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> {
                        binding.tvMore.hide()
                        binding.srContent.isRefreshing = true
                    }
                    ExecutionStatus.SUCCESS -> {
                        binding.tvMore.show()
                        binding.srContent.isRefreshing = false
                        accountSmallAdapter.setList(it.allUsers)
                    }
                    else -> Unit
                }
            }
        }

    }
}