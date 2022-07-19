package com.baimsg.chat.fragment.login.local

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.R
import com.baimsg.chat.adapter.LocalKeyAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLocalKeyBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalKeyFragment :
    BaseFragment<FragmentLocalKeyBinding>(R.layout.fragment_local_key) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val localKeyAdapter by lazy {
        LocalKeyAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                lifecycleScope.launch {
                    localKeyAdapter.setList(loginViewModel.allAppKeys)
                    isRefreshing = false
                }
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = localKeyAdapter
            lifecycleScope.launch {
                localKeyAdapter.setList(loginViewModel.allAppKeys)
            }
        }

        localKeyAdapter.setOnItemClickListener { adapter, _, position ->
            val appKey = adapter.data[position] as String
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(res = R.string.select_action)
                    listItems(items = listOf("修改备注", "删除数据", "查看账号列表")) { dialog, index, text ->
                        dialog.dismiss()
                        when (index) {
                            0 -> {
                                MaterialDialog(requireContext()).show {
                                    input(hint = "请输入备注") { materialDialog, charSequence ->
                                        materialDialog.dismiss()
                                        KvUtils.put(appKey, charSequence.toString())
                                        adapter.notifyItemChanged(position)
                                    }
                                }
                            }
                            1 -> {
                                loginViewModel.deleteAppKey(appKey)
                                adapter.removeAt(position)
                            }
                            else -> {
                                findNavController().navigate(
                                    LocalKeyFragmentDirections.actionLocalKeyFragmentToLocalAccountFragment(
                                        appKey = appKey
                                    )
                                )
                            }
                        }
                    }
                    negativeButton()
                }

        }


    }
}