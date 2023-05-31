package com.photoshooto.ui.purchase

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity

class PaymentSuccessDialog : DialogFragment() {
    var cTimer: CountDownTimer? = null

    companion object {
        const val TAG = "SimpleDialog"
        const val ORDER_ID = "ORDER_ID"

        fun newInstance(listITem: String?): PaymentSuccessDialog {
            val args = Bundle()
            val fragment = PaymentSuccessDialog()
            if (listITem != null) {
                args.putString(ORDER_ID, listITem)
                fragment.arguments = args
            }
            return fragment
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
        //recordScreenView(requireActivity(),"StandeeDetailsAdded", FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup_your_account_approval_in_just_24hr)

        return inflater.inflate(R.layout.payment_success_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tvRedirect = view.findViewById(R.id.tvRedirect) as TextView
        var tvTrackOrder = view.findViewById(R.id.tvTrackOrder) as TextView

        tvTrackOrder.setOnClickListener {
            dialog?.dismiss()

            val bundle = Bundle()
            if (requireArguments().containsKey(ORDER_ID)) {
                bundle.putString("item", requireArguments().get(ORDER_ID).toString())
            }
            findNavController().navigate(
                R.id.action_photographerLandingPageFragment_to_photographerTrackOrderFragment,
                bundle
            )
        }

        cTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(l: Long) {
                tvRedirect.text =
                    "${resources.getString(R.string.redirect_to_home_page)} ${(l / 1000)}s"
            }

            override fun onFinish() {
                //dialog?.dismiss()
                //navigateToHomeScreen()
            }
        }
        cTimer?.start()
    }

    private fun navigateToHomeScreen() {
//        findNavController().navigate(R.id.landingPageFragment)
        val intent = Intent(requireActivity(), PhotographerDashboardActivity::class.java)
        intent.apply {
            startActivity(this)
            requireActivity().finishAffinity()
        }
    }


    fun cancelTimer() {
        if (cTimer != null) cTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
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