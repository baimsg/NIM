package com.baimsg.data.db.daos

import com.baimsg.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
@InstallIn(SingletonComponent::class)
@Module
class DaoModule {

    @Provides
    fun crateUserInfoDao(app: AppDatabase) = app.crateUserInfoDao()

    @Provides
    fun crateLoginRecordDao(app: AppDatabase) = app.crateLoginRecordDao()

    @Provides
    fun crateTaskAccountDao(app: AppDatabase) = app.crateTaskAccountDao()

}