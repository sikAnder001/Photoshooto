package com.photoshooto.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import com.photoshooto.R
import com.photoshooto.databinding.DialogDatePickerBinding
import com.photoshooto.databinding.DialogTimePickerBinding
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.to12HrFormat
import com.photoshooto.ui.job.utility.twoDigitNUmber
import org.joda.time.DateTime

class DateTimeDialog {

    companion object {

        fun showDateDialog(
            ctx: Context,
            buttonClickListener: (selectedDate: String) -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogDatePickerBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            dialogBinding.tvDialogOk.setSafeOnClickListener {
                var tmp =
                    "${dialogBinding.datePickerDialog.year}-${(dialogBinding.datePickerDialog.month + 1).twoDigitNUmber()}-${dialogBinding.datePickerDialog.dayOfMonth.twoDigitNUmber()}"
                buttonClickListener.invoke(tmp)
                dialog.dismiss()
            }

            /*if (selectedYear != 0 && selectedMonth != 0 && selecteDay != 0) {
                dialogBinding.datePicker.updateDate(selectedYear,selectedMonth,selecteDay)
            }*/
            dialogBinding.datePickerDialog.minDate = DateTime.now().millis + 86400000

            dialogBinding.datePickerDialog.descendantFocusability =
                DatePicker.FOCUS_BLOCK_DESCENDANTS

            dialogBinding.tvDialogCancel.setSafeOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setDimAmount(0.7f)
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

        fun showTimeDialog(
            ctx: Context,
            hour12Format: Boolean = true,
            buttonClickListener: (selectedTime: String) -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            var dialogBinding = DialogTimePickerBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)

            dialogBinding.tvDialogOk.setSafeOnClickListener {
                var tmp = if (!hour12Format) {
                    "${dialogBinding.timePickerDialog.hour.twoDigitNUmber()}:${dialogBinding.timePickerDialog.minute.twoDigitNUmber()}"
                } else {
                    listOf(
                        dialogBinding.timePickerDialog.hour,
                        dialogBinding.timePickerDialog.minute
                    ).to12HrFormat()
                }
                buttonClickListener.invoke(tmp)
                dialog.dismiss()
            }

            dialogBinding.timePickerDialog.descendantFocusability =
                TimePicker.FOCUS_BLOCK_DESCENDANTS


            dialogBinding.tvDialogCancel.setSafeOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setDimAmount(0.7f)
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

        fun showPostDateDialog(
            ctx: Context,
            buttonClickListener: (selectedDate: String) -> Unit
        ) {

            val dialog = Dialog(ctx, R.style.DialogSlideAnim)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogDatePickerBinding.inflate(LayoutInflater.from(ctx))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            dialogBinding.tvDialogOk.setSafeOnClickListener {
                var tmp =
                    "${dialogBinding.datePickerDialog.year}-${(dialogBinding.datePickerDialog.month + 1).twoDigitNUmber()}-${dialogBinding.datePickerDialog.dayOfMonth.twoDigitNUmber()}"
                buttonClickListener.invoke(tmp)
                dialog.dismiss()
            }

            /*if (selectedYear != 0 && selectedMonth != 0 && selecteDay != 0) {
                dialogBinding.datePicker.updateDate(selectedYear,selectedMonth,selecteDay)
            }*/
            dialogBinding.datePickerDialog.maxDate = DateTime.now().millis + 86400000

            dialogBinding.datePickerDialog.descendantFocusability =
                DatePicker.FOCUS_BLOCK_DESCENDANTS

            dialogBinding.tvDialogCancel.setSafeOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setDimAmount(0.7f)
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