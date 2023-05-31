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
import androidx.fragment.app.DialogFragment
import com.photoshooto.R
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.recordScreenView

class InfoPwdDialog : DialogFragment() {
    var CUSTOMER_ID = ""

    companion object {
        const val TAG = "InfoPwdDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"
        fun newInstance(customerId: String, context: Context): InfoPwdDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            val fragment = InfoPwdDialog()
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

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }
        recordScreenView(
            requireActivity(),
            "InfoPwdDialog",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Password_Guidelines
        )
        return inflater.inflate(R.layout.dialog_pwd_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog?.setCanceledOnTouchOutside(false)
    }

}