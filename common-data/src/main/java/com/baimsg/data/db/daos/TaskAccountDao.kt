package com.baimsg.data.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.baimsg.data.db.BaseDao
import com.baimsg.data.model.entities.NIMTaskAccount
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TaskAccountDao : BaseDao<NIMTaskAccount>() {
    @Transaction
    @Query("DELETE FROM nim_task_account WHERE id=:id")
    abstract override suspend fun deleteById(id: String)

    @Query("DELETE FROM nim_task_account")
    abstract override suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM nim_task_account")
    abstract override fun entries(): List<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account")
    abstract override fun observeEntries(): Flow<List<NIMTaskAccount>>

    @Transaction
    @Query("SELECT * FROM nim_task_account LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): List<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account LIMIT :count OFFSET :offset")
    abstract override fun observeEntries(count: Int, offset: Int): Flow<List<NIMTaskAccount>>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id = :id")
    abstract override fun entriesById(id: String): NIMTaskAccount

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id = :id")
    abstract override fun observeEntriesById(id: String): Flow<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE appKey = :appKey ORDER BY account ASC LIMIT :count OFFSET :offset")
    abstract fun entriesByAppKey(appKey: String, count: Int, offset: Int): List<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE appKey = :appKey ORDER BY account ASC")
    abstract fun entriesByAppKey(appKey: String): List<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE appKey = :appKey ORDER BY account ASC")
    abstract fun observeEntriesByAppKey(appKey: String): Flow<List<NIMTaskAccount>>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id IN (:ids)")
    abstract override fun entriesByIds(ids: List<String>): List<NIMTaskAccount>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id IN (:ids)")
    abstract override fun observeEntriesByIds(ids: List<String>): Flow<List<NIMTaskAccount>>

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id = :id")
    abstract override fun entriesByIdNullable(id: String): NIMTaskAccount?

    @Transaction
    @Query("SELECT * FROM nim_task_account WHERE id = :id")
    abstract override fun observeEntriesByIdNullable(id: String): Flow<NIMTaskAccount?>

    @Query("SELECT COUNT(*) FROM nim_task_account")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM nim_task_account")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM nim_task_account WHERE id=:id")
    abstract override suspend fun countById(id: String): Int

    @Query("SELECT COUNT(*) FROM nim_task_account WHERE id=:id")
    abstract override fun observeCountById(id: String): Flow<Int>

}