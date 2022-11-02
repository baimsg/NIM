package com.baimsg.chat.fragment.login


import android.annotation.SuppressLint
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentLoginBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
import com.netease.nimlib.sdk.auth.ClientType
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

    private val clientTypes by lazy {
        listOf(
            "Android",
            "ios",
            "windows",
            "mac",
            "网页",
            "默认"
        )
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        updateView()

        binding.tvClientType.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.client_type)
                listItemsSingleChoice(
                    items = clientTypes,
                    initialSelection = clientTypes.indexOf(Constant.LOGIN_CLIENT_TYPE.toName())
                ) { _, index, _ ->
                    KvUtils.put(
                        Constant.KEY_LOGIN_CLIENT_TYPE, when (index) {
                            0 -> ClientType.Android
                            1 -> ClientType.iOS
                            2 -> ClientType.Windows
                            3 -> ClientType.MAC
                            4 -> ClientType.Web
                            else -> ClientType.UNKNOW
                        }
                    )
                    updateView()
                }
                negativeButton { }
                positiveButton { }
            }
        }

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

    private fun updateView() {
        binding.tvClientTypeValue.text = Constant.LOGIN_CLIENT_TYPE.toName()
    }

    private fun Int.toName(): String {
        return when (this) {
            ClientType.Android -> "Android"
            ClientType.iOS -> "ios"
            ClientType.Windows -> "windows"
            ClientType.MAC -> "mac"
            ClientType.Web -> "网页"
            else -> "默认"
        }
    }

    override fun onPause() {
        binding.editAccount.hideKeyboard()
        super.onPause()
    }

}

