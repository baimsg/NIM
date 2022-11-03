package com.baimsg.chat.fragment.batch.result

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TaskAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FragmentBatchExecuteContentBinding
import com.baimsg.chat.databinding.FragmentBatchExecuteResultBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.data.model.entities.NIMTaskAccount
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/11/3
 *
 **/
class BatchExecuteResultFragment :
    BaseFragment<FragmentBatchExecuteResultBinding>(R.layout.fragment_batch_execute_result) {

    private val batchExecuteResultViewModel by viewModels<BatchExecuteResultViewModel>()

    private val tabs by lazy {
        listOf(R.string.success, R.string.fail)
    }

    private val fragments by lazy {
        listOf(
            ContentFragment(batchExecuteResultViewModel, true),
            ContentFragment(batchExecuteResultViewModel, false)
        )
    }

    private var mediator: TabLayoutMediator? = null

    override fun initView() {
        initTab()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun initTab() {

        binding.vpMain.apply {
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

            adapter = object : FragmentStateAdapter(this@BatchExecuteResultFragment) {
                override fun getItemCount(): Int = fragments.size

                override fun createFragment(position: Int): Fragment = fragments[position]

            }

            mediator = TabLayoutMediator(binding.tabMain, this) { _, _ ->
            }

            mediator?.attach()
        }

        binding.tabMain.apply {
            removeAllTabs()
            tabs.forEachIndexed { index, i ->
                addTab(newTab().apply {
                    setText(i)
                    tag = index
                })
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        mediator?.detach()
        binding.vpMain.apply {
            adapter = null
        }
    }

    internal class ContentFragment(
        private val batchExecuteResultViewModel: BatchExecuteResultViewModel,
        private val success: Boolean,
    ) : BaseFragment<FragmentBatchExecuteContentBinding>(R.layout.fragment_batch_execute_content) {

        private val taskAccountAdapter by lazy {
            TaskAccountAdapter()
        }

        override fun initView() {
            binding.srContent.apply {
                setColorSchemeResources(R.color.color_primary)
                setOnRefreshListener {
                    batchExecuteResultViewModel.loadData()
                    isRefreshing = false
                }
            }

            binding.ryContent.apply {
                taskAccountAdapter.animationEnable = true
                taskAccountAdapter.adapterAnimation = AlphaInAnimation()

                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = taskAccountAdapter

                val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
                EmptyBaseBinding.bind(emptyView).apply {
                    taskAccountAdapter.setEmptyView(emptyView)
                    tvTip.text = "没有数据哦:)"
                }
            }

            taskAccountAdapter.setOnItemClickListener { adapter, _, position ->
                val data = adapter.data[position] as NIMTaskAccount
                findNavController().navigate(BatchExecuteResultFragmentDirections.actionBatchExecuteResultFragmentToUserDetailFragment(
                    account = data.account))
            }
        }

        override fun initLiveData() {
            repeatOnLifecycleStarted {
                batchExecuteResultViewModel.observeTaskResult.collectLatest { tasks ->
                    taskAccountAdapter.setList(tasks.filter { it.success == success }
                        .map { it.task.copy(processed = !success) })
                }
            }
        }
    }

}
