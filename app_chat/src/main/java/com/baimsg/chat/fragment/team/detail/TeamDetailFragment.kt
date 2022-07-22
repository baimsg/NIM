package com.baimsg.chat.fragment.team.detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentTeamDetailBinding
import com.baimsg.chat.fragment.team.TeamViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
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

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvMore.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                listItems(items = listOf("解散该群")) { dialog, _, _ ->
                    detailViewModel.dismissTeam()
                    dialog.dismiss()
                }
                negativeButton()
            }
        }

        binding.vIntroduce.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet())
                .show {
                    title(R.string.introduce)
                    input(
                        hint = "请输入群简介",
                        prefill = detailViewModel.introduce,
                        waitForPositiveButton = false,
                        allowEmpty = true
                    ) { _, sequence ->
                        message(text = sequence)
                    }
                    positiveButton {
                        detailViewModel.apply {
                            TeamFieldEnum.Introduce set it.getInputField().text.toString()
                        }
                    }
                }
        }

        binding.vAnnouncement.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet())
                .show {
                    title(R.string.announcement)
                    input(
                        hint = "请输入群公告",
                        prefill = detailViewModel.announcement,
                        waitForPositiveButton = false,
                        allowEmpty = true
                    ) { _, sequence ->
                        message(text = sequence)
                    }
                    positiveButton {
                        detailViewModel.apply {
                            TeamFieldEnum.Announcement set it.getInputField().text.toString()
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

                    binding.tvAnnouncementValue.apply {
                        show(!announcement.isNullOrEmpty())
                        text = announcement
                    }

                    binding.tvIntroduceValue.apply {
                        show(!introduce.isNullOrEmpty())
                        text = introduce
                    }

                    binding.tvTeamNameValue.text = name

                    binding.tvTeamId.text = id

                    binding.ivAvatar.loadImage(icon)

                    binding.tvTeamMemberValue.text = "$memberCount/$memberLimit"

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

        repeatOnLifecycleStarted {
            detailViewModel.observeDismissTeamViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.SUCCESS -> findNavController().navigateUp()
                    ExecutionStatus.FAIL -> {
                        showError(it.message)
                    }
                    else -> Unit
                }
            }
        }
    }
}