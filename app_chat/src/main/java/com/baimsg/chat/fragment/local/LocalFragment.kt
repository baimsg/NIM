package com.baimsg.chat.fragment.local

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLocalBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showSuccess
import com.baimsg.chat.util.extensions.showWarning
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asTask
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

        binding.ivMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("导入数据", "导入数据", "清空数据")) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            showWarning("导入功能待完善")
                        }
                        1 -> {
                            showWarning("导出功能待完善")
                        }
                        2 -> {
                            showWarning("清空功能待完善")
                        }
                    }
                }
                negativeButton(res = R.string.cancel)
            }
        }

        binding.fabAdd.setOnClickListener {
            localViewModel.addTaskAll()
            showSuccess("已将数据全部添加到任务")
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
                            localViewModel.deleteAccountById(data.id)
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
            localViewModel.observeAllAccount.collectLatest {
                accountMediumAdapter.setList(it)
                binding.tvCount.text = "(${it.size})"
            }
        }
    }
}