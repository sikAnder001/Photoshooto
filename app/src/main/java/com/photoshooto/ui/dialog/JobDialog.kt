package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.photoshooto.R
import com.photoshooto.databinding.JobExhaustedDialogBinding
import com.photoshooto.databinding.JobSuccessDialogBinding

class JobDialog {

    companion object {
        fun showSuccessDialog(
            ctx: Context, adId: String, clickListener: () -> Unit
        ) {
            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            var dialogBinding = JobSuccessDialogBinding.inflate(LayoutInflater.from(ctx))

            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnCancel.setOnClickListener {
                clickListener.invoke()
                dialog.dismiss()
            }

            dialogBinding.adId.text = "Your AD ID: $adId"

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(R.color.colorTransparent)

            if (!dialog.isShowing) {
                dialog.show()
            }
        }

        fun showExhaustedSuccessDialog(
            ctx: Context, message: String, clickListener: () -> Unit
        ) {
            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            var dialogBinding = JobExhaustedDialogBinding.inflate(LayoutInflater.from(ctx))

            dialog.setContentView(dialogBinding.root)

            dialogBinding.label2.text = message
            dialogBinding.cross.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnPlan.setOnClickListener {
                clickListener.invoke()
                dialog.dismiss()
            }

            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(R.color.colorTransparent)

            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }
}