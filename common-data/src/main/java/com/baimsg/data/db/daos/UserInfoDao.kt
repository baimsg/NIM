package com.baimsg.data.db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.baimsg.data.db.BaseDao
import com.baimsg.data.model.entities.NIMUserInfo
import kotlinx.coroutines.flow.Flow

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
@Dao
abstract class UserInfoDao : BaseDao<NIMUserInfo>() {
    @Transaction
    @Query("DELETE FROM nim_user_info WHERE id=:id")
    abstract override suspend fun deleteById(id: String)

    @Transaction
    @Query("DELETE FROM nim_user_info WHERE appKey=:appKey")
    abstract suspend fun deleteByAppKey(appKey: String)

    @Query("DELETE FROM nim_user_info")
    abstract override suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM nim_user_info")
    abstract override fun entries(): List<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info")
    abstract override fun observeEntries(): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): List<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info LIMIT :count OFFSET :offset")
    abstract override fun observeEntries(count: Int, offset: Int): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id = :id")
    abstract override fun entriesById(id: String): NIMUserInfo

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id = :id")
    abstract override fun observeEntriesById(id: String): Flow<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE appKey = :appKey ORDER BY account ASC LIMIT :count OFFSET :offset")
    abstract fun entriesByAppKey(appKey: String, count: Int, offset: Int): List<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE appKey = :appKey ORDER BY account ASC")
    abstract fun entriesByAppKey(appKey: String): List<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE appKey = :appKey ORDER BY account ASC")
    abstract fun observeEntriesByAppKey(appKey: String): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id IN (:ids)")
    abstract override fun entriesByIds(ids: List<String>): List<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id IN (:ids)")
    abstract override fun observeEntriesByIds(ids: List<String>): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id = :id")
    abstract override fun entriesByIdNullable(id: String): NIMUserInfo?

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE id = :id")
    abstract override fun observeEntriesByIdNullable(id: String): Flow<NIMUserInfo?>

    @Query("SELECT COUNT(*) FROM nim_user_info")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM nim_user_info")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM nim_user_info WHERE id=:id")
    abstract override suspend fun countById(id: String): Int

    @Query("SELECT COUNT(*) FROM nim_user_info WHERE id=:id")
    abstract override fun observeCountById(id: String): Flow<Int>

}