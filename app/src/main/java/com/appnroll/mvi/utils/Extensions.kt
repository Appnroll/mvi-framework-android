package com.appnroll.mvi.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Inflate new layout using [ViewGroup] parent's context
 * */
fun ViewGroup.inflateItem(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)

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