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
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.ui.dashboard.ui.home.HomeViewModel

class PwdChangStatusDialog : DialogFragment() {

    var CUSTOMER_ID = ""

    companion object {
        const val TAG = "SimpleDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"

        fun newInstance(customerId: String, context: Context): PwdChangStatusDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            val fragment = PwdChangStatusDialog()
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
        val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_pwd_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var okayBtn = view.findViewById(R.id.okayBtn) as TextView

        okayBtn.setOnClickListener {
            dialog?.dismiss()
            findNavController().navigate(R.id.action_FragmentResetPwd_to_FragmentRouting)
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