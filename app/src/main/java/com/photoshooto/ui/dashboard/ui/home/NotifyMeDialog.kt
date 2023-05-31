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
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.photoshooto.R
import com.photoshooto.domain.usecase.notificationApi.NotificationRequestModel
import com.photoshooto.domain.usecase.notificationApi.NotificationViewModel
import com.photoshooto.util.PreferenceManager
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotifyMeDialog : DialogFragment() {

    var CUSTOMER_ID = ""
    var msgClicked = ""
    var whatsapClicked = ""

    private val viewModel: NotificationViewModel by viewModel()

    var notificationReq = NotificationRequestModel()

    var sms: Array<String>? = null
    var whatsapp: Array<String>? = null
    var both: Array<String>? = null

    companion object {
        const val TAG = "NotifyMeDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"

        fun newInstance(customerId: String, context: Context): NotifyMeDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            val fragment = NotifyMeDialog()
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
        val rootView = inflater.inflate(R.layout.dialog_notify, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var notifyDone = view.findViewById(R.id.notifyDone) as AppCompatButton
        var messageCheck = view.findViewById(R.id.messageCheck) as CheckBox
        var whatsappCheck = view.findViewById(R.id.whatsappCheck) as CheckBox
        var progressBar = view.findViewById(R.id.progress_bar) as ProgressBar

        notifyDone.setOnClickListener {

            notificationReq.type = "info"
            notificationReq.message = "message text"
            notificationReq.status = "unread"
            notificationReq.notify_user_id = "US232"

            println("praveen msgClicked " + msgClicked)
            println("praveen whatsapClicked " + whatsapClicked)

            if (msgClicked.isNotEmpty()) {
                both = arrayOf("sms")
            }

            if (whatsapClicked.isNotEmpty()) {
                both = arrayOf("whatsapp")
            }

            if (msgClicked.isNotEmpty() && whatsapClicked.isNotEmpty()) {
                both = arrayOf("sms", "whatsapp")
            }

            println("praveen both " + both)
            notificationReq.channel = both

            if (msgClicked.isEmpty() && whatsapClicked.isEmpty()) {
                onToast(getString(R.string.you_have_select), requireContext())
            } else {
                viewModel.reqNotifications(
                    notificationReq,
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )
            }

        }

        messageCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            println("praveen messageCheck checked " + isChecked)
            if (isChecked) {
                msgClicked = "sms"
            } else {
                msgClicked = ""
            }
            println("praveen messageCheck msgClicked " + msgClicked)
        }

        whatsappCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            println("praveen whatsappCheck checked " + isChecked)
            if (isChecked) {
                whatsapClicked = "whatapp"
            } else {
                whatsapClicked = ""
            }
            println("praveen whatsappCheck whatsapClicked " + whatsapClicked)
        }

        with(viewModel) {
            reqNotifyModel.observe(this@NotifyMeDialog, androidx.lifecycle.Observer {
                if (it.success!!) {
//                    onToast(it.message, requireContext())
                    with(PreferenceManager) {
                        saveNotifyDone = true
                    }

                    ThankYouDialog.newInstance("", requireContext())
                        .show(getParentFragmentManager(), NotifyMeDialog.TAG)
                    dismiss()
                } else {
                    onToast(it.message!!, requireContext())
                }
            })
            showProgressbar.observe(
                this@NotifyMeDialog,
                androidx.lifecycle.Observer { isVisible ->
                    progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
                })

        }


    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


}