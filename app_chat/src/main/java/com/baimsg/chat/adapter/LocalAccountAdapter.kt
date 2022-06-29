package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemLocalAccountBinding
import com.baimsg.data.model.entities.NIMLoginRecord

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalAccountAdapter :
    BaseBindingAdapter<ItemLocalAccountBinding, NIMLoginRecord>() {
    override fun convert(holder: VBViewHolder<ItemLocalAccountBinding>, item: NIMLoginRecord) {
        holder.vb.apply {
            tvAccountValue.text = item.account
            tvTokenValue.text = item.token
        }
    }
}