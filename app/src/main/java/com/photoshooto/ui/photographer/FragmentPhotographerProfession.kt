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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.bottomsheet.ImagePreviewBottomFragment
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentPhotographerProfessionBinding
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

class FragmentPhotographerProfession : Fragment(),
    EasyPermissions.PermissionCallbacks,
    View.OnClickListener {

    private lateinit var binding: FragmentPhotographerProfessionBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()

    var uploadedFileUrl = ""

    var imageFilePath: String? = null
    var photoFile: File? = null
    lateinit var customProgress: CustomProgress // used in child activities
    var fileUploadMultipart: MultipartBody.Part? = null

    var visitingcardFilePath: String? = null
    var assosiationFilePath: String? = null

    private val professionList = listOf(
        "Wedding Photography",
        "Event Photography",
        "Product Photography",
        "Corporate Photography",
        "Fashion Photography",
        "Pre-wedding",
        "Portrait Photography"
    )

    var minteger = 0

    var attachmentsOld = ArrayList<FileUpload>()
    var attachments = ArrayList<FileUpload>()

    var typeOfCard: String? = null
    var visitingUploded = false
    var associationUploded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerProfessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarEditProfessionalDetails
        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.profession_details)

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
        customProgress = CustomProgress().getInstance()!!

        binding.editProfession.isFocusable = false
        binding.experienceCount.isFocusable = false

        typeOfCard = getString(R.string.visiting_card)
        binding.editCard.text = activity?.getString(R.string.upload_your_visiting_card)

        binding.rgCardSelection.setOnCheckedChangeListener { _, _ ->
            if (binding.rbVisitingCard.isChecked) {
                typeOfCard = getString(R.string.visiting_card)
                binding.editCard.text = activity?.getString(R.string.upload_your_visiting_card)
            } else if (binding.rbAssociationCard.isChecked) {
                typeOfCard = getString(R.string.association_card)
                binding.editCard.text = activity?.getString(R.string.upload_your_association_card)
            } else {
                typeOfCard = getString(R.string.visiting_card)
                binding.editCard.text = activity?.getString(R.string.upload_your_visiting_card)
            }
        }

        binding.editProfession.setOnClickListener(this)
        binding.minusImg.setOnClickListener(this)
        binding.plusImg.setOnClickListener(this)
        binding.btnProfessionUpdate.setOnClickListener(this)
        binding.editCard.setOnClickListener(this)
        binding.cardPreview.setOnClickListener(this)

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

            showProgressbar.observe(
                requireActivity(),
                Observer { isVisible ->
                    binding.progressBar.visibility =
                        if (isVisible) View.VISIBLE else View.GONE
                }
            )
        }

