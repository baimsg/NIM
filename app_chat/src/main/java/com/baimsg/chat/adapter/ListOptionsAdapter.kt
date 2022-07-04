package com.baimsg.chat.adapter

import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemListOptionsBinding
import com.baimsg.chat.util.extensions.hide
import com.baimsg.chat.util.extensions.show
import com.baimsg.data.model.ItemListOptions

/**
 * Create by Baimsg on 2022/7/4
 *
 **/
class ListOptionsAdapter : BaseBindingAdapter<ItemListOptionsBinding, ItemListOptions>() {
    override fun convert(holder: VBViewHolder<ItemListOptionsBinding>, item: ItemListOptions) {
        holder.vb.apply {
            ivIcon.apply {
                if (item.icon == -1) {
                    hide()
                } else {
                    show()
                    setImageResource(item.icon)
                }
            }
            tvName.text = item.name
        }
    }
}