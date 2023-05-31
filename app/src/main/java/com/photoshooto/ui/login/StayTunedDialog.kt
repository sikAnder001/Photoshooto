package com.photoshooto.ui.login

import android.content.Intent
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
import com.photoshooto.InviteFriendsActivity
import com.photoshooto.R
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.dashboard.ui.home.HomeViewModel
import com.photoshooto.util.recordScreenView

class StayTunedDialog : DialogFragment() {

    var CUSTOMER_ID = ""

    companion object {
        const val TAG = "SimpleDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"

        fun newInstance(): StayTunedDialog {
            return StayTunedDialog()
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
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        recordScreenView(
            requireActivity(),
            "StayTunedDialog",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup_your_account_approval_in_just_24hr
        )

        return inflater.inflate(R.layout.dialog_stay_tuned, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var okayBtn = view.findViewById(R.id.okayBtn) as TextView

        okayBtn.setOnClickListener {
            dialog?.dismiss()
            var i = Intent(activity, InviteFriendsActivity::class.java)
            startActivity(i)
            /* AccountVerifiedDialog.newInstance("", requireContext())
                 .show(getParentFragmentManager(), AccountVerifiedDialog.TAG)
 */
            // (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(resources.getString(R.string.open_drawer))
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