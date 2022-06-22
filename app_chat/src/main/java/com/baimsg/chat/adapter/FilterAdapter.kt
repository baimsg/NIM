package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemFilterBinding

/**
 * 过滤内容适配器
 */
class FilterAdapter : BaseBindingAdapter<ItemFilterBinding, String>() {
    var onRemove: (Int) -> Unit = {}
    override fun convert(holder: VBViewHolder<ItemFilterBinding>, item: String) {
        holder.vb.apply {
            tvFilter.text = item
            ivRemove.setOnClickListener {
                onRemove(holder.layoutPosition)
            }
        }
    }
}