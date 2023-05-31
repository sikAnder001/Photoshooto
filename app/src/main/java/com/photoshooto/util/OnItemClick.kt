package com.photoshooto.util

interface OnItemClick<T> {
    fun onItemClick(model: T, position: Int)
}