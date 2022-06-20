package com.baimsg.chat.adapter

import com.baimsg.chat.databinding.ItemFriendBinding
import com.baimsg.chat.util.extensions.dp2px
import com.baimsg.data.model.entities.NIMUserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.module.LoadMoreModule

class FriendAdapter : BaseBindingAdapter<ItemFriendBinding, NIMUserInfo>(), LoadMoreModule {
    override fun convert(holder: VBViewHolder<ItemFriendBinding>, item: NIMUserInfo) {
        holder.vb.apply {
            Glide.with(ivAvatar).load(item.avatar).apply(
                RequestOptions()
                    .transform(
                        CenterCrop(), RoundedCorners(context.dp2px(88.0f).toInt())
                    )
            ).into(ivAvatar)
            tvName.text = item.name
        }
    }
}