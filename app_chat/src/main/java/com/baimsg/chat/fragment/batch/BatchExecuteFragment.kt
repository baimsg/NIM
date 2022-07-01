package com.baimsg.chat.fragment.batch

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TaskAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentBatchExecuteBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@AndroidEntryPoint
class BatchExecuteFragment :
    BaseFragment<FragmentBatchExecuteBinding>(R.layout.fragment_batch_execute) {

    private val batchExecuteViewModel by viewModels<BatchExecuteViewModel>()

    private val args by navArgs<BatchExecuteFragmentArgs>()

    private val taskAccountAdapter by lazy {
        TaskAccountAdapter()
    }

    override fun initView() {
        batchExecuteViewModel.loadAllAccount(appKey = args.appKey)

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                batchExecuteViewModel.loadAllAccount(appKey = args.appKey)
                isRefreshing = false
            }
        }

        binding.ryContent.apply {
            taskAccountAdapter.animationEnable = true
            taskAccountAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = taskAccountAdapter
        }

        taskAccountAdapter.setOnItemClickListener { adapter, _, position ->
        }


    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeTaskAccount.collectLatest {
                taskAccountAdapter.setList(it)
                binding.tvCount.text = "(${it.size})"
            }
        }
    }
}