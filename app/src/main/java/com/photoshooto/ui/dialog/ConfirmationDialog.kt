package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.photoshooto.R
import com.photoshooto.databinding.DialogConfirmationBinding

class ConfirmationDialog {

    companion object {
        fun showDialog(
            ctx: Context, eventName: String, clickListener: () -> Unit
        ) {
            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogConfirmationBinding.inflate(LayoutInflater.from(ctx))

            dialog.setContentView(dialogBinding.root)
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setBackgroundDrawableResource(R.color.colorTransparent)
            dialog.show()

            val priceSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(ctx, R.color.textColor)
                    ds.typeface = Typeface.DEFAULT_BOLD
                }
            }

            val spannableString = SpannableString(eventName)
            spannableString.setSpan(
                priceSpan, 0, eventName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            dialogBinding.message.text = TextUtils.concat(
                "Are you sure about removing the \"", spannableString, "\" Job"
            )

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnRemove.setOnClickListener {
                clickListener.invoke()
                dialog.dismiss()
            }

        }
    }
}