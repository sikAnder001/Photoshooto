package com.photoshooto.ui.login

import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import com.photoshooto.R
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.dashboard.ui.home.HomeViewModel
import com.photoshooto.util.callPhoneNumber
import com.photoshooto.util.recordScreenView
import com.photoshooto.util.sendEmail

class GetSupportDialog : DialogFragment() {

    var CUSTOMER_ID = ""

    companion object {
        const val TAG = "SimpleDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"

        fun newInstance(customerId: String, context: Context): GetSupportDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            val fragment = GetSupportDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            CUSTOMER_ID = it.getString(KEY_CUSTOMER_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        recordScreenView(
            requireActivity(),
            "GetSupportDialog",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_LoginSupport
        )

        return inflater.inflate(R.layout.dialog_get_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val okayBtn = view.findViewById(R.id.okayBtn) as TextView
        val tvPhone = view.findViewById(R.id.support_no) as TextView
        val tvEmail = view.findViewById(R.id.support_email_id) as TextView

        tvEmail.setOnClickListener {
            sendEmail(activity!!, getString(R.string.support_email_id), "Support")
        }

        tvPhone.setOnClickListener {
            callPhoneNumber(activity!!, getString(R.string.support_phone_no))
        }

        okayBtn.setOnClickListener {
            dialog?.dismiss()
        }
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
}