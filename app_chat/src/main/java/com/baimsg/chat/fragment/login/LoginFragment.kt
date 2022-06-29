package com.baimsg.chat.fragment.login

import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            lifecycleScope.launch {
                show(loginViewModel.appKeys().isNotEmpty())
            }
            setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_localKeyFragment)
            }
        }

        binding.editAppKey.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                val appKey = loginViewModel.getLoginInfo().appKey
                setText(appKey)
                showKeyboard(true)
                setSelection(appKey.length)
            }
            addTextChangedListener {
                loginViewModel.upDateAppKey(it.toString())
            }
        }

        binding.editAccount.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                setText(loginViewModel.getLoginInfo().account)
            }
            addTextChangedListener {
                loginViewModel.updateAccount(it.toString())
            }
        }

        binding.editToken.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                setText(loginViewModel.getLoginInfo().token)
            }
            addTextChangedListener {
                loginViewModel.updateToken(it.toString())
            }
        }

        binding.btnLogin.setOnClickListener {
            it?.hideKeyboard()
            loginViewModel.login()
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
            loginViewModel.statusCode.collectLatest { status ->
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
                    else -> {}
                }
            }
        }
    }

    override fun onPause() {
        binding.editAccount.hideKeyboard()
        super.onPause()
    }

}