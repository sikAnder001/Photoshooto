package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.text.SpannableString
import android.view.*
import android.widget.LinearLayout
import android.widget.RadioButton
import com.photoshooto.R
import com.photoshooto.databinding.DialogReportBinding
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class ReportDialog {

    companion object {
        fun showDialog(
            ctx: Context,
            title: SpannableString,
            radioList: java.util.ArrayList<String>,
            reportClickListener: (selectedOption: String) -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogReportBinding
                .inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)

            dialogBinding.title.text = title
            dialogBinding.btnReport.setSafeOnClickListener {
                val radio =
                    dialogBinding.group.findViewById<RadioButton>(dialogBinding.group.checkedRadioButtonId)

                reportClickListener.invoke(radio.text.toString())
                dialog.dismiss()
            }
            dialogBinding.btnCancel.setSafeOnClickListener {
                dialog.dismiss()
            }

            val colorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_enabled)
                ), intArrayOf(
                    ctx.resources.getColor(R.color.colorOrange),  // checked
                    ctx.resources.getColor(R.color.grey_78),  // unchecked
                )
            )

            radioList.forEachIndexed { index, it ->
                val rbn = RadioButton(dialogBinding.group.context).apply {
                    id = View.generateViewId()
                    text = it
                    setBackgroundColor(ctx.resources.getColor(R.color.colorTransparent))
                    buttonTintList = colorState
                    setTextColor(colorState)
                    isChecked = (index == 0)
                }

                dialogBinding.group.addView(rbn)
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