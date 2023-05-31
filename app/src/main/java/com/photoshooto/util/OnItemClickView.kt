package com.photoshooto.util

import android.view.View

interface OnItemClickView<T> {
    fun onItemClick(model: T, position: Int, view: View)
}