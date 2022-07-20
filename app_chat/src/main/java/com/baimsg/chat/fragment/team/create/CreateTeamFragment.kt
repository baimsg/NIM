package com.baimsg.chat.fragment.team.create

import android.text.InputType
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentTeamCreateBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.fragment.team.TeamViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import com.baimsg.chat.util.extensions.showWarning
import com.netease.nimlib.sdk.team.constant.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.io.Serializable
import java.lang.StringBuilder

/**
 * Create by Baimsg on 2022/7/19
 *
 **/
@AndroidEntryPoint
class CreateTeamFragment : BaseFragment<FragmentTeamCreateBinding>(R.layout.fragment_team_create) {

    private val teamViewModel by viewModels<TeamViewModel>()

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val loadDialog by lazy {
        MaterialDialog(requireContext()).cancelable(false)
            .cancelOnTouchOutside(false)
            .customView(R.layout.dialog_loading)
    }

    private val fields: MutableMap<TeamFieldEnum, Serializable?> by lazy {
        mutableMapOf(
            TeamFieldEnum.Announcement to "",
            TeamFieldEnum.Introduce to "",
            TeamFieldEnum.Name to "群聊",
            TeamFieldEnum.VerifyType to VerifyTypeEnum.Apply,
            TeamFieldEnum.BeInviteMode to TeamBeInviteModeEnum.NoAuth,
            TeamFieldEnum.InviteMode to TeamInviteModeEnum.Manager,
            TeamFieldEnum.TeamUpdateMode to TeamUpdateModeEnum.Manager,
            TeamFieldEnum.AllMute to TeamAllMuteModeEnum.MuteALL,
        )
    }

    private var teamSum = 1

    private val sb by lazy {
        StringBuilder()
    }

    override fun initView() {
        updateView()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvCreate.setOnClickListener {
            sb.delete(0, sb.length)
            loadDialog.show()
            teamViewModel.batchCreateTeam(
                teamSum,
                fields = fields,
                type = TeamTypeEnum.Advanced,
                postscript = null,
                members = listOf(loginViewModel.currentAccount)
            )
        }

        binding.vIntroduce.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.introduce)
                    input(hint = "请输入群简介", allowEmpty = true) { _, sequence ->
                        fields[TeamFieldEnum.Introduce] = sequence.toString()
                        updateView()
                    }
                }
        }

        binding.vAnnouncement.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.announcement)
                    input(hint = "请输入群公告", allowEmpty = true) { _, sequence ->
                        fields[TeamFieldEnum.Announcement] = sequence.toString()
                        updateView()
                    }
                }
        }

        binding.tvTeamName.setOnClickListener {
            MaterialDialog(requireContext())
                .cancelOnTouchOutside(false)
                .show {
                    title(R.string.team_name)
                    input(hint = "请输入群聊名称") { _, sequence ->
                        fields[TeamFieldEnum.Name] = sequence.toString()
                        updateView()
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
                        teamSum = sequence.toString().toInt()
                        updateView()
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
                        fields[TeamFieldEnum.VerifyType] = when (index) {
                            0 -> VerifyTypeEnum.Apply
                            1 -> VerifyTypeEnum.Free
                            else -> VerifyTypeEnum.Private
                        }
                        updateView()
                    }
                    negativeButton()
                }
        }

        binding.scBeInviteMode.setOnCheckedChangeListener { _, isChecked ->
            fields[TeamFieldEnum.BeInviteMode] =
                if (isChecked) TeamBeInviteModeEnum.NoAuth else TeamBeInviteModeEnum.NeedAuth
        }

        binding.scInviteMode.setOnCheckedChangeListener { _, isChecked ->
            fields[TeamFieldEnum.InviteMode] =
                if (isChecked) TeamInviteModeEnum.Manager else TeamInviteModeEnum.All
            updateView()
        }

        binding.scTeamUpdateMode.setOnCheckedChangeListener { _, isChecked ->
            fields[TeamFieldEnum.TeamUpdateMode] =
                if (isChecked) TeamUpdateModeEnum.Manager else TeamUpdateModeEnum.All
            updateView()
        }

        binding.scAllMute.setOnCheckedChangeListener { _, isChecked ->
            fields[TeamFieldEnum.AllMute] =
                if (isChecked) TeamAllMuteModeEnum.MuteALL else TeamAllMuteModeEnum.Cancel
        }


    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            teamViewModel.observeCreateTeamState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.EMPTY -> {
                        loadDialog.dismiss()
                        MaterialDialog(requireContext()).show {
                            message(text = sb.toString())
                            positiveButton()
                        }
                    }
                    ExecutionStatus.SUCCESS -> {
                        sb.append("「" + it.name + "」创建成功\n")
                    }
                    ExecutionStatus.FAIL -> {
                        sb.append("「" + it.name + "」${it.message}\n")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateView() {

        binding.tvTeamNameValue.text = fields[TeamFieldEnum.Name].toString()

        binding.tvTeamSumValue.text = "$teamSum"

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