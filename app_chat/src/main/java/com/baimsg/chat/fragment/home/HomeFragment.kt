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


class HomeFragment : BaseFragment<FragmnetHomeBinding>(R.layout.fragmnet_home) {
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
            val account = binding.tvAccount.text
            val token = binding.tvToken.text
            if (account.isNullOrBlank() || token.isNullOrBlank()) {
                showShort("参数不可以为空")
                return@setOnClickListener
            }
            val info =
                LoginInfo(account.toString(), token.toString(), "8d3e6faf3fb7ce8c678b2b2900cbb6c9")
            val callback: RequestCallback<LoginInfo> = object : RequestCallback<LoginInfo> {
                override fun onSuccess(param: LoginInfo) {
                    showShort("登录成功")
                }

                override fun onFailed(code: Int) {
                    if (code == 302) {
                        showShort("账号密码错误")
                    } else {
                        showShort("未知错误：$code-${info.appKey}")
                    }
                }

                override fun onException(exception: Throwable) {
                    binding.tvStatus.text = exception.message
                }
            }
            NIMClient.getService(AuthService::class.java).login(info).setCallback(callback)
        }
    }

}