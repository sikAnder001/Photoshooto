package com.photoshooto.ui.landing_page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.photoshooto.R
import com.photoshooto.databinding.FragmentLandingPageBinding
import com.photoshooto.domain.model.GetLandingScreenResponse
import com.photoshooto.domain.model.TopFeature
import com.photoshooto.domain.model.VideoItem
import com.photoshooto.domain.usecase.landing_screen.LandingScreenViewModel
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.landing_page.adapter.OurAchievementAdapter
import com.photoshooto.ui.landing_page.adapter.PicOfDayAdapter
import com.photoshooto.ui.landing_page.adapter.TestimonialAdapter
import com.photoshooto.ui.landing_page.adapter.TopFeatureAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * landing screen
 * created by amardeep manav
 */
class LandingPageFragment : Fragment() {

    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LandingScreenViewModel by viewModel()
    private var picOfDayAdapter: PicOfDayAdapter? = null
    private var topFeatureAdapter: TopFeatureAdapter? = null
    private var ourAchievementAdapter: OurAchievementAdapter? = null
    private var testimonialAdapter: TestimonialAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingPageBinding.inflate(inflater, container, false)

        viewModel.getLandingScreenData("photographer")

        observeLandingDataResponse()

        binding.ivSideMenu.setOnClickListener {
            (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
                resources.getString(R.string.open_drawer)
            )
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
        // pic of the day adapter
        val picOfDay = response?.data?.pic_of_day
        if (!picOfDay.isNullOrEmpty()) {
            picOfDayAdapter = PicOfDayAdapter(requireContext(), picOfDay)
            binding.carouselViewpager.adapter = picOfDayAdapter
        }

        // top feature adapter
        val topFeature = response?.data?.top_features
        if (!topFeature.isNullOrEmpty()) {
            topFeatureAdapter = TopFeatureAdapter(requireContext(),
                topFeature,
                object : TopFeatureAdapter.OnItemClickListener {
                    override fun onDetailsClick(topFeature: TopFeature) {
                        Log.i("topFeature", topFeature.title!!)
                        /*if (topFeature.title == "Standee") {
                            findNavController().navigate(R.id.action_photographerLandingPageFragment_to_fragmentQRCodeGenerationStandeeIntro)
                        } else if (topFeature.title == "Standee") {
                            findNavController().navigate(R.id.action_photographerLandingPageFragment_to_introTshirtPurchaseFragment)
                        }*/
                        //onToast(topFeature.title!!, requireContext())
                    }
                })
            binding.topFeatureRecyclerView.adapter = topFeatureAdapter
        }

        // videos adapter
        //val videoList = response?.data?.videoList
        val videoListModify = mutableListOf<VideoItem>()
        videoListModify.add(
            VideoItem(
                "",
                "Standee",
                "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
                ""
            )
        )
//        videoListModify.add(
//            VideoItem(
//                "",
//                "T-shirt",
//                "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
//                ""
//            )
//        )

        if (!videoListModify.isNullOrEmpty()) {

            /* val viewPager = binding.playerViewPager
             val tabLayout = binding.tabLayout
             val adapter = LandingPageVideoAdapter(parentFragmentManager, lifecycle, videoListModify)
             viewPager.adapter = adapter

             TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                 tab.text = videoListModify[position].title
             }.attach()*/

        }
        // our achievement adapter
        val ourAchievement = response?.data?.our_achievements
        if (!ourAchievement.isNullOrEmpty()) {
            ourAchievementAdapter = OurAchievementAdapter(requireContext(), ourAchievement)
            binding.ourAchievementsRecyclerView.adapter = ourAchievementAdapter
        }

        // testimonial adapter
        val testimonialList = response?.data?.testimonials
        if (!testimonialList.isNullOrEmpty()) {
            testimonialAdapter = TestimonialAdapter(requireContext(), testimonialList)
            binding.testimonialsRecyclerView.adapter = testimonialAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}