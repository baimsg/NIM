package com.baimsg.data.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.baimsg.data.db.BaseDao
import com.baimsg.data.model.entities.NIMLoginRecord
import kotlinx.coroutines.flow.Flow

/**
 * Create by Baimsg on 2022/6/28
 *
 **/
@Dao
abstract class LoginRecordDao : BaseDao<NIMLoginRecord>() {
    @Transaction
    @Query("DELETE FROM nim_login_record WHERE id=:id")
    abstract override suspend fun deleteById(id: String)

    @Query("DELETE FROM nim_login_record")
    abstract override suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM nim_login_record")
    abstract override fun entries(): List<NIMLoginRecord>

    @Transaction
    @Query("SELECT * FROM nim_login_record")
    abstract override fun observeEntries(): Flow<List<NIMLoginRecord>>

    @Transaction
    @Query("SELECT * FROM nim_login_record LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): List<NIMLoginRecord>

    @Transaction
    @Query("SELECT * FROM nim_login_record LIMIT :count OFFSET :offset")
    abstract override fun observeEntries(count: Int, offset: Int): Flow<List<NIMLoginRecord>>

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id = :id")
    abstract override fun entriesById(id: String): NIMLoginRecord

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id = :id")
    abstract override fun observeEntriesById(id: String): Flow<NIMLoginRecord>

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id IN (:ids)")
    abstract override fun entriesByIds(ids: List<String>): List<NIMLoginRecord>

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id IN (:ids)")
    abstract override fun observeEntriesByIds(ids: List<String>): Flow<List<NIMLoginRecord>>

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id = :id")
    abstract override fun entriesByIdNullable(id: String): NIMLoginRecord?

    @Transaction
    @Query("SELECT * FROM nim_login_record WHERE id = :id")
    abstract override fun observeEntriesByIdNullable(id: String): Flow<NIMLoginRecord?>

    @Query("SELECT COUNT(*) FROM nim_login_record")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM nim_login_record")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM nim_login_record WHERE id=:id")
    abstract override suspend fun countById(id: String): Int

    @Query("SELECT COUNT(*) FROM nim_login_record WHERE id=:id")
    abstract override fun observeCountById(id: String): Flow<Int>

    @Query("SELECT DISTINCT appKey FROM nim_login_record")
    abstract fun appKeys(): List<String>

    @Query("UPDATE nim_login_record SET used = 0")
    abstract fun cancelUsed()

    @Query("SELECT * FROM nim_login_record WHERE used = 1")
    abstract fun used(): List<NIMLoginRecord>
}