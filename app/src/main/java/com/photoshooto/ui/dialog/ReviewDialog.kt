package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.photoshooto.R
import com.photoshooto.databinding.DialogReviewBinding
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.toastError

class ReviewDialog {
    companion object {
        fun showDialog(
            ctx: Context,
            userName: String,
            submitClickListener: (feedback: String, rating: Float) -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogReviewBinding
                .inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)

            val nameTitle = object : ClickableSpan() {
                override fun onClick(widget: View) {
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(ctx, R.color.colorOrange)
                    ds.isUnderlineText = false
                }
            }

            var tmp = "Report User, ${userName}"
            var spannableString = SpannableString(tmp)
            spannableString.setSpan(
                nameTitle, 12, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            dialogBinding.title.text = spannableString

            tmp = "How was the experience with user, ${userName}"
            spannableString = SpannableString(tmp)
            spannableString.setSpan(
                nameTitle, 34, tmp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            dialogBinding.label.text = spannableString

            dialogBinding.btnReview.setSafeOnClickListener {
                if (dialogBinding.feedback.text.isNotBlank()) {
                    submitClickListener.invoke(
                        dialogBinding.feedback.text.toString().trim(),
                        dialogBinding.rating.rating
                    )
                    dialog.dismiss()
                } else {
                    ctx.toastError("Please write some review")
                }
            }

            dialogBinding.cross.setSafeOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.feedback.doAfterTextChanged {
                dialogBinding.charCount.text = "${it.toString().length} / 100"

                if (it.toString().length > 90) {
                    dialogBinding.charCount.setTextColor(ctx.resources.getColor(R.color.colorRed))
                } else {
                    dialogBinding.charCount.setTextColor(ctx.resources.getColor(R.color.colorBlack))
                }
            }

            dialogBinding.rating.setOnRatingChangeListener { ratingBar, rating ->

                if (rating % 1 == 0f) {
                    if (rating <= 1.0)
                        dialogBinding.ratingCount.text = "${rating.toInt()} Star"
                    else
                        dialogBinding.ratingCount.text = "${rating.toInt()} Stars"
                } else {
                    if (rating <= 1.0)
                        dialogBinding.ratingCount.text = "${rating} Star"
                    else
                        dialogBinding.ratingCount.text = "${rating} Stars"
                }
            }

            dialog.window?.setDimAmount(0.7f)
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