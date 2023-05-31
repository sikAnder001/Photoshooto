package com.photoshooto.ui.userhomepage

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.bottomsheet.CreatePlansDialog
import com.photoshooto.bottomsheet.FragmentEnquiryBottomSheet
import com.photoshooto.databinding.FragmentPhotographerDetailBinding
import com.photoshooto.domain.model.*
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.photographer.adapter.PlanAdapter
import com.photoshooto.ui.photographer.adapter.ReviewAdapter
import com.photoshooto.ui.photographer.adapter.WorkDemoAdapter
import com.photoshooto.ui.photographersScreens.WriteReviewDialog
import com.photoshooto.ui.qrcodesetup.createEvent.CreateEventViewModel
import com.photoshooto.util.*
import okhttp3.MultipartBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class PhotographerDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentPhotographerDetailBinding

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    private val uploadFileViewModel: UploadFileViewModel by viewModel()

    var imageUpdation = false
    var uploadedFileUrl = ""
    var uploadedBackgroundFileUrl = ""

    var genderSelected = ""
    val genderList = arrayOf(
        "Select", "Male", "Female", "Others"
    )

    private var mEventDuration = ""
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
    var userID: String? = null
    private val mViewModel: CreateEventViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userID = requireArguments().get("userID").toString()

        profileViewModel.getProfileByIDUseCase(
            userID,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
        mViewModel.getEventTypeList(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

        customProgress = CustomProgress().getInstance()!!
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.editProfileImage.setOnClickListener(this)


        with(profileViewModel) {
            getUserData.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            setUserData(
                                response.data?.data?.profile_details!!,
                                response.data.data.id!!
                            )
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            showProgressbar.observe(requireActivity(), androidx.lifecycle.Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }
        var updateProfileModel: UpdateProfileModel

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            onToast(response.data?.message!!, requireActivity())
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
                            } else {
                                uploadedBackgroundFileUrl =
                                    response.data.data.get(0).file_path.toString()
                                updateProfileModel = UpdateProfileModel(
                                    background_image = uploadedBackgroundFileUrl
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
            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        with(profileViewModel) {
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
            showProgressbar.observe(requireActivity(), androidx.lifecycle.Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

        profileViewModel.apply {
            deletePlanResponse.observe(
                requireActivity()
            ) { result ->
                if (result.success!!) {
                    showPackagesView()
                } else {
                    onToast(result.message!!, requireContext())
                }
            }
        }

        binding.wordDemo.setOnClickListener(this)
        binding.reviews.setOnClickListener(this)
        binding.packages.setOnClickListener(this)
        binding.btnBookNow.setOnClickListener {
            loadEnquiryBottomSheet()
        }
        loadDefaultWorkDemo()
    }

    private fun setUserData(userProfileModel: GetProfileDetails, id: String) {

        binding.eventsCompleted.text = ""
        binding.eventsOngoing.text = ""
        binding.rating.text = userProfileModel.rates.toString()
        binding.proId.text = "Pro ID : " + " " + id
        binding.userName.text = userProfileModel.name
        binding.studioname.text = userProfileModel.studio_name
        if (userProfileModel.profession?.isNotEmpty() == true) {
            binding.photographerType.text = userProfileModel.profession
        }
        //binding.photographerType.text = userProfileModel.profession
//        binding.photoType.setText(userProfileModel.name)

        Glide.with(requireActivity()).load(urlAddingForPicture(userProfileModel.profile_image!!))
            .error(R.drawable.splas_logo_ps).into(binding.profileImage)

        Glide.with(requireActivity()).load(urlAddingForPicture(userProfileModel.background_image!!))
            .error(R.drawable.splas_logo_ps).into(binding.backgroundImg)

        uploadedFileUrl = userProfileModel.profile_image
        uploadedBackgroundFileUrl = userProfileModel.background_image

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.word_demo -> {
                loadDefaultWorkDemo()
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
//                addPlansFilters(getString(R.string.filter_by), filterData)
            }
//            R.id.btnAddPlan -> {
//
//            }
        }
    }

    private fun loadDefaultWorkDemo() {
        binding.wordDemo.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.white_bg_corners)
        binding.reviews.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        binding.packages.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.plain_bg_corners)
        showWorkDemoView()
    }

    private fun showWorkDemoView() {
        binding.profileWorkDemoView.root.visibility = View.VISIBLE
        binding.profileReviewView.root.visibility = View.GONE
        binding.profilePackageView.root.visibility = View.GONE

        profileViewModel.getWorkDemo(SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN))

        with(binding.profileWorkDemoView.rvWorkDemo) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        val adapter = WorkDemoAdapter(
            requireContext(),
            object : WorkDemoAdapter.OnItemClickListener {
                override fun onDetailsClick(position: Int, data: ArrayList<WorkDemoItem>) {

                }

            })
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.profileWorkDemoView.rvWorkDemo.isNestedScrollingEnabled = false
        binding.profileWorkDemoView.rvWorkDemo.layoutManager = layoutManager
        binding.profileWorkDemoView.rvWorkDemo.adapter = adapter

        profileViewModel.getWorkDemo.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {

                        adapter.setList(response.data?.data!! as ArrayList<WorkDemoItem>)
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

    private fun showReviewView() {
        profileViewModel.getReviewByUserId(
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN),
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID), 10, 0
        )

        binding.profileWorkDemoView.root.visibility = View.GONE
        binding.profileReviewView.root.visibility = View.VISIBLE
        binding.profilePackageView.root.visibility = View.GONE


        binding.profileReviewView.llRateMe.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "PhotographerOrderDetailsFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographerOrder_Details_Review
            )
            userID?.let { it1 ->
                WriteReviewDialog.newInstance(it1, "", requireContext())
                    .show(parentFragmentManager, WriteReviewDialog.TAG)
            }
        }

        profileViewModel.getReviewByUserId.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        val adapter = ReviewAdapter(
                            response.data?.data?.list!!,
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
        profileViewModel.getPlanByUserId(
            SharedPrefsHelper.read(SharedPrefConstant.USER_ID),
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), 10, 0
        )

        binding.profileWorkDemoView.root.visibility = View.GONE
        binding.profileReviewView.root.visibility = View.GONE
        binding.profilePackageView.root.visibility = View.VISIBLE
        /*binding.profilePackageView.addNewPlanText.setOnClickListener {
            val dialog = CreatePlansDialog.newInstance("", "", "")
            dialog.show(parentFragmentManager, CreatePlansDialog.TAG)
        }*/
        profileViewModel.getPlanById.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        val res = response.data
                        val data = res?.data
                        val planAdapter = PlanAdapter(
                            requireContext(),
                            data!!,
                            object : PlanAdapter.OnItemClickListener {
                                override fun onDetailsClick(
                                    position: Int, data: PhotographerPlanResponse.PlanDetails
                                ) {
                                    addEditPlan(
                                        data.id,
                                        data.type,
                                        data.amount,
                                        data.session_hours
                                    )
                                }

                                override fun onBookNowClicked() {
                                    loadEnquiryBottomSheet()
                                }

                                override fun onDeleteClicked(
                                    position: Int,
                                    data: PhotographerPlanResponse.PlanDetails
                                ) {
                                    deletePlanApi(data)
                                }

                            }, isPhotographer = false
                        )
                        binding.profilePackageView.recyclerViewPackage.isNestedScrollingEnabled =
                            false
                        binding.profilePackageView.recyclerViewPackage.adapter = planAdapter
                        binding.profilePackageView.recyclerViewPackage.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
                profileViewModel.resetGetPlan()
            }
        }

        binding.profilePackageView.llEnquire.setOnClickListener {
            loadEnquiryBottomSheet()
        }
    }

    private fun addEditPlan(id: String?, type: String?, amount: String?, sessionHours: String?) {
        val dialog = CreatePlansDialog.newInstance(id, type, amount, sessionHours)
        dialog.show(parentFragmentManager, CreatePlansDialog.TAG)
    }

    private fun deletePlanApi(data: PhotographerPlanResponse.PlanDetails) {
        profileViewModel.deletePlan(
            data.id!!,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun loadEnquiryBottomSheet() {
        FragmentEnquiryBottomSheet.showDialog(
            userID,
            activity?.supportFragmentManager!!,
            "PhotographerDetailFragment"
        )
    }
}