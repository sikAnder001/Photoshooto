package com.photoshooto.ui.job.fragments

import android.Manifest
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.text.trimmedLength
import androidx.core.view.forEachIndexed
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.DialogTimePickerBinding
import com.photoshooto.databinding.FragmentJobCreateBinding
import com.photoshooto.domain.model.JobModel
import com.photoshooto.domain.model.PostJobPRQ
import com.photoshooto.domain.model.SpinnerModel
import com.photoshooto.ui.dialog.*
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.SpinnerCustomAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class JobCreateFragment : BaseFragment() {

    private var time: String = ""
    private var sDate: String = ""
    private var eDate: String = ""
    private var sTime: String = ""
    private var sTime2: String = ""
    lateinit var binding: FragmentJobCreateBinding
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var spinnerAdapter: SpinnerCustomAdapter
    var arrayPhotographerTypes = arrayListOf<String>()
    var eventExpertiseTypes = arrayListOf<String>()
    private val navArgs: JobCreateFragmentArgs by navArgs()
    var formAction = "create"
    lateinit var prefillJObModel: JobModel
    var spinnerList = arrayListOf<SpinnerModel>()
    var mobNumberChipId = 1
    var locChipId = 1
    var cameraChipId = 1
    var videoChipId = 1
    var hireType = ""   //hireme / hireyou
    var otherChipId = 1
    var latitude = 0.0
    var longitude = 0.0
    var photographerCount = 1
    var arrayContact = arrayListOf<Long>()
    var arrayPhotoCam = arrayListOf<String>()
    var arrayLocation = arrayListOf<String>()
    var arrayVideoCam = arrayListOf<String>()
    var arrayOtherReq = arrayListOf<String>()

    private var fromDash = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJobCreateBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarCreateJob
        fromDash = arguments?.getString("fromDash", "").toString()
        if (!fromDash.isNullOrEmpty()) {
            backPress()
        }
        val backBtn = toolbarBinding.backBtn
        backBtn.setOnClickListener {
            if (binding.btnProceedOrPost.text.contentEquals("Post Job") ||
                binding.btnProceedOrPost.text.contentEquals("Update Job")
            ) {
                showFirstPage()
            } else {
                // requireActivity().onBackPressed()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val titleTxt = toolbarBinding.tvTitle
        titleTxt.text = "Post Job"

        return binding.root
    }

    private fun backPress() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val args = Bundle().apply {
                        putString("fromDash", "yes")
                        putString("userId", arguments!!.getString("userId"))
                        putString("userName", arguments!!.getString("userName"))
                        putString("userAddress", arguments!!.getString("userAddress"))
                        putString(
                            "userProfile",
                            arguments!!.getString("userProfile")
                        )
                        putBoolean(
                            "userSubscribe", arguments!!.getBoolean("userSubscribe")
                        )
                    }
                    view?.findNavController()
                        ?.navigate(R.id.action_jobCreate_to_calendarFragment, args)
                }
            }
            )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hireType = navArgs.jobType
        formAction = navArgs.action

        binding.radioGroupJobType.setOnCheckedChangeListener { _, id ->
            requireContext().hideKeyboardFrom(binding.radioGroupJobType)
            if (id == R.id.radioButtonPost) {

                binding.groupJob1.visible()
                binding.groupJob2.visible()
                binding.txtEventExpertise.gone()
                binding.etEventExpertise.gone()
                binding.txtAddLoc.gone()
                binding.txtViewMaps.gone()
                binding.chipGroupLocation.invisible()
                binding.location.isEnabled = true
                binding.location.setImageResource(R.drawable.ic_baseline_my_location_24)

                binding.txtCameraRequired.text = getString(R.string.photo_camera_required)
                binding.txtVideoRequired.text = getString(R.string.video_camera_required)
                binding.txtOtherRequirement.text = getString(R.string.other_equipment_required)
                binding.txtGigDescription.text = getString(R.string.gig_description)
                binding.location.visible()
            } else {

                binding.txtCameraRequired.text = getString(R.string.photo_camera_available)
                binding.txtVideoRequired.text = getString(R.string.video_camera_available)
                binding.txtOtherRequirement.text = getString(R.string.equipments_available)
                binding.txtGigDescription.text = getString(R.string.description)

                //  binding.location.setImageResource(R.drawable.down_arrow_dark)
                binding.location.invisible()
                binding.location.isEnabled = false
                binding.groupJob1.gone()
                binding.groupJob2.gone()
                binding.txtAddLoc.visible()
                binding.txtEventExpertise.visible()
                binding.etEventExpertise.visible()
                binding.txtViewMaps.gone()
                binding.chipGroupLocation.visible()
            }
        }

        if (hireType.equals(PostType.GET_HIRED.toString(), true)) {
            binding.radioButtonHiredAsPhotographer.isChecked = true
        } else {
            binding.radioButtonPost.isChecked = true
        }

        binding.location.setSafeOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()
        }

        showFirstPage()

        spinnerList.clear()
        spinnerList.add(SpinnerModel("Free       ", false))
        spinnerList.add(SpinnerModel("Promote       ", true))

        attachObserver()

        jobHomeViewModel.photographerTypesList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        jobHomeViewModel.getEventType(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

        binding.gigDescription.doAfterTextChanged {
            binding.txtGigDescriptionSize.text = "${it.toString().length} / 400"

            if (it.toString().length > 390) {
                binding.txtGigDescriptionSize.setTextColor(requireContext().resources.getColor(R.color.colorRed))
            } else {
                binding.txtGigDescriptionSize.setTextColor(requireContext().resources.getColor(R.color.colorBlack))
            }
        }

        binding.tvEventStartDate.setSafeOnClickListener {
            DateTimeDialog.showDateDialog(requireContext()) { date ->
                val tmp = date.getFormattedDatetime(
                    inputFormat = "yyyy-MM-dd", outputFormat = "dd-MM-yyyy"
                )
                sDate = tmp
                binding.tvEventStartDate.text = tmp
            }
        }

        binding.tvEventEndDate.setSafeOnClickListener {
            DateTimeDialog.showDateDialog(requireContext()) { date ->
                val tmp = date.getFormattedDatetime(
                    inputFormat = "yyyy-MM-dd", outputFormat = "dd-MM-yyyy"
                )
                eDate = tmp
                binding.tvEventEndDate.text = tmp
            }
        }

        binding.tvEventStartTimePicker.setSafeOnClickListener {
            DateTimeDialog.showTimeDialog(requireContext()) { time ->
                binding.tvEventStartTimePicker.text = time
                sTime = get24Time(time)
                sTime2 = time
            }
        }


        binding.tvEventEndTimePicker.setSafeOnClickListener {
            if (sDate.isEmpty()) {
                Toast.makeText(context, "Please select Event Start Date", Toast.LENGTH_SHORT).show()
            } else if (sTime.isEmpty()) {
                Toast.makeText(context, "Please select Event Start Time", Toast.LENGTH_SHORT).show()
            } else if (eDate.isEmpty()) {
                Toast.makeText(context, "Please select Event End Date", Toast.LENGTH_SHORT).show()
            } else timeChangeListener()
            /* DateTimeDialog.showTimeDialog(requireContext()) { time ->
                    binding.tvEventEndTimePicker.text = time
                }*/
        }

        binding.tvSpinnerJobType.setSafeOnClickListener {
            binding.spinnerPostJob.performClick()
        }

        spinnerAdapter = SpinnerCustomAdapter(requireContext(), spinnerList) { pos ->
            binding.tvSpinnerJobType.text = spinnerList[pos].baseName
            spinnerList.forEach {
                it.isSelected = false
            }
            spinnerList[pos].isSelected = true
            hideSpinnerDropDown(binding.spinnerPostJob)
        }

        binding.spinnerPostJob.adapter = spinnerAdapter
        binding.spinnerPostJob.invisible()


        binding.btnProceedOrPost.setSafeOnClickListener {

            if (!formValidatedSuccess()) {
                return@setSafeOnClickListener
            }
            if (binding.btnProceedOrPost.text.contentEquals("Proceed")) {
                showSecondPage()
            } else {

                val postJobModel = PostJobPRQ(
                    type = if (binding.radioButtonPost.isChecked) {
                        "hireyou"
                    } else {
                        "hireme"
                    },
                    jobType = binding.tvSpinnerJobType.text.toString().lowercase().trim(),
                    title = binding.etJobTitle.text.toString(),
                    mobile = arrayContact,
                    address = binding.etJobLocation.text.toString(),
                    cameraUse = arrayPhotoCam,
                    videoUse = arrayVideoCam,
                    otherUse = arrayOtherReq,
                    city = arrayLocation,
                    description = binding.gigDescription.text.toString(),
                    endDate = binding.tvEventEndDate.text.toString().getFormattedDatetime(
                        inputFormat = "dd-MM-yyyy",
                        outputFormat = "yyyy-MM-dd",
                        withZoneCalculation = false
                    ),
                    numberOfPhotographers = photographerCount,
                    endTime =
                    if (binding.tvEventEndTimePicker.text.toString().contains(" ")) {
                        binding.tvEventEndTimePicker.text.toString().lowercase()
                            .getFormattedDatetime(
                                inputFormat = "hh:mm a",
                                outputFormat = "hh:mma",
                                withZoneCalculation = false
                            ).uppercase()
                    } else {
                        binding.tvEventEndTimePicker.text.toString().uppercase()
                    },

                    startTime =
                    if (binding.tvEventStartTimePicker.text.toString().contains(" ")) {
                        binding.tvEventStartTimePicker.text.toString().lowercase()
                            .getFormattedDatetime(
                                inputFormat = "hh:mm a",
                                outputFormat = "hh:mma",
                                withZoneCalculation = false
                            ).uppercase()
                    } else {
                        binding.tvEventStartTimePicker.text.toString().uppercase()
                    },

                    photographyType = binding.tvPhotographerTypeSpinner.text.toString(),
                    startDate = binding.tvEventStartDate.text.toString().getFormattedDatetime(
                        inputFormat = "dd-MM-yyyy",
                        outputFormat = "yyyy-MM-dd",
                        withZoneCalculation = false
                    ),
                    eventType = if (binding.radioButtonPost.isChecked) {
                        binding.etEventType.text.toString()
                    } else {
                        binding.etEventExpertise.text.toString()
                    },
                    latitude = latitude,
                    longitude = longitude
                )

                showProgress()

                if (formAction == "create")
                    jobHomeViewModel.postJobs(
                        postJobModel
                    )
                else {
                    jobHomeViewModel.updatePost(
                        postJobModel, prefillJObModel.id
                    )
                }
            }
        }

        binding.txtViewPlans.setSafeOnClickListener {
            findNavController().navigate(JobCreateFragmentDirections.actionJobCreateToSubscriptionFragment())
        }

        binding.tvPhotographerTypeSpinner.setOnClickListener {
            RadioCustomDialog.showDialog(
                requireContext(), arrayPhotographerTypes, "Photography Types"
            ) {
                binding.tvPhotographerTypeSpinner.text = it
            }
        }

        binding.etEventType.setOnClickListener {
            RadioCustomDialog.showDialog(
                requireContext(), eventExpertiseTypes, "Event Types", false,
            ) {
                binding.etEventType.text = it
            }
        }

        binding.etEventExpertise.setOnClickListener {
            CheckboxCustomDialog.showDialog(
                requireContext(),
                eventExpertiseTypes,
                "Event Expertise"
            ) {

                binding.etEventExpertise.text = it.replace("[", "").replace("]", "")
            }
        }

        binding.txtAddContact.setSafeOnClickListener {
            requireContext().hideKeyboardFrom(it)

            if (!binding.etContactDetails.text.isNullOrBlank() && binding.etContactDetails.text?.length == 10) {
                val chipAddCamera: Chip = layoutInflater.inflate(
                    R.layout.custom_chip_layout, binding.chipGroupContact, false
                ) as Chip
                chipAddCamera.id = mobNumberChipId
                chipAddCamera.text = binding.etContactDetails.text.toString().trim()
                chipAddCamera.isCheckable = false
                chipAddCamera.isCloseIconVisible = true

                chipAddCamera.setOnCloseIconClickListener { chip ->
                    binding.chipGroupContact.removeView(chip)
                    arrayContact.remove((chip as Chip).text.toString().toLong())
                }

                binding.chipGroupContact.forEachIndexed { index, view ->
                    if ((view as Chip).text.toString().lowercase().trim()
                            .equals(chipAddCamera.text.toString().lowercase().trim(), true)
                    ) {
                        return@setSafeOnClickListener
                    }
                }
                arrayContact.add(binding.etContactDetails.text.toString().toLong())
                binding.chipGroupContact.addView(chipAddCamera)
                binding.etContactDetails.text?.clear()
                mobNumberChipId++
            } else {
                Toast.makeText(
                    requireContext(), "Contact number must have 10 digits", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.ivAddPhotographer.setOnClickListener {
            photographerCount++
            binding.tvShowPhotographersCount.text =
                getString(R.string.count_photographer, photographerCount.toString())
        }

        binding.ivSubstractPhotographer.setOnClickListener {
            if (photographerCount > 1) {
                photographerCount--
                binding.tvShowPhotographersCount.text =
                    getString(R.string.count_photographer, photographerCount.toString())
            }
        }

        binding.txtAddCamera.setOnClickListener {
            if (!binding.tvPhotoCamRequired.text.isNullOrBlank()) {
                val chipAddCamera: Chip = layoutInflater.inflate(
                    R.layout.custom_chip_layout, binding.chipGroupCameraRequired, false
                ) as Chip
                chipAddCamera.id = cameraChipId
                chipAddCamera.text = binding.tvPhotoCamRequired.text.toString().trim()
                chipAddCamera.isCheckable = false
                chipAddCamera.isCloseIconVisible = true

                chipAddCamera.setOnCloseIconClickListener { chip ->
                    binding.chipGroupCameraRequired.removeView(chip)
                    arrayPhotoCam.remove((chip as Chip).text.toString())
                }

                binding.chipGroupCameraRequired.forEachIndexed { index, view ->
                    if ((view as Chip).text.toString().lowercase().trim()
                            .equals(chipAddCamera.text.toString().lowercase().trim(), true)
                    ) {
                        return@setOnClickListener
                    }
                }

                arrayPhotoCam.add(binding.tvPhotoCamRequired.text.toString())
                binding.chipGroupCameraRequired.addView(chipAddCamera)
                binding.tvPhotoCamRequired.text?.clear()
                cameraChipId++
            }
        }

        binding.txtAddLoc.setOnClickListener {
            Log.e("@@@@@@@", "!@!@${it}")
            if (!binding.etJobLocation.text.isNullOrBlank()) {
                val chipAddCamera: Chip = layoutInflater.inflate(
                    R.layout.custom_chip_layout, binding.chipGroupLocation, false
                ) as Chip
                chipAddCamera.id = locChipId
                chipAddCamera.text = binding.etJobLocation.text.toString().trim()
                chipAddCamera.isCheckable = false
                chipAddCamera.isCloseIconVisible = true

                chipAddCamera.setOnCloseIconClickListener { chip ->
                    binding.chipGroupLocation.removeView(chip)
                    arrayLocation.remove((chip as Chip).text.toString())
                }
                binding.chipGroupLocation.forEachIndexed { index, view ->
                    if ((view as Chip).text.toString().lowercase().trim()
                            .equals(chipAddCamera.text.toString().lowercase().trim(), true)
                    ) {
                        return@setOnClickListener
                    }
                }

                arrayLocation.add(binding.etJobLocation.text.toString())
                binding.chipGroupLocation.addView(chipAddCamera)
                binding.etJobLocation.text?.clear()
                locChipId++
            }
        }

        binding.txtAddVideoCamera.setOnClickListener {
            if (!binding.tvVideoCamRequired.text.isNullOrBlank()) {
                val chipAddCamera: Chip = layoutInflater.inflate(
                    R.layout.custom_chip_layout, binding.chipGroupVideoCameraRequired, false
                ) as Chip
                chipAddCamera.id = videoChipId
                chipAddCamera.text = binding.tvVideoCamRequired.text.toString().trim()
                chipAddCamera.isCheckable = false
                chipAddCamera.isCloseIconVisible = true

                chipAddCamera.setOnCloseIconClickListener { chip ->
                    binding.chipGroupVideoCameraRequired.removeView(chip)
                    arrayVideoCam.remove((chip as Chip).text.toString())
                }

                binding.chipGroupVideoCameraRequired.forEachIndexed { index, view ->
                    if ((view as Chip).text.toString().lowercase().trim()
                            .equals(chipAddCamera.text.toString().lowercase().trim(), true)
                    ) {
                        return@setOnClickListener
                    }
                }

                arrayVideoCam.add(binding.tvVideoCamRequired.text.toString())
                binding.chipGroupVideoCameraRequired.addView(chipAddCamera)
                binding.tvVideoCamRequired.text?.clear()
                videoChipId++
            }
        }

        binding.txtAddOtherRequirement.setOnClickListener {

            if (!binding.tvOtherEquipmentRequired.text.isNullOrBlank()) {

                val chipAddCamera: Chip = layoutInflater.inflate(
                    R.layout.custom_chip_layout, binding.chipGroupOtherRequired, false
                ) as Chip

                chipAddCamera.id = otherChipId
                chipAddCamera.text = binding.tvOtherEquipmentRequired.text.toString().trim()
                chipAddCamera.isCheckable = false
                chipAddCamera.isCloseIconVisible = true

                chipAddCamera.setOnCloseIconClickListener { chip ->
                    binding.chipGroupOtherRequired.removeView(chip)
                    arrayOtherReq.remove((chip as Chip).text.toString())
                }

                binding.chipGroupOtherRequired.forEachIndexed { index, view ->
                    if ((view as Chip).text.toString().lowercase().trim()
                            .equals(chipAddCamera.text.toString().lowercase().trim(), true)
                    ) {
                        return@setOnClickListener
                    }
                }

                arrayOtherReq.add(binding.tvOtherEquipmentRequired.text.toString())
                binding.chipGroupOtherRequired.addView(chipAddCamera)
                binding.tvOtherEquipmentRequired.text?.clear()
                otherChipId++
            }
        }

        binding.etContactDetails.setText(SharedPrefsHelper.getUserCommon()?.profile_details?.mobile)

        if (!binding.etContactDetails.text.toString().isNullOrBlank()) {
            binding.txtAddContact.callOnClick()
        }

        if (formAction.equals("update")) {
            prefillJObModel = AppConstant.tmpJobModel

            goForPrefillData()
        }
    }

    private fun get24Time(time12: String): String {
        val date12Format = SimpleDateFormat("hh:mm a")
        val date24Format = SimpleDateFormat("HH:mm")
        return date24Format.format(date12Format.parse(time12))
    }

    private fun goForPrefillData() {
        //setting prefill data

        binding.apply {
            prefillJObModel.apply {
                if (type == PostType.POST_JOB.toString()) {
                    radioButtonPost.isChecked = true
                } else {
                    radioButtonHiredAsPhotographer.isChecked = true
                }

                if (jobType == null) {
                    tvSpinnerJobType.text = "Free"
                    spinnerPostJob.setSelection(0)
                } else {
                    tvSpinnerJobType.text = if (jobType.equals("Free", true)) {
                        spinnerPostJob.setSelection(0)
                        "Free"
                    } else {
                        spinnerPostJob.setSelection(1)
                        "Promote"
                    }
                }

                etJobTitle.setText(title)
                etEventExpertise.text = eventType
                etEventType.setText(eventType)
                tvEventStartDate.text = startDate.getFormattedDatetime(outputFormat = "dd-MM-yyyy")
                tvEventEndDate.text = endDate.getFormattedDatetime(outputFormat = "dd-MM-yyyy")
                tvEventStartTimePicker.text = startTime
                tvEventEndTimePicker.text = endTime
                gigDescription.setText(description)
                tvPhotographerTypeSpinner.text = photographyType
                tvShowPhotographersCount.text = getString(
                    R.string.count_photographer,
                    numberOfPhotographers.toString()
                )

                prefillJObModel.photoCameraUse.forEach {
                    binding.tvPhotoCamRequired.setText(it)
                    binding.txtAddCamera.callOnClick()
                }

                prefillJObModel.videoCameraUse.forEach {
                    binding.tvVideoCamRequired.setText(it)
                    binding.txtAddVideoCamera.callOnClick()
                }

                prefillJObModel.otherEquipments.forEach {
                    binding.tvOtherEquipmentRequired.setText(it)
                    binding.txtAddOtherRequirement.callOnClick()
                }

                //hiding contact

                txtContactDetails.gone()
                txtAddContact.gone()
                tvCountryPicker.gone()
                etContactDetails.gone()
                chipGroupContact.gone()
            }
        }

        if (prefillJObModel.type == PostType.GET_HIRED.toString()) {
            binding.etJobLocation.setText(prefillJObModel.addressObj.parseAddress())
            latitude = prefillJObModel.addressObj.getLatitudeAddress().toDouble()
            longitude = prefillJObModel.addressObj.getLongitudeAddress().toDouble()
        } else {
            //chips
            prefillJObModel.city.forEach {
                binding.etJobLocation.setText(it)
                binding.txtAddLoc.callOnClick()
            }
        }


    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun formValidatedSuccess(): Boolean {
        binding.apply {
            if (radioButtonPost.isChecked) {
                //Hire You
                if (etJobTitle.text.isNullOrBlank()) {
                    showSnackBar("Job Title Required")
                    return false
                } else if (etJobLocation.text.isNullOrBlank()) {
                    showSnackBar("Job Location Required")
                    return false
                } else if (tvEventStartDate.text.isNullOrBlank()) {
                    showSnackBar("Start Date Required")
                    return false
                } else if (tvEventStartTimePicker.text.isNullOrBlank()) {
                    showSnackBar("Start Time Required")
                    return false
                } else if (tvEventEndDate.text.isNullOrBlank()) {
                    showSnackBar("End Date Required")
                    return false
                } else if (tvEventEndTimePicker.text.isNullOrBlank()) {
                    showSnackBar("End Time Required")
                    return false
                } else if (arrayContact.isEmpty()) {
                    showSnackBar("Contact Detail Required")
                    return false
                } else if (etEventType.text.isNullOrBlank()) {
                    showSnackBar("Event Type Required")
                    return false
                } else {
                    return true
                }
            } else {
                //Hire Me
                if (etEventExpertise.text.isNullOrBlank()) {
                    showSnackBar("Event Expertise Required")
                    return false
                } else if (arrayLocation.isEmpty()) {
                    showSnackBar("Job Location Required")
                    return false
                } else if (arrayContact.isEmpty()) {
                    showSnackBar("Contact Detail Required")
                    return false
                } else if (tvEventStartDate.text.isNullOrBlank()) {
                    showSnackBar("Start Date Required")
                    return false
                } else if (tvEventStartTimePicker.text.isNullOrBlank()) {
                    showSnackBar("Start Time Required")
                    return false
                } else if (tvEventEndDate.text.isNullOrBlank()) {
                    showSnackBar("End Date Required")
                    return false
                } else if (tvEventEndTimePicker.text.isNullOrBlank()) {
                    showSnackBar("End Time Required")
                    return false
                } else {
                    return true
                }
            }
        }

        return true
    }

    private fun attachObserver() {
        jobHomeViewModel.updatePostJobs.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                requireContext().toastSuccess(it.message.toString())
                findNavController().navigate(R.id.jobHome)
            } else {
                ErrorDialog.showDialog(requireContext(), it.message.toString(), "Okay", true) {
                }
            }
        }

        jobHomeViewModel.postJobs.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {
                JobDialog.showSuccessDialog(requireContext(), it.data?.id.toString()) {
                    findNavController().navigate(R.id.jobHome)
                }
            } else {
                if (it.message.toString().contains("exhausted")) {
                    JobDialog.showExhaustedSuccessDialog(requireContext(), it.message.toString()) {
                        findNavController().navigate(JobCreateFragmentDirections.actionJobCreateToSubscriptionFragment())
                    }
                } else {
                    ErrorDialog.showDialog(requireContext(), it.message.toString(), "Okay", true) {

                    }
                }
            }
        }

        jobHomeViewModel.photographerTypesLiveData.observe(viewLifecycleOwner) { data ->
            if (data.success) {

                arrayPhotographerTypes.clear()
                data.photographerTypes.forEachIndexed { index, photographerTypes ->
                    arrayPhotographerTypes.add(photographerTypes.type)
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.eventTypeList.observe(viewLifecycleOwner) { data ->
            if (data.success) {
                eventExpertiseTypes.clear()
                data.data.list.forEachIndexed { index, types ->
                    eventExpertiseTypes.add(types.type)
                }
            }
        }
    }


    private fun showSecondPage() {
        binding.btnProceedOrPost.text = if (formAction.equals("create")) {
            "Post Job"
        } else {
            "Update Job"
        }

        binding.txtSteps.text = "Step 02/02"
        binding.constraintPageFirst.gone()
        binding.viewFirst.setBackgroundColor(requireActivity().resources.getColor(R.color.colorBackgroundGray))
        binding.viewSecond.setBackgroundColor(requireActivity().resources.getColor(R.color.colorBackgroundGray))
        binding.viewSecond.setBackgroundColor(requireActivity().resources.getColor(R.color.colorBackgroundGray))
        binding.viewSecond.setBackgroundColor(requireActivity().resources.getColor(R.color.colorOrange))
        binding.constraintPageSecond.visible()
    }

    private fun showFirstPage() {
        binding.btnProceedOrPost.text = "Proceed"
        binding.txtSteps.text = "Step 01/02"
        binding.constraintPageSecond.gone()
        binding.viewFirst.setBackgroundColor(requireActivity().resources.getColor(R.color.colorOrange))
        binding.viewSecond.setBackgroundColor(requireActivity().resources.getColor(R.color.colorBackgroundGray))
        binding.constraintPageFirst.visible()
    }


    private fun permissionRationaleMultiLocation(
        permissions: Array<PermissionBean>, token: PermissionToken
    ) {
        token.continuePermissionRequest()
    }

    private fun inCaseOfErrorLocation(e: Exception) {
        requireContext().toastError("Error for permission : " + e.message)
    }

    private fun permissionResultMultiLocation(
        permissions: Array<PermissionBean>
    ) {
        val isAllPermanentlyDenied = permissions.all {
            it.isPermanentlyDenied
        }
        if (isAllPermanentlyDenied) {

            val snackbar: Snackbar = Snackbar.make(
                binding.root,
                getString(R.string.permission_always_denied),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                activity?.openPermissionSettings()
            }
            snackbar.show()
            return
        }
        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            showProgress()
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                hideProgress()
                latitude = userLatitude
                longitude = userLongitude
                try {
                    binding.etJobLocation.setText("")
                    var googleAddress = ""
                    Log.w("address", "address ${address}")

                    if (address.isNotEmpty()) {
                        googleAddress =
                            (address[0].getAddressLine(0))
                    }
                    binding.etJobLocation.setText(googleAddress)
                    binding.etJobLocation.setSelection(
                        binding.etJobLocation.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun timeChangeListener() {

        val dialog = Dialog(context!!, R.style.DialogSlideAnim)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        var dialogBinding = DialogTimePickerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        try {
            val sdf = SimpleDateFormat("HH:mm")
            var date: Date? = Date()
            date = sdf.parse(sTime)
            val c: Calendar = Calendar.getInstance()
            c.time = date
            c.add(Calendar.HOUR_OF_DAY, 4)
            dialogBinding.timePickerDialog.hour = c.get(Calendar.HOUR_OF_DAY)
            dialogBinding.timePickerDialog.minute = c.get(Calendar.MINUTE)
            time = getTimeFun(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        } catch (e: Exception) {
        }

        dialogBinding.timePickerDialog.setOnTimeChangedListener { _, hour, minute ->
            time = getTimeFun(hour, minute)
        }
        dialogBinding.tvDialogOk.setSafeOnClickListener {
            val mToday = Date()
            val sdf = SimpleDateFormat("hh:mm aa")
            val start = sdf.parse(sTime2)
            val end = sdf.parse(time)

            if (sDate == eDate) {
                if (end.before(start)) {
                    Toast.makeText(
                        context,
                        "Event End Time should be Greater than Start Time",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (timeInMilli("$eDate $time") < timeInMilli2("$sDate $sTime2")) {
                    Toast.makeText(
                        context,
                        "Minimum Event duration should be Four Hours",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.tvEventEndTimePicker.text = time
                    dialog.dismiss()
                }
            } else {
                binding.tvEventEndTimePicker.text = time
                dialog.dismiss()
            }

        }

        dialogBinding.tvDialogCancel.setSafeOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setDimAmount(0.7f)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(R.color.colorTransparent)

        dialog.show()
    }

    private fun timeInMilli2(dateString: String): Long {
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm aa")
        val date = format.parse(dateString)
        val millisecond = date.time
        return millisecond + 14400000
    }

    private fun timeInMilli(dateString: String): Long {
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm aa")
        val date = format.parse(dateString)
        val millisecond = date.time
        return millisecond
    }

    private fun getTimeFun(hour: Int, minute: Int): String {
        var hour = hour
        var am_pm = ""
        when {
            hour == 0 -> {
                hour += 12
                am_pm = "AM"
            }
            hour == 12 -> am_pm = "PM"
            hour > 12 -> {
                hour -= 12
                am_pm = "PM"
            }
            else -> am_pm = "AM"
        }
        val hours = if (hour < 10) "0$hour" else hour
        val min = if (minute < 10) "0$minute" else minute

        return "$hours:$min $am_pm"
    }


    enum class PostType {
        POST_JOB, GET_HIRED
    }
}