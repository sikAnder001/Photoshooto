package com.photoshooto.ui.photographer

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.photoshooto.BuildConfig
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.bottomsheet.ImagePreviewBottomFragment
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentPhotographerPersonalBinding
import com.photoshooto.domain.model.FileUpload
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

//TODO upload aadhar and licence ask abhijith to do it praveen

class FragmentPhotographerPersonal : Fragment(), EasyPermissions.PermissionCallbacks,
    View.OnClickListener {

    private lateinit var binding: FragmentPhotographerPersonalBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()

    var uploadedFileUrl = ""

    var genderSelected = ""

    val gender = listOf("Male", "Female", "Transgender")

    var imageFilePath: String? = null
    var photoFile: File? = null
    lateinit var customProgress: CustomProgress // used in child activities
    var fileUploadMultipart: MultipartBody.Part? = null

    var passConfirmShow = false

    var aadharcardFilePath: String? = null
    var licenceFilePath: String? = null

    var uploadingPhotoOf: String? = null

    var attachmentsOld = ArrayList<FileUpload>()
    var attachments = ArrayList<FileUpload>()

    var aadharUploded = false
    var licenceUploded = false
    var selectedCardType = R.id.rb_aadhar_card


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
        customProgress = CustomProgress().getInstance()!!

        val toolbar = binding.toolbarEditPersonalDetails

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.personal_details)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        //  binding.editGender.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.editAadharCard.setOnClickListener(this)
        binding.editLicence.setOnClickListener(this)
        binding.aadharPreview.setOnClickListener(this)
        binding.licencePreview.setOnClickListener(this)

        binding.rgCardSelection.setOnCheckedChangeListener { radioGroup, id ->
            if (id == R.id.rb_aadhar_card) {
                showAadhar()
            } else {
                showLicense()
            }
        }
    }

    private fun showAadhar() {

        if (attachments.any { it.filterAttachments("aadhar") } ||
            profileViewModel.getUserData.value?.data?.data?.attachments?.any {
                it.filterAttachments(
                    "aadhar"
                )
            } == true
        ) {
            binding.aadharPreview.visible()
        } else {
            binding.aadharPreview.gone()
        }

        binding.layoutAadharCard.visible()
        binding.licencePreview.gone()
        binding.layoutLicence.gone()
    }

    private fun showLicense() {
        if (attachments.any { it.filterAttachments("licence") } ||
            profileViewModel.getUserData.value?.data?.data?.attachments?.any {
                it.filterAttachments(
                    "licence"
                )
            } == true
        ) {
            binding.licencePreview.visible()
        } else {
            binding.licencePreview.gone()
        }

        binding.layoutLicence.visible()
        binding.aadharPreview.gone()
        binding.layoutAadharCard.gone()
    }

    private fun setObservers() {

        with(profileViewModel) {

            getUserData.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            setUserData(
                                response.data?.data?.profile_details!!,
                                response.data.data.attachments!!
                            )
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }


        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!!, requireActivity())
                            if (uploadingPhotoOf.equals("aadhar")) {
                                aadharUploded = true
                                attachments.add(response.data.data[0])
                                aadharcardFilePath = response.data.data[0].file_path
                                binding.aadharUploadStatus.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireActivity(),
                                        R.drawable.ic_green_right_mark
                                    )
                                )
                            } else {
                                licenceUploded = true
                                attachments.add(response.data.data[0])
                                licenceFilePath = response.data.data[0].file_path
                                binding.licenceUploadStatus.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireActivity(),
                                        R.drawable.ic_green_right_mark
                                    )
                                )
                            }
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
                        lifecycleScope.launchWhenResumed {
                            findNavController().popBackStack()
                        }
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
            }
            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            })
        }
    }

    private fun updateProfileData() {

        val usernameText = getStringFromEditText(binding.edittextName)
        val emailText = getStringFromEditText(binding.editEmail)
        val phoneText = getStringFromEditText(binding.editPhone)
        val altenateNoText = getStringFromEditText(binding.editAlternativeNumber)

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

        if (phoneText.isEmpty() && !isValid(phoneText)) {
            binding.editPhone.requestFocus()
            binding.errorPhone.visibility = View.VISIBLE
            return
        }
        binding.errorPhone.visibility = View.GONE

        if (!aadharUploded && selectedCardType == R.id.rb_aadhar_card) {
            binding.errorAadhar.visibility = View.VISIBLE
            return
        }
        binding.errorAadhar.visibility = View.GONE

        if (!licenceUploded && selectedCardType == R.id.rb_license_card) {
            binding.errorLicence.visibility = View.VISIBLE
            return
        }
        binding.errorLicence.visibility = View.GONE

        for (i in attachmentsOld.indices) {
            if (attachmentsOld[i].category.equals("aadhar")) {
                if (!aadharUploded) {
                    attachments.add(attachmentsOld[i])
                }
            } else if (attachmentsOld[i].category.equals("licence")) {
                if (!licenceUploded) {
                    attachments.add(attachmentsOld[i])
                }
            } else if (attachmentsOld[i].category.equals("visiting_card")) {
                attachments.add(attachmentsOld[i])
            } else if (attachmentsOld[i].category.equals("association_card")) {
                attachments.add(attachmentsOld[i])
            }
        }

        val updateProfileModel = UpdateProfileModel(
            name = usernameText,
            email = emailText,
            attachments = attachments
        )

        profileViewModel.updateProfileUseCase(
            updateProfileModel, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN).toString()
        )
    }

    private fun setUserData(userProfileModel: GetProfileDetails, attachments: List<FileUpload>) {
        binding.edittextName.setText(userProfileModel.name)
        binding.editPhone.setText(userProfileModel.mobile)
        binding.editEmail.setText(userProfileModel.email)
        binding.editAlternativeNumber.setText(userProfileModel.alt_mobile)

        for (i in attachments.indices) {
            attachmentsOld.add(attachments[i])
            if (attachmentsOld[i].category.equals("aadhar")) {
                aadharcardFilePath = attachments[i].file_path

                binding.rgCardSelection.check(R.id.rb_aadhar_card)
                binding.aadharPreview.visibility = View.VISIBLE
                binding.aadharUploadStatus.visibility = View.VISIBLE
                aadharUploded = true
                binding.aadharUploadStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_green_right_mark
                    )
                )
            } else if (attachmentsOld[i].category.equals("licence")) {
                licenceFilePath = attachments[i].file_path

                binding.rgCardSelection.check(R.id.rb_license_card)
                binding.licencePreview.visibility = View.VISIBLE
                binding.licenceUploadStatus.visibility = View.VISIBLE
                licenceUploded = true
                binding.licenceUploadStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_green_right_mark
                    )
                )
            }
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
        val requestFile: RequestBody? =
            photoFile?.asRequestBody("image/*".toMediaTypeOrNull())
        requestFile?.apply {
            fileUploadMultipart = MultipartBody.Part.createFormData(
                "photos",
                photoFile?.name,
                this
            )
            imageFilePath?.let {
                AddPhotoBottomDialogFragment.onImageUpLoadListener?.onCameraSelected(
                    fileUploadMultipart!!,
                    it
                )
            }
            customProgress.showProgress(requireActivity(), "wait..", false)

            if (uploadingPhotoOf.equals("aadhar")) {
                binding.aadharPreview.visibility = View.VISIBLE
                aadharcardFilePath = imageFilePath
                binding.aadharUploadStatus.visibility = View.VISIBLE
                customProgress.hideProgress()

                val aadhar: RequestBody = createPartFromString("aadhar")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = aadhar
                uploadFileViewModel.updateImgFile(
                    fileUploadMultipart,
                    map,
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )

            } else {
                binding.licencePreview.visibility = View.VISIBLE
                binding.licenceUploadStatus.visibility = View.VISIBLE
                licenceFilePath = imageFilePath

                customProgress.hideProgress()

                val licence: RequestBody = createPartFromString("licence")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = licence
                uploadFileViewModel.updateImgFile(
                    fileUploadMultipart,
                    map,
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )

            }
        }
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


    @Deprecated("Deprecated in Java")
    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AddPhotoBottomDialogFragment.ACTION_CAMERA_REQUEST_CODE -> {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName =
                    "Photoshooto$timeStamp.jpg"
                if (resultCode == Activity.RESULT_OK) {
                    val requestFile: RequestBody? =
                        photoFile?.asRequestBody("image/*".toMediaTypeOrNull())
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

                        if (uploadingPhotoOf.equals("aadhar")) {
                            binding.aadharPreview.visibility = View.VISIBLE
                            aadharcardFilePath = imageFilePath
                            binding.aadharUploadStatus.visibility = View.VISIBLE
                            customProgress.hideProgress()

                            val aadhar: RequestBody = createPartFromString("aadhar")
                            val map: HashMap<String, RequestBody> = HashMap()
                            map["category"] = aadhar
                            uploadFileViewModel.updateImgFile(
                                fileUploadMultipart,
                                map,
                                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                            )

                        } else {
                            binding.licencePreview.visibility = View.VISIBLE
                            binding.licenceUploadStatus.visibility = View.VISIBLE
                            licenceFilePath = imageFilePath

                            customProgress.hideProgress()

                            val licence: RequestBody = createPartFromString("licence")
                            val map: HashMap<String, RequestBody> = HashMap()
                            map["category"] = licence
                            uploadFileViewModel.updateImgFile(
                                fileUploadMultipart,
                                map,
                                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                            )

                        }
//                        Glide.with(requireActivity()).load(imageFilePath)
//                            .into(binding!!.profileImage)
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

    //    var aadharClicked = false
//    var licenceClicked = false

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_gender -> {
                addDocuments("Gender", gender)
            }

            R.id.btn_save -> {
                updateProfileData()
            }
            R.id.edit_aadhar_card -> {
                aadharcardFilePath = ""
                binding.aadharPreview.visibility = View.GONE
                uploadingPhotoOf = "aadhar"
                //takePicFromCamera()
                selectMediaFile()
                aadharUploded = false
            }

            R.id.edit_licence -> {
                licenceFilePath = ""
                binding.licencePreview.visibility = View.GONE
                uploadingPhotoOf = "licence"
                //takePicFromCamera()
                selectMediaFile()
                licenceUploded = false
            }

            R.id.aadhar_preview -> {
                if (aadharcardFilePath != null && aadharcardFilePath!!.isNotEmpty())
                    showPreview(
                        requireActivity().getString(R.string.aadhar_card1),
                        aadharcardFilePath!!
                    )
            }

            R.id.licence_preview -> {
                if (licenceFilePath != null && licenceFilePath!!.isNotEmpty())
                    showPreview(
                        requireActivity().getString(R.string.driving_licence),
                        licenceFilePath!!
                    )
            }

        }
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(title, args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {
                    // binding.editGender.setText(genderSelected)
                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }


    private fun showPreview(title: String, imagePreview: String) {
        val imagePreviewBottomFragment = ImagePreviewBottomFragment.newInstance(title, imagePreview)
        imagePreviewBottomFragment.show(parentFragmentManager, "ImagePreviewBottomFragment")
    }
}