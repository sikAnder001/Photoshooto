package com.sm.app.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.photoshooto.R

class GenericPinTextWatcher internal constructor(
    private val currentView: EditText,
    private val nextView: EditText?,
    private val button: AppCompatButton
) : TextWatcher {
    override fun beforeTextChanged(
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int,
    ) {
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        val text = editable.toString()
        when (currentView.id) {
            R.id.otp_edit_text_1 -> {
                if (text.length == 1) nextView?.requestFocus()
                button.isEnabled = false
            }
            R.id.otp_edit_text_2 -> {
                if (text.length == 1) nextView?.requestFocus()
                button.isEnabled = false
            }
            R.id.otp_edit_text_3 -> {
                if (text.length == 1) nextView?.requestFocus()
                button.isEnabled = false
            }
            R.id.otp_edit_text_4 -> {
                if (text.length == 1) {
                    nextView?.requestFocus()
                    button.isEnabled = true
                } else {
                    button.isEnabled = false
                }
            }
            R.id.otp_edit_text_5 -> if (text.length == 1) nextView?.requestFocus()
//            R.id.date_edit_textt_text -> if (text.length == 2) nextView?.requestFocus()
//            R.id.month_edit_text -> if (text.length == 2) nextView?.requestFocus()
            // You can use EditText6 same as above to hide the keyboard
        }
    }
}
