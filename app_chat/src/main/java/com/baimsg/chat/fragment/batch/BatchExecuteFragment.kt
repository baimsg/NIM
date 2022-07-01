package com.baimsg.chat.fragment.batch

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.baimsg.chat.databinding.FragmentBatchExecuteBinding
import com.baimsg.chat.fragment.team.TeamViewModel
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Success
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

    private val teamViewModel by viewModels<TeamViewModel>()

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
                message(text = "[昵称] ${data.name}\n[id] ${data.account}\n你确定从批量操作中移除吗？")
                negativeButton(R.string.cancel)
                positiveButton(R.string.sure) {
                    taskAccountAdapter.removeAt(position)
                    batchExecuteViewModel.deleteById(data)
                }
            }
        }

        binding.ivMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("清空数据")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            taskAccountAdapter.setList(null)
                            batchExecuteViewModel.deleteByAppKey(args.appKey)
                            showWarning("已将数据库清空")
                        }
                    }
                }
                negativeButton(res = R.string.cancel)
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeViewState.collectLatest {
                binding.tvCount.text = "(${it.allTaskAccounts.size})"
                when (it.updateStatus) {
                    UpdateStatus.REFRESH, UpdateStatus.REMOVE -> {
                        taskAccountAdapter.setList(it.allTaskAccounts)
                    }
                    else -> {
                    }
                }
            }
        }

        repeatOnLifecycleStarted {
            batchExecuteViewModel.observeTestView.collectLatest {
                binding.tvCount.text = "[${it.index}]-${it.executionStatus}-${it.name}"
            }
        }

        repeatOnLifecycleStarted {
            teamViewModel.viewState.collectLatest { data ->
                when (val teams = data.teams) {
                    is Fail -> {
                        showError(teams.error.message ?: "")
                    }
                    is Success -> {
                        val list = teams.invoke()
                        if (list.isEmpty()) {
                            showWarning("您还没有群聊哦")
                            return@collectLatest
                        }
                        MaterialDialog(requireContext()).show {
                            title(text = "选择要操作的群")
                            listItemsMultiChoice(
                                items = list.map { it.name }) { _, indices, _ ->
                                indices.forEach { index ->
                                    batchExecuteViewModel.addTeam(list[index])
                                }
                                batchExecuteViewModel.startInvite()
                            }
                            cancelable(false)
                            cancelOnTouchOutside(false)
                            negativeButton(R.string.cancel) {
                                findNavController().navigateUp()
                            }
                            positiveButton(R.string.sure)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}