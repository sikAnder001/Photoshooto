package com.photoshooto.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.photoshooto.BuildConfig
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentProfileBinding
import com.photoshooto.domain.model.GetProfileDetails
import com.photoshooto.domain.model.UpdateProfileModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentUserProfile : Fragment(), EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()

    var imageUpdation = false
    var uploadedFileUrl = ""

    var genderSelected = ""
    val genderList = listOf("Male", "Female", "Others")

    var imageFilePath: String? = null
    var photoFile: File? = null
    lateinit var customProgress: CustomProgress // used in child activities
    var fileUploadMultipart: MultipartBody.Part? = null

    var passConfirmShow = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getProfileByIDUseCase(
            PreferenceManager.saveUserID,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
        customProgress = CustomProgress().getInstance()!!

        binding.editGender.setOnClickListener(this)
        binding.restPass.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.editProfileImage.setOnClickListener(this)

//        val dataAdapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(requireActivity(), R.layout.itemview_spinner, genderList)
//        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
//        binding.spinner.adapter = dataAdapter
//        binding.spinner.onItemSelectedListener = this
//        binding.editGender.setOnFocusChangeListener { v, hasFocus ->
//            binding.spinner.performClick()
//        }

        binding.editDob.transformIntoDatePicker(
            requireActivity(),
            "dd-MM-yyyy",
            Date()
        )

        with(profileViewModel) {
            getUserData.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            setUserData(response.data?.data?.profile_details!!)
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            })
        }

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!!, requireActivity())
                            imageUpdation = true
                            uploadedFileUrl = response.data.data[0].file_path.toString()
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            })
        }

        with(profileViewModel) {
            updateProfileData.observe(requireActivity()) { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        onToast(response.data?.message!!, requireActivity())
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
            }
            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }

    }

    private fun updateProfileData() {

        val usernameText = binding.edittextName.text.toString()
        val emailText = binding.editEmail.text.toString()
        val genderText = binding.editGender.text.toString()
        val dobText = binding.editDob.text.toString()

        if (usernameText.isEmpty()) {
            binding.edittextName.requestFocus()
            binding.errorName.visibility = View.VISIBLE
            return
        }
        binding.errorName.visibility = View.GONE

        if (emailText.isEmpty() && !isValid(emailText)) {
            binding.editEmail.requestFocus()
            binding.errorEmail.visibility = View.VISIBLE
            return
        }
        binding.errorEmail.visibility = View.GONE

        if (genderText.isEmpty()) {
            binding.editGender.requestFocus()
            binding.errorGender.visibility = View.VISIBLE
            return
        }
        binding.errorGender.visibility = View.GONE

        if (dobText.isEmpty()) {
            binding.editDob.requestFocus()
            binding.errordob.visibility = View.VISIBLE
            return
        }
        binding.errordob.visibility = View.GONE

        if (imageUpdation) {
            val profile_image: RequestBody = createPartFromString(uploadedFileUrl)
        }
        val updateProfileModel = UpdateProfileModel(
            name = usernameText,
            gender = genderText,
            email = emailText,
            profile_image = uploadedFileUrl,
            birth_date = dobText
        )
        profileViewModel.updateProfileUseCase(
            updateProfileModel, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString()
        )
    }

    private fun setUserData(userProfileModel: GetProfileDetails) {

        binding.userName.text = userProfileModel.name
        binding.edittextName.setText(userProfileModel.name)
        binding.userId.text = userProfileModel._id

        binding.editPhone.setText(userProfileModel.mobile)
        binding.editEmail.setText(userProfileModel.email)

        binding.editGender.setText(userProfileModel.gender)
        binding.editDob.setText(userProfileModel.birth_date)

        Glide.with(requireActivity())
            .load(urlAddingForPicture(userProfileModel.profile_image!!))
            .error(R.drawable.ic_girl_profile)
            .into(binding.profileImage)

        uploadedFileUrl = userProfileModel.profile_image

    }

    @AfterPermissionGranted(AddPhotoBottomDialogFragment.CAMERA_PERMISSION_CODE)
    private fun takePicFromCamera() {
        apply {
            val perms =
                arrayOf<String>(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
                openCamera()
            } else {
                apply {
                    EasyPermissions.requestPermissions(
                        requireActivity(),
                        "We need permissions so that we can upload the respective documents",
                        AddPhotoBottomDialogFragment.CAMERA_PERMISSION_CODE,
                        *perms
                    )
                }

            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        let {
            if (cameraIntent.resolveActivity(requireContext().packageManager) != null) {
                //Create a file to store the image
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("Camera Exception", "" + ex.message)
                }
                if (photoFile != null) {
                    photoFile?.apply {
                        val photoURI: Uri = FileProvider
                            .getUriForFile(
                                requireActivity(),
                                "${BuildConfig.APPLICATION_ID}.provider", this
                            )
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(
                            cameraIntent,
                            AddPhotoBottomDialogFragment.ACTION_CAMERA_REQUEST_CODE
                        )
                    }

                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "Capture_$timeStamp"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile: File = File.createTempFile(
            imageFileName,  /* prefix */".jpg",  /* suffix */storageDir /* directory */
        )
        imageFilePath = imageFile.absolutePath
        return imageFile
    }


    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AddPhotoBottomDialogFragment.ACTION_CAMERA_REQUEST_CODE -> {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName =
                    "PhotoShooto$timeStamp.jpg"
                if (resultCode == Activity.RESULT_OK) {
                    val requestFile: RequestBody? =
                        photoFile?.let {
                            it
                                .asRequestBody("image/*".toMediaTypeOrNull())
                        }
                    requestFile?.apply {
                        fileUploadMultipart = MultipartBody.Part.createFormData(
                            "photos",
                            imageFileName,
                            this
                        )
                        imageFilePath?.let {
                            AddPhotoBottomDialogFragment.onImageUpLoadListener?.onCameraSelected(
                                fileUploadMultipart!!,
                                it
                            )
                        }
                        customProgress.showProgress(requireActivity(), "wait..", false)

                        Glide.with(requireActivity()).load(imageFilePath)
                            .into(binding.profileImage)
                        customProgress.hideProgress()

                        val profile_image: RequestBody = createPartFromString("profile_image")
                        val map: HashMap<String, RequestBody> = HashMap()
                        map["category"] = profile_image
                        uploadFileViewModel.updateImgFile(
                            fileUploadMultipart,
                            map,
                            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                        )

                    }

                }
            }
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> {

            }
            else -> {
                println("no handler onActivityReenter")
            }
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_gender -> {
                addDocuments("Gender", genderList)
            }

            R.id.forgot_pass -> {
                findNavController().navigate(R.id.action_FragmentProfile_to_FragmentMobileVerify)
            }

            R.id.btn_save -> {
                updateProfileData()
            }

            R.id.editProfileImage -> {
                takePicFromCamera()
            }
        }
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(title, args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {
//                    relation = selectedValue
                    binding.editGender.setText(imagePath)
                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }
}