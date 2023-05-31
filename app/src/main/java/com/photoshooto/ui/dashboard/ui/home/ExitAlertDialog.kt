package com.photoshooto.ui.dashboard.ui.home

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
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity

class ExitAlertDialog : DialogFragment() {

    var from = ""

    companion object {
        const val TAG = "SimpleDialog"
        private const val KEY_from = "from"

        fun newInstance(from: String, context: Context): ExitAlertDialog {
            val args = Bundle()
            args.putString(KEY_from, from)
            val fragment = ExitAlertDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(KEY_from)!!
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
        return inflater.inflate(R.layout.dialog_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnYes = view.findViewById(R.id.btnYes) as TextView
        val btnNo = view.findViewById(R.id.btnNo) as TextView
        btnYes.setOnClickListener {
            dialog?.dismiss()
            handleFinishApp()
        }
        btnNo.setOnClickListener {
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

    private fun handleFinishApp() {
        if (from == "DashBoardScreenActivity") {
            (activity as DashBoardScreenActivity).closeApp()
        } else if (from == "PhotographerDashboardActivity") {
            (activity as PhotographerDashboardActivity).closeApp()
        }
    }

}