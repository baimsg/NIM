package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemAccountMediumBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * 添加好友适配器
 */
class AccountMediumAdapter : BaseBindingAdapter<ItemAccountMediumBinding, NIMUserInfo>() {
    override fun convert(holder: VBViewHolder<ItemAccountMediumBinding>, item: NIMUserInfo) {
        holder.vb.apply {
            ivAvatar.loadImage(item.avatar)
            tvName.text = item.name
            tvAccount.text = "账号:${item.account}"
        }
    }
}