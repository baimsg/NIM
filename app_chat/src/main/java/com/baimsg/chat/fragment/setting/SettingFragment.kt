package com.baimsg.chat.fragment.setting

import android.text.InputType
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSettingBinding
import com.baimsg.chat.util.extensions.showError
import com.baimsg.chat.util.extensions.showInfo
import com.baimsg.chat.util.extensions.showSuccess
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.misc.DirCacheFileType
import com.netease.nimlib.sdk.misc.MiscService


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
            val fileTypes = listOf(
                DirCacheFileType.THUMB,
                DirCacheFileType.IMAGE,
                DirCacheFileType.AUDIO
            )
            NIMClient.getService(AuthService::class.java).logout()
            NIMClient.getService(MiscService::class.java).clearDirCache(fileTypes, 0, 0)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        showSuccess("已退出本账号登录")
                        requireActivity().finish()
                    }

                    override fun onFailed(p0: Int) {
                        showError("删除缓存失败")
                    }

                    override fun onException(p0: Throwable?) {
                        showError("删除缓存失败")
                    }
                })
        }
    }


    private fun updateView() {
        binding.tvAppKeyValue.text = Constant.APP_KEY

        binding.tvPrefixValue.text = Constant.SEARCH_PREFIX

        binding.tvVerifyValue.text = Constant.ADD_VERIFY

        binding.tvScopeValue.text = "${Constant.SEARCH_COUNT}次"
    }
}