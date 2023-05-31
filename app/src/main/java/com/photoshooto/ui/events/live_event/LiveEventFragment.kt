package com.photoshooto.ui.events.live_event

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.BottomsheetEndEventBinding
import com.photoshooto.databinding.FragmentLiveEventBinding
import com.photoshooto.util.showToast
import java.util.*

class LiveEventFragment : Fragment() {
    private lateinit var liveBinding: FragmentLiveEventBinding

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var time = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        liveBinding = FragmentLiveEventBinding.inflate(inflater, container, false)

        liveBinding.apply {
            extendTime.setOnClickListener {
                openCustomDialog()
            }

            endEvent.setOnClickListener {
                openBottomSheet()
            }

            live.setOnClickListener {
                openCustomDialogDuration()
            }
            title2.setOnClickListener {
                openCustomDialogRemainingTime()
            }
            registeredUser.setOnClickListener {
                view?.findNavController()!!
                    .navigate(R.id.action_liveEventFragment_to_registeredUsersFragment)
            }

        }
        return liveBinding.root
    }

    private fun openCustomDialogRemainingTime() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_remining_event)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }

    private fun openCustomDialogDuration() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_duration_box)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }

    private fun openBottomSheet() {
        val eventEndBinding = BottomsheetEndEventBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())

        bottomSheetDialog.setContentView(eventEndBinding.root)

        bottomSheetDialog.show()

        eventEndBinding.imageBSDateTimeClose.setOnClickListener {
            bottomSheetDialog.hide()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openCustomDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_time_picker)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var cancelBtn = dialog.findViewById<AppCompatTextView>(R.id.tvDialogCancel)
        var okBtn = dialog.findViewById<AppCompatTextView>(R.id.tvDialogOk)
        var timePicker = dialog.findViewById<TimePicker>(R.id.timePickerDialog)

        val hour = timePicker.hour
        val minute = timePicker.minute

        time = "$hour:$minute"

        timePicker.setOnTimeChangedListener { _, hour, minute ->

            time = "$hour:$minute"

//            var hour = hour
//            var am_pm = ""
//            when {
//                hour == 0 -> {
//                    hour += 12
//                    am_pm = "AM"
//                }
//                hour == 12 -> am_pm = "PM"
//                hour > 12 -> {
//                    hour -= 12
//                    am_pm = "PM"
//                }
//                else -> am_pm = "AM"
//            }
//            val hours = if (hour < 10) "0$hour" else hour
//            val min = if (minute < 10) "0$minute" else minute
//
        }
        okBtn.setOnClickListener {
            showToast(requireContext(), time)
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

}