package com.photoshooto.ui.login

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.photoshooto.R
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import java.text.ParseException
import java.util.*

class AccountVerifiedDialog : DialogFragment() {

    var CUSTOMER_ID = ""
    var cTimer: CountDownTimer? = null

    companion object {
        const val TAG = "SimpleDialog"

        fun newInstance(): AccountVerifiedDialog {
            val fragment = AccountVerifiedDialog()
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
        }
        return inflater.inflate(R.layout.dialog_verified_account, container, false)
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var okayBtn = view.findViewById(R.id.okayBtn) as TextView
        var text2 = view.findViewById(R.id.textView2) as TextView
        var progressBar = view.findViewById(R.id.progress_bar) as ProgressBar

        //2022-12-07T11:29:58.021Z
        val format = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = SharedPrefsHelper.read(SharedPrefConstant.USER_REGISTERED_ON)
        val registerDate = uTCToLocal(format, format, date)
        val userDob: Date = dateFormat.parse(registerDate)

        val today = Date()
        val diff: Long = today.time - userDob.time
        val timer: Long = ((1000 * 60 * 60) * 24) - diff
        //val numOfDays = (diff / (1000 * 60 * 60 * 24)).toInt()
        //val hours = (diff / (1000 * 60 * 60)).toInt()

        cTimer = object : CountDownTimer(timer, 1000) {
            override fun onTick(milliseconds: Long) {

                var seconds = (milliseconds / 1000) % 60
                var minutes = (milliseconds / (1000 * 60) % 60)
                var hours = (milliseconds / (1000 * 60 * 60) % 24)

                var dividedHour: Float = (hours * 100).toFloat()
                val i: Int = ((dividedHour / 24)).toInt()
                val progress: Int = 100 - i

                progressBar.progress = progress


                text2.text = "$hours : $minutes: $seconds"

            }

            override fun onFinish() {
                text2.text = "00:00:00"
            }
        }
        cTimer?.start()

        okayBtn.setOnClickListener {
            dialog?.dismiss()

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun uTCToLocal(
        dateFormatInPut: String?,
        dateFomratOutPut: String?,
        datesToConvert: String?
    ): String? {
        var dateToReturn = datesToConvert
        val sdf = SimpleDateFormat(dateFormatInPut)
        sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
        var gmt: Date? = null
        val sdfOutPutToSend = SimpleDateFormat(dateFomratOutPut)
        sdfOutPutToSend.timeZone = android.icu.util.TimeZone.getDefault()
        try {
            gmt = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(gmt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateToReturn
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer?.cancel()
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

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }

}