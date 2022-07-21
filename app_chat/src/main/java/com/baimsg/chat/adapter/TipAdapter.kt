package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemTipBinding

/**
 * Create by Baimsg on 2022/7/21
 *
 **/
class TipAdapter : BaseBindingAdapter<ItemTipBinding, String>() {
    override fun convert(holder: VBViewHolder<ItemTipBinding>, item: String) {
        holder.vb.apply {
            tvTip.text = item
        }
    }
}