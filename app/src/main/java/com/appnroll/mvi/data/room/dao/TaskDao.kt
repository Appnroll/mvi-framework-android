package com.appnroll.mvi.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.appnroll.mvi.data.room.entities.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM task where isDone = 1")
    suspend fun getAllDone(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id IN (:id)")
    suspend fun getForId(id: Long): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM task")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(tasks: List<TaskEntity>)

    @Query("DELETE FROM task")
    suspend fun deleteAll()
}