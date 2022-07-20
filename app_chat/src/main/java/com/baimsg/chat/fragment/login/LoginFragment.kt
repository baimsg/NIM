package com.baimsg.chat.fragment.login

import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val args by navArgs<LoginFragmentArgs>()

    override fun initView() {

        binding.ivBack.apply {
            show(!args.hard)
            setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.tvLocalAccount.apply {
            show(loginViewModel.allAppKeys.isNotEmpty())
            setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_localKeyFragment)
            }
        }

        binding.editAppKey.apply {
            setText(loginViewModel.currentAppKey)
            showKeyboard(true)
            setSelection(loginViewModel.currentAppKey.length)
        }

        binding.editAccount.setText(loginViewModel.currentAccount)

        binding.editToken.setText(loginViewModel.currentToken)

        binding.btnLogin.setOnClickListener {
            it?.hideKeyboard()
            loginViewModel.login(
                appKey = binding.editAppKey.text.toString(),
                account = binding.editAccount.text.toString(),
                token = binding.editToken.text.toString()
            )
        }

        if (args.hard) {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                requireActivity().finish()
            }
        }
    }

    override fun initLiveData() {
        /**
         * 监听登录状态
         */
        repeatOnLifecycleStarted {
            loginViewModel.observerStatusCode.collectLatest { status ->
                binding.tvStatus.text = status.message()
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.observeViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.SUCCESS -> if (args.hard) findNavController().navigateUp() else
                        findNavController().navigate(
                            R.id.action_loginFragment_to_homeFragment
                        )
                    ExecutionStatus.FAIL -> showError(it.message)
                    else -> Unit
                }
            }
        }
    }

    override fun onPause() {
        binding.editAccount.hideKeyboard()
        super.onPause()
    }

}