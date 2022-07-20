package com.baimsg.chat.adapter

import com.baimsg.base.util.extensions.toTime
import com.baimsg.chat.adapter.base.BaseBindingAdapter
import com.baimsg.chat.adapter.base.VBViewHolder
import com.baimsg.chat.databinding.ItemLocalAccountBinding
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.data.model.entities.NIMLoginRecord
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Create by Baimsg on 2022/6/29
 *
 **/
class LocalAccountAdapter :
    BaseBindingAdapter<ItemLocalAccountBinding, NIMLoginRecord>() {
    override fun convert(holder: VBViewHolder<ItemLocalAccountBinding>, item: NIMLoginRecord) {
        holder.vb.apply {
            tvLastLogin.text = "登录于: ${item.loginTime.toTime()}"
            tvName.text = item.account
            tvAccount.text = item.token

            NIMClient.getService(UserService::class.java).fetchUserInfo(listOf(item.account))
                .setCallback(object : RequestCallback<List<NimUserInfo>> {
                    override fun onSuccess(infos: List<NimUserInfo>?) {
                        infos?.firstOrNull()?.apply {
                            ivAvatar.loadImage(avatar)
                            tvName.text = name
                            tvAccount.text = account
                        }
                    }

                    override fun onFailed(p0: Int) = Unit
                    override fun onException(p0: Throwable?) = Unit
                })
        }
    }

}