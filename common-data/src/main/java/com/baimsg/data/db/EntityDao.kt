package com.baimsg.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.baimsg.data.model.base.BaseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
abstract class BaseDao<E : BaseEntity> {

    /**
     * 插入一个实体
     * @param entity 实体
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: E): Long

    /**
     * 插入多个实体
     * @param entity 实体
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg entity: E)

    /**
     * 插入实体集合
     * @param entities 实体集合
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<E>): List<Long>

    /**
     * 更新实体
     * @param entity 实体
     */
    @Update
    abstract suspend fun update(entity: E)

    /**
     * 执行事务
     * @param tx 事务函数
     */
    @Transaction
    open suspend fun withTransaction(tx: suspend () -> Unit) = tx()

    /**
     * 更新或插入实体
     * @param entity 实体
     */
    @Update
    suspend fun updateOrInsert(entity: E) {
        val entry = entriesById(entity.getIdentifier()).firstOrNull()
        if (entry == null) {
            insert(entity)
        } else update(entity)
    }

    /**
     * 更新或者插入多个实体
     * @param entities 实体集合
     */
    @Transaction
    open suspend fun updateOrInsert(entities: List<E>) {
        entities.forEach {
            updateOrInsert(it)
        }
    }

    /**
     * 删除实体
     * @param entity 实体
     */
    @Delete
    abstract suspend fun delete(entity: E): Int

    /**
     * 根据id删除实体
     * @param id id
     */
    abstract suspend fun deleteById(id: String)

    /**
     * 删除所有实体
     */
    abstract suspend fun deleteAll()

    /**
     * 获取所有实体
     * @return Flow数据
     */
    @Transaction
    abstract fun entries(): Flow<List<E>>

    /**
     * 获取数据
     * @return paging数据
     */
    @Transaction
    abstract fun entriesPagingSource(): PagingSource<Int, E>

    /**
     * 根据条件获取数据
     * @param count 总数
     * @param offset 偏移位置
     */
    @Transaction
    abstract fun entries(count: Int, offset: Int): Flow<List<E>>

    /**
     * 根据id获取数据
     * @param id id
     */
    @Transaction
    abstract fun entriesById(id: String): Flow<E>

    @Transaction
    abstract fun entriesById(ids: List<String>): Flow<List<E>>

    /**
     * 根据id获取可空数据
     * @param id id
     */
    abstract fun entriesNullable(id: String): Flow<E?>

    abstract suspend fun count(): Int
    abstract fun observeCount(): Flow<Int>

    abstract suspend fun exists(id: String): Int
    abstract fun observeExists(id: String): Flow<Int>

}



