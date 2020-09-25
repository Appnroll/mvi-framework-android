package com.appnroll.mvi.model

import android.os.Parcelable
import com.appnroll.mvi.data.room.entities.TaskEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    val id: Long,
    val content: String,
    val isDone: Boolean
): Parcelable

fun Task.toTaskEntity() = TaskEntity(
    id,
    content,
    isDone
)

fun TaskEntity.toTask() = Task(
    id,
    content,
    isDone
)