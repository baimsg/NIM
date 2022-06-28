package com.baimsg.chat.di

import com.baimsg.base.util.inititializer.AppInitializers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun appInitializers(sdkAppInitializer: SDKAppInitializer): AppInitializers =
        AppInitializers(sdkAppInitializer)

}