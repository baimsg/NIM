package com.baimsg.chat.fragment.team.create

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TipAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentTeamCreateBinding
import com.baimsg.chat.fragment.team.TeamViewModel
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.util.extensions.*
import com.netease.nimlib.sdk.team.constant.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/19
 *
 **/
@AndroidEntryPoint
class CreateTeamFragment : BaseFragment<FragmentTeamCreateBinding>(R.layout.fragment_team_create) {

    private val teamViewModel by viewModels<TeamViewModel>()

    private val tipAdapter by lazy {
        TipAdapter()
    }

    override fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_createTeamFragment_to_settingFragment)
        }

        binding.fabQuit.setOnClickListener {
            tipAdapter.setList(null)
            teamViewModel.stopBatchCreateTeam()
        }

        binding.ivCreate.setOnClickListener {
            teamViewModel.startBatchCreateTeam()
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = tipAdapter
        }

        binding.vIntroduce.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.introduce)
                    input(hint = "请输入群简介", allowEmpty = true) { _, sequence ->
                        teamViewModel.apply {
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
                    input(hint = "请输入群公告", allowEmpty = true) { _, sequence ->
                        teamViewModel.apply {
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
                        teamViewModel.apply {
                            TeamFieldEnum.Name set sequence.toString()
                        }
                    }
                }
        }

        binding.tvTeamIcon.setOnClickListener {
            showWarning("暂时不支持设置群头像")
        }

        binding.tvTeamSum.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.team_sum)
                    input(
                        hint = "请输入创建数量",
                        inputType = InputType.TYPE_CLASS_NUMBER,
                        maxLength = 4
                    ) { _, sequence ->
                        teamViewModel + sequence.toString().toInt()
                    }
                }
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
                        teamViewModel.apply {
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
            teamViewModel.apply {
                TeamFieldEnum.BeInviteMode set if (isChecked) TeamBeInviteModeEnum.NoAuth else TeamBeInviteModeEnum.NeedAuth
            }
        }

        binding.scInviteMode.setOnCheckedChangeListener { _, isChecked ->
            teamViewModel.apply {
                TeamFieldEnum.InviteMode set if (isChecked) TeamInviteModeEnum.Manager else TeamInviteModeEnum.All
            }
        }

        binding.scTeamUpdateMode.setOnCheckedChangeListener { _, isChecked ->
            teamViewModel.apply {
                TeamFieldEnum.TeamUpdateMode set if (isChecked) TeamUpdateModeEnum.Manager else TeamUpdateModeEnum.All
            }
        }

        binding.scAllMute.setOnCheckedChangeListener { _, isChecked ->
            teamViewModel.apply {
                TeamFieldEnum.AllMute set if (isChecked) TeamAllMuteModeEnum.MuteALL else TeamAllMuteModeEnum.Cancel
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            teamViewModel.observeCreateTeamState.collectLatest { viewState ->
                viewState.apply {
                    binding.proLoading.show(running())
                    binding.fabQuit.show(pause() || stop())
                    binding.qplSetting.show(unknown())
                    binding.groupTip.show(!unknown())
                    binding.ivCreate.setImageResource(if (running()) R.drawable.ic_pause else R.drawable.ic_play)
                    when (status) {
                        BatchStatus.UNKNOWN -> {
                            updateView(this)
                        }
                        BatchStatus.RUNNING -> {
                            if (message.isNotBlank()) {
                                tipAdapter.addData("序号[${if (message.contains("成功")) index else index + 1}] 「${fields[TeamFieldEnum.Name]}」 -> $message")
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun updateView(createTeamViewState: CreateTeamViewState) {
        createTeamViewState.apply {
            binding.tvTeamNameValue.text = fields[TeamFieldEnum.Name].toString()

            binding.tvTeamSumValue.text = "$limit"

            binding.tvVerifyTypeValue.text =
                (fields[TeamFieldEnum.VerifyType] as VerifyTypeEnum).message()

            binding.tvIntroduceValue.apply {
                val introduce = fields[TeamFieldEnum.Introduce]?.toString()
                show(!introduce.isNullOrEmpty())
                text = introduce
            }

            binding.tvAnnouncementValue.apply {
                val announcement = fields[TeamFieldEnum.Announcement]?.toString()
                show(!announcement.isNullOrEmpty())
                text = announcement
            }

            binding.scBeInviteMode.isChecked =
                fields[TeamFieldEnum.BeInviteMode] == TeamBeInviteModeEnum.NoAuth

            binding.scInviteMode.apply {
                isChecked =
                    fields[TeamFieldEnum.InviteMode] == TeamInviteModeEnum.Manager
                binding.tvInviteModeValue.text = if (isChecked) "仅管理" else "任何人"
            }

            binding.scTeamUpdateMode.apply {
                isChecked =
                    fields[TeamFieldEnum.TeamUpdateMode] == TeamUpdateModeEnum.Manager
                binding.tvTeamUpdateModeValue.text = if (isChecked) "仅管理" else "任何人"
            }

            binding.scAllMute.isChecked =
                fields[TeamFieldEnum.AllMute] == TeamAllMuteModeEnum.MuteALL

        }

    }
}