package com.sm.app.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class GenericTextWatcherMobile internal constructor(
    private val currentView: EditText,
    private val nextView: EditText?,
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
            //  R.id.otp_mobile_1 -> if (text.length == 1) nextView?.requestFocus()
            //  R.id.otp_mobile_2 -> if (text.length == 1) nextView?.requestFocus()
            //  R.id.otp_mobile_3 -> if (text.length == 1) nextView?.requestFocus()
            //  R.id.otp_mobile_4 -> if (text.length == 1) nextView?.requestFocus()
            // R.id.otp_mobile_5 -> if (text.length == 1) nextView?.requestFocus()
        }
    }
}
