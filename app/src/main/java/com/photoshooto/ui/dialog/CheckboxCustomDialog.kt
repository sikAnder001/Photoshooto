package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.isVisible
import com.photoshooto.R
import com.photoshooto.databinding.DialogCheckCustomBinding
import com.photoshooto.ui.job.utility.*

class CheckboxCustomDialog {

    companion object {

        var arraySelect = ArrayList<String>()

        fun showDialog(
            ctx: Context,
            dataList: ArrayList<String>,
            header: String = "",
            showOther: Boolean = true,
            clickListener: (selection: String) -> Unit
        ) {
            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DialogCheckCustomBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)

            arraySelect = arrayListOf<String>()
            val colorState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled)
                    ), intArrayOf(
                        ctx.getColor(R.color.colorOrange),  // checked
                        ctx.getColor(R.color.grey_78),  // unchecked
                    )
                )
            } else {
                TODO("VERSION.SDK_INT < M")
            }

            dataList.forEach {
                var rdbtn = CheckBox(ctx)
                rdbtn.id = View.generateViewId()
                rdbtn.text = it
                rdbtn.includeFontPadding = false
                rdbtn.setTextAppearance(ctx, R.style.tvFontRegular)
                rdbtn.setPadding((15).dpToInt(), (10).dpToInt(), 0, (10).dpToInt())
                // rdbtn.setButtonDrawable(R.drawable.radio_auth_selector);
                rdbtn.buttonTintList = colorState
                rdbtn.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) arraySelect.add(compoundButton.text.toString())
                    else arraySelect.remove(compoundButton.text.toString())
                }
                rdbtn.background = AppCompatResources.getDrawable(ctx, R.color.colorTransparent)
                dialogBinding.ckGroup.addView(rdbtn)
            }

            dialogBinding.tvHeader.text = header

            dialogBinding.btnApply.setSafeOnClickListener {

                if (arraySelect.isEmpty()) {
                    ctx.toastError("Please select Type")
                    return@setSafeOnClickListener
                }
                if (dialogBinding.etOther.isVisible) {
                    arraySelect.add(dialogBinding.etOther.text.toString())
                }
                dialog.dismiss()
                clickListener.invoke(arraySelect.toString())
            }

            dialogBinding.radioOther.setOnCheckedChangeListener { _, check ->
                if (check) {
                    dialogBinding.radioOther.gone()
                    dialogBinding.dummy.visible()
                    dialogBinding.etOther.visible()
                    dialogBinding.etOther.setText("")
                    dialogBinding.etOther.requestFocus()
                    ctx.showKeyboard()
                } else {
                    dialogBinding.radioOther.visible()
                    dialogBinding.etOther.setText("")
                    dialogBinding.dummy.gone()
                    dialogBinding.etOther.gone()
                    ctx.hideKeyboardFrom(dialogBinding.etOther)
                }
            }

            dialogBinding.dummy.setSafeOnClickListener {
                dialogBinding.radioOther.isChecked = false
            }

            dialogBinding.tvClearAll.setSafeOnClickListener {
                dialogBinding.radioOther.isChecked = false
                ctx.hideKeyboardFrom(dialogBinding.etOther)
                dialogBinding.ckGroup.children.forEach {
                    (it as CheckBox).isChecked = false
                }
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