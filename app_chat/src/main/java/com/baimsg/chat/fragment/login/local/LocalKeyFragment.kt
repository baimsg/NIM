package com.baimsg.chat.fragment.login.local

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AppKeyAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLocalKeyBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalKeyFragment :
    BaseFragment<FragmentLocalKeyBinding>(R.layout.fragment_local_key) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val appKeyAdapter by lazy {
        AppKeyAdapter()
    }

    override fun initView() {
        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                lifecycleScope.launch {
                    appKeyAdapter.setList(loginViewModel.appKeys())
                    isRefreshing = false
                }
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = appKeyAdapter
            lifecycleScope.launch {
                appKeyAdapter.setList(loginViewModel.appKeys())
            }
        }

        appKeyAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController().navigate(
                LocalKeyFragmentDirections.actionLocalKeyFragmentToLocalAccountFragment(
                    appKey = adapter.data[position] as String
                )
            )
        }

        appKeyAdapter.setOnItemLongClickListener { adapter, view, position ->
            true
        }
    }
}