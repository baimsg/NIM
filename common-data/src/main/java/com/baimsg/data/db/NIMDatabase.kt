package com.baimsg.data.db

import com.baimsg.data.db.daos.LoginRecordDao
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.db.daos.UserInfoDao

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
interface NIMDatabase {
    fun crateUserInfoDao(): UserInfoDao

    fun crateLoginRecordDao(): LoginRecordDao

    fun crateTaskAccountDao(): TaskAccountDao
}