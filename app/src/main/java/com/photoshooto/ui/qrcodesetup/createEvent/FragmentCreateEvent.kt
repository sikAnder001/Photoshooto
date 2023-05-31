package com.photoshooto.ui.qrcodesetup.createEvent


import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.*
import com.photoshooto.domain.model.FolderModel
import com.photoshooto.domain.model.GetEventTypesResponse
import com.photoshooto.domain.model.StandeeElement
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateEventRequest
import com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel.CreateFolderRequest
import com.photoshooto.ui.qrcodesetup.createEvent.adapter.GeneratedQrCodesAdapter
import com.photoshooto.ui.qrcodesetup.createEvent.adapter.SelectFolderAndTypeAdapter
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressWarnings("All")
class FragmentCreateEvent : Fragment() {

    private lateinit var mBinding: FragmentCreateEventBinding

    private val mViewModel: CreateEventViewModel by viewModel()

    private var navController: NavController? = null

    private var mListFolders = arrayListOf<FolderModel?>()
    private var mListStandee = arrayListOf<StandeeElement>()
    private var mListEventTypesMain =
        arrayListOf<GetEventTypesResponse.GetEventTypesData.GetEventTypesModel>()

    private var mAdapterQrCodes: GeneratedQrCodesAdapter? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var mStartDate = ""
    private var mStartDay = ""
    private var mStartTime = ""

    private var mEndDate = ""
    private var mEndDay = ""
    private var mEndTime = ""

    private var mEventDuration = ""

    private var mSelectedLocationAddress = ""

    private var mQRSelected = -1

    private lateinit var dialogCreateFolder: BottomSheetDialog

    private var mSelectedFolder: FolderModel? = null
    private var mSelectedFolderAdapter: SelectFolderAndTypeAdapter? = null

    private var mSelectedEventType: GetEventTypesResponse.GetEventTypesData.GetEventTypesModel? =
        null
    private var mSelectedEventTypeAdapter: SelectFolderAndTypeAdapter? = null

    private lateinit var mCreateEventRequest: CreateEventRequest

