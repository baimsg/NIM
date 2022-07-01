package com.baimsg.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.baimsg.data.db.converter.AppTypeConverters
import com.baimsg.data.db.converter.BaseTypeConverters
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMLoginRecord
import com.baimsg.data.model.entities.NIMTaskAccount
import com.baimsg.data.model.entities.NIMUserInfo

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
@Database(
    version = DatabaseMigrations.DB_VERSION,
    entities = [
        NIMUserInfo::class,
        NIMLoginRecord::class,
        NIMTaskAccount::class
    ],
    exportSchema = true
)
@TypeConverters(
    BaseTypeConverters::class,
    AppTypeConverters::class
)
abstract class AppDatabase : RoomDatabase(), NIMDatabase {
}