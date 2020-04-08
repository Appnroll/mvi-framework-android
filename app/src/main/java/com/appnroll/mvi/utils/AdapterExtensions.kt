package com.appnroll.mvi.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate [RecyclerView.Adapter] update to [DiffUtil]
 * */
inline fun <T> Adapter<*>.update(
    crossinline task: (oldList: List<T>, newList: List<T>) -> Callback
) = object : ReadWriteProperty<Adapter<*>, List<T>> {
    var current = emptyList<T>()

    override fun getValue(thisRef: Adapter<*>, property: KProperty<*>): List<T> {
        return current
    }

    override fun setValue(thisRef: Adapter<*>, property: KProperty<*>, value: List<T>) {
        val diffTask = task(current, value)
        DiffUtil.calculateDiff(diffTask).dispatchUpdatesTo(thisRef)
        current = value
    }
}