package com.photoshooto.ui.feedback

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.photoshooto.R

class SuccessDialog : DialogFragment() {

    companion object {
        const val TAG = "SimpleDialog"
        var message: String? = null
        lateinit var listener: Listener
        fun newInstance(messageToDisplay: String, listener: Listener): SuccessDialog {
            message = messageToDisplay
            this.listener = listener
            return SuccessDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
        }
        return inflater.inflate(R.layout.feedback_success_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvHomeBtn = view.findViewById(R.id.tvHomeBtn) as TextView

        val textToDisplay = view.findViewById(R.id.textToDisplay) as TextView
        if (!message.isNullOrEmpty()) {
            textToDisplay.text = message
        }
        tvHomeBtn.setOnClickListener {
            navigateToHomeScreen()
        }
    }

    private fun navigateToHomeScreen() {
        dialog?.dismiss()
        listener.goToHome()
    }


    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setCanceledOnTouchOutside(false)
    }

    interface Listener {
        fun goToHome()
    }
}