package com.baimsg.chat.fragment.setting.filter

import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FilterAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentFilterBinding
import com.baimsg.data.model.DEFAULT_JSON_FORMAT
import com.chad.library.adapter.base.animation.AlphaInAnimation
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class FilterFragment : BaseFragment<FragmentFilterBinding>(R.layout.fragment_filter) {

    private val filters by lazy {
        Constant.ADD_FILTERS.toMutableList()
    }

    private val filterAdapter by lazy {
        FilterAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        filterAdapter.setList(filters)
        update()

        filterAdapter.onRemove = { position ->
            if (position in filters.indices) {
                filters.removeAt(position)
                filterAdapter.removeAt(position)
                update()
            }
        }

        binding.tvAdd.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(hint = "请输入过滤关键词") { _, charSequence ->
                    filterAdapter.addData(charSequence.toString())
                    filters.add(charSequence.toString())
                    update()
                }
            }
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                filterAdapter.setList(filters)
                isRefreshing = false
                update()
            }
        }

        binding.ryContent.apply {
            filterAdapter.animationEnable = true
            filterAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            adapter = filterAdapter
        }
    }

    private fun update() {
        binding.tvTitle.text = "过滤词(${filters.size})"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        KvUtils.put(
            Constant.KEY_ADD_FILTER,
            DEFAULT_JSON_FORMAT.encodeToString(ListSerializer(String.serializer()), filters)
        )
    }
}