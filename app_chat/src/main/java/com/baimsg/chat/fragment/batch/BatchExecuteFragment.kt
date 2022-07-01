package com.baimsg.chat.fragment.batch

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TaskAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentBatchExecuteBinding
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.data.model.entities.NIMTaskAccount
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
            val data = adapter.data[position] as NIMTaskAccount
            MaterialDialog(requireContext()).show {
                title(text = "移除任务")
                message(text = "你确定将\n[昵称] ${data.name}\n[id] ${data.account}\n从批量操作中移除？")
                negativeButton(R.string.cancel)
                positiveButton(R.string.sure) {
                    taskAccountAdapter.removeAt(position)
                    batchExecuteViewModel.deleteById(data)
                }
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeViewState.collectLatest {
                binding.tvCount.text = "(${it.allTaskAccounts.size})"
                when (it.updateStatus) {
                    UpdateStatus.REFRESH -> {
                        taskAccountAdapter.setList(it.allTaskAccounts)
                    }
                    else -> {
                    }
                }
            }
        }
    }
}