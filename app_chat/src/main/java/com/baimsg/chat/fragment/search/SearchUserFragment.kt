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
            findNavController().popBackStack().apply {
                logD(this)
            }
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_searchUserFragment_to_settingFragment)
        }

        binding.ivSave.setOnClickListener {

        }

        binding.ivStart.setOnClickListener {
            if (searchUserViewModel.running.value) {
                searchUserViewModel.stopSearchUser()
            } else {
                searchUserViewModel.searchUser()
            }
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                friendAdapter.setList(searchUserViewModel.allUsers.value.filter { it.loaded })
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
                searchUserViewModel.updateAccount(it?.toString())
            }
        }


    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            searchUserViewModel.users.collectLatest { users ->
                val list = users.filter { it.loaded }
                friendAdapter.addData(list)
                friendAdapter.notifyItemRangeChanged(friendAdapter.data.size - list.size, list.size)
            }
        }

        repeatOnLifecycleStarted {
            searchUserViewModel.index.collectLatest {
                binding.tvProgress.text = "($it/${Constant.SEARCH_COUNT})"
            }
        }

        repeatOnLifecycleStarted {
            searchUserViewModel.running.collectLatest {
                if (it) {
                    binding.ivEdit.hide()
                    binding.ivSave.hide()
                    binding.ivStart.setImageResource(R.drawable.ic_pause)
                    binding.editAccount.isEnabled = false
                } else {
                    binding.ivEdit.show()
                    binding.ivSave.show()
                    binding.editAccount.isEnabled = true
                    binding.ivStart.setImageResource(R.drawable.ic_start)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.ivBack.hideKeyboard()
    }
}