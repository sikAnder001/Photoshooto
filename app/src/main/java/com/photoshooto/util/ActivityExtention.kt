package com.photoshooto.util

import android.app.Activity
import android.widget.Toast

/**
 * show toast
 * @param message message to display
 * @param lengthShort length for toast
 */
fun Activity.showToast(message: String, lengthShort: Boolean = true) {
    val length = if (lengthShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, message, length).show()
}