package com.baimsg.chat.fragment.batch

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.expandBottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TaskAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FragmentBatchExecuteBinding
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.type.BatchType
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.hide
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import com.baimsg.chat.util.extensions.showWarning
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

    private val taskAccountAdapter by lazy {
        TaskAccountAdapter()
    }

    private val dialog: MaterialDialog
        get() = MaterialDialog(requireContext()).cancelable(false).cancelOnTouchOutside(false)


    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                batchExecuteViewModel.loadAllAccount()
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
                tvTip.text = "暂时没有批量操作数据哦:)"
            }
        }

        taskAccountAdapter.setOnItemClickListener { adapter, _, position ->
            if (batchExecuteViewModel.batchExecuteViewState.running()) {
                return@setOnItemClickListener
            }
            val data = adapter.data[position] as NIMTaskAccount
            dialog.show {
                title(text = "移除任务")
                message(text = "[昵称] ${data.name}\n[id] ${data.account}\n你确定从批量操作中移除吗？")
                negativeButton(R.string.cancel)
                positiveButton(R.string.sure) {
                    taskAccountAdapter.removeAt(position)
                    batchExecuteViewModel.deleteById(data)
                }
            }
        }

        binding.ivClean.setOnClickListener {
            dialog.show {
                title(text = "清空任务")
                message(text = "你确定清空批量操作任务吗？")
                negativeButton(res = R.string.cancel)
                positiveButton(res = R.string.sure) {
                    taskAccountAdapter.setList(null)
                    batchExecuteViewModel.deleteAll()
                    showWarning("已清空")
                }
            }
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_batchExecuteFragment_to_settingFragment)
        }

        binding.fabQuit.setOnClickListener {
            batchExecuteViewModel.stop()
        }

        binding.ivStart.setOnClickListener {
            if (batchExecuteViewModel.batchType == BatchType.UNKNOWN) {
                dialog.show {
                    title(text = "请选择操作类型")
                    listItems(items = listOf("加好友", "邀请进群")) { dialog, index, _ ->
                        dialog.dismiss()
                        when (index) {
                            0 -> {
                                batchExecuteViewModel.start(batchType = BatchType.FRIEND)
                            }
                            else -> {
                                val allTeam = batchExecuteViewModel.allTeam
                                if (allTeam.isEmpty()) {
                                    showWarning("您还没有群聊哦：)")
                                } else {
                                    MaterialDialog(
                                        requireContext(),
                                        BottomSheet(LayoutMode.WRAP_CONTENT)
                                    )
                                        .cancelable(false)
                                        .cancelOnTouchOutside(false)
                                        .show {
                                            listItemsMultiChoice(items = allTeam.map { it.name + "-" + it.id + "[${it.memberCount}]" })
                                            { _, indices, _ ->
                                                batchExecuteViewModel.updateTeam(indices)
                                            }
                                            negativeButton(res = R.string.cancel)
                                            positiveButton(res = R.string.sure)
                                        }
                                }

                            }
                        }
                    }
                    negativeButton(res = R.string.cancel)
                }
            } else {
                batchExecuteViewModel.start()
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeTaskAccountViewState.collectLatest {
                binding.tvCount.text = "(${it.allTaskAccounts.size})"
                binding.ivClean.show(it.allTaskAccounts.isNotEmpty() && !batchExecuteViewModel.batchExecuteViewState.running())
                when (it.updateStatus) {
                    UpdateStatus.REFRESH -> {
                        taskAccountAdapter.setList(it.allTaskAccounts)
                    }
                    UpdateStatus.REMOVE -> {
                        taskAccountAdapter.remove(it.task)
                    }
                    UpdateStatus.UPDATE -> {
                        val index = taskAccountAdapter.data.indexOf(it.task)
                        if (index != -1) taskAccountAdapter.notifyItemChanged(index)
                    }
                    else -> {
                    }
                }
            }
        }

        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeBatchExecuteViewState.collectLatest { value ->
                value.apply {
                    binding.tvProgress.apply {
                        text = value.message
                        show(!text.isNullOrBlank())
                    }
                    binding.srContent.isEnabled = !running()
                    binding.proLoading.show(running())
                    binding.fabQuit.show(pause())
                    binding.ivBack.show(!running())
                    binding.ivSetting.show(!running())
                    binding.ivClean.show(batchExecuteViewModel.allTaskAccounts.isNotEmpty() && !running())
                    binding.ivStart.setImageResource(if (running()) R.drawable.ic_pause else R.drawable.ic_play)
                }
            }
        }
    }
}