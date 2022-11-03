package com.baimsg.chat.adapter

import android.annotation.SuppressLint
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemLocalKeyBinding

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalKeyAdapter : BaseBindingAdapter<ItemLocalKeyBinding, String>() {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: VBViewHolder<ItemLocalKeyBinding>, item: String) {
        holder.vb.apply {
            tvName.text = "app -> ${Constant.appKeyRemark(item)}"
            tvAppKey.text = item
        }
    }
}