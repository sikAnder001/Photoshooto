package com.photoshooto.ui.userhomepage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.BottomsheetSelectDatesBinding
import com.photoshooto.databinding.DialogDatePickerBinding
import com.photoshooto.databinding.DialogTimePickerBinding
import com.photoshooto.databinding.FragmentEnquiryFormBottomsheetBinding
import com.photoshooto.domain.model.GetEventTypesResponse
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class UserCreateEnquiryFragment : BottomSheetDialogFragment() {

    companion object {
        var referralScreen: String? = null

        @JvmStatic
        private fun getInstance() = UserCreateEnquiryFragment()

        @JvmStatic
        fun showDialog(
            fm: FragmentManager,
            tag: String
        ) {
            if (!fm.isStateSaved) {
                val dialog = getInstance()
                dialog.isCancelable = false
                referralScreen = tag
                dialog.show(fm, tag)
            }
        }
    }

    // Global
    private lateinit var binding: FragmentEnquiryFormBottomsheetBinding
    private var mStartDate = ""
    private var mStartDay = ""
    private var mStartTime = ""

    private var mEndDate = ""
    private var mEndDay = ""
    private var mEndTime = ""

    private var mEventDuration = ""

    private var mSelectedLocationAddress = ""
    private val mViewModel: CreateEventViewModel by viewModel()

    private var mListEventTypesMain =
        arrayListOf<GetEventTypesResponse.GetEventTypesData.GetEventTypesModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener {
                (it as? BottomSheetDialog)?.behavior?.apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isDraggable = false
                    peekHeight = 0
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnquiryFormBottomsheetBinding
            .inflate(
                inflater,
                container, false
            )

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        setListener()
    }

    /**
     * sets up view
     */
    private fun setupView() {
        setObserverApis()

        mViewModel.getEventTypeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
    }

    private fun setObserverApis() {
        with(mViewModel) {
            getEventTypeListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListEventTypesMain = arrayListOf()
                    if (it.data != null) {
                        if (!it.data!!.list.isNullOrEmpty()) {
                            mListEventTypesMain.addAll(it.data!!.list!!)
                        }
                    }
                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }
        }
    }


    /**
     * Set Listeners
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListener() {
        /* binding.dateText.setOnClickListener {
             if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                 if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
                     openSelectDateAndTimeDialog()
                 } else {
                     openDatePickerDialog("end") { date, day ->
                         mEndDate = date
                         mEndDay = day
                         setDurationBetweenDates()
                     }
                 }
             }
         }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openSelectDateAndTimeDialog() {
        var isEdit = false
        val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = BottomsheetSelectDatesBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvSelectEventStartDate.text =
            checkStringReturnValue(mStartDate, "__/__/____")
        mDialogBinding.tvSelectDateStartDay.setText(checkStringReturnValue(mStartDay))
        mDialogBinding.tvSelectDateStartTime.setText(checkStringReturnValue(mStartTime, "__:__"))

        mDialogBinding.tvSelectEventEndDate.text = checkStringReturnValue(mEndDate, "__/__/____")
        mDialogBinding.tvSelectDateEndDay.setText(checkStringReturnValue(mEndDay))
        mDialogBinding.tvSelectDateEndTime.setText(checkStringReturnValue(mEndTime, "__:__"))

        setDurationBetweenDates()
        mDialogBinding.tvLabelDuration.text = mEventDuration

        mDialogBinding.imageBSDateTimeClose.setOnClickListener {
            dialog.dismiss()
        }

        mDialogBinding.tvSelectEventStartDate.setOnClickListener {
            if (isEdit) {
                openDatePickerDialog("start") { date, day ->
                    mDialogBinding.tvSelectEventStartDate.text = date
                    mDialogBinding.tvSelectDateStartDay.setText(day)

                    mStartDate = date
                    mStartDay = day

                    setDateAndTime()
                    setDurationBetweenDates()
                    mDialogBinding.tvLabelDuration.text = mEventDuration
                }
            }
        }

        mDialogBinding.tvSelectEventEndDate.setOnClickListener {
            if (isEdit) {
                if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                    openDatePickerDialog("end") { date, day ->
                        mDialogBinding.tvSelectEventEndDate.text = date
                        mDialogBinding.tvSelectDateEndDay.setText(day)

                        mEndDate = date
                        mEndDay = day

                        setDateAndTime()
                        setDurationBetweenDates()
                        mDialogBinding.tvLabelDuration.text = mEventDuration
                    }
                }
            }
        }

        mDialogBinding.tvSelectDateStartTime.setOnClickListener {
            if (isEdit) {
                openTimePickerDialog {
                    mStartTime = it
                    mDialogBinding.tvSelectDateStartTime.setText(it)

                    setDurationBetweenDates()
                    mDialogBinding.tvLabelDuration.text = mEventDuration
                }
            }
        }

        mDialogBinding.tvSelectDateEndTime.setOnClickListener {
            if (isEdit) {
                if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                    openTimePickerDialog {
                        mEndTime = it
                        mDialogBinding.tvSelectDateEndTime.setText(it)

                        setDurationBetweenDates()
                        mDialogBinding.tvLabelDuration.text = mEventDuration
                    }
                }
            }
        }

        mDialogBinding.tvBSEdit.setOnClickListener {
            isEdit = true
            onToast("Please click on date and time for update", requireActivity())
        }

        mDialogBinding.tvBSConfirm.setOnClickListener {
            setDateAndTime()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setDateAndTime() {
        /*if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
            binding.tvSelectEventStartTime.text = "$mStartDate $mStartTime"
        }

        if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
            mBinding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
        }*/
    }

    private fun setDurationBetweenDates() {
        if (checkStringValue(mStartDate) && checkStringValue(mStartTime) && checkStringValue(
                mEndDate
            ) && checkStringValue(mEndTime)
        ) {
            val dateFrom = SimpleDateFormat("dd/MM/yyyy hh:mm a").parse("$mStartDate $mStartTime")
            val dateTo = SimpleDateFormat("dd/MM/yyyy hh:mm a").parse("$mEndDate $mEndTime")
            val diff = dateTo.time - dateFrom.time
            val second = diff / 1000
            val minute = second / 60
            val hour = minute / 60

            mEventDuration = "${(hour.toInt())}h:${(minute % 60).toInt()}min"

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openDatePickerDialog(type: String, onDateSelected: (String, String) -> Unit) {
        val dialog = Dialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = DialogDatePickerBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        val cal = Calendar.getInstance()
        mDialogBinding.datePickerDialog.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        mDialogBinding.tvDialogOk.setOnClickListener {
            onDateSelected.invoke(
                SimpleDateFormat("dd/MM/yyyy").format(cal.time),
                SimpleDateFormat("EEEE").format(cal.time)
            )
            dialog.dismiss()
            if (type == "start") {
                if (!checkStringValue(mStartTime)) {
                    openTimePickerDialog {
                        mStartTime = it
                        setDurationBetweenDates()
                    }
                }
            } else {
                if (!checkStringValue(mEndTime)) {
                    openTimePickerDialog {
                        mEndTime = it
                        setDurationBetweenDates()
                    }
                }
            }
            setDateAndTime()
        }

        dialog.show()
    }

    private fun openTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val dialog = Dialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = DialogTimePickerBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }

        val cal = Calendar.getInstance()
        mDialogBinding.timePickerDialog.setOnTimeChangedListener { _, hours, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hours)
            cal.set(Calendar.MINUTE, minute)
        }

        mDialogBinding.tvDialogOk.setOnClickListener {
            onTimeSelected.invoke(SimpleDateFormat("hh:mm a").format(cal.time))
            setDateAndTime()
            dialog.dismiss()
        }

        dialog.show()
    }

}