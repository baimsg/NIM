package com.baimsg.data.model

interface Incomplete

sealed class Async<out T>(val complete: Boolean, val shouldLoad: Boolean) {
    open operator fun invoke(): T? = null

    val isLoading get() = this is Loading
}

/**
 * 加载ing
 */
class Loading<out T> : Async<T>(complete = false, shouldLoad = false), Incomplete {
    override fun equals(other: Any?) = other is Loading<*>
    override fun hashCode() = "Loading".hashCode()
}

/**
 * 加载失败
 */
data class Fail<out T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true)

/**
 * 加载成功
 */
data class Success<out T>(private val value: T) : Async<T>(complete = true, shouldLoad = false) {
    override operator fun invoke(): T = value

}

/**
 * 未初始化
 */
object Uninitialized : Async<Nothing>(complete = false, shouldLoad = true), Incomplete
