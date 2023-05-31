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
import com.photoshooto.databinding.DialogFirstOrderBinding

class FirstOrderDialog {

    private lateinit var clickListener: () -> Unit

    companion object {
        fun showDialog(
            ctx: Context,
            price1: String,
            price2: String,
            clickListener: () -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogFirstOrderBinding
                .inflate(LayoutInflater.from(ctx))

            dialog.setContentView(dialogBinding.root)

            val priceSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(ctx, R.color.buttonGreen)
                    ds.textSize = 60.0f
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.isUnderlineText = false
                }
            }

            val price2Span = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(ctx, R.color.buttonGreen)
                    ds.textSize = 60.0f
                    ds.typeface = Typeface.DEFAULT_BOLD
                    ds.isUnderlineText = false
                }
            }

            val spannableString = SpannableString(price1)
            spannableString.setSpan(
                priceSpan, 0, price1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val spannableString2 = SpannableString(price2)
            spannableString2.setSpan(
                price2Span, 0, price2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            dialogBinding.message.text = TextUtils.concat(
                "Get your 1st Order of ", spannableString, " for as low ", spannableString2,
                " per order"
            )

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