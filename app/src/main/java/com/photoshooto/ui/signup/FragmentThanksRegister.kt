package com.photoshooto.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentThanksRegisterBinding
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.Constant
import com.photoshooto.util.SharedPrefConstant
import com.photoshooto.util.SharedPrefsHelper
import com.photoshooto.util.recordScreenView

class FragmentThanksRegister : Fragment() {
    private lateinit var binding: FragmentThanksRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThanksRegisterBinding.inflate(inflater, container, false)

        recordScreenView(
            requireActivity(),
            "FragmentThanksRegister",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup_Thanks_for_Registering_Screen
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.visibility = View.VISIBLE
        binding.imageViewBack.setOnClickListener {
            if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_CLIENT.name) {
                (activity as DashBoardScreenActivity).openDrawer()
            } else {

                (activity as PhotographerDashboardActivity).openDrawer()
            }
        }
        binding.tvEditProfile.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentThanksRegister",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_SignupEditProfile
            )
            if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name) {
                findNavController().navigate(R.id.action_fragmentThanksRegister_to_fragmentPhotographerProfileDashBoard)
            } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_CLIENT.name) {
                findNavController().navigate(R.id.action_FragmentThanksRegister_to_FragmentEditProfile)
            }

        }
    }
}