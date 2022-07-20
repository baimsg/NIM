package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemAccountSmallBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMUserInfo
import com.chad.library.adapter.base.module.LoadMoreModule

/**
 * 好友列表适配器
 */
class AccountSmallAdapter : BaseBindingAdapter<ItemAccountSmallBinding, NIMUserInfo>(),
    LoadMoreModule {
    override fun convert(holder: VBViewHolder<ItemAccountSmallBinding>, item: NIMUserInfo) {
        holder.vb.apply {
            ivAvatar.loadImage(item.avatar)
            tvName.text = item.name
        }
    }
}