package com.baimsg.chat.adapter

import com.baimsg.chat.R
import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemTaskAccountBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMTaskAccount

class TaskAccountAdapter(val a: Int = R.layout.item_task_account) :
    BaseBindingAdapter<ItemTaskAccountBinding, NIMTaskAccount>() {
    override fun convert(holder: VBViewHolder<ItemTaskAccountBinding>, item: NIMTaskAccount) {
        holder.vb.apply {
            ivAvatar.loadImage(item.avatar)
            tvName.text = item.name
            tvAccount.text = "账号:${item.account}"
        }
    }
}