package com.baimsg.chat.fragment.team

import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TeamItemAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FooterTeamChatBinding
import com.baimsg.chat.databinding.FragmentTeamBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMTeam
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding>(R.layout.fragment_team) {

    private val teamViewModel by viewModels<TeamViewModel>()

    private val teamItemAdapter by lazy {
        TeamItemAdapter()
    }

    private lateinit var tvCount: TextView

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

        binding.tvClear.setOnClickListener {
            teamViewModel.allTeam?.map { it.id }?.forEachIndexed { index, s ->
                teamViewModel.dismissTeam(s)
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
            MaterialDialog(requireContext()).show {
                title(text = "群信息")
                message(text = "[群名] ${data.name}\n[ID] ${data.id}\n[群容量] ${data.memberLimit}\n[群成员] ${data.memberCount}")
                positiveButton(R.string.sure)
            }
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            teamViewModel.observeViewState.collectLatest {
                when (val teams = it.teams) {
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
    }
}