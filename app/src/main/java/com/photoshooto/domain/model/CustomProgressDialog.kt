package com.photoshooto.domain.model

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.photoshooto.R

class CustomProgressDialog(context: Context) : Dialog(context, R.style.full_screen_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dialog_progress)
    }
}