package com.baimsg.chat.fragment.home

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentHomeBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showShort
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val teams = mutableListOf<Team>()

    private val members = mutableListOf<TeamMember>()

    private val openLogin by lazy {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    override fun initView() {
        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
                if (status.wontAutoLogin()) openLogin
            }
        }

        binding.btnLogin.setOnClickListener {
            val account = binding.editAccount.text
            val token = binding.editToken.text
            if (account.isNullOrBlank() || token.isNullOrBlank()) {
                showShort("参数不可以为空")
                return@setOnClickListener
            }
            val info =
                LoginInfo(account.toString(), token.toString())
            val callback: RequestCallback<LoginInfo> = object : RequestCallback<LoginInfo> {
                override fun onSuccess(param: LoginInfo) {
                    showShort("登录成功")
                }

                override fun onFailed(code: Int) {
                    if (code == 302) {
                        showShort("账号密码错误")
                    } else {
                        showShort("未知错误：$code")
                    }
                }

                override fun onException(exception: Throwable) {
                    binding.tvStatus.text = exception.message
                }
            }
            NIMClient.getService(AuthService::class.java).login(info).setCallback(callback)
        }

        binding.btnGroupList.setOnClickListener {
            NIMClient.getService(TeamService::class.java).queryTeamList()
                .setCallback(object : RequestCallback<List<Team>> {
                    override fun onSuccess(list: List<Team>?) {
                        list?.let {
                            teams.addAll(it)
                            binding.tvStatus.text = "${it.size}个群"
                        }
                    }

                    override fun onFailed(code: Int) {
                        showShort("未知错误：$code")
                    }

                    override fun onException(e: Throwable?) {
                        binding.tvStatus.text = e?.message
                    }
                })
        }

        binding.btnMemberList.setOnClickListener {
            if (teams.isEmpty()) {
                showShort("没有找到群哦！")
                return@setOnClickListener
            }
            NIMClient.getService(TeamService::class.java).queryMemberList(teams[0].id)
                .setCallback(object : RequestCallback<List<TeamMember>> {
                    override fun onSuccess(list: List<TeamMember>?) {
                        if (list != null) {
                            members.addAll(list)
                            binding.tvStatus.text =
                                "${list.size}个成员\n${list[0].account}-${list[0].tid}"
                        }
                    }

                    override fun onFailed(code: Int) {
                        showShort("未知错误：$code")
                    }

                    override fun onException(e: Throwable?) {
                        binding.tvStatus.text = e?.message
                    }
                })
        }

        binding.btnTest.setOnClickListener {
            if (members.isEmpty()) {
                showShort("没有找好友哦！")
                return@setOnClickListener
            }
            val verifyType = VerifyType.DIRECT_ADD
//            members.forEachIndexed { index, teamMember ->
            val teamMember = members[1]
            binding.tvStatus.text = "正在添加${teamMember.type.name}"
            NIMClient.getService(FriendService::class.java)
                .addFriend(AddFriendData(teamMember.account, verifyType))
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        binding.tvStatus.text = "${teamMember.type.name}添加成功"
                    }

                    override fun onFailed(code: Int) {
                        binding.tvStatus.text =
                            "${teamMember.type.name}添加失败-$code"
                    }

                    override fun onException(e: Throwable?) {
                        binding.tvStatus.text = e?.message
                    }
                })
//            }
        }


    }


}