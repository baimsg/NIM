package com.baimsg.chat.fragment.setting

import android.text.InputType
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {


    override fun initView() {

        updateView()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvAppKey.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_appKeyFragment)
        }

        binding.tvPrefix.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(hint = "请输入字典通用前缀") { _, charSequence ->
                    KvUtils.put(Constant.KEY_SEARCH_PREFIX, charSequence.toString())
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

    }

    private fun updateView() {
        binding.tvAppKeyValue.text = Constant.APP_KEY

        binding.tvPrefixValue.text = Constant.SEARCH_PREFIX

        binding.tvScopeValue.text = "${Constant.SEARCH_COUNT}次"
    }
}