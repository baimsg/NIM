package com.baimsg.chat.fragment.team

import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TeamItemAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FooterTeamChatBinding
import com.baimsg.chat.databinding.FragmentTeamBinding
import com.baimsg.chat.fragment.search.SearchUserViewModel
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import com.baimsg.data.model.entities.NIMTeam
import kotlinx.coroutines.flow.collectLatest

class TeamFragment : BaseFragment<FragmentTeamBinding>(R.layout.fragment_team) {

    private val teamViewModel by activityViewModels<TeamViewModel>()

    private val searchUserViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchUserViewModel::class.java]
    }

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

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = teamItemAdapter
            val footerView = View.inflate(requireContext(), R.layout.footer_team_chat, null)
            FooterTeamChatBinding.bind(footerView).apply {
                teamItemAdapter.setFooterView(footerView)
                this@TeamFragment.tvCount = tvCount
            }
        }

        teamItemAdapter.setOnItemClickListener { adapter, view, posion ->
            teamViewModel.addMembers(
                (adapter.data[posion] as NIMTeam).id ?: "",
                searchUserViewModel.searchViewState.value.allUser.map { it.account },
                "test"
            )
        }


    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            teamViewModel.viewState.collectLatest {
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