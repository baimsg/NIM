package com.baimsg.chat.fragment.local

import android.text.Editable
import android.text.TextWatcher
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
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.*
import com.baimsg.data.model.entities.NIMUserInfo
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
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

    private val keyWordsTextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(editable: Editable?) {
                val keyWord = editable?.toString()
                binding.ivClear.show(!keyWord.isNullOrEmpty())
                binding.srContent.isEnabled = keyWord.isNullOrBlank()
                when {
                    keyWord.isNullOrBlank() -> {
                        accountMediumAdapter.setList(localViewModel.allAccounts)
                    }
                    else -> {
                        accountMediumAdapter.setList(localViewModel.allAccounts.filter {
                            it.name.contains(
                                keyWord
                            )
                        })
                    }
                }
            }
        }
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
                listItems(
                    items = listOf(
                        "清空数据",
                        "导入数据",
                        "导出全部数据",
                        "导出列表数据",
                        "导出账号信息",
                        "全部加入任务"
                    )
                ) { dialog, index, _ ->
                    dialog.dismiss()
                    if (isEmpty()) return@listItems
                    when (index) {
                        0 -> {
                            lifecycleScope.launch {
                                loadDialog.show()
                                localViewModel.deleteAllByAppKey()
                                accountMediumAdapter.setList(null)
                                loadDialog.dismiss()
                                showSuccess("已将数据库清空")
                            }
                        }
                        1 -> {
                            showWarning("导入功能待完善")
                        }
                        2 -> {
                            showWarning("导出功能待完善")
                        }
                        3 -> {
                            showWarning("导出功能待完善")
                        }
                        4 -> {
                            showWarning("导出功能待完善")
                        }
                        else -> {
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
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("账号信息", "加入任务", "删除数据")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            findNavController().navigate(
                                LocalFragmentDirections.actionLocalFragmentToUserDetailFragment(
                                    account = data.account
                                )
                            )
                        }
                        1 -> {
                            localViewModel.addTask(data)
                            showSuccess("[${data.name}-${data.account}]添加成功")
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

        binding.editSearch.apply {
            removeTextChangedListener(keyWordsTextWatcher)
            addTextChangedListener(keyWordsTextWatcher)
        }

        binding.ivClear.setOnClickListener {
            binding.editSearch.text = null
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            localViewModel.observeViewState.collect {
                val allAccounts = it.allAccounts
                binding.tvCount.text = "(${allAccounts.size})"
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> {
                        binding.srContent.isRefreshing = true
                    }
                    ExecutionStatus.SUCCESS -> {
                        binding.srContent.isRefreshing = false
                        when (it.updateStatus) {
                            UpdateStatus.REFRESH -> {
                                accountMediumAdapter.setList(allAccounts)
                            }
                            else -> Unit
                        }
                    }
                    else -> {
                        binding.srContent.isRefreshing = false
                    }
                }
            }
        }

        repeatOnLifecycleStarted {
            localViewModel.observeOperateViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> {
                        loadDialog.show()
                    }
                    ExecutionStatus.SUCCESS -> {
                        loadDialog.dismiss()
                        showSuccess(it.tip)
                    }
                    ExecutionStatus.FAIL -> {
                        loadDialog.dismiss()
                        showError(it.tip)
                    }
                    else -> {
                        loadDialog.dismiss()
                    }
                }
            }
        }
    }

    override fun initData() {
        localViewModel.loadAllAccount()
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