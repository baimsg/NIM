package com.baimsg.chat.fragment.setting

import android.text.InputType
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSettingBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.showSuccess
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthService


class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun initView() {

        updateView()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.scAddMode.setOnCheckedChangeListener { _, isChecked ->
            KvUtils.put(Constant.KEY_ADD_MODE, isChecked)
            updateView()
        }

        binding.tvPrefix.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(hint = "请输入字典通用前缀") { _, charSequence ->
                    KvUtils.put(Constant.KEY_SEARCH_PREFIX, charSequence.toString())
                    updateView()
                }
            }
        }

        binding.tvAddFriendDelay.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入加好友延时/单位毫秒",
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    maxLength = 8
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_ADD_FRIEND_DELAY, charSequence.toString().toLong())
                    updateView()
                }
            }
        }

        binding.tvScope.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入字典运行次数",
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    maxLength = 8
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_SEARCH_COUNT, charSequence.toString().toLong())
                    updateView()
                }
            }
        }

        binding.tvVerify.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入加好友验证信息",
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_ADD_VERIFY, charSequence.toString())
                    updateView()
                }
            }
        }

        binding.tvFilter.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_filterFragment)
        }

        binding.tvSwitchAccount.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
        }

        binding.tvQuit.setOnClickListener {
            XPopup.Builder(requireContext())
                .asBottomList(
                    null,
                    arrayOf(getString(R.string.logout), getString(R.string.exit_app))
                ) { position, _ ->
                    when (position) {
                        0 -> {
                            loginViewModel.logout()
                            showSuccess("已退出本账号登录")
                            findNavController().navigateUp()
                        }
                        else -> {
                            loginViewModel.exit()
                        }
                    }
                }.show()
        }
    }


    private fun updateView() {
        val addMode = Constant.ADD_MODE

        binding.tvPrefixValue.text = Constant.SEARCH_PREFIX

        binding.tvVerifyValue.text = Constant.ADD_VERIFY

        binding.tvScopeValue.text = "${Constant.SEARCH_COUNT}次"

        binding.tvAddFriendDelayValue.text = "${Constant.ADD_FRIEND_DELAY}毫秒"

        binding.scAddMode.isChecked = addMode

        binding.tvVerify.isEnabled = !addMode

        binding.tvAddMode.text = if (addMode) "直接添加" else "发送验证"
    }
}