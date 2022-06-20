package com.baimsg.data.db.daos

import androidx.paging.PagingSource
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
    @Query("DELETE FROM nim_user_info WHERE account=:id")
    abstract override suspend fun deleteById(id: String)

    @Query("DELETE FROM nim_user_info")
    abstract override suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM nim_user_info")
    abstract override fun entries(): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info")
    abstract override fun entriesPagingSource(): PagingSource<Int, NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info LIMIT :count OFFSET :offset")
    abstract override fun entries(count: Int, offset: Int): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE account = :id")
    abstract override fun entriesById(id: String): Flow<NIMUserInfo>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE account IN (:ids)")
    abstract override fun entriesById(ids: List<String>): Flow<List<NIMUserInfo>>

    @Transaction
    @Query("SELECT * FROM nim_user_info WHERE account = :id")
    abstract override fun entriesNullable(id: String): Flow<NIMUserInfo?>

    @Query("SELECT COUNT(*) FROM nim_user_info")
    abstract override suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM nim_user_info")
    abstract override fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM nim_user_info WHERE account=:id")
    abstract override suspend fun exists(id: String): Int

    @Query("SELECT COUNT(*) FROM nim_user_info WHERE account=:id")
    abstract override fun observeExists(id: String): Flow<Int>

}