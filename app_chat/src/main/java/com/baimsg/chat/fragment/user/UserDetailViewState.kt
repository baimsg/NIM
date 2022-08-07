package com.baimsg.chat.fragment.user

import com.baimsg.data.model.Async
import com.baimsg.data.model.Uninitialized

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
data class UserDetailViewState(
    val data: Async<String>
) {
    companion object {
        val EMPTY = UserDetailViewState(data = Uninitialized)
    }
}