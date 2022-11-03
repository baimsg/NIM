package com.baimsg.chat.adapter

import android.annotation.SuppressLint
import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemTaskAccountBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMTaskAccount

class TaskAccountAdapter : BaseBindingAdapter<ItemTaskAccountBinding, NIMTaskAccount>() {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: VBViewHolder<ItemTaskAccountBinding>, item: NIMTaskAccount) {
        holder.vb.apply {
            root.isSelected = item.processed
            ivAvatar.loadImage(item.avatar)
            tvName.text = item.name
            tvAccount.text = "ID:${item.account}"
        }
    }
}