package com.baimsg.chat.adapter

import com.baimsg.chat.bean.NIMUserInfo
import com.baimsg.chat.databinding.ItemFriendBinding
import com.baimsg.chat.util.extensions.dp2px
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class FriendAdapter : BaseBindingAdapter<ItemFriendBinding, NIMUserInfo>() {
    override fun convert(holder: VBViewHolder<ItemFriendBinding>, item: NIMUserInfo) {
        holder.vb.apply {
            Glide.with(ivAvatar).load(item.avatar).apply(
                RequestOptions()
                    .transform(
                        CenterCrop(), RoundedCorners(context.dp2px(4.0f).toInt())
                    )
            ).into(ivAvatar)
            tvName.text = item.name
        }
    }


}