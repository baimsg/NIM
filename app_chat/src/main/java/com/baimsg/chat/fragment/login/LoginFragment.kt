package com.baimsg.chat.fragment.login

import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
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
        //拦截返回事件
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        repeatOnLifecycleStarted {
            loginViewModel.statusCode.collectLatest {

            }
        }

    }

}