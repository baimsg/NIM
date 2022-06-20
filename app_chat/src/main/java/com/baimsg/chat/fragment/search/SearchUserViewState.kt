package com.baimsg.chat.fragment.search

data class SearchUserViewState(
    val startValue: Long, //开始值
    val currentValue: Long, //当前值
    val executing: Boolean,

) {
}