package com.baimsg.chat.view

import java.io.Serializable

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
data class TurntableItem(
    val text: String,
    val weight: Int = 1
) : Serializable