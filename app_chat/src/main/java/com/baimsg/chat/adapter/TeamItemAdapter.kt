package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemTeamBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMTeam

class TeamItemAdapter() : BaseBindingAdapter<ItemTeamBinding, NIMTeam>() {
    override fun convert(holder: VBViewHolder<ItemTeamBinding>, item: NIMTeam) {
        holder.vb.apply {
            ivAvatar.loadImage(item.icon)
            tvName.text = item.name
        }

    }
}