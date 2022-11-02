package com.baimsg.chat.fragment.team

import android.annotation.SuppressLint
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
import com.baimsg.chat.adapter.TeamItemAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FooterTeamChatBinding
import com.baimsg.chat.databinding.FragmentTeamBinding
import com.baimsg.chat.fragment.bulk.BulkData
import com.baimsg.chat.fragment.bulk.BulkType
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.model.Fail
import com.baimsg.data.model.JSON
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMTeam
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.builtins.ListSerializer

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding>(R.layout.fragment_team) {

    private val teamViewModel by viewModels<TeamViewModel>()

    private val teamItemAdapter by lazy {
        TeamItemAdapter()
    }

    private lateinit var tvCount: TextView

    @SuppressLint("CheckResult")
    override fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                teamViewModel.loadTeams()
            }
        }

        binding.tvMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("群发消息", "解散所有群聊")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            if (teamViewModel.allTeam.isEmpty()) {
                                showWarning("没有群可以群发")
                                return@listItems
                            }
                            MaterialDialog(requireContext(),
                                BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                                message(text = "快捷操作&emsp;<a href=\"\">全部</a>") {
                                    html {
                                        toggleAllItemsChecked()
                                    }
                                }
                                listItemsMultiChoice(
                                    items = teamViewModel.allTeam.map { it.name + "-" + it.id + "[${it.memberCount}]" },
                                ) { _, indices, _ ->
                                    teamViewModel.upCheckTeam(indices, BulkType.TeamSendMessage)
                                    findNavController().navigate(TeamFragmentDirections.actionTeamFragmentToBulkFragment(
                                        bulks = JSON.encodeToString(ListSerializer(BulkData.serializer()),
                                            teamViewModel.selectBulks)))
                                }
                                negativeButton()
                                positiveButton()
                            }
                        }
                        else -> {
                            teamViewModel.dismissTeamAll()
                        }
                    }
                }
                negativeButton()
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = teamItemAdapter
            val footerView = View.inflate(requireContext(), R.layout.footer_team_chat, null)
            FooterTeamChatBinding.bind(footerView).apply {
                teamItemAdapter.setFooterView(footerView)
                this@TeamFragment.tvCount = tvCount
            }
            val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
            EmptyBaseBinding.bind(emptyView).apply {
                teamItemAdapter.setEmptyView(emptyView)
                tvTip.text = "您还没有群聊哦：）"
            }
        }

        teamItemAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as NIMTeam
            findNavController().navigate(TeamFragmentDirections.actionTeamFragmentToTeamDetailFragment(
                teamInfo = data))
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            teamViewModel.observeViewState.collectLatest { data ->
                when (val teams = data.teams) {
                    is Loading -> {
                        binding.srContent.isRefreshing = true
                    }
                    is Fail -> {
                        binding.srContent.isRefreshing = false
                        showError(teams.error.message ?: "")
                    }
                    is Success -> {
                        binding.srContent.isRefreshing = false
                        teamItemAdapter.setList(teams())
                        tvCount.text = "${teams().size}个群聊"
                    }
                    else -> {
                        binding.srContent.isRefreshing = false
                    }
                }
            }
        }

        repeatOnLifecycleStarted {
            teamViewModel.loadTeams()
        }
    }
}