package com.baimsg.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
object DatabaseMigrations {
    const val DB_VERSION = 1

    val MIGRATIONS: Array<Migration>
        get() = arrayOf(
            migration1to2()
        )

    /**
     * 升级数据库2
     */
    private fun migration1to2(): Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            }
    }


}