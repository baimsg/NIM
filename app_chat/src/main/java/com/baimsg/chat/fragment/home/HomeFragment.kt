package com.baimsg.chat.fragment.home

import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmnetHomeBinding
import com.baimsg.chat.util.extensions.showShort
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.friend.constant.VerifyType
import com.netease.nimlib.sdk.friend.model.AddFriendData
import com.netease.nimlib.sdk.team.TeamService
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import java.lang.StringBuilder


class HomeFragment : BaseFragment<FragmnetHomeBinding>(R.layout.fragmnet_home) {
    private val teams = mutableListOf<Team>()

    private val members = mutableListOf<TeamMember>()

    override fun initView() {
        NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus({ status ->
            binding.tvStatus.text = when (status) {
                StatusCode.LOGINING -> "正在登录中"
                StatusCode.SYNCING -> "正在同步数据"
                StatusCode.LOGINED -> "已成功登录"
                StatusCode.UNLOGIN -> "未登录/登录失败"
                StatusCode.NET_BROKEN -> "网络连接已断开"
                StatusCode.CONNECTING -> "正在连接服务器"
                StatusCode.KICKOUT -> "被其他端的登录踢掉"
                StatusCode.KICK_BY_OTHER_CLIENT -> "被同时在线的其他端主动踢掉"
                StatusCode.FORBIDDEN -> "被服务器禁止登录"
                StatusCode.VER_ERROR -> "客户端版本错误"
                StatusCode.PWD_ERROR -> "用户名或密码错误"
                StatusCode.DATA_UPGRADE -> "数据库需要迁移到加密状态"
                else -> "未定义"
            }
        }, true)

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