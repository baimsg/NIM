package com.baimsg.chat.fragment.scanning

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentScanningAccountBinding
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.util.extensions.*
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@AndroidEntryPoint
class ScanningAccountFragment :
    BaseFragment<FragmentScanningAccountBinding>(R.layout.fragment_scanning_account) {

    private val scanningAccountViewModel by viewModels<ScanningAccountViewModel>()

    private val accountMediumAdapter by lazy {
        AccountMediumAdapter()
    }

    override fun initView() {
        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                accountMediumAdapter.setList(scanningAccountViewModel.allUser.filter { it.loaded })
                isRefreshing = false
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_searchUserFragment_to_settingFragment)
        }

        binding.ivClean.setOnClickListener {
            scanningAccountViewModel.stopSearchAccount()
        }

        binding.ivSave.setOnClickListener {

        }

        binding.ivStart.setOnClickListener {
            scanningAccountViewModel.searchAccount()
        }

        binding.fabAdd.setOnClickListener {

        }

        binding.ryContent.apply {
            accountMediumAdapter.animationEnable = true
            accountMediumAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountMediumAdapter
        }

        binding.editAccount.apply {
            showKeyboard(true)
            addTextChangedListener {
                try {
                    val account = if (it.isNullOrBlank()) 0 else it.toString().toLong()
                    scanningAccountViewModel.updateAccount(account)
                } catch (e: Exception) {
                    showError("请检查您输入的内容")
                }
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            scanningAccountViewModel.observeViewState.collectLatest { value ->
                value.apply {
                    binding.tvCount.text = "(${allUser.size})"
                    binding.tvProgress.text = "(${count}/${Constant.SEARCH_COUNT})"
                    when (status) {
                        BatchStatus.RUNNING -> {
                            binding.ivStart.setImageResource(R.drawable.ic_pause)
                            binding.ivBack.hide()
                            binding.ivEdit.hide()
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            binding.fabAdd.hide()
                            binding.proLoading.show()
                            binding.editAccount.isEnabled = false
                            if (update) accountMediumAdapter.addData(users)
                            binding.editAccount.setText("${value.account}")
                        }
                        BatchStatus.STOP -> {
                            binding.fabAdd.hide()
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            accountMediumAdapter.setList(null)
                            binding.editAccount.isEnabled = true
                        }
                        else -> {
                            binding.proLoading.hide(true)
                            binding.ivBack.show()
                            binding.ivEdit.show()
                            if (allUser.isNotEmpty()) {
                                binding.ivSave.show()
                                binding.ivClean.show()
                                binding.fabAdd.show()
                            }
                            binding.ivStart.setImageResource(R.drawable.ic_play)
                        }
                    }
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()
        binding.editAccount.hideKeyboard()
    }
}