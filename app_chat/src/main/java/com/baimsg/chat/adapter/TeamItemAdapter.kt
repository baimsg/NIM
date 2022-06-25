package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemTeamBinding
import com.baimsg.chat.util.extensions.dp2px
import com.baimsg.data.model.entities.NIMTeam
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TeamItemAdapter() : BaseBindingAdapter<ItemTeamBinding, NIMTeam>() {
    override fun convert(holder: VBViewHolder<ItemTeamBinding>, item: NIMTeam) {
        holder.vb.apply {
            Glide.with(ivAvatar).load(item.icon).apply(
                RequestOptions()
                    .transform(
                        CenterCrop(), RoundedCorners(context.dp2px(88.0f).toInt())
                    )
            ).into(ivAvatar)
            tvName.text = item.name
        }

    }
}