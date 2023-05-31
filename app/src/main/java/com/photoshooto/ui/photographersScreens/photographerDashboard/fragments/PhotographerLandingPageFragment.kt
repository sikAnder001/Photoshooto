package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.FragmentPhotographerLandingPageBinding
import com.photoshooto.domain.model.Banner
import com.photoshooto.domain.model.GetLandingScreenResponse
import com.photoshooto.domain.model.TopFeature
import com.photoshooto.domain.usecase.landing_screen.LandingScreenViewModel
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.landing_page.adapter.*
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.photographer.adapter.HomeBannerAdapter
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs


class PhotographerLandingPageFragment : Fragment() {

    private lateinit var binding: FragmentPhotographerLandingPageBinding
    private val viewModel: LandingScreenViewModel by viewModel()
    private var picOfDayAdapter: PicOfDayAdapter? = null
    private var topFeatureAdapter: TopFeatureAdapter? = null
    private var ourAchievementAdapter: OurAchievementAdapter? = null
    private var testimonialAdapter: TestimonialAdapter? = null
    var currentPage = 0


    private val orderViewModel: OrdersViewModel by viewModel()

    /*private val AES_MODE = "AES/CBC/ISO10126Padding"
    private val CHARSET = "UTF-8"
*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerLandingPageBinding.inflate(inflater, container, false)

        viewModel.getLandingScreenData("photographer")

        observeLandingDataResponse()

        binding.ivSideMenu.setOnClickListener {
            (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
                resources.getString(R.string.open_drawer)
            )
        }

        binding.swipeRefreshLayout.setOnRefreshListener {

            observeLandingDataResponse()
            binding.swipeRefreshLayout.isRefreshing = false

        }

        return binding.root
    }


    private fun observeLandingDataResponse() {
        viewModel.landingScreenResponse.observe(requireActivity()) {
            if (it.success!!) {
                initAdapter(it)
            }
        }
    }

    private fun initAdapter(response: GetLandingScreenResponse?) {

        val allBanner = response?.data?.banners
        val photographerBanner: MutableList<Banner> = ArrayList()
        if (!allBanner.isNullOrEmpty()) {
            for (banner in allBanner) {
                val userType = banner.user_type
                if (userType == "photographer") {
                    photographerBanner.add(banner)
                }
            }
        }

        binding.carouselViewpager.adapter = HomeBannerAdapter(photographerBanner,
            object : HomeBannerAdapter.OnItemClickListener {
                override fun onBannerClick(data: Banner) {
                    if (data.offer_type == "standee") {
                        //findNavController().navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)
                    } else if (data.offer_type == "tshirt") {
                        //findNavController().navigate(R.id.action_photographerLandingPageFragment_to_introTshirtPurchaseFragment)
                    } else if (data.offer_type == "job") {
                        findNavController().navigate(R.id.action_photographerLandingPageFragment_to_jobHome)
                    }
                }
            })
        binding.carouselViewpager.offscreenPageLimit = photographerBanner.size
        binding.carouselViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.carouselViewpager.setPageTransformer(compositePageTransformer)
        TabLayoutMediator(binding.indicator, binding.carouselViewpager) { _, _ ->
            // Some implementation
        }.attach()
        setAutoSlider(binding.carouselViewpager, response?.data?.banners!!.size)

        // top feature adapter
        val topFeature = response.data.top_features
        if (!topFeature.isNullOrEmpty()) {
            topFeatureAdapter = TopFeatureAdapter(requireContext(),
                topFeature,
                object : TopFeatureAdapter.OnItemClickListener {
                    override fun onDetailsClick(topFeature: TopFeature) {

                        if (topFeature.title == "Standee") {
                            navigateToSurprises()
                            //findNavController().navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)
                        } else if (topFeature.title == "T-Shirt") {
                            navigateToSurprises()
                            //findNavController().navigate(R.id.action_photographerLandingPageFragment_to_introTshirtPurchaseFragment)
                        } else if (topFeature.title == "Albums") {
                            navigateToSurprises()
                        } else if (topFeature.title == "Upgrade") {
                            GetSupportDialog.newInstance("", requireContext())
                                .show(parentFragmentManager, GetSupportDialog.TAG)
                        } else if (topFeature.title == "Jobs") {
                            findNavController().navigate(R.id.action_photographerLandingPageFragment_to_jobHome)
                        }
                        //onToast(topFeature.title!!, requireContext())
                    }
                })
            binding.topFeatureRecyclerView.adapter = topFeatureAdapter
        }

        // videos adapter
        val videoList = response.data.videos
        if (!videoList.isNullOrEmpty()) {
            val viewPager = binding.playerViewPager
            val tabLayout = binding.tabLayout
            val adapter = LandingPageVideoAdapter(childFragmentManager, lifecycle, videoList)
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = videoList[position].type
            }.attach()

        }
        // our achievement adapter
        val ourAchievement = response.data.our_achievements
        if (!ourAchievement.isNullOrEmpty()) {
            ourAchievementAdapter = OurAchievementAdapter(requireContext(), ourAchievement)
            binding.ourAchievementsRecyclerView.adapter = ourAchievementAdapter
        }

        // testimonial adapter
        val testimonialList = response.data.testimonials
        if (!testimonialList.isNullOrEmpty()) {
            testimonialAdapter = TestimonialAdapter(requireContext(), testimonialList)
            binding.testimonialsRecyclerView.adapter = testimonialAdapter
        }

        binding.upgradeNowButton.setOnClickListener {
            GetSupportDialog.newInstance("", requireContext())
                .show(parentFragmentManager, GetSupportDialog.TAG)
        }
    }

    /*  override fun onResume() {
          super.onResume()

          observeLandingDataResponse()
      }*/

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

    private fun navigateToSurprises() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_unlock_features)
        val btnUnlock = dialog.findViewById(R.id.btnUnlockFeatures) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnUnlock.text = context?.getString(R.string.coming_soon)
        btnUnlock.setOnClickListener {
            /*  orderViewModel.getOrderRequestList("standee", 10, 1)
              initObserveOrderList(dialog)*/
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initObserveOrderList(dialog: Dialog) {
        with(orderViewModel) {
            ordersLiveData.observe(requireActivity()) { ordersLiveData ->
                ordersLiveData.success.let {
                    if (it) {
                        ordersLiveData.data?.list?.let { orderList ->
                            dialog.dismiss()
                            findNavController().navigate(R.id.fragmentQRCodeGenerationStandeeIntro)
                            /* if (orderList.isNullOrEmpty()) {
                                 navigateToQrEventStandee()
                             } else {
                                 navController.navigate(R.id.photographerRedeemGiftFragment)
                             }*/
                        }
                    } else {
                        dialog.dismiss()
                        onToast(ordersLiveData.message, requireContext())
                    }
                }
            }
        }

    }
}