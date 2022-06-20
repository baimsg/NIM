package com.baimsg.data.db

import com.baimsg.data.db.daos.UserInfoDao

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
interface NIMDatabase {
    fun crateUserInfoDao(): UserInfoDao
}