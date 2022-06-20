package com.baimsg.chat.adapter

import com.baimsg.chat.databinding.ItemFilterBinding

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