    private var viewMain: View? = null

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (viewMain == null) {
            mBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_create_event, container, false)
            viewMain = mBinding.root
        }
        return viewMain!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        setObserverApis()
        setClickEvent()

        callRequiredApis()
    }

    private fun callRequiredApis() {
        mViewModel.getFolderList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        mViewModel.getStandeeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        mViewModel.getEventTypeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
    }

    private fun setObserverApis() {
        with(mViewModel) {
            getFolderListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListFolders = arrayListOf()
                    if (it.data != null) {
                        if (!it.data.list.isNullOrEmpty()) {
                            mListFolders.addAll(it.data.list)
                        }
                    }

                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }

            getStandeeListResponse.observe(requireActivity()) {
                if (it.success) {
                    mListStandee = arrayListOf()
                    if (it.data != null) {
                        if (!it.data.list.isNullOrEmpty()) {
                            mListStandee.addAll(it.data.list)
                        }
                    }

                    setGeneratedQRCodes()

                } else {
                    onToast(it.message, requireActivity())
                }
            }

            getEventTypeListResponse.observe(requireActivity()) {
                if (it.success!!) {
                    mListEventTypesMain = arrayListOf()
                    if (it.data != null) {
                        if (!it.data!!.list.isNullOrEmpty()) {
                            mListEventTypesMain.addAll(it.data!!.list!!)
                        }
                    }

                    //setGeneratedQRCodes()

                } else {
                    it.message?.let { it1 -> onToast(it1, requireActivity()) }
                }
            }

            createFolderResponse.observe(requireActivity()) {
                if (it.success!!) {
                    if (::dialogCreateFolder.isInitialized) {
                        if (dialogCreateFolder.isShowing) {
                            dialogCreateFolder.dismiss()
                        }
                    }
                    mViewModel.getFolderList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
                }
                it.message?.let { it1 -> onToast(it1, requireActivity()) }
            }
            showProgressbar.observe(requireActivity()) { isVisible ->
                mBinding.progressBarCommon.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setGeneratedQRCodes() {
        if (mListStandee.isNotEmpty()) {
            mAdapterQrCodes = GeneratedQrCodesAdapter(
                requireActivity(),
                mListStandee,
                mQRSelected,
                object : OnItemClick<StandeeElement> {
                    override fun onItemClick(
                        model: StandeeElement,
                        position: Int
                    ) {
                        mQRSelected = position
                    }
                })
            mBinding.recyclerViewGeneratedQr.apply {
                adapter = mAdapterQrCodes
            }
        }
    }

    private fun setClickEvent() {
        mBinding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.tvFindMyEventLocation.setOnClickListener {
            getLocation()
        }

        mBinding.linearCreateNewFolder.setOnClickListener {
            openCreateNewFolderDialog()
        }

        mBinding.linearSelectFolder.setOnClickListener {
            openSelectFolderDialog {
                mBinding.tvSelectFolderTitle.text = it
            }
        }

        mBinding.linearSelectEventType.setOnClickListener {
            openSelectEventDialog {
                mBinding.tvSelectEventTitle.text = it
            }
        }

        mBinding.tvSelectEventStartTime.setOnClickListener {
            if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                openSelectDateAndTimeDialog()
            } else {
                openDatePickerDialog("start") { date, day ->
                    mStartDate = date
                    mStartDay = day
                    setDateAndTime()
                    setDurationBetweenDates()
                }
            }
        }

        mBinding.tvSelectEventEndTime.setOnClickListener {
            if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
                if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
                    openSelectDateAndTimeDialog()
                } else {
                    openDatePickerDialog("end") { date, day ->
                        mEndDate = date
                        mEndDay = day
                        mBinding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
                        setDurationBetweenDates()
                    }
                }
            }
        }

        mBinding.btnProceed.setOnClickListener {
            /*val countryCode = mBinding.countryPicker.selectedCountryCode
            onToast(countryCode, requireActivity())*/
            checkValidation {
                val directions = R.id.actionFragmentCreateEventToEventSetupFragment

                val bundle = Bundle()
                bundle.putParcelable("mCreateEventRequest", mCreateEventRequest)
                bundle.putString("mFolderName", checkStringReturnValue(mSelectedFolder!!.name))
                bundle.putString("mEventDuration", mEventDuration)
                bundle.putString(
                    "mImage",
                    mListStandee[mAdapterQrCodes!!.mPositionSelect].qrcode!![0]!!.url
                )

                findNavController().navigate(
                    R.id.actionFragmentCreateEventToEventSetupFragment,
                    bundle
                )

                /*navController?.navigate(
                    FragmentCreateEventDirections.actionCreateEventToSetUpEvent(
                        mCreateEventRequest,
                        checkStringReturnValue(mSelectedFolder!!.name),
                        mEventDuration,
                        mListStandee[mAdapterQrCodes!!.mPositionSelect].qrcode!![0]!!.url
                    )
                )*/
            }
        }
    }

    private fun openCreateNewFolderDialog() {
        dialogCreateFolder = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialogCreateFolder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCreateFolder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCreateFolder.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val mDialogBinding = BottomsheetCreateNewFolderBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialogCreateFolder.setContentView(mDialogBinding.root)

        mDialogBinding.imageBSNewFolderClose.setOnClickListener {
            dialogCreateFolder.dismiss()
        }

        mDialogBinding.btnCreate.setOnClickListener {
            mDialogBinding.tvErrorFolderName.hide()
            if (checkStringValue(mDialogBinding.ednCreateFolderName.editableText.toString())) {
                mViewModel.callCreateFolderApi(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
                    CreateFolderRequest(mDialogBinding.ednCreateFolderName.editableText.toString())
                )
            } else {
                mDialogBinding.tvErrorFolderName.apply {
                    text = requireActivity().getStringValue(R.string.validation_enter_folder_name)
                    show()
                }
            }
        }

        dialogCreateFolder.show()
    }

    private fun openSelectFolderDialog(onDataSelected: (String) -> Unit) {
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

        val mListSelectFolder = arrayListOf<String>()
        mListSelectFolder.addAll(mListFolders.map { checkStringReturnValue(it!!.name) })

        val selectedName = if (mSelectedFolder != null) {
            checkStringReturnValue(mSelectedFolder!!.name)
        } else {
            ""
        }
        mSelectedFolderAdapter = SelectFolderAndTypeAdapter(
            requireActivity(),
            mListSelectFolder,
            selectedName,
            object : OnItemClick<String> {
                override fun onItemClick(model: String, position: Int) {
                    val folderModel = mListFolders[position]
                    mSelectedFolder = folderModel
                    onDataSelected.invoke(checkStringReturnValue(mSelectedFolder!!.name))
                    dialog.dismiss()
                }
            })

        mDialogBinding.recyclerViewItems.apply {
            adapter = mSelectedFolderAdapter
        }

        mDialogBinding.imageBSNewFolderClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun checkValidation(validate: () -> Unit) {
        mBinding.tvErrorSelectFolder.hide()
        mBinding.tvErrorSelectEventType.hide()
        mBinding.tvErrorEventName.hide()
        mBinding.tvErrorClientName.hide()
        mBinding.tvErrorClientNumber.hide()

        if (!checkStringValue(mSelectedLocationAddress)) {
            onToast("Please select location", requireActivity())
            return
        }
        if (mSelectedFolder == null) {
            mBinding.tvErrorSelectFolder.show()
            return
        }

        if (mAdapterQrCodes != null && mAdapterQrCodes!!.mPositionSelect == (-1)) {
            onToast("Please select Generated QR code", requireActivity())
            return
        }

        if (mAdapterQrCodes != null && mListStandee[mAdapterQrCodes!!.mPositionSelect].qrcode.isNullOrEmpty()) {
            onToast("Selected Generat QR code have not QR id", requireActivity())
            return
        }

        if (mSelectedEventType == null) {
            mBinding.tvErrorSelectEventType.show()
            return
        }

        if (!checkStringValue(mBinding.edSelectEventName.editableText.toString())) {
            mBinding.tvErrorEventName.show()
            return
        }

        if (!checkStringValue(mStartDate) || !checkStringValue(mStartTime)) {
            onToast("Please select Event Start Data and Time", requireActivity())
            return
        }

        if (!checkStringValue(mEndDate) || !checkStringValue(mEndTime)) {
            onToast("Please select Event End Data and Time", requireActivity())
            return
        }

        if (!checkStringValue(mBinding.edClientName.editableText.toString())) {
            mBinding.tvErrorClientName.show()
            return
        }

        if (!checkStringValue(mBinding.edMobileNumber.editableText.toString())) {
            mBinding.tvErrorClientNumber.apply {
                text = requireActivity().getStringValue(R.string.enter_mobile_empid)
                show()
            }
            return
        }

        if (!mBinding.edMobileNumber.editableText.toString().isValidMobileNumber()) {
            mBinding.tvErrorClientNumber.apply {
                text = requireActivity().getStringValue(R.string.error_mobile_num)
                show()
            }
            return
        }

        mCreateEventRequest = CreateEventRequest(
            project_id = mSelectedFolder!!.id,
            event_name = mBinding.edSelectEventName.getTextValue(),
            event_type = mSelectedEventType!!.type,
            event_start_date = mStartDate,
            event_end_date = mEndDate,
            event_start_time = mStartTime,
            event_end_time = mEndTime,
            location = mSelectedLocationAddress,
            standee_type = mListStandee[mQRSelected].type,
            qrcode_id = mListStandee[mAdapterQrCodes!!.mPositionSelect].qrcode!![0]!!.id,
            client_name = mBinding.edClientName.getTextValue(),
            client_contact_number = "${mBinding.countryPicker.selectedCountryCodeWithPlus} ${mBinding.edMobileNumber.getTextValue()}",
        )

        validate.invoke()
    }

    private fun setDateAndTime() {
        if (checkStringValue(mStartDate) && checkStringValue(mStartTime)) {
            mBinding.tvSelectEventStartTime.text = "$mStartDate $mStartTime"
        }

        if (checkStringValue(mEndDate) && checkStringValue(mEndTime)) {
            mBinding.tvSelectEventEndTime.text = "$mEndDate $mEndTime"
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
                    val location: android.location.Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val completeAddress = list?.get(0)

                        if (completeAddress != null) {


                            val address: String =
                                completeAddress.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            val society: String =
                                checkStringReturnValue(completeAddress.subLocality) + ""
                            val city: String = checkStringReturnValue(completeAddress.locality) + ""

                            mSelectedLocationAddress = city
                            mBinding.tvCreateEventLocationName.text = city
                            val state: String =
                                checkStringReturnValue(completeAddress.adminArea) + ""
                            val postalCode: String =
                                checkStringReturnValue(completeAddress.postalCode) + ""
                            val featureName: String =
                                completeAddress.featureName + ""

                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun openSelectEventDialog(onDataSelected: (String) -> Unit) {
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

        val mListEventTypes = arrayListOf<String>()
        mListEventTypes.addAll(mListEventTypesMain.map { checkStringReturnValue(it.type) })

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
                    val eventTypeModel = mListEventTypesMain[position]
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

        }
    }

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

    private fun openSetTimerDialog() {
        val dialog = Dialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = DialogSetTimerBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        mDialogBinding.tvDialogCancel.setOnClickListener {
            dialog.dismiss()
        }
        mDialogBinding.tvDialogOk.setOnClickListener {
            val time =
                "${mDialogBinding.scrollHmsPicker.hours} hr : ${mDialogBinding.scrollHmsPicker.minutes} min"
            Toast.makeText(requireActivity(), time, Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private var timerCountDown: CountDownTimer? = null
    private val totalMilliSecond: Long = 100 * 1000
    private fun openViewTimerDialog() {
        val dialog = Dialog(requireActivity(), R.style.BottomSheetDialogEvent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mDialogBinding = DialogShowTimerBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        dialog.setContentView(mDialogBinding.root)

        if (timerCountDown != null) {
            timerCountDown!!.cancel()
        }

        timerCountDown = object : CountDownTimer(totalMilliSecond, 1000) {
            override fun onTick(milliSecond: Long) {
                val totalSecs = milliSecond / 1000

                val hours = totalSecs / 3600
                val minutes = (totalSecs % 3600) / 60
                val seconds = totalSecs % 60

                mDialogBinding.tvBSTimeRemain.text =
                    "${hours.convertToTimeFormat()}:${minutes.convertToTimeFormat()}:${seconds.convertToTimeFormat()}"

                val percentage = ((milliSecond * 100) / totalMilliSecond)
                mDialogBinding.circularProgressbar.progress = percentage.toFloat()
                Log.e("AppName", "onTick: $percentage")
            }

            override fun onFinish() {
                mDialogBinding.circularProgressbar.progress = 0f
                mDialogBinding.tvBSTimeRemain.text = "00:00:00"
            }
        }.start()

        mDialogBinding.circularProgressbar.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        dialog.show()
    }
}


fun Long.convertToTimeFormat(): String {
    val stString = "$this"
    return if (stString.length < 2) {
        "0$stString"
    } else {
        stString
    }
}