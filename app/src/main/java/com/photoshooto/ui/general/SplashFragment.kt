//package com.photoshooto.ui.general
//
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.os.Handler
//import android.os.Looper
//import android.view.*
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.photoshooto.databinding.FragmentSplashBinding
//import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
//import com.photoshooto.util.recordScreenView
//
//
//class SplashFragment : Fragment() {
//
//    private var _binding: FragmentSplashBinding? = null
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentSplashBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//        recordScreenView(requireActivity(),"SplashFragment", FirebaseAnalytics_Event_ScreenName.screenSplash)
//
//        Handler(Looper.myLooper()!!).postDelayed({
//            val direction = SplashFragmentDirections.actionSplashFragmentToIntroSliderFragment()
//            findNavController().navigate(direction)
//        },1000)
//        return root
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requireActivity().window.requestFeature(Window.FEATURE_NO_TITLE)
//        requireActivity().window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//    }
//
///*
//    init {
//        object : CountDownTimer(500L, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//            }
//            override fun onFinish() {
//                val direction =
//                    SplashFragmentDirections.actionIntroSliderFragmentToFragmentRouting()
//                findNavController().navigate(direction)
//            }
//        }.start()
//    }
//*/
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//
//}
//
////    private fun initsview() = with(binding) {
////        buttonSignInToContinueWelcome.setOnClickListener {
////            val direction = WelcomeFragmentDirections.actionWelcomeFragmentToSignInFragment()
////            it.findNavController().navigate(direction)
////        }
////
////        buttonJoinWithNewAccountWelcome.setOnClickListener {
////            val direction = WelcomeFragmentDirections.actionWelcomeFragmentToSignUpFragment()
////            it.findNavController().navigate(direction)
////        }
////    }