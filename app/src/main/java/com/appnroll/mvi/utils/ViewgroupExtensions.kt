package com.appnroll.mvi.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Inflate new layout using [ViewGroup] parent's context
 * */
fun ViewGroup.inflateItem(@LayoutRes layout: Int): View =
    LayoutInflater.from(context).inflate(layout, this, false)