//        var fileUpload: FileUpload? = null

        with(uploadFileViewModel) {

            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!!, requireActivity())
                            if (typeOfCard == getString(R.string.visiting_card)) {
                                visitingUploded = true
                                attachments.add(response.data.data[0])
                                visitingcardFilePath = response.data.data[0].file_path

                                binding.rbVisitingCard.isChecked = true
                                binding.rbAssociationCard.isChecked = false
                                binding.cardStatus.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireActivity(),
                                        R.drawable.ic_green_right_mark
                                    )
                                )
                            } else {
                                associationUploded = true
                                attachments.add(response.data.data[0])
                                assosiationFilePath = response.data.data[0].file_path
                                binding.rbVisitingCard.isChecked = false
                                binding.rbAssociationCard.isChecked = true
                                binding.cardStatus.setImageDrawable(
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


            showProgressbar.observe(
                requireActivity(),
                Observer { isVisible ->
                    binding.progressBar.visibility =
                        if (isVisible) View.VISIBLE else View.GONE
                }
            )
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
            showProgressbar.observe(
                requireActivity()
            ) { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateProfileData() {
        val editProfession = getStringFromEditText(binding.editProfession)
        if (editProfession.isEmpty()) {
            binding.editProfession.requestFocus()
            binding.errorprofession.visibility = View.VISIBLE
            return
        }
        binding.errorprofession.visibility = View.GONE

        val editStudio = getStringFromEditText(binding.editStusio)
        if (editStudio.isEmpty()) {
            binding.editStusio.requestFocus()
            binding.errorstudio.visibility = View.VISIBLE
            return
        }
        binding.errorstudio.visibility = View.GONE

        val editEquipment = getStringFromEditText(binding.editEquipment)

        val experienceCount = getStringFromEditText(binding.experienceCount)
        if (experienceCount.isEmpty()) {
            binding.experienceCount.requestFocus()
            binding.errorEquipment.visibility = View.VISIBLE
            return
        }
        binding.errorEquipment.visibility = View.GONE

        for (i in attachmentsOld.indices) {
            if (attachmentsOld[i].category.equals("aadhar")) {
                attachments.add(attachmentsOld[i])
            } else if (attachmentsOld[i].category.equals("licence")) {
                attachments.add(attachmentsOld[i])
            } else if (attachmentsOld[i].category.equals("visiting_card")) {
                if (!visitingUploded) {
                    attachments.add(attachmentsOld[i])
                }
            } else if (attachmentsOld[i].category.equals("association_card")) {
                if (!associationUploded) {
                    attachments.add(attachmentsOld[i])
                }
            }
        }

        SharedPrefsHelper.write(
            SharedPrefConstant.USER_EXP,
            minteger
        )
        val updateProfileModel = UpdateProfileModel(
            profession = editProfession,
            studio_name = editStudio,
            equipment_use = editEquipment,
            experience = minteger.toString(),
            attachments = attachments
        )
        profileViewModel.updateProfileUseCase(
            updateProfileModel,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun setUserData(userProfileModel: GetProfileDetails, attachments: List<FileUpload>) {
        if (userProfileModel.profession?.isNotEmpty() == true) {
            binding.editProfession.setText(userProfileModel.profession)
        }
        //binding.editProfession.setText(userProfileModel.profession)
        binding.editStusio.setText(userProfileModel.studio_name)
        binding.editEquipment.setText(userProfileModel.equipment_use)

        val experience = userProfileModel.experience ?: 0
        binding.experienceCount.setText("$experience Yrs")
        minteger = userProfileModel.experience ?: 0

        uploadedFileUrl = userProfileModel.profile_image!!

        for (i in attachments.indices) {
            attachmentsOld.add(attachments[i])
            if (attachmentsOld[i].category.equals("visiting_card")) {
                visitingcardFilePath = attachments[i].file_path
                binding.cardPreview.visibility = View.VISIBLE
                binding.cardStatus.visibility = View.VISIBLE
                visitingUploded = true
                binding.rbVisitingCard.isChecked = true
                binding.rbAssociationCard.isChecked = false
                binding.cardStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_green_right_mark
                    )
                )
            } else if (attachmentsOld[i].category.equals("association_card")) {
                assosiationFilePath = attachments[i].file_path
                binding.cardPreview.visibility = View.VISIBLE
                binding.cardStatus.visibility = View.VISIBLE
                associationUploded = true
                binding.rbVisitingCard.isChecked = false
                binding.rbAssociationCard.isChecked = true
                binding.cardStatus.setImageDrawable(
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

            if (typeOfCard == getString(R.string.visiting_card)) {
                binding.cardPreview.visibility = View.VISIBLE
                visitingcardFilePath = imageFilePath
                binding.cardStatus.visibility = View.VISIBLE

                customProgress.hideProgress()

                val visiting_card: RequestBody = createPartFromString("visiting_card")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = visiting_card
                uploadFileViewModel.updateImgFile(
                    fileUploadMultipart,
                    map,
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )
            } else {
                binding.cardPreview.visibility = View.VISIBLE
                binding.cardStatus.visibility = View.VISIBLE
                assosiationFilePath = imageFilePath

                customProgress.hideProgress()

                val association_card: RequestBody =
                    createPartFromString("association_card")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = association_card
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
                // Create a file to store the image
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
                                "${activity?.packageName}.provider",
                                this
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
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
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

                        if (typeOfCard == getString(R.string.visiting_card)) {
                            binding.cardPreview.visibility = View.VISIBLE
                            visitingcardFilePath = imageFilePath
                            binding.cardStatus.visibility = View.VISIBLE

                            customProgress.hideProgress()

                            val visiting_card: RequestBody = createPartFromString("visiting_card")
                            val map: HashMap<String, RequestBody> = HashMap()
                            map["category"] = visiting_card
                            uploadFileViewModel.updateImgFile(
                                fileUploadMultipart,
                                map,
                                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                            )
                        } else {
                            binding.cardPreview.visibility = View.VISIBLE
                            binding.cardStatus.visibility = View.VISIBLE
                            assosiationFilePath = imageFilePath

                            customProgress.hideProgress()

                            val association_card: RequestBody =
                                createPartFromString("association_card")
                            val map: HashMap<String, RequestBody> = HashMap()
                            map["category"] = association_card
                            uploadFileViewModel.updateImgFile(
                                fileUploadMultipart,
                                map,
                                SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                            )
                        }
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
            R.id.edit_profession -> {
                addDocuments("Profession", professionList)
            }

            R.id.btnProfessionUpdate -> {
                updateProfileData()
            }

            R.id.edit_card -> {
                binding.cardPreview.visibility = View.GONE
                if (typeOfCard == getString(R.string.visiting_card)) {
                    visitingcardFilePath = ""
                    selectMediaFile()
                    visitingUploded = false
                } else {
                    assosiationFilePath = ""
                    selectMediaFile()
                    associationUploded = false
                }

            }

            R.id.minus_img -> {
                decreaseInteger()
            }

            R.id.plus_img -> {
                increaseInteger()
            }

            R.id.card_preview -> {
                if (typeOfCard == getString(R.string.visiting_card)) {
                    if (visitingcardFilePath != null && visitingcardFilePath!!.isNotEmpty()) {
                        showPreview(
                            requireActivity().getString(R.string.visiting_card),
                            visitingcardFilePath!!
                        )
                    }
                } else {
                    if (assosiationFilePath != null && assosiationFilePath!!.isNotEmpty()) {
                        showPreview(
                            requireActivity().getString(R.string.association_card),
                            assosiationFilePath!!
                        )
                    }
                }
            }
        }
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(
            title,
            args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {
                    Toast.makeText(
                        requireActivity(),
                        imagePath,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.editProfession.setText(imagePath)
                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }

    private fun increaseInteger() {
        minteger += 1
        display(minteger)
    }

    private fun decreaseInteger() {
        if (minteger > 0) {
            minteger -= 1
            display(minteger)
        }
    }

    private fun display(number: Int) {
        binding.experienceCount.setText("$number Yrs")
    }

    private fun showPreview(title: String, imagePreview: String) {
        val imagePreviewBottomFragment = ImagePreviewBottomFragment.newInstance(title, imagePreview)
        imagePreviewBottomFragment.show(parentFragmentManager, "ImagePreviewBottomFragment")
    }
}
