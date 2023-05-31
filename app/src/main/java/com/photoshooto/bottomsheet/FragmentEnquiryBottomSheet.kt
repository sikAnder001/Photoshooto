package com.photoshooto.bottomsheet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.*
import com.photoshooto.domain.model.AddEnquiryReqModel
import com.photoshooto.domain.model.GetEventTypesResponse
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.feedback.SuccessDialog
import com.photoshooto.ui.purchase.PaymentSuccessDialog
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.ui.qrcodesetup.createEvent.adapter.SelectFolderAndTypeAdapter
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentEnquiryBottomSheet : BottomSheetDialogFragment(), SuccessDialog.Listener {

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private var mStartDate = ""
    private var mStartDay = ""
    private var mStartTime = ""

    private var mEndDate = ""
    private var mEndDay = ""
    private var mEndTime = ""

    private var mEventDuration = ""
    private var mSelectedEventType: GetEventTypesResponse.GetEventTypesData.GetEventTypesModel? =
        null
    private var mSelectedEventTypeAdapter: SelectFolderAndTypeAdapter? = null
    private var mListEventPhotographyMain =
        arrayListOf<GetEventTypesResponse.GetEventTypesData.GetEventTypesModel>()
    private val mViewModel: CreateEventViewModel by viewModel()
    var isValidEndDate: Boolean = true

    companion object {

        var userID: String? = null

        @JvmStatic
        private fun newInstance() = FragmentEnquiryBottomSheet()

        @JvmStatic
        fun showDialog(
            userId: String?,
            fm: FragmentManager,
            tag: String
        ) {
            if (!fm.isStateSaved) {
                userID = userId
                newInstance().apply {
                    isCancelable = false
                    show(fm, tag)
                }
            }
        }
    }

    // Global
    private val TAG = FragmentEnquiryBottomSheet::class.java.simpleName
    private lateinit var binding: FragmentEnquiryFormBottomsheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.RoundedBottomSheetDialogStyle)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_enquiry_form_bottomsheet,
            container, false
        )

        if (!userID.isNullOrEmpty()) {
            profileViewModel.getProfileByIDUseCase(
                userID,
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
            )
        }
        mViewModel.getEventTypeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

        if (!userID.isNullOrEmpty()) {
            with(profileViewModel) {
                getUserData.observe(requireActivity()) { response ->
                    if (response != null) {
                        when (response.status) {
                            Status.SUCCESS -> {
                            }
                            Status.ERROR -> {
                                response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        with(mViewModel) {
            getEventTypeListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListEventPhotographyMain = arrayListOf()
                    if (it.data != null) {
                        if (!it.data!!.list.isNullOrEmpty()) {
                            mListEventPhotographyMain.addAll(it.data!!.list!!)
                        }
                    }

                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }
        }

        init()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        binding.edtName.setText(
            SharedPrefsHelper.read(SharedPrefConstant.USER_NAME).toString()
        )
        binding.edtPhone.setText(
            SharedPrefsHelper.read(SharedPrefConstant.MOBILE_NUMBER).toString()
        )

        binding.tvEventType.setOnClickListener {
            openSelectPhotographerTypeDialog { binding.tvEventType.text = it }
        }
        binding.icClose.setOnClickListener { dismiss() }

        binding.tvSelectEventStartTime.setOnClickListener {
            if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                openSelectDateAndTimeDialog()
            } else {
                openDatePickerDialog("start") { date, day ->
                    mStartDate = date
                    mStartDay = day
                    setDateAndTime()
                    if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                        binding.tvSelectEventStartTime.text = "$mStartDate $mStartTime"
                    }

                    if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
                        binding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
                    }
                    setDurationBetweenDates()
                }
            }
        }

        binding.tvSelectEventEndTime.setOnClickListener {
            if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
                    openSelectDateAndTimeDialog()
                } else {
                    openDatePickerDialog("end") { date, day ->
                        mEndDate = date
                        mEndDay = day
                        binding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
                        setDurationBetweenDates()
                    }
                }
            }
        }

        val fragmentActivity = activity
        if (fragmentActivity != null) {
            profileViewModel.addEnquiryResponse.observe(fragmentActivity) {
                if (it.success!!) {
                    SuccessDialog.newInstance(it.message!!, this)
                        .show(fragmentActivity.supportFragmentManager, PaymentSuccessDialog.TAG)
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            if (isValidEndDate) {
                if (profileViewModel.showProgressbar.value == true) {
                    return@setOnClickListener
                }
                if (binding.edtName.text.toString().isEmpty()) {
                    onToast("Please enter name", requireActivity())
                } else if (binding.edtLocation.text.toString().isEmpty()) {
                    onToast("Please enter location", requireActivity())
                } else if (binding.edtLocality.text.toString().isEmpty()) {
                    onToast("Please enter locality", requireActivity())
                } else if (binding.tvEventType.text.toString().isEmpty()) {
                    onToast("Please select event type", requireActivity())
                } else if (binding.tvSelectEventStartTime.text.toString().isEmpty()) {
                    onToast("Please select start time", requireActivity())
                } else if (binding.tvSelectEventEndTime.text.toString().isEmpty()) {
                    onToast("Please select end time", requireActivity())
                } else {
                    val arrayList = ArrayList<String>()
                    arrayList.add(mSelectedEventType!!.type!!)

                    val photographerId = if (!userID.isNullOrEmpty()) {
                        profileViewModel.getUserData.value?.data?.data?.id!!
                    } else ""

                    val addEnquiryReqModel =
                        AddEnquiryReqModel(
                            name = getStringFromEditText(binding.edtName),
                            mobile = getStringFromEditText(binding.edtPhone),
                            location = getStringFromEditText(binding.edtLocation),
                            locality = getStringFromEditText(binding.edtLocality),
                            event_type = arrayList,
                            start_date = mStartDate,
                            end_date = mEndDate,
                            budget = getStringFromEditText(binding.edtBudget),
                            start_time = mStartTime,
                            end_time = mEndTime,
                            photographer_id = photographerId
                        )
                    profileViewModel.addEnquiryAPI(addEnquiryReqModel)
                }
            } else {
                Toast.makeText(
                    context,
                    "End date-time cannot be less than start date-time",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openSelectPhotographerTypeDialog(onDataSelected: (String) -> Unit) {
        val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = BottomsheetSelectFolderBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvBSTitle.text = "Event Type"
        val mListEventTypes = arrayListOf<String>()
        mListEventTypes.addAll(mListEventPhotographyMain.map { checkStringReturnValue(it.type) })

        val selectedName = if (mSelectedEventType != null) {
            checkStringReturnValue(mSelectedEventType!!.type)
        } else {
            ""
        }
        mSelectedEventTypeAdapter = SelectFolderAndTypeAdapter(
            requireActivity(),
            mListEventTypes,
            selectedName,
            object : OnItemClick<String> {
                override fun onItemClick(model: String, position: Int) {
                    val eventTypeModel = mListEventPhotographyMain[position]
                    mSelectedEventType = eventTypeModel
                    onDataSelected.invoke(checkStringReturnValue(mSelectedEventType!!.type))
                    dialog.dismiss()
                }
            })
        mDialogBinding.recyclerViewItems.apply {
            adapter = mSelectedEventTypeAdapter
        }

        mDialogBinding.imageBSNewFolderClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

            isValidEndDate = !(hour.toInt() < 0 || minute < 0)
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


        if (type == "start") {
            mDialogBinding.datePickerDialog.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        } else {
            mDialogBinding.datePickerDialog.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            if (mStartDate.isNotEmpty() && mStartTime.isNotEmpty()) {
                val startDateinMillis = getDateTimeStamp("$mStartDate $mStartTime")
                mDialogBinding.datePickerDialog.minDate = startDateinMillis
            }
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

    fun getDateTimeStamp(date: String): Long {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        val oldDate: Date = dateFormat.parse(date)

        return oldDate.time
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

    private fun setDateAndTime() {
        if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
            binding.tvSelectEventStartTime.text = "$mStartDate $mStartTime"
        }

        if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
            binding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
        }
    }

    fun onBackPressed() {
        dismiss()
    }

    override fun goToHome() {
        onBackPressed()
    }

}