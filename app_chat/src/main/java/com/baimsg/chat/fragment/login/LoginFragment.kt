package com.baimsg.chat.fragment.login

import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.util.extensions.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val args by navArgs<LoginFragmentArgs>()

    private val loginViewModel by activityViewModels<LoginViewModel>()

    //拦截返回事件
    private val tough by lazy {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    private var account: String = ""

    private var token: String = ""


    override fun initView() {

//        binding.tvAppKey.text = Constant.APP_KEY.run { ifBlank { "点击设置appKey" } }

        binding.editAccount.apply {
//            setText(Constant.ACCOUNT)
            showKeyboard(true)
            addTextChangedListener {
                account = it.toString()
            }
        }

        binding.editToken.apply {
//            setText(Constant.TOKEN)
            addTextChangedListener {
                token = it.toString()
            }
        }

        binding.tvAppKey.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_appKeyFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (account.isBlank() || token.isBlank()) {
                showShort("账号或密码为空")
                return@setOnClickListener
            }
            val info =
                LoginInfo(account, token, "")
            val callback: RequestCallback<LoginInfo> = object : RequestCallback<LoginInfo> {
                override fun onSuccess(param: LoginInfo) {
                    findNavController().navigateUp()
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

        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
            }
        }

        if (args.tough) tough
    }

    override fun onPause() {
        binding.editAccount.hideKeyboard()
        super.onPause()
    }

}