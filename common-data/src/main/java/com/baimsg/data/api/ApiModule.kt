package com.baimsg.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    @Provides
    @Singleton
    fun providesBaseEndpoints(@Named("base") retrofit: Retrofit): BaseEndpoints =
        retrofit.create(BaseEndpoints::class.java)

}