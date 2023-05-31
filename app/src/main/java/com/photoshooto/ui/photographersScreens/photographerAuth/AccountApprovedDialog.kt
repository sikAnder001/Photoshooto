package com.photoshooto.ui.photographersScreens.photographerAuth

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
import com.photoshooto.R
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.recordScreenView

class AccountApprovedDialog : DialogFragment() {

    private var CUSTOMER_ID = ""
    private var ORDER_ID = ""

    companion object {
        const val TAG = "AccountApprovedDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun newInstance(
            customerId: String,
            orderId: String,
            context: Context
        ): AccountApprovedDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            args.putString(KEY_ORDER_ID, orderId)
            val fragment = AccountApprovedDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            CUSTOMER_ID = it.getString(KEY_CUSTOMER_ID)!!
            ORDER_ID = it.getString(KEY_ORDER_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        recordScreenView(
            requireActivity(),
            "ApproveSignupDialog",
            FirebaseAnalytics_Event_ScreenName.screenApproved_Signup
        )

        return inflater.inflate(R.layout.dialog_account_approve, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val okayBtn = view.findViewById(R.id.okayBtn) as TextView

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