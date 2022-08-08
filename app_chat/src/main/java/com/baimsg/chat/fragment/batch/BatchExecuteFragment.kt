package com.baimsg.chat.fragment.batch

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TaskAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FragmentBatchExecuteBinding
import com.baimsg.chat.fragment.bulk.BulkData
import com.baimsg.chat.type.BatchType
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import com.baimsg.chat.util.extensions.showSuccess
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.model.JSON
import com.baimsg.data.model.entities.NIMTaskAccount
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.builtins.ListSerializer

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

    private val tipDialog: MaterialDialog by lazy {
        MaterialDialog(requireContext())
    }

    private val taskTypes by lazy {
        listOf("加好友", "邀请进群", "群发消息")
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_batchExecuteFragment_to_settingFragment)
        }

        binding.ivClean.setOnClickListener {
            tipDialog.show {
                title(text = "清空任务")
                message(text = "您确定清空任务吗？")
                negativeButton(res = R.string.cancel)
                positiveButton(res = R.string.sure) {
                    taskAccountAdapter.setList(null)
                    batchExecuteViewModel.deleteAll()
                    showSuccess("已清空")
                }
            }
        }

        binding.fabQuit.setOnClickListener {
            batchExecuteViewModel.stop()
        }

        binding.ivStart.setOnClickListener {
            if (batchExecuteViewModel.batchType == BatchType.UNKNOWN) {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    listItems(items = taskTypes) { dialog, index, _ ->
                        dialog.dismiss()
                        when (index) {
                            0 -> {
                                batchExecuteViewModel.start(batchType = BatchType.ADD_FRIEND)
                            }
                            1 -> {
                                val allTeam = batchExecuteViewModel.allTeam
                                if (allTeam.isEmpty()) {
                                    showWarning("您还没有群聊哦：)")
                                } else {
                                    MaterialDialog(
                                        requireContext(),
                                        BottomSheet(LayoutMode.WRAP_CONTENT)
                                    ).show {
                                        listItemsMultiChoice(items = allTeam.map { it.name + "-" + it.id + "[${it.memberCount}]" })
                                        { _, indices, _ ->
                                            batchExecuteViewModel.updateTeam(indices)
                                        }
                                        negativeButton()
                                        positiveButton()
                                    }
                                }
                            }
                            else -> {
                                findNavController().navigate(
                                    BatchExecuteFragmentDirections.actionBatchExecuteFragmentToBulkFragment(
                                        bulks = JSON.encodeToString(
                                            ListSerializer(BulkData.serializer()),
                                            batchExecuteViewModel.allTaskAccounts.map {
                                                BulkData(
                                                    it.account,
                                                    it.name
                                                )
                                            }
                                        ),
                                        sessionType = SessionTypeEnum.P2P
                                    )
                                )
                            }
                        }
                    }
                    negativeButton(res = R.string.cancel)
                }
            } else {
                batchExecuteViewModel.start()
            }
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
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("账号信息", "移除任务")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            findNavController().navigate(BatchExecuteFragmentDirections.actionBatchExecuteFragmentToUserDetailFragment())
                        }
                        else -> {
                            taskAccountAdapter.removeAt(position)
                            batchExecuteViewModel.deleteById(data)
                        }
                    }
                }
                negativeButton(R.string.cancel)
            }
        }

        taskAccountAdapter.setOnItemLongClickListener { adapter, _, position ->
            if (batchExecuteViewModel.batchExecuteViewState.running()) {
                return@setOnItemLongClickListener true
            }
            val data = adapter.data[position] as NIMTaskAccount
            tipDialog.show {
                title(text = "移除任务")
                message(text = "${data.name}-[${data.account}]")
                negativeButton(R.string.cancel)
                positiveButton(R.string.sure) {
                    taskAccountAdapter.removeAt(position)
                    batchExecuteViewModel.deleteById(data)
                }
            }
            true
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