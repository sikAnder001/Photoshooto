package com.photoshooto.ui.photographersScreens

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R

class SpecialOfferDialog : DialogFragment() {


    companion object {
        const val TAG = "SimpleDialog"

        fun newInstance(): SpecialOfferDialog {
            return SpecialOfferDialog()
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
        return inflater.inflate(R.layout.special_offer_dialog_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var okayBtn = view.findViewById(R.id.tvOrderNow) as Button

        okayBtn.setOnClickListener {
            dialog?.dismiss()
            /* val bundle = Bundle()
             bundle.putString(KEY_QR_ID, arguments?.getString(com.photoshooto.util.KEY_QR_ID))
             findNavController().navigate(R.id.action_move_to_standee_cart, bundle)*/
            findNavController().navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)

        }
    }


    override fun onStart() {
        super.onStart()
        /*val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)*/
        dialog?.setCanceledOnTouchOutside(false)
    }

}