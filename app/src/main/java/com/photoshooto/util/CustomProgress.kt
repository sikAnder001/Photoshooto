package com.photoshooto.util

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.photoshooto.R

class CustomProgress {
    private var customProgress: CustomProgress? = null
    var mDialog: Dialog? = null

    fun getInstance(): CustomProgress? {
        if (customProgress == null) {
            customProgress = CustomProgress()
        }
        return customProgress
    }

    fun showProgress(
        context: Context?,
        message: String,
        cancelable: Boolean
    ) {
        mDialog = Dialog(context!!)
        // no tile for the dialog
        mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog!!.setContentView(R.layout.dialog_prograss_bar)
        var mProgressBar = mDialog!!.findViewById(R.id.progress_bar) as ProgressBar
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        val progressText = mDialog!!.findViewById(R.id.progress_text) as TextView
        progressText.text = message
        progressText.visibility = View.VISIBLE
        mProgressBar.setVisibility(View.VISIBLE)
        // you can change or add this line according to your need
        mProgressBar.setIndeterminate(true)
        mDialog!!.setCancelable(cancelable)
        mDialog!!.setCanceledOnTouchOutside(cancelable)
        mDialog!!.show()
    }

    fun hideProgress() {
        if (mDialog != null) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }
}