package com.baimsg.chat.adapter

import com.baimsg.base.util.KvUtils
import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemAppKeyBinding

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class AppKeyAdapter : BaseBindingAdapter<ItemAppKeyBinding, String>() {

    override fun convert(holder: VBViewHolder<ItemAppKeyBinding>, item: String) {
        holder.vb.apply {
            tvName.text = KvUtils.getString(item, "无备注")
            tvAppKey.text = item
        }
    }
}