package com.baimsg.chat.fragment.search

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSearchUserBinding
import com.baimsg.chat.util.extensions.*
import kotlinx.coroutines.flow.collectLatest

class SearchUserFragment : BaseFragment<FragmentSearchUserBinding>(R.layout.fragment_search_user) {

    private val searchUserViewModel by lazy {
        ViewModelProvider(this)[SearchUserViewModel::class.java]
    }

    override fun initView() {
        binding.editAccount.apply {
            showKeyboard(true)
            addTextChangedListener {
                searchUserViewModel.updateAccount(it?.toString())
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_addFriendFragment_to_settingFragment)
        }

        binding.ivStart.setOnClickListener {
            if (searchUserViewModel.searchUserParam.value.running) {

            }
            searchUserViewModel.searchUser()
        }

    }

    override fun initLiveData() {

        repeatOnLifecycleStarted {
            searchUserViewModel.searchUserParam.collectLatest {
                binding.ivStart.setImageResource(if (it.running) R.drawable.ic_pause else R.drawable.ic_start)
                binding.tvProgress.text =
                    if (it.index >= Constant.SEARCH_COUNT) "已完成" else "(${it.index}/${Constant.SEARCH_COUNT})"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.ivBack.hideKeyboard()
    }
}