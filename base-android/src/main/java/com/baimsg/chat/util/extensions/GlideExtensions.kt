package com.baimsg.chat.util.extensions

import android.widget.ImageView
import com.baimsg.base.android.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(
    url: String?,
    round: Float = 12f,
    fallback: Int = R.drawable.ic_avatar_default,
    error: Int = R.drawable.ic_picture_split
) {
    Glide.with(this).load(url)
        .fallback(fallback)
        .error(error).apply(
            RequestOptions()
                .transform(
                    CenterCrop(),
                    RoundedCorners(context.dp2px(round).toInt())
                )
        ).into(this)
}