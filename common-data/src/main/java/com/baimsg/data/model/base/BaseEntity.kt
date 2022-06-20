package com.baimsg.data.model.base

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
interface BaseEntity {


    /**
     * 获取主键
     */
    fun getIdentifier(): String
}
