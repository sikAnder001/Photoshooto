//package com.photoshooto.ui.intro
//
//import android.os.Bundle
//import android.view.*
//import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
//import android.widget.ImageView
//import android.widget.LinearLayout
//import androidx.core.content.ContextCompat
//import androidx.core.view.get
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import androidx.viewpager2.widget.ViewPager2
//import com.photoshooto.R
//import com.photoshooto.databinding.FragmentIntrosliderBinding
//import com.photoshooto.util.FirstPrefManager
//import com.photoshooto.util.PreferenceManager
//
//class IntroSliderFragment : Fragment() {
//
//    private var _binding: FragmentIntrosliderBinding? = null
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    private var prefManager: FirstPrefManager? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        _binding = FragmentIntrosliderBinding.inflate(inflater, container, false)
//
//        prefManager = FirstPrefManager(requireActivity())
//
//        println("in intro "+prefManager?.isFirstTimeLaunch)
//        if (!prefManager!!.isFirstTimeLaunch) {
//            launchHomeScreen()
//        }
//
//        binding.introSliderViewPager2.adapter = introSliderAdapter
//        //calling function
//        setupIndicators()
//        setCurrentIndicator(0)
//        binding.introSliderViewPager2.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                setCurrentIndicator(position)
//            }
//        })
//
//        binding.btnNext.setOnClickListener {
//            if (binding.introSliderViewPager2.currentItem + 1 < introSliderAdapter.itemCount) {
//                binding.introSliderViewPager2.currentItem += 1
//            } else {
//                launchHomeScreen()
//            }
//        }
//
//        binding.tvSkipIntro.setOnClickListener {
//            val direction = IntroSliderFragmentDirections.actionIntroSliderFragmentToFragmentRouting()
//            findNavController().navigate(direction)
//        }
//
//        val root: View = binding.root
//        return root
//    }
//
//    private fun launchHomeScreen() {
//        prefManager?.isFirstTimeLaunch = false
//        if (PreferenceManager.isLogged){
//            val direction = IntroSliderFragmentDirections.actionIntroSliderFragmentToHomeFragment()
//            findNavController().navigate(direction)
//        }else{
//            val direction = IntroSliderFragmentDirections.actionIntroSliderFragmentToFragmentRouting()
//            findNavController().navigate(direction)
//        }
//    }
//
//    private val introSliderAdapter = IntroSliderAdapter(
//        listOf(
//            IntroSlide(
//                "Scan QR Code",
//                "Quis praesent sem pulvinar eget\n feugiat nunc aliquam quam. Neque,\n odio volutpat nec enim.",
//                R.drawable.ic_frame
//            ),
//            IntroSlide(
//                "Register",
//                "Quis praesent sem pulvinar eget\n feugiat nunc aliquam quam. Neque,\n odio volutpat nec enim.",
//                R.drawable.ic_frame1
//            ),
//            IntroSlide(
//                "View & Download",
//                "Quis praesent sem pulvinar eget\n feugiat nunc aliquam quam. Neque,\n odio volutpat nec enim.",
//                R.drawable.ic_frame2
//            )
//        )
//    )
//
//    private fun setCurrentIndicator(index: Int) {
//        val childCount = binding.indicatorsContainer.childCount
//        for (i in 0 until childCount) {
//            val imageView2 = binding.indicatorsContainer[i] as ImageView
//            if (i == index) {
//                imageView2.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        requireActivity(),
//                        R.drawable.indicator_active
//                    )
//                )
//            } else {
//                imageView2.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        requireActivity(),
//                        R.drawable.indicator_inactive
//                    )
//                )
//            }
//        }
//    }
//
//    private fun setupIndicators() {
//        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
//        val layoutParams: LinearLayout.LayoutParams =
//            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
//        layoutParams.setMargins(8, 0, 8, 0)
//        for (i in indicators.indices) {
//            indicators[i] = ImageView(requireActivity())
//            indicators[i].apply {
//                this?.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        requireActivity(),
//                        R.drawable.indicator_inactive
//                    )
//                )
//                this?.layoutParams = layoutParams
//            }
//            binding.indicatorsContainer.addView(indicators[i])
//        }
//    }
//
//
//}
//
//
//
//
//
//
//
