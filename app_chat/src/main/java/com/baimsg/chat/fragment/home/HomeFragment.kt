package com.baimsg.chat.fragment.home

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showInfo
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val loadDialog by lazy {
        MaterialDialog(requireContext()).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    private val openLogin by lazy {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(hard = true))
    }

    override fun initView() {
        binding.ivAdd.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(
                    items = listOf("创建群聊", "加好友/群")
                ) { dialog, index, _ ->
                    dialog.dismiss()
                    when (index) {
                        0 -> {
                            findNavController().navigate(R.id.action_homeFragment_to_createTeamFragment)
                        }
                        else -> {

                        }
                    }
                }
                negativeButton {
                    dismiss()
                }
            }
        }

        binding.vNewFriend.setOnClickListener {
            showInfo("unknown 该功能待开发")
        }

        binding.vFriendList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_friendFragment)
        }

        binding.vTeamChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_teamFragment)
        }

        binding.vScanning.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanningAccountFragment)
        }

        binding.vBatchExe.setOnClickListener {
            lifecycleScope.launch {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBatchExecuteFragment(
                        appKey = loginViewModel.currentLoginRecord.appKey
                    )
                )
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            loginViewModel.observerStatusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                lifecycleScope.launch(Dispatchers.Main) {
                    val nimLoginRecord = loginViewModel.currentLoginRecord
                    if (nimLoginRecord.mustEmpty() || nimLoginRecord.appKeyEmpty() || status.wontAutoLogin()) openLogin
                }
            }

        }
    }

    override fun initData() {
//        examine()
    }

    private fun examine() {
        lifecycleScope.launch {
            loadDialog.show()
            val base = loginViewModel.getBaseConfig()
            loadDialog.dismiss()
            when (base) {
                is Success -> {
                    if (base().id != Constant.ID) {
                        MaterialDialog(requireContext())
                            .cancelable(false)
                            .cancelOnTouchOutside(false).show {
                                input(
                                    hint = "id",
                                    waitForPositiveButton = false
                                ) { materialDialog, charSequence ->
                                    val isValid = charSequence.toString() == base().id
                                    materialDialog.setActionButtonEnabled(
                                        WhichButton.POSITIVE,
                                        isValid
                                    )
                                    KvUtils.put(Constant.KEY_ID, charSequence.toString())
                                }
                                negativeButton(R.string.cancel) { loginViewModel.exit() }
                                positiveButton(R.string.sure)
                            }
                    }
                }
                is Fail -> {
                    MaterialDialog(requireContext())
                        .cancelable(false)
                        .cancelOnTouchOutside(false).show {
                            title(text = "网络异常")
                            message(text = "你的网络好像已经丢失了:)")
                            negativeButton(R.string.cancel) { loginViewModel.exit() }
                            positiveButton(R.string.retry) {
                                examine()
                            }
                        }
                }
                else -> {}
            }

        }
    }

}