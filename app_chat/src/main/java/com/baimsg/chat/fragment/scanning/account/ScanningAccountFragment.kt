package com.baimsg.chat.fragment.scanning.account

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentScanningAccountBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.type.UpdateStatus
import com.baimsg.chat.util.extensions.*
import com.baimsg.chat.util.getId
import com.chad.library.adapter.base.animation.AlphaInAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@AndroidEntryPoint
class ScanningAccountFragment :
    BaseFragment<FragmentScanningAccountBinding>(R.layout.fragment_scanning_account) {

    private val loadDialog by lazy {
        MaterialDialog(requireContext()).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    private val scanningAccountViewModel by viewModels<ScanningAccountViewModel>()

    private val loginViewModel by activityViewModels<LoginViewModel>()

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

        binding.ryContent.apply {
            accountMediumAdapter.animationEnable = true
            accountMediumAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = accountMediumAdapter
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_scanningAccountFragment_to_settingFragment)
        }

        binding.ivClean.setOnClickListener {
            scanningAccountViewModel.cleanSearchAccount()
        }

        binding.ivSave.setOnClickListener {
            lifecycleScope.launch {
                loadDialog.show()
                scanningAccountViewModel.saveDatabase(loginViewModel.currentLoginRecord.appKey)
                loadDialog.dismiss()
                showSuccess("已保存数据库")
            }
        }

        binding.ivStart.setOnClickListener {
            scanningAccountViewModel.searchAccount()
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

        binding.fabQuit.setOnClickListener {
            accountMediumAdapter.setList(null)
            scanningAccountViewModel.stopSearchAccount()
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            scanningAccountViewModel.observeViewState.collectLatest { value ->
                value.apply {
                    binding.proLoading.show(running())
                    binding.ivBack.show(!running())
                    binding.ivSave.show(!running() && allUser.isNotEmpty())
                    binding.ivClean.show(!running() && allUser.isNotEmpty())
                    binding.ivSetting.show(!running())
                    binding.ivStart.setImageResource(if (running()) R.drawable.ic_pause else R.drawable.ic_play)
                    binding.editAccount.isEnabled = unknown()

                    binding.fabQuit.show(pause() || stop())

                    val searchCount = Constant.SEARCH_COUNT
                    binding.tvCount.text = "(${allUser.size})"
                    binding.tvProgress.text = "(${count}/${searchCount})"

                    if (running()) binding.editAccount.setText(account.getId())

                    when (updateStatus) {
                        UpdateStatus.APPEND -> accountMediumAdapter.addData(newUsers)
                        UpdateStatus.REFRESH, UpdateStatus.CLEAN ->
                            accountMediumAdapter.setList(allUser)
                        else -> Unit
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