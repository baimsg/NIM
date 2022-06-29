package com.baimsg.chat.fragment.login.local

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.LocalAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLocalAccountBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalAccountFragment :
    BaseFragment<FragmentLocalAccountBinding>(R.layout.fragment_local_account) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val args by navArgs<LocalAccountFragmentArgs>()

    private val localAccountAdapter by lazy {
        LocalAccountAdapter()
    }

    override fun initView() {
        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                lifecycleScope.launch {
                    localAccountAdapter.setList(loginViewModel.accounts(appKey = args.appKey))
                    isRefreshing = false
                }
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = localAccountAdapter
            lifecycleScope.launch {
                localAccountAdapter.setList(loginViewModel.accounts(appKey = args.appKey))
            }
        }

        localAccountAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController().navigate(
                LocalKeyFragmentDirections.actionLocalKeyFragmentToLocalAccountFragment(
                    appKey = adapter.data[position] as String
                )
            )
        }

        localAccountAdapter.setOnItemLongClickListener { adapter, view, position ->
            true
        }
    }
}