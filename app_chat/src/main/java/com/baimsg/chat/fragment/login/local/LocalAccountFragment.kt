package com.baimsg.chat.fragment.login.local

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.baimsg.chat.R
import com.baimsg.chat.adapter.LocalAccountAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.EmptyBaseBinding
import com.baimsg.chat.databinding.FragmentLocalAccountBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showError
import com.baimsg.data.model.entities.NIMLoginRecord
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalAccountFragment :
    BaseFragment<FragmentLocalAccountBinding>(R.layout.fragment_local_account) {

    private val loginViewModel by activityViewModels<LoginViewModel>()

    private val args by navArgs<LocalAccountFragmentArgs>()

    private val localAccountAdapter by lazy {
        LocalAccountAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                lifecycleScope.launch {
                    localAccountAdapter.setList(loginViewModel.accounts(appKey = args.appKey))
                    isRefreshing = false
                }
            }
        }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = localAccountAdapter
            val emptyView = View.inflate(requireContext(), R.layout.empty_base, null)
            EmptyBaseBinding.bind(emptyView).apply {
                localAccountAdapter.setEmptyView(emptyView)
                tvTip.text = "本地账号为空：）"
            }
            lifecycleScope.launch {
                localAccountAdapter.setList(loginViewModel.accounts(appKey = args.appKey))
            }
        }

        localAccountAdapter.setOnItemClickListener { adapter, _, position ->
            loginViewModel.switchAccount(nimLoginRecord = adapter.data[position] as NIMLoginRecord)
        }

        localAccountAdapter.setOnItemLongClickListener { adapter, view, position ->
            val data = (adapter.data[position] as NIMLoginRecord)
            MaterialDialog(requireContext()).show {
                title(text = "删除账号")
                message(text = "您确定要删除 -> ${data.account}吗？")
                negativeButton()
                positiveButton {
                    adapter.removeAt(position)
                    loginViewModel.deleteById("${data.appKey}-${data.account}")
                }
            }
            true
        }
    }

    override fun initLiveData() {
        /**
         * 监听登录状态
         */
        repeatOnLifecycleStarted {
            loginViewModel.observerStatusCode.collectLatest { status ->
                binding.tvTitle.text = status.message()
            }
        }

        repeatOnLifecycleStarted {
            loginViewModel.observeViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.SUCCESS -> findNavController().navigate(R.id.action_localAccountFragment_to_homeFragment)
                    ExecutionStatus.FAIL -> showError(it.message)
                    else -> {}
                }
            }
        }
    }
}