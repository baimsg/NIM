package com.baimsg.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun nimDatabase(context: Context): AppDatabase {
        val builder = Room.databaseBuilder(context, AppDatabase::class.java, "nim_data")
            .addMigrations(*DatabaseMigrations.MIGRATIONS)
            .fallbackToDestructiveMigration()
        return builder.build()
    }
}