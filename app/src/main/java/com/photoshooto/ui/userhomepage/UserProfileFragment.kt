package com.photoshooto.ui.userhomepage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.databinding.FragmentUserProfileBinding
import com.photoshooto.domain.model.ProfileData
import com.photoshooto.domain.model.UpdateProfileModel
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.photographersScreens.photographerAuth.AuthActivity
import com.photoshooto.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()
    var imageFilePath: String? = null
    var photoFile: File? = null
    var fileUploadMultipart: MultipartBody.Part? = null
    private val userLoginViewModel: UserLoginViewModel by viewModel()
    lateinit var customProgress: CustomProgress // used in child activities

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgress = CustomProgress().getInstance()!!

        profileViewModel.getProfileByIDUseCase(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )

        initializeData()
        initLiveData()
        clickListener()
    }

    private fun initializeData() {
        binding.tvUserName.text = SharedPrefsHelper.read(SharedPrefConstant.USER_NAME).toString()
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
                        }
                        else -> {}
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
                            updateProfileModel = UpdateProfileModel(
                                profile_image = uploadedFileUrl
                            )

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

        with(userLoginViewModel) {
            userLogoutStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    Log.e("logout response", "" + response.toString())
                    when (response.status) {
                        Status.SUCCESS -> {
                            logoutAndRedirect()
                        }
                        Status.ERROR -> {
                            logoutAndRedirect()
                            response.data?.message?.let { it1 -> showToast(context, it1) }
                        }
                        else -> {
                            logoutAndRedirect()
                        }
                    }
                }
            }
        }

    }

    private fun logoutAndRedirect() {
        val context1 = context
        SharedPrefsHelper.clearAllPreferences()
        SharedPrefsHelper.write(SharedPrefConstant.SHOW_ON_BOARDING, true)
        if (context1 != null) {
            clearApplicationData(context1)
            clearPreferences(context1)
        }
        navigateToAuthScreen()
    }

    private fun navigateToAuthScreen() {
        val fragmentActivity = activity
        if (fragmentActivity != null) {
            val intent = Intent(fragmentActivity, AuthActivity::class.java)
            intent.apply {
                startActivity(this)
                fragmentActivity.finish()
            }
        }
    }

    private fun setUserData(profileData: ProfileData, id: String) {
        val userProfileModel = profileData.profile_details!!
        binding.tvUserName.text = userProfileModel.name

        val profileImage = userProfileModel.profile_image!!
        SharedPrefsHelper.write(
            SharedPrefConstant.PROFILE_URL,
            profileImage
        )
        Glide.with(requireActivity()).load(urlAddingForPicture(profileImage))
            .error(R.drawable.ic_girl_profile).into(binding.profileImage)

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
    }


    private fun clickListener() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvMyEnquiries.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userEnquiriesFragment)
        }

        binding.imgSupport.setOnClickListener {
            GetSupportDialog.newInstance("", requireContext())
                .show(parentFragmentManager, GetSupportDialog.TAG)
        }

        binding.imgFav.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_favouritePhotographerFragment)
        }

        binding.tvNotification.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userNotificationsFragment)
        }

        binding.tvPersonalDetails.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userEditProfileFragment)
        }

        binding.imgEdit.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userEditProfileFragment)
        }

        binding.imgUpload.setOnClickListener {
            selectMediaFile()
        }

        binding.tvLogout.setOnClickListener {
            navigateToLogoutDialog()
        }
    }

    private fun navigateToLogoutDialog() {
        val context1 = context
        if (context1 != null) {
            MaterialAlertDialogBuilder(
                context1, R.style.Theme_BottomNavWithSideNav_Dialog_Alert
            ).setTitle(this.resources.getString(R.string.logout))
                .setMessage(this.resources.getString(R.string.would_you_like_to_logout))
                .setPositiveButton(
                    this.resources.getString(R.string.logout_ok)
                ) { dialog, which ->
                    userLoginViewModel.logout(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))
                }.setNegativeButton(
                    this.resources.getString(R.string.cancel)
                ) { dialog, which -> dialog.cancel() }.show()
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