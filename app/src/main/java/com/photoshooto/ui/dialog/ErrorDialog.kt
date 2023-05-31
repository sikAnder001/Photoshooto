package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.photoshooto.R
import com.photoshooto.databinding.DialogErrorCustomBinding

class ErrorDialog {

    private lateinit var clickListener: () -> Unit

    companion object {
        fun showDialog(
            ctx: Context,
            message: String,
            buttonText: String,
            outsideTouch: Boolean,
            clickListener: () -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            var dialogBinding = DialogErrorCustomBinding
                .inflate(LayoutInflater.from(ctx))

            dialog.setContentView(dialogBinding.root)
            dialog.setCanceledOnTouchOutside(outsideTouch)
            dialog.setCancelable(outsideTouch)

            dialogBinding.message.text = message
            dialogBinding.btn.text = buttonText
            dialogBinding.btn.setOnClickListener {
                clickListener.invoke()
                dialog.dismiss()
            }

            dialog.window
                ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(R.color.colorTransparent)

            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }
}