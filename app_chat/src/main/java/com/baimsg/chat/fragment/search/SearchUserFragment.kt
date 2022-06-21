package com.baimsg.chat.fragment.search

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.base.util.extensions.logD
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FriendAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSearchUserBinding
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.util.extensions.*
import com.chad.library.adapter.base.animation.AlphaInAnimation
import kotlinx.coroutines.flow.collectLatest

class SearchUserFragment : BaseFragment<FragmentSearchUserBinding>(R.layout.fragment_search_user) {

    private val searchUserViewModel by lazy {
        ViewModelProvider(this)[SearchUserViewModel::class.java]
    }

    private val friendAdapter by lazy {
        FriendAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_searchUserFragment_to_settingFragment)
        }

        binding.ivClean.setOnClickListener {
            searchUserViewModel.stopSearchUser()
        }

        binding.ivSave.setOnClickListener {
        }

        binding.ivStart.setOnClickListener {
            searchUserViewModel.searchUser()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                friendAdapter.setList(searchUserViewModel.searchViewState.value.allUser.filter { it.loaded })
                isRefreshing = false
            }
        }

        binding.ryContent.apply {
            friendAdapter.animationEnable = true
            friendAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = friendAdapter
        }

        binding.editAccount.apply {
            showKeyboard(true)
            addTextChangedListener {
                try {
                    val account = if (it.isNullOrBlank()) 0 else it.toString().toLong()
                    searchUserViewModel.updateAccount(account)
                } catch (e: Exception) {
                    showError("请检查您输入的内容")
                }
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            searchUserViewModel.searchViewState.collectLatest { value ->
                value.apply {
                    binding.tvProgress.text = "(${count}/${Constant.SEARCH_COUNT})"
                    when (status) {
                        BatchStatus.RUNNING -> {
                            binding.ivStart.setImageResource(R.drawable.ic_pause)
                            binding.ivBack.hide()
                            binding.ivEdit.hide()
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            binding.editAccount.isEnabled = false
                            if (update) friendAdapter.addData(users)
                        }
                        BatchStatus.STOP -> {
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            friendAdapter.setList(null)
                            binding.editAccount.isEnabled = true
                        }
                        else -> {
                            binding.ivBack.show()
                            binding.ivEdit.show()
                            if (allUser.isNotEmpty()) {
                                binding.ivSave.show()
                                binding.ivClean.show()
                            }
                            binding.ivStart.setImageResource(R.drawable.ic_start)
                        }
                    }
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()
        binding.ivBack.hideKeyboard()
    }
}