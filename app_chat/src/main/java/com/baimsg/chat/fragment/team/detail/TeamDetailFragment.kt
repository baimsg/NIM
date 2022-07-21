package com.baimsg.chat.fragment.team.detail

import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentTeamDetailBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showWarning
import com.netease.nimlib.sdk.team.constant.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/21
 *
 **/
@AndroidEntryPoint
class TeamDetailFragment : BaseFragment<FragmentTeamDetailBinding>(R.layout.fragment_team_detail) {

    private val detailViewModel by viewModels<TeamDetailViewModel>()

    private val loadDialog by lazy {
        MaterialDialog(requireContext()).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    override fun initView() {

        binding.vIntroduce.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.introduce)
                    input(hint = "请输入群简介", allowEmpty = true) { _, sequence ->
                        detailViewModel.apply {
                            TeamFieldEnum.Introduce set sequence.toString()
                        }
                    }
                }
        }

        binding.vAnnouncement.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.announcement)
                    input(
                        hint = "请输入群公告",
                        prefill = detailViewModel.announcement,
                        allowEmpty = true
                    ) { _, sequence ->
                        detailViewModel.apply {
                            TeamFieldEnum.Announcement set sequence.toString()
                        }
                    }
                }
        }

        binding.tvTeamName.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.team_name)
                    input(hint = "请输入群聊名称") { _, sequence ->
                        detailViewModel.apply {
                            TeamFieldEnum.Name set sequence.toString()
                        }
                    }
                }
        }

        binding.tvTeamIcon.setOnClickListener {
            showWarning("暂时不支持设置群头像")
        }

        binding.tvVerifyType.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .show {
                    title(R.string.verify_type)
                    listItems(
                        items = listOf(
                            "需要身份验证",
                            "允许任何人加入",
                            "不允许任何人加入"
                        )
                    ) { dialog, index, _ ->
                        dialog.dismiss()
                        detailViewModel.apply {
                            TeamFieldEnum.VerifyType set when (index) {
                                0 -> VerifyTypeEnum.Apply
                                1 -> VerifyTypeEnum.Free
                                else -> VerifyTypeEnum.Private
                            }
                        }
                    }
                    negativeButton()
                }
        }

        binding.tvBeInviteMode.setOnClickListener {
            detailViewModel.apply {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.be_invite_mode)
                    listItems(items = listOf("不需要同意", "需要对方同意")) { dialog, index, text ->
                        dialog.dismiss()
                        TeamFieldEnum.BeInviteMode set if (index == 0) TeamBeInviteModeEnum.NoAuth else TeamBeInviteModeEnum.NeedAuth
                    }
                    negativeButton()
                }
            }
        }

        binding.tvInviteMode.setOnClickListener {
            detailViewModel.apply {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.invite_mode)
                    listItems(items = listOf("仅管理", "任何人")) { dialog, index, text ->
                        dialog.dismiss()
                        TeamFieldEnum.InviteMode set if (index == 0) TeamInviteModeEnum.Manager else TeamInviteModeEnum.All
                    }
                    negativeButton()
                }
            }
        }

        binding.tvTeamUpdateMode.setOnClickListener {
            detailViewModel.apply {
                MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(R.string.team_update_mode)
                    listItems(items = listOf("仅管理", "任何人")) { dialog, index, text ->
                        dialog.dismiss()
                        TeamFieldEnum.TeamUpdateMode set if (index == 0) TeamUpdateModeEnum.Manager else TeamUpdateModeEnum.All
                    }
                    negativeButton()
                }
            }
        }

        binding.tvAllMute.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.all_mute)
                listItems(items = listOf("全体禁言", "取消禁言")) { dialog, index, text ->
                    dialog.dismiss()
                    detailViewModel.allMute(index == 0)
                }
                negativeButton()
            }
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            detailViewModel.observeTeamInfo.collectLatest {
                loadDialog.dismiss()

                it.apply {
                    binding.tvAnnouncementValue.text = announcement

                    binding.tvIntroduceValue.text = introduce

                    binding.tvTeamNameValue.text = name

                    binding.ivAvatar.loadImage(icon)

                    binding.tvVerifyTypeValue.text = verifyType.message()

                    binding.tvBeInviteModeValue.text =
                        teamBeInviteMode.message()

                    binding.tvInviteModeValue.text =
                        teamInviteMode.message()

                    binding.tvTeamUpdateModeValue.text =
                        teamUpdateMode.message()

                    binding.tvAllMuteValue.text = muteMode.message()

                }

            }
        }
    }
}