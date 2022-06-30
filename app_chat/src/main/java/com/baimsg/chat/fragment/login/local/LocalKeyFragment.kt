package com.baimsg.chat.fragment.login.local

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.LocalKeyAdapter
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

    private val localKeyAdapter by lazy {
        LocalKeyAdapter()
    }

    override fun initView() {
        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                lifecycleScope.launch {
                    localKeyAdapter.setList(loginViewModel.appKeys())
                    isRefreshing = false
                }
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = localKeyAdapter
            lifecycleScope.launch {
                localKeyAdapter.setList(loginViewModel.appKeys())
            }
        }

        localKeyAdapter.setOnItemClickListener { adapter, _, position ->
            findNavController().navigate(
                LocalKeyFragmentDirections.actionLocalKeyFragmentToLocalAccountFragment(
                    appKey = adapter.data[position] as String
                )
            )
        }

        localKeyAdapter.setOnItemLongClickListener { adapter, view, position ->
            true
        }
    }
}