package com.baimsg.chat.fragment.login

import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.util.KeyboardHelper
import com.baimsg.chat.util.extensions.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    override fun initView() {

        binding.tvAppKey.text = KvUtils.getString(Constant.KEY_APP_KEY, Constant.DEFAULT_APP_KEY)

        binding.editAccount.apply {
            setText(
                KvUtils.getString(
                    Constant.KEY_ACCOUNT,
                    Constant.DEFAULT_ACCOUNT
                )
            )
            showKeyboard(true)
        }

        binding.editToken.setText(
            KvUtils.getString(
                Constant.KEY_TOKEN,
                Constant.DEFAULT_TOKEN
            )
        )

        binding.tvAppKey.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_appKeyFragment)
        }

        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
            }
        }

        //拦截返回事件
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest {

            }
        }

    }

    override fun onPause() {
        binding.editAccount.hideKeyboard()
        super.onPause()
    }

    override fun onResume() {
        requireActivity().setStatusBarDarkMode()
        super.onResume()
    }
}