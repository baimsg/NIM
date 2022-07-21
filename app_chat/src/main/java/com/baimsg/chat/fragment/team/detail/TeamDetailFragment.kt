package com.baimsg.chat.fragment.team.detail

import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
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
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.verify_type)
                    listItems(
                        items = listOf(
                            "需要审核",
                            "不需要审核",
                            "不允许加群"
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

        binding.scBeInviteMode.setOnCheckedChangeListener { _, isChecked ->
            detailViewModel.apply {
                TeamFieldEnum.BeInviteMode set if (isChecked) TeamBeInviteModeEnum.NoAuth else TeamBeInviteModeEnum.NeedAuth
            }
        }

        binding.scInviteMode.setOnCheckedChangeListener { _, isChecked ->
            detailViewModel.apply {
                TeamFieldEnum.InviteMode set if (isChecked) TeamInviteModeEnum.Manager else TeamInviteModeEnum.All
            }
        }

        binding.scTeamUpdateMode.setOnCheckedChangeListener { _, isChecked ->
            detailViewModel.apply {
                TeamFieldEnum.TeamUpdateMode set if (isChecked) TeamUpdateModeEnum.Manager else TeamUpdateModeEnum.All
            }
        }


        binding.scAllMute.setOnCheckedChangeListener { _, isChecked ->
            detailViewModel.allMute(isChecked)
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

                    binding.scBeInviteMode.isChecked =
                        teamBeInviteMode == TeamBeInviteModeEnum.NoAuth

                    binding.scInviteMode.isChecked =
                        teamInviteMode == TeamInviteModeEnum.Manager

                    binding.scTeamUpdateMode.isChecked =
                        teamUpdateMode == TeamUpdateModeEnum.Manager

                    binding.scAllMute.isChecked = allMute

                }

            }
        }
    }
}