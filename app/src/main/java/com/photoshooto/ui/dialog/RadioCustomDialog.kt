package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.view.*
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.photoshooto.R
import com.photoshooto.databinding.DialogRadioCustomBinding
import com.photoshooto.ui.job.utility.*

class RadioCustomDialog {

    companion object {

        fun showDialog(
            ctx: Context,
            dataList: ArrayList<String>,
            header: String = "",
            showOther: Boolean = true,
            clickListener: (selection: String) -> Unit
        ) {
            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DialogRadioCustomBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)

            if (!showOther) {
                dialogBinding.radioOther.gone()
            }

            dataList.forEach {
                val rdbtn = RadioButton(ctx)
                rdbtn.id = View.generateViewId()
                rdbtn.text = it
                rdbtn.includeFontPadding = false
                rdbtn.setTextAppearance(ctx, R.style.tvFontRegular)
                rdbtn.setPadding((15).dpToInt(), (10).dpToInt(), 0, (10).dpToInt())
                // rdbtn.setButtonDrawable(R.drawable.radio_auth_selector);
                rdbtn.buttonTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled)
                    ), intArrayOf(
                        ctx.getColor(R.color.colorOrange),  // checked
                        ctx.getColor(R.color.grey_78),  // unchecked
                    )
                )
                rdbtn.background = AppCompatResources.getDrawable(ctx, R.color.colorTransparent)
                dialogBinding.rdGroup.addView(rdbtn)
            }

            dialogBinding.tvHeader.text = header

            dialogBinding.btnApply.setOnClickListener {

                if (showOther and dialogBinding.radioOther.isGone and
                    dialogBinding.etOther.text.toString().isBlank()
                ) {
                    ctx.toastError("Please mention type")
                } else if (showOther and dialogBinding.radioOther.isGone) {
                    clickListener.invoke(dialogBinding.etOther.text.toString())
                    dialog.dismiss()
                } else if (dialogBinding.rdGroup.getSelectedValue().isBlank()) {
                    ctx.toastError("Please select Type")
                } else {
                    clickListener.invoke(dialogBinding.rdGroup.getSelectedValue())
                    dialog.dismiss()
                }
            }

            dialogBinding.radioOther.setOnCheckedChangeListener { compoundButton, check ->
                if (check && compoundButton.isPressed) {
                    dialogBinding.rdGroup.clearCheck()
                    dialogBinding.radioOther.gone()
                    dialogBinding.etOther.visible()
                    dialogBinding.etOther.setText("")
                    dialogBinding.etOther.requestFocus()
                    ctx.showKeyboard()
                }
            }

            dialogBinding.rdGroup.setOnCheckedChangeListener { _, _ ->
                ctx.hideKeyboardFrom(dialogBinding.etOther)

                dialogBinding.radioOther.isVisible = showOther

                dialogBinding.radioOther.isChecked = false
                dialogBinding.etOther.gone()
            }

            dialogBinding.tvClearAll.setSafeOnClickListener {
                dialogBinding.rdGroup.clearCheck()
                ctx.hideKeyboardFrom(dialogBinding.etOther)
                dialogBinding.radioOther.isVisible = showOther
                dialogBinding.radioOther.isChecked = false
                dialogBinding.etOther.gone()
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