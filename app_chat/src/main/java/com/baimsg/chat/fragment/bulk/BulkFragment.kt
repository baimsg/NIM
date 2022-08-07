package com.baimsg.chat.fragment.bulk

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.input.input
import com.baimsg.chat.R
import com.baimsg.chat.adapter.TipAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentBulkBinding
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/7/22
 *
 **/
@AndroidEntryPoint
class BulkFragment : BaseFragment<FragmentBulkBinding>(R.layout.fragment_bulk) {

    private val bulkViewModel by viewModels<BulkViewModel>()

    private val tipAdapter by lazy {
        TipAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        MaterialDialog(requireContext(), BottomSheet())
            .cancelable(false).cancelOnTouchOutside(false).show {
                title(text = "群发消息")
                input(
                    hint = "请输入群发内容",
                    inputType = InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
                ) { _, charSequence ->
                    bulkViewModel.setMessage(charSequence.toString())
                }
                negativeButton { findNavController().navigateUp() }
                positiveButton()
            }

        binding.ryContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = tipAdapter
        }

        binding.ivStart.setOnClickListener {
            bulkViewModel.startSend()
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            bulkViewModel.observeViewState.collectLatest { viewState ->
                viewState.apply {
                    binding.proLoading.show(running())
                    binding.ivStart.setImageResource(if (running()) R.drawable.ic_pause else R.drawable.ic_play)
                    when (status) {
                        BatchStatus.RUNNING -> {
                            if (tip.isNotBlank()) {
                                tipAdapter.addData("${bulkData.name}「${bulkData.id}」 -> $tip")
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}