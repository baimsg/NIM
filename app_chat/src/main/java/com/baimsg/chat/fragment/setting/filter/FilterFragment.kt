package com.baimsg.chat.fragment.setting.filter

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.FilterAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FooterFilterBinding
import com.baimsg.chat.databinding.FragmentFilterBinding
import com.baimsg.data.model.DEFAULT_JSON_FORMAT
import com.chad.library.adapter.base.animation.AlphaInAnimation
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class FilterFragment : BaseFragment<FragmentFilterBinding>(R.layout.fragment_filter) {

    private val filters by lazy {
        val data = Constant.ADD_FILTER
        if (data.isNotBlank()) DEFAULT_JSON_FORMAT.decodeFromString(
            ListSerializer(String.serializer()),
            data
        ).toMutableList()
        else emptyList<String>().toMutableList()
    }

    private val filterAdapter by lazy {
        FilterAdapter()
    }

    override fun initView() {

        filterAdapter.onRemove = { position ->
            filters.removeAt(position)
            filterAdapter.removeAt(position)
            filterAdapter.notifyItemChanged(position - 1)
            update()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                filterAdapter.setList(filters)
                filterAdapter.notifyDataSetChanged()
                isRefreshing = false
                update()
            }
        }

        binding.ryContent.apply {
            filterAdapter.animationEnable = true
            filterAdapter.adapterAnimation = AlphaInAnimation()

            val footerView = View.inflate(requireContext(), R.layout.footer_filter, null)
            FooterFilterBinding.bind(footerView).apply {
                filterAdapter.setFooterView(footerView)
                tvAdd.setOnClickListener {
                    MaterialDialog(requireContext()).show {
                        input(hint = "请输入过滤关键词") { _, charSequence ->
                            filterAdapter.addData(charSequence.toString())
                            filterAdapter.notifyItemChanged(filters.size - 1)
                            filters.add(charSequence.toString())
                            update()
                        }
                    }
                }
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
            adapter = filterAdapter
        }
    }

    private fun update() {
        binding.tvTitle.text = "过滤词(${filters.size})"
    }
}