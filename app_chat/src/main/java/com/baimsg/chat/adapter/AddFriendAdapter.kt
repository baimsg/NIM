package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemAddFriendBinding
import com.baimsg.chat.util.extensions.dp2px
import com.baimsg.data.model.entities.NIMUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * 添加好友适配器
 */
class AddFriendAdapter : BaseBindingAdapter<ItemAddFriendBinding, NIMUserInfo>() {
    override fun convert(holder: VBViewHolder<ItemAddFriendBinding>, item: NIMUserInfo) {
        holder.vb.apply {
            Glide.with(ivAvatar).load(item.avatar).apply(
                RequestOptions()
                    .transform(
                        CenterCrop(), RoundedCorners(context.dp2px(12.0f).toInt())
                    )
            ).into(ivAvatar)
            tvName.text = item.name
            tvAccount.text = "账号:${item.account}"
        }
    }
}