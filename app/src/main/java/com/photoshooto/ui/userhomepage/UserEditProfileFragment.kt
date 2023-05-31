package com.photoshooto.ui.userhomepage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.databinding.FragmentUserEditProfileBinding
import com.photoshooto.domain.model.ProfileData
import com.photoshooto.domain.model.UpdateProfileModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UserEditProfileFragment : Fragment(), TextWatcher {

    private lateinit var binding: FragmentUserEditProfileBinding
    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()
    var imageFilePath: String? = null
    var photoFile: File? = null
    var fileUploadMultipart: MultipartBody.Part? = null
    var isDirty: Boolean? = false
    lateinit var customProgress: CustomProgress // used in child activities
    var imageType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgress = CustomProgress().getInstance()!!

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )

        initLiveData()
        clickListener()
    }


    private fun initLiveData() {
        with(profileViewModel) {
            getUserData.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            setUserData(
                                response.data?.data!!, response.data.data.id!!
                            )
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                            addFieldListeners()
                        }
                        else -> {
                            addFieldListeners()
                        }
                    }
                }
            }

            updateProfileData.observe(requireActivity()) { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        onToast(response.data?.message!!, requireActivity())
                        profileViewModel.getProfileByIDUseCase(
                            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
                            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                        )
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
            }
        }
        var updateProfileModel: UpdateProfileModel

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!!, requireActivity())

                            val uploadedFileUrl = response.data.data[0].file_path.toString()

                            updateProfileModel = if (imageType == "profileImage") {
                                UpdateProfileModel(
                                    profile_image = uploadedFileUrl
                                )
                            } else {
                                UpdateProfileModel(
                                    background_image = uploadedFileUrl
                                )
                            }

                            profileViewModel.updateProfileUseCase(
                                updateProfileModel,
                                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                            )
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

    private fun addFieldListeners() {
        binding.edtUserName.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtEmail.addTextChangedListener(this)
        binding.edtPassword.addTextChangedListener(this)

        binding.edtPassword.isEnabled = false

        binding.changePasswordTxt.setOnClickListener {
            binding.edtPassword.isEnabled = true
            binding.edtPassword.requestFocus()
        }
    }

    private fun setUserData(profileData: ProfileData, id: String) {
        val userProfileModel = profileData.profile_details!!
        Log.i("userDetails", Gson().toJson(userProfileModel))

        binding.edtUserName.setText(userProfileModel.name)

        val dob = userProfileModel.birth_date
        if (!dob.isNullOrEmpty() && dob != "-") {
            binding.tvDateOfBirth.text = dob
        }

        val emailId = userProfileModel.email
        if (!emailId.isNullOrEmpty() && emailId != "-") {
            binding.edtEmail.setText(emailId)
        }

        val mobileNo = userProfileModel.mobile
        if (!mobileNo.isNullOrEmpty() && mobileNo != "-") {
            binding.edtMobileNumber.setText(mobileNo)
        }

        val profileImage = userProfileModel.profile_image
        Glide.with(requireActivity()).load(urlAddingForPicture(profileImage!!))
            .error(R.drawable.ic_girl_profile).into(binding.profileImage)

        val backgroundImg = userProfileModel.background_image
        Glide.with(requireActivity()).load(urlAddingForPicture(backgroundImg!!))
            .error(R.drawable.ic_edit_profile).into(binding.backgroundImg)

        val userLocation = profileData.location
        val userCity = userLocation?.city
        val userState = userLocation?.state

        val cityStateBuilder = StringBuilder()
        if (!userCity.isNullOrEmpty()) {
            cityStateBuilder.append(userCity)
        }

        if (!userState.isNullOrEmpty()) {
            cityStateBuilder.append(", ")
            cityStateBuilder.append(userState)
        }

        val cityState = cityStateBuilder.toString()
        if (cityState.isNotEmpty()) {
            binding.tvUserLocation.text = cityState
        }

        addFieldListeners()
    }


    private fun clickListener() {

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.imgUpload.setOnClickListener {
            imageType = "profileImage"
            selectMediaFile()
        }

        binding.backgroundImg.setOnClickListener {
            imageType = "backgroundImage"
            selectMediaFile()
        }

        binding.tvDateOfBirth.setOnClickListener {
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                { datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    try {
                        c[year, monthOfYear] = dayOfMonth
                        @SuppressLint("SimpleDateFormat") val dateFormat =
                            SimpleDateFormat("dd/MM/yyyy")
                        val date =
                            dateFormat.parse((monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year)
                        val dateString = date?.let { it1 -> dateFormat.format(it1) }

                        val dob = dateFormat.format(c.time)
                        if (!dob.isNullOrEmpty() && dob != "-") {
                            binding.tvDateOfBirth.text = dob
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    datePicker.maxDate = System.currentTimeMillis()
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        binding.btnEditProfile.setOnClickListener {
            validateAndUpdateProfile()
        }
    }

    private fun selectMediaFile() {
        val intent = Intent(requireActivity(), AccessMediaUtil::class.java)
        mStartAccessMediaForResult.launch(intent)
    }

    private var mStartAccessMediaForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val intent = result.data
                photoFile = if (intent!!.hasExtra(PICTURE_PATH_LIST)) {
                    Log.i(
                        "selectedImage",
                        Gson().toJson(intent.getStringArrayListExtra(PICTURE_PATH_LIST))
                    )
                    val filePath = intent.getStringArrayListExtra(PICTURE_PATH_LIST)?.get(0)!!
                    File(filePath)
                } else {
                    Log.i("selectedImage", Gson().toJson(intent.getStringExtra(PICTURE_PATH)))
                    val filePath = intent.getStringExtra(PICTURE_PATH)!!
                    File(filePath)
                }

                uploadFileFromCameraOrGallery()
            }
        }
    }


    private fun uploadFileFromCameraOrGallery() {
        val requestFile: RequestBody? = photoFile?.asRequestBody("image/*".toMediaTypeOrNull())
        requestFile?.apply {
            fileUploadMultipart = MultipartBody.Part.createFormData(
                "photos", photoFile?.name, this
            )
            imageFilePath?.let {
                AddPhotoBottomDialogFragment.onImageUpLoadListener?.onCameraSelected(
                    fileUploadMultipart!!, it
                )
            }
            customProgress.showProgress(requireActivity(), "wait..", false)

            if (imageType == "profileImage") {
                Glide.with(requireActivity()).load(imageFilePath)
                    .into(binding.profileImage)
            } else {
                Glide.with(requireActivity()).load(imageFilePath)
                    .into(binding.backgroundImg)
            }
            customProgress.hideProgress()

            val imageType: RequestBody = if (imageType == "profileImage") {
                createPartFromString("profile_image")
            } else {
                createPartFromString("background_image")
            }
            val map: HashMap<String, RequestBody> = HashMap()
            map["category"] = imageType
            uploadFileViewModel.updateImgFile(
                fileUploadMultipart,
                map,
                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
            )
        }
    }

    private fun validateAndUpdateProfile() {
        if (binding.edtUserName.text.toString().isEmpty()) {
            Toast.makeText(context, "Please Enter the Name", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.tvDateOfBirth.text.toString().isEmpty()) {
            Toast.makeText(context, "Please Enter the Date of Birth", Toast.LENGTH_SHORT).show()
            return
        } else if (binding.edtEmail.text.toString().isEmpty()) {
            Toast.makeText(context, "Please Enter the Email Id", Toast.LENGTH_SHORT).show()
            return
        }

        val updateProfileModel = UpdateProfileModel(
            profile_image = profileViewModel.getUserData.value?.data?.data!!.profile_details?.profile_image!!,
            background_image = profileViewModel.getUserData.value?.data?.data!!.profile_details?.background_image!!,
            name = binding.edtUserName.text.toString(),
            email = binding.edtEmail.text.toString(),
            mobile = binding.edtMobileNumber.text.toString(),
            gender = binding.spGender.selectedItem.toString(),
            birth_date = binding.tvDateOfBirth.text.toString()
        )

        profileViewModel.updateProfileUseCase(
            updateProfileModel,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        isDirty = true
        showBtnEnabledBtn(isDirty!!)
    }

    private fun showBtnEnabledBtn(dirty: Boolean) {
        binding.btnEditProfile.backgroundTintList = if (dirty) {
            ColorStateList.valueOf(resources.getColor(R.color.orange_clr))
        } else {
            ColorStateList.valueOf(resources.getColor(R.color.grey3))
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }
}