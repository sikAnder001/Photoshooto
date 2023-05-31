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
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.bottomsheet.CreatePlansDialog
import com.photoshooto.bottomsheet.SingleSelectionBottomDialogFragment
import com.photoshooto.databinding.FragmentPhotographerProfileBinding
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.manage_address.ManageAddressViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.ui.job.utility.gone
import com.photoshooto.ui.job.utility.visible
import com.photoshooto.ui.photographer.adapter.PlanAdapter
import com.photoshooto.ui.photographer.adapter.ReviewAdapter
import com.photoshooto.ui.photographer.adapter.WorkDemoAdapter
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.*
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.stfalcon.imageviewer.StfalconImageViewer
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

class FragmentPhotographerProfile : Fragment(), EasyPermissions.PermissionCallbacks,
    View.OnClickListener {

    private lateinit var binding: FragmentPhotographerProfileBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()
    private val viewModel: ManageAddressViewModel by viewModel()

    var imageUpdation = false
    var uploadedFileUrl = ""
    var uploadedBackgroundFileUrl = ""
    lateinit var workDemoAdapter: WorkDemoAdapter

    var genderSelected = ""
    val genderList = arrayOf(
        "Select", "Male", "Female", "Others"
    )

    val filterData = listOf("Most Recent", "Most Helpful", "Positive First", "Negative First")

    var flag = false

    var imageFilePath: String? = null
    var photoFile: File? = null
    lateinit var customProgress: CustomProgress // used in child activities
    var fileUploadMultipart: MultipartBody.Part? = null

    var passConfirmShow = false
    var uploadingImgType = ""
    var path = ArrayList<Uri>()
    private var updateAlbumFromWorkDemo = false

    private var detailsFilled = MutableList(3) { false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setListeners()

        customProgress = CustomProgress().getInstance()!!

        setObservers()

        //? Setting Default view for profile
        showWorkDemoView()
    }

    private fun loadUserData() {
        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    fun loadPlans() {
        profileViewModel.getPlanByUserId(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), 10, 0
        )
    }


    private fun setListeners() {
        binding.ivBack.setOnClickListener {
            (activity as PhotographerDashboardActivity).openDrawer()
            findNavController().popBackStack()
        }

        binding.imgEdit.visible()
        binding.imgEdit.setOnClickListener(this)
        binding.tvDetailsDone.setOnClickListener(this)
        binding.editProfileImage.setOnClickListener(this)
        binding.editBackgroundImage.setOnClickListener(this)

        binding.tvPersonalDetails.setOnClickListener(this)
        binding.tvProfessionalDetails.setOnClickListener(this)
        binding.tvAddressDetails.setOnClickListener(this)

        binding.wordDemo.setOnClickListener(this)
        binding.reviews.setOnClickListener(this)
        binding.packages.setOnClickListener(this)

//        binding.filterClick.setOnClickListener(this)
//        binding.btnAddPlan.setOnClickListener(this)
    }

    private fun setObservers() {

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
        var updateProfileModel: UpdateProfileModel

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!! + "...", requireActivity())

                            profileViewModel.getWorkDemo(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
                            workDemoAdapter.notifyDataSetChanged()

                            loadUserData()
                            if (updateAlbumFromWorkDemo) {
                                updateAlbumFromWorkDemo = false
                                return@observe
                            }
                            imageUpdation = true


                            if (uploadingImgType == "profile") {
                                uploadedFileUrl = response.data.data[0].file_path.toString()
                                updateProfileModel = UpdateProfileModel(
                                    profile_image = uploadedFileUrl
                                )

                                profileViewModel.updateProfileUseCase(
                                    updateProfileModel,
                                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                                )
                            } else {
                                uploadedBackgroundFileUrl =
                                    response.data.data.get(0).file_path.toString()
                                updateProfileModel = UpdateProfileModel(
                                    background_image = uploadedBackgroundFileUrl
                                )
                                profileViewModel.updateProfileUseCase(
                                    updateProfileModel,
                                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
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
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

        with(profileViewModel) {
            updateProfileData.observe(requireActivity()) { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        onToast(response.data?.message!!, requireActivity())
                        loadUserData()
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
            }
            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        with(profileViewModel) {
            profileViewModel.apply {
                deletePlanResponse.observe(
                    requireActivity()
                ) { result ->
                    if (result.success!!) {

                    } else {
                        onToast(result.message!!, requireContext())
                    }
                }
            }
        }
    }

    private fun checkDetailsFilled(profileData: ProfileData) {
        if (!profileData.profile_details?.name.isNullOrBlank() &&
            !profileData.profile_details?.email.isNullOrBlank() &&
            (profileData.attachments?.any { it.filterAttachments("licence") } == true ||
                    profileData.attachments?.any { it.filterAttachments("aadhar") } == true)
        ) {
            detailsFilled[0] = true
        }
        if ((!profileData.profile_details?.profession.isNullOrBlank() ||
                    !profileData.profile_details?.profession.equals("-"))
            &&
            !profileData.profile_details?.email.isNullOrBlank() &&
            (profileData.attachments?.any { it.filterAttachments("licence") } == true ||
                    profileData.attachments?.any { it.filterAttachments("aadhar") } == true)
        ) {
            detailsFilled[1] = true
        }
    }

    private fun setUserData(profileData: ProfileData, id: String) {
        val userProfileModel = profileData.profile_details!!
        checkDetailsFilled(profileData)
        binding.eventsCompleted.text = "0"
        binding.eventsOngoing.text = "0"
        binding.rating.text = userProfileModel.rates.toString()
        if (profileData.status == ACCEPT) {
            binding.proId.text = "Pro ID :  $id"
        }
        binding.userName.text = userProfileModel.name
        binding.studioname.text = userProfileModel.studio_name
        if (userProfileModel.profession?.isNotEmpty() == true) {
            binding.photographerType.text = userProfileModel.profession
        }

        val profileImage = userProfileModel.profile_image!!
        SharedPrefsHelper.write(
            SharedPrefConstant.PROFILE_URL,
            profileImage
        )
        Glide.with(requireActivity()).load(urlAddingForPicture(profileImage))
            .error(R.drawable.ic_profile_placeholder).into(binding.profileImage)

        Glide.with(requireActivity()).load(urlAddingForPicture(userProfileModel.background_image!!))
            .error(R.drawable.upload_cover_bg).into(binding.backgroundImg)

        uploadedFileUrl = userProfileModel.profile_image
        uploadedBackgroundFileUrl = userProfileModel.background_image

    }


    private fun selectMediaFile() {
        val intent = Intent(requireActivity(), AccessMediaUtil::class.java)
        mStartAccessMediaForResult.launch(intent)
    }

    @AfterPermissionGranted(AddPhotoBottomDialogFragment.CAMERA_PERMISSION_CODE)
    private fun takePicFromCamera() {
        apply {
            val perms = arrayOf<String>(
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
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
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireActivity(), "${context?.packageName}.provider", this
                        )
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT, photoURI
                        )
                        startActivityForResult(
                            cameraIntent, AddPhotoBottomDialogFragment.ACTION_CAMERA_REQUEST_CODE
                        )
                    }

                }
            }
        }
    }

    private var mStartAccessMediaForResult = registerForActivityResult(
        StartActivityForResult()
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

            if (uploadingImgType.equals("profile")) {
                Glide.with(requireActivity()).load(imageFilePath)
                    .placeholder(R.drawable.ic_profile_placeholder)
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

            } else {
                Glide.with(requireActivity()).load(imageFilePath)
                    .placeholder(R.drawable.upload_cover_bg)
                    .into(binding.backgroundImg)
                customProgress.hideProgress()

                val background_image: RequestBody =
                    createPartFromString("background_image")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = background_image
                uploadFileViewModel.updateImgFile(
                    fileUploadMultipart,
                    map,
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )
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
                val imageFileName = "PhotoShooto$timeStamp.jpg"
                if (resultCode == Activity.RESULT_OK) {
                    val requestFile: RequestBody? =
                        photoFile?.asRequestBody("image/*".toMediaTypeOrNull())
                    requestFile?.apply {
                        fileUploadMultipart = MultipartBody.Part.createFormData(
                            "photos", imageFileName, this
                        )
                        imageFilePath?.let {
                            AddPhotoBottomDialogFragment.onImageUpLoadListener?.onCameraSelected(
                                fileUploadMultipart!!, it
                            )
                        }
                        customProgress.showProgress(requireActivity(), "wait..", false)

                        if (uploadingImgType == "profile") {
                            Glide.with(requireActivity()).load(imageFilePath)
                                .placeholder(R.drawable.ic_profile_placeholder)
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

                        } else {
                            Glide.with(requireActivity()).load(imageFilePath)
                                .placeholder(R.drawable.upload_cover_bg)
                                .into(binding.backgroundImg)
                            customProgress.hideProgress()

                            val background_image: RequestBody =
                                createPartFromString("background_image")
                            val map: HashMap<String, RequestBody> = HashMap()
                            map["category"] = background_image
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
            R.id.editProfileImage -> {
                uploadingImgType = "profile"
                selectMediaFile()
            }
            R.id.editBackgroundImage -> {
                uploadingImgType = "background"
                selectMediaFile()
            }

            R.id.img_edit -> {
                showProfileView()
            }
            R.id.tvDetailsDone -> {
                showWorkDemoView()
            }
            R.id.tvPersonalDetails -> {
                findNavController().navigate(R.id.action_fragmentPhotographerProfile_to_fragmentPhotographerPersonal)
            }

            R.id.tvProfessionalDetails -> {
                findNavController().navigate(R.id.action_fragmentPhotographerProfile_to_fragmentPhotographerProfession)
            }

            R.id.tvAddressDetails -> {
                findNavController().navigate(R.id.action_fragmentPhotographerProfile_to_fragmentPhotographerAddress)
            }

            R.id.word_demo -> {
                showWorkDemoView()
            }

            R.id.reviews -> {
                binding.wordDemo.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.reviews.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
                binding.packages.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                showReviewView()
            }

            R.id.packages -> {
                binding.wordDemo.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.reviews.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
                binding.packages.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
                showPackagesView()
            }

            R.id.filterClick -> {
                addPlansFilters(getString(R.string.filter_by), filterData)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showWorkDemoView()
        profileViewModel.getWorkDemo(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
        workDemoAdapter.notifyDataSetChanged()
    }

    private fun showProfileView() {
        binding.layoutUpdate.visibility = View.VISIBLE
        binding.tabsLayout.gone()
        binding.profileWorkDemoView.root.visibility = View.GONE
        binding.profileReviewView.root.visibility = View.GONE
        binding.profilePackageView.root.visibility = View.GONE

        binding.wordDemo.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        binding.reviews.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        binding.packages.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)

        // mark for completed sections
        if (detailsFilled[0]) {
            binding.tvPersonalDetails.setCompoundDrawablesWithIntrinsicBounds(
                0, 0,
                R.drawable.ic_green_tick_circle,
                0
            )
        }


    }

    private fun showWorkDemoView() {
        binding.wordDemo.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
        binding.reviews.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        binding.packages.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)

        binding.layoutUpdate.visibility = View.GONE
        binding.tabsLayout.visible()
        binding.profileWorkDemoView.root.visibility = View.VISIBLE
        binding.profileReviewView.root.visibility = View.GONE
        binding.profilePackageView.root.visibility = View.GONE

        profileViewModel.getWorkDemo(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

        with(binding.profileWorkDemoView.rvWorkDemo) {
            layoutManager = GridLayoutManager(context, 2)
        }

        binding.profileWorkDemoView.uploadFile.setOnClickListener {
            openUploadView()
        }

        workDemoAdapter = WorkDemoAdapter(
            requireContext(),
            object : WorkDemoAdapter.OnItemClickListener {
                override fun onDetailsClick(position: Int, data: ArrayList<WorkDemoItem>) {
                    StfalconImageViewer.Builder(
                        context,
                        data
                    ) /*{


                                           Glide.with(requireActivity()).load(image.file_path)
                                        .placeholder(R.drawable.upload_cover_bg)
                                        .into(binding.backgroundImg)
                                    customProgress.hideProgress()

                                    }*/

                    { view, image ->
                        Glide.with(requireActivity()).load(image.file_path)
                            .placeholder(R.drawable.upload_cover_bg)
                            .into(binding.backgroundImg)
                        // Picasso.get().load(image.file_path).into(view)
                    }.show().setCurrentPosition(position)
                }
            })

        binding.profileWorkDemoView.rvWorkDemo.adapter = workDemoAdapter

        profileViewModel.getWorkDemo.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.data?.size.log()
                        workDemoAdapter.setList(response.data!!.data as ArrayList<WorkDemoItem>)
                        workDemoAdapter.notifyDataSetChanged()

                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
                profileViewModel.resetWorkDemo()
            }
        }
    }

    private fun openUploadView() {
        val list = ArrayList<MimeType>()
//        list.add(MimeType.WEBP)
//        list.add(MimeType.JPEG)
//        list.add(MimeType.PNG)
        list.add(MimeType.BMP)
        list.add(MimeType.GIF)
        FishBun.with(requireActivity()).setImageAdapter(GlideAdapter())
            .setMaxCount(10).setSelectedImages(path)
            .exceptMimeType(list)
            .startAlbumWithActivityResultCallback(startForResult)
    }

    private val startForResult =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                updateAlbumFromWorkDemo = true
                path = it.data?.getParcelableArrayListExtra(INTENT_PATH) ?: arrayListOf()
                Log.d("startForResult", path[0].path.toString())
                val filePath = FileUtils.getPath(requireContext(), path[0])
                val file = filePath?.let { it1 -> File(it1) }
                val requestFile: RequestBody? = file?.asRequestBody("image/*".toMediaTypeOrNull())

                val fileUploadMultipart = requestFile?.let { it1 ->
                    MultipartBody.Part.createFormData(
                        "photos", file.name, it1
                    )
                }

                val workDemo: RequestBody = createPartFromString("work_demo")
                val map: HashMap<String, RequestBody> = HashMap()
                map["category"] = workDemo
                Log.d("startForResult", fileUploadMultipart.toString())
                uploadFileViewModel.updateImgFile(
                    fileUploadMultipart, map, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
                )
            }
        }

    private fun showReviewView() {
        profileViewModel.getReviewByUserId(
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            10,
            0
        )

        binding.layoutUpdate.visibility = View.GONE
        binding.tabsLayout.visible()
        binding.profileWorkDemoView.root.visibility = View.GONE
        binding.profileReviewView.root.visibility = View.VISIBLE
        binding.profilePackageView.root.visibility = View.GONE

        binding.profileReviewView.filterClick.setOnClickListener {
            addPlansFilters(getString(R.string.filter_by), filterData)
        }

        profileViewModel.getReviewByUserId.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        val listOfReviews = response.data?.data?.list
                        if (!listOfReviews.isNullOrEmpty()) {
                            val adapter = ReviewAdapter(listOfReviews,
                                object : ReviewAdapter.OnItemClickListener {
                                    override fun onDetailsClick(
                                        position: Int,
                                        data: ReviewByUserIdResponse.ReviewData.UserReviewsList
                                    ) {

                                    }
                                })
                            binding.profileReviewView.rvReviews.isNestedScrollingEnabled = false
                            binding.profileReviewView.rvReviews.visibility = View.VISIBLE
                            binding.profileReviewView.rvReviews.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                            binding.profileReviewView.rvReviews.adapter = adapter
                            binding.profileReviewView.rvReviews.visible()
                            binding.profileReviewView.reviewEmptyScreenLayout.gone()
                        } else {
                            binding.profileReviewView.reviewEmptyScreenLayout.visible()
                            binding.profileReviewView.rvReviews.gone()
                        }
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
                profileViewModel.resetReview()
            }
        }
    }

    private fun showPackagesView() {
        loadPlans()

        binding.layoutUpdate.visibility = View.GONE
        binding.tabsLayout.visible()
        binding.profileWorkDemoView.root.visibility = View.GONE
        binding.profileReviewView.root.visibility = View.GONE
        binding.profilePackageView.root.visibility = View.VISIBLE

        binding.profilePackageView.addNewPlanText.setOnClickListener {
            addEditPlan("", "", "", "")
        }

        with(profileViewModel) {
            profileViewModel.getPlanById.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            val data = response.data?.data
                            if (!data.isNullOrEmpty()) {
                                val planAdapter = PlanAdapter(
                                    requireContext(), data,
                                    object : PlanAdapter.OnItemClickListener {
                                        override fun onDetailsClick(
                                            position: Int,
                                            data: PhotographerPlanResponse.PlanDetails
                                        ) {
                                            addEditPlan(
                                                data.id,
                                                data.type,
                                                data.amount,
                                                data.session_hours
                                            )
                                        }

                                        override fun onBookNowClicked() {

                                        }

                                        override fun onDeleteClicked(
                                            position: Int,
                                            data: PhotographerPlanResponse.PlanDetails
                                        ) {
                                            deletePlanApi(data)
                                        }
                                    })
                                binding.profilePackageView.recyclerViewPackage.isNestedScrollingEnabled =
                                    false
                                binding.profilePackageView.recyclerViewPackage.adapter = planAdapter
                                binding.profilePackageView.recyclerViewPackage.visible()
                                binding.profilePackageView.packageEmptyScreenLayout.gone()
                            } else {
                                binding.profilePackageView.recyclerViewPackage.gone()
                                binding.profilePackageView.packageEmptyScreenLayout.visible()
                            }
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                    profileViewModel.resetGetPlan()
                }
            }
        }
    }

    /**/
    private fun addEditPlan(id: String?, type: String?, amount: String?, sessionHours: String?) {
        val dialog = CreatePlansDialog.newInstance(id, type, amount, sessionHours)
        dialog.show(childFragmentManager, CreatePlansDialog.TAG)
    }

    private fun deletePlanApi(data: PhotographerPlanResponse.PlanDetails) {
        profileViewModel.deletePlan(
            data.id!!,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun addPlansFilters(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SingleSelectionBottomDialogFragment.newInstance(title,
            args,
            false,
            object : SingleSelectionBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {
                    // filter reviews by selected data
                    Toast.makeText(
                        requireActivity(), "$imagePath is ", Toast.LENGTH_SHORT
                    ).show()
                }
            })
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }
}