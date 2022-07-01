package com.baimsg.chat.fragment.local

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLocalBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@AndroidEntryPoint
class LocalFragment : BaseFragment<FragmentLocalBinding>(R.layout.fragment_local) {

    private val localViewModel by viewModels<LocalViewModel>()

    private val args by navArgs<LocalFragmentArgs>()

    private val accountMediumAdapter by lazy {
        AccountMediumAdapter()
    }

    override fun initView() {
        localViewModel.loadAllAccount(appKey = args.appKey)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivFilter.setOnClickListener {
            findNavController().navigate(R.id.action_localFragment_to_filterFragment)
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                localViewModel.loadAllAccount(appKey = args.appKey)
                isRefreshing = false
            }
        }

        binding.ryContent.apply {
            accountMediumAdapter.animationEnable = true
            accountMediumAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountMediumAdapter
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            localViewModel.observeAllAccount.collectLatest {
                accountMediumAdapter.setList(it)
                binding.tvCount.text = "(${it.size})"
            }
        }
    }
}