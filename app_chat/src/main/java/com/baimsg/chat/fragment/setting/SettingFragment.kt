package com.baimsg.chat.fragment.setting

import android.text.InputType
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.BottomLoginBinding
import com.baimsg.chat.databinding.FragmentSettingBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.showSuccess


class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun initView() {

        updateView()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.scAddDirect.setOnCheckedChangeListener { _, isChecked ->
            KvUtils.put(Constant.KEY_ADD_DIRECT, isChecked)
            updateView()
        }

        binding.scAutofill.setOnCheckedChangeListener { buttonView, isChecked ->
            KvUtils.put(Constant.KEY_AUTO_FILL, isChecked)
            updateView()
        }

        binding.tvPrefix.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(hint = "请输入字典通用前缀", allowEmpty = true) { _, charSequence ->
                    KvUtils.put(Constant.KEY_SEARCH_PREFIX, charSequence.toString())
                    updateView()
                }
            }
        }

        binding.tvDelay.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入加好友延时/单位毫秒",
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    maxLength = 8
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_DELAY, charSequence.toString().toLong())
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

        binding.tvFriendDescribe.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入加好友验证信息",
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_ADD_FRIEND_DESCRIBE, charSequence.toString())
                    updateView()
                }
            }
        }

        binding.tvTeamDescribe.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入邀请进群描述",
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_TEAM_DESCRIBE, charSequence.toString())
                    updateView()
                }
            }
        }

        binding.tvTeamLimit.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                    hint = "请输入每个群上限人数",
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    maxLength = 8
                ) { _, charSequence ->
                    KvUtils.put(Constant.KEY_TEAM_LIMIT, charSequence.toString().toLong())
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

            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                val view = View.inflate(requireContext(), R.layout.bottom_login, null)
                BottomLoginBinding.bind(view).apply {
                    customView(view = view)
                    tvLogout.setOnClickListener {
                        loginViewModel.logout()
                        showSuccess("已退出本账号登录")
                        findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
                        dismiss()
                    }

                    tvQuit.setOnClickListener {
                        loginViewModel.exit()
                        dismiss()
                    }

                    tvCancel.setOnClickListener {
                        dismiss()
                    }
                }
            }
        }
    }


    private fun updateView() {
        val addMode = Constant.ADD_DIRECT

        binding.tvPrefixValue.text = Constant.SEARCH_PREFIX

        binding.tvFriendDescribeValue.text = Constant.ADD_FRIEND_DESCRIBE
        binding.tvFriendDescribeValue.isEnabled = !addMode

        binding.tvScopeValue.text = "${Constant.SEARCH_COUNT}次"

        binding.tvTeamLimitValue.text = "${Constant.TEAM_LIMIT}人"

        binding.tvDelayValue.text = "${Constant.DELAY}毫秒"

        binding.scAddDirect.isChecked = addMode

        binding.tvAddDirect.text = if (addMode) "直接添加" else "发送验证"

        binding.scAutofill.isChecked = Constant.AUTO_FILL

        binding.tvTeamDescribeValue.text = Constant.TEAM_DESCRIBE
    }
}