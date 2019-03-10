package com.appnroll.mvi.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "isDone") val isDone: Boolean
)