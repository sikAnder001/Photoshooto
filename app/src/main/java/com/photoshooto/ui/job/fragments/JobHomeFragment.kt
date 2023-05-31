package com.photoshooto.ui.job.fragments

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.FragmentJobHomeBinding
import com.photoshooto.domain.model.*
import com.photoshooto.ui.dialog.FirstOrderDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.HomeSliderCarouselAdapter
import com.photoshooto.ui.job.adapters.JobHomeListAdapter
import com.photoshooto.ui.job.adapters.PhotographerHomeListAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.ui.photographer.adapter.HomeBannerAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.UsedProductsListAdapter
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class JobHomeFragment : Fragment() {

    lateinit var binding: FragmentJobHomeBinding
    private var bannerHandlerBanner = Handler(Looper.getMainLooper())
    private var bannerRunnableFeature: Runnable? = null
    private var bannerHandlerFeature = Handler(Looper.getMainLooper())
    private var bannerRunnableBanner: Runnable? = null
    var user: User? = null
    private var bannerCurrentPage = 0
    private val jobHomeViewModel: JobViewModel by viewModel()
    private var homeSliderCarouselAdapter: HomeSliderCarouselAdapter? = null
    var arrayBanner = ArrayList<Banner>()
    var arrayJobs = ArrayList<JobModel>()
    var arrayPhotographers = ArrayList<AvailablePhotographers>()
    var arrayCamera = ArrayList<CameraModel>()
    private lateinit var jobHomeListAdapter: JobHomeListAdapter
    private lateinit var photographerHomeListAdapter: PhotographerHomeListAdapter
    private lateinit var usedProductsListAdapter: UsedProductsListAdapter

    var currentPage = 0
    lateinit var toolbarTitleView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJobHomeBinding.inflate(inflater, container, false)
        backPress()

        return binding.root
    }

    private fun backPress() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.photographerLandingPageFragment)
                }
            }
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarBinding = binding.toolbarJobPostingHome
        toolbarTitleView = toolbarBinding.tvTitle


/*        toolbarBinding.backBtn.setOnClickListener {
//            (activity as PhotographerDashboardActivity).openDrawer()
//            findNavController().popBackStack()
            backPress()
        }*/

        user = SharedPrefsHelper.getUserCommon()

        jobHomeListAdapter = JobHomeListAdapter(onDetailClick = {
            findNavController().navigate(
                JobHomeFragmentDirections.actionHomeToJobDetail(it.id)
            )
        })

        showDialog()

        photographerHomeListAdapter = PhotographerHomeListAdapter(onDetailClick = {
            findNavController().navigate(
                JobHomeFragmentDirections.actionHomeToJobDetail(it.id)
            )
        })

        usedProductsListAdapter = UsedProductsListAdapter(onClick = { d ->

            findNavController().navigate(
                JobHomeFragmentDirections.actionHomeToPostDetails(d.id, true)
            )
        })

        binding.recyclerJob.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL, false
            )

            addItemDecoration(
                HorizontalSpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = jobHomeListAdapter
        }

        binding.searchLayout.location.setSafeOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()
        }

        binding.recyclerPhotographers.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL, false
            )

            addItemDecoration(
                HorizontalSpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            adapter = photographerHomeListAdapter
        }
        binding.recyclerCamera.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerCamera.adapter = usedProductsListAdapter

        jobHomeViewModel.productsListing(0)

        attachObserver()

        toolbarTitleView.text = "Welcome ${user?.profile_details?.name},"

        val nameTitle = object : ClickableSpan() {
            override fun onClick(widget: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorBlack)
                ds.isUnderlineText = false
            }
        }

        val spannableString = SpannableString(toolbarTitleView.text)
        spannableString.setSpan(
            nameTitle, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        toolbarTitleView.text = spannableString
    }

    private fun attachObserver() {
        binding.toolbarJobPostingHome.backBtn.setOnClickListener {
            findNavController().navigate(R.id.photographerLandingPageFragment)
        }
        jobHomeViewModel.dashboardLiveData.observe(viewLifecycleOwner) { data ->
            if (data.success) {

                data.dashboard.let {
                    arrayJobs.clear()
                    arrayPhotographers.clear()
                    arrayBanner.clear()

                    if (it?.banners?.isNotEmpty() == true) {
                        binding.viewPagerBanner.visible()

                        val allBanner = it.banners
                        val photographerBanner: MutableList<Banner> = ArrayList()
                        if (allBanner.isNotEmpty()) {
                            for (banner in allBanner) {
                                val userType = banner.user_type
                                if (userType == "photographer") {
                                    photographerBanner.add(banner)
                                }
                            }
                        }

                        binding.viewPagerBanner.adapter = HomeBannerAdapter(photographerBanner,
                            object : HomeBannerAdapter.OnItemClickListener {
                                override fun onBannerClick(data: Banner) {
                                    if (data.offer_type == "standee") {
                                        findNavController().navigate(R.id.fragmentQRCodeGenerationStandeeIntro)
                                    } else if (data.offer_type == "tshirt") {
                                        findNavController().navigate(R.id.introTshirtPurchaseFragment)
                                    }
                                }
                            })
                        binding.viewPagerBanner.offscreenPageLimit = photographerBanner.size
                        binding.viewPagerBanner.getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER
                        val compositePageTransformer = CompositePageTransformer()
                        compositePageTransformer.addTransformer(MarginPageTransformer(40))
                        compositePageTransformer.addTransformer { page, position ->
                            val r = 1 - abs(position)
                            page.scaleY = 0.85f + r * 0.15f
                        }
                        binding.viewPagerBanner.setPageTransformer(compositePageTransformer)
                        TabLayoutMediator(binding.dotsIndicator, binding.viewPagerBanner) { _, _ ->
                            // Some implementation
                        }.attach()
                        setAutoSlider(binding.viewPagerBanner, photographerBanner.size)
                    }

                    if (it?.jobsListings?.isNotEmpty() == true) {
                        binding.recyclerJob.visible()

                        arrayJobs.addAll(it.jobsListings)

                        if (arrayJobs.isNotEmpty()) {
                            jobHomeListAdapter.submitList(arrayJobs.toMutableList())
                        }

                    } else {
                        binding.recyclerJob.gone()
                    }

                    if (it?.availablePhotographers?.isNotEmpty() == true) {
                        binding.recyclerPhotographers.visible()

                        arrayPhotographers.addAll(it.availablePhotographers)

                        if (arrayPhotographers.isNotEmpty()) {
                            photographerHomeListAdapter.submitList(arrayPhotographers.toMutableList())
                        }
                    } else {
                        binding.recyclerPhotographers.gone()
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.userProfileData.observe(viewLifecycleOwner) { data ->
            if (data.success) {
                data.data?.let { SharedPrefsHelper.setUser(it) }
            }
        }

        jobHomeViewModel.productsList.observe(viewLifecycleOwner) { data ->

            if (data.success) {
                data.data.let { data ->

                    if (data?.list?.isNotEmpty() == true) {
                        if (data.page == 0) {
                            usedProductsListAdapter.swapList(null, false)
                        }
                        usedProductsListAdapter.swapList(data.list, false)
                    }

                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }
    }

    private fun setAutoSlider(viewPager2: ViewPager2, noOfPage: Int) {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == noOfPage) {
                currentPage = 0
            }
            viewPager2.setCurrentItem(currentPage++, true)
        }

        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 1000, 3000)

    }


    private fun showDialog() {
        if (SharedPrefsHelper.read(SharedPrefConstant.SHOW_JOB_POSTING_POPUP, true)) {
            SharedPrefsHelper.write(SharedPrefConstant.SHOW_JOB_POSTING_POPUP, false)
            FirstOrderDialog.showDialog(
                requireContext(),
                "₹3000+", "₹0"
            ) {

            }
        }
    }

    private fun startAutoScrollBanner() {
        if (bannerRunnableBanner == null) {
            bannerRunnableBanner = Runnable {
                if (activity != null && activity?.isFinishing == false) {
                    if (bannerCurrentPage == arrayBanner.size) {
                        bannerCurrentPage = 0
                    }
                    if (activity?.isFinishing == false) {
                        binding.viewPagerBanner.setCurrentItem(bannerCurrentPage++, true)
                    }
                }
            }

            val size = arrayBanner.size
            if (size > 0) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        bannerHandlerBanner.post(bannerRunnableBanner!!)
                    }
                }, 500L, 3000.toLong())
            }
        }
    }

    private fun stopBannerScrollBanner() {
        if (bannerHandlerBanner != null && bannerRunnableBanner != null) {
            bannerHandlerBanner.removeCallbacks(bannerRunnableBanner!!)
        }
    }

    private fun stopBannerScrollFeature() {
        if (bannerHandlerFeature != null && bannerRunnableFeature != null) {
            bannerHandlerFeature.removeCallbacks(bannerRunnableFeature!!)
        }
    }

    override fun onPause() {
        stopBannerScrollBanner()
        stopBannerScrollFeature()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {

        jobHomeViewModel.dashBoardData()
        jobHomeViewModel.getUserProfile()

        binding.viewAll2.setOnClickListener {
            findNavController().navigate(JobHomeFragmentDirections.actionHomeToPhotographerList())
        }
        binding.viewAll1.setOnClickListener {
            findNavController().navigate(JobHomeFragmentDirections.actionHomeToJobList())
        }

        binding.viewAll3.setOnClickListener {
            findNavController().navigate(JobHomeFragmentDirections.actionHomeToPostProductStep1())
        }
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

            val snackbar: Snackbar = Snackbar
                .make(
                    binding.root,
                    getString(R.string.permission_always_denied),
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction("Settings") {
                    activity?.openPermissionSettings()
                }
            snackbar.show()
            return
        }

        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            GetCurrentLocation.getLastLocation(requireContext()) { latitude, longitude, address ->
                try {
                    binding.searchLayout.search.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress = (address[0].locality)
                    }
                    binding.searchLayout.search.setText(googleAddress)
                    binding.searchLayout.search.setSelection(
                        binding.searchLayout.search.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
