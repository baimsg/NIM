package com.baimsg.chat.fragment.local

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FragmentLocalBinding
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.chat.util.extensions.showSuccess
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.model.entities.NIMUserInfo
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

    private val accountMediumAdapter by lazy {
        AccountMediumAdapter()
    }

    private val loadDialog by lazy {
        MaterialDialog(requireContext()).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivFilter.setOnClickListener {
            findNavController().navigate(R.id.action_localFragment_to_filterFragment)
        }

        binding.ivMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("导入数据", "导出数据", "清空数据", "一键加入任务")) { dialog, index, _ ->
                    dialog.dismiss()
                    if (isEmpty()) return@listItems
                    when (index) {
                        0 -> {
                            showWarning("导入功能待完善")
                        }
                        1 -> {
                            showWarning("导出功能待完善")
                        }
                        2 -> {
                            lifecycleScope.launch {
                                loadDialog.show()
                                localViewModel.deleteAllByAppKey()
                                accountMediumAdapter.setList(null)
                                loadDialog.dismiss()
                                showSuccess("已将数据库清空")
                            }
                        }
                        3 -> {
                            lifecycleScope.launch {
                                loadDialog.show()
                                localViewModel.addTaskAll()
                                loadDialog.dismiss()
                                showSuccess("数据已加入任务列表")
                            }

                        }
                    }
                }
                negativeButton(res = R.string.cancel)
            }
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                localViewModel.loadAllAccount()
                isRefreshing = false
            }
        }

        binding.ryContent.apply {
            accountMediumAdapter.animationEnable = true
            accountMediumAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountMediumAdapter

            val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
            EmptyBaseBinding.bind(emptyView).apply {
                accountMediumAdapter.setEmptyView(emptyView)
                tvTip.text = "数据库暂时没有数据哦:)"
            }
        }

        accountMediumAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as NIMUserInfo
            MaterialDialog(requireContext()).show {
                listItems(items = listOf("加入任务", "删除数据")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            localViewModel.addTask(data)
                            showSuccess("已将[${data.name}-${data.account}]添加到任务")
                        }
                        else -> {
                            localViewModel.deleteAccountById(data)
                            accountMediumAdapter.removeAt(position)
                        }
                    }
                }
                negativeButton(R.string.cancel)
            }
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            localViewModel.observeViewState.collectLatest {
                val allAccounts = it.allAccounts
                binding.tvCount.text = "(${allAccounts.size})"
                when (it.updateStatus) {
                    UpdateStatus.REFRESH -> {
                        accountMediumAdapter.setList(allAccounts)
                    }
                    else -> {}
                }
            }
        }
    }


    private fun isEmpty(): Boolean {
        return if (localViewModel.allAccounts.isEmpty()) {
            showError("数据库不存在数据:)")
            true
        } else {
            false
        }
    }
}