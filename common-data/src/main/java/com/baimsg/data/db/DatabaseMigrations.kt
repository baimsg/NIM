package com.baimsg.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
object DatabaseMigrations {
    const val DB_VERSION = 2

    val MIGRATIONS: Array<Migration>
        get() = arrayOf(
            migration1to2()
        )

    /**
     * 升级数据库2
     */
    private fun migration1to2(): Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `nim_task_account` (`id` TEXT NOT NULL, `appKey` TEXT NOT NULL, `account` TEXT NOT NULL, `name` TEXT NOT NULL, `avatar` TEXT,  `createTime` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }
}