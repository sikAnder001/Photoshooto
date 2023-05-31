package com.photoshooto.ui.qrcodegenration

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
import com.photoshooto.util.KEY_QR_ID

class StandeeDetailsAddedDialog : DialogFragment() {

    var CUSTOMER_ID = ""
    var qrId = ""

    companion object {
        const val TAG = "SimpleDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"
        fun newInstance(keyQrId: String, context: Context): StandeeDetailsAddedDialog {
            val args = Bundle()
            args.putString(KEY_QR_ID, keyQrId)
            val fragment = StandeeDetailsAddedDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //CUSTOMER_ID = it.getString(KEY_CUSTOMER_ID)!!
            qrId = it.getString(KEY_QR_ID)!!
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
        }
        //recordScreenView(requireActivity(),"StandeeDetailsAdded", FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup_your_account_approval_in_just_24hr)

        return inflater.inflate(R.layout.standee_details_added_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var okayBtn = view.findViewById(R.id.tvDone) as TextView

        okayBtn.setOnClickListener {
            dialog?.dismiss()

            /* val bundle = Bundle()
             bundle.putString(KEY_QR_ID, arguments?.getString(com.photoshooto.util.KEY_QR_ID))
             findNavController().navigate(R.id.action_move_to_standee_cart, bundle)*/

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