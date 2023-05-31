package com.photoshooto.ui.photographersScreens.photographerAuth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentLoginBinding
import com.photoshooto.domain.usecase.login.UserLoginRequest
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.job.utility.checkIfDateGone
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.*
import com.pixplicity.easyprefs.library.Prefs
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: UserLoginViewModel by viewModel()
    private val getUserProfileViewModel: GetUserProfileViewModel by viewModel()
    private var passConfirmShow = false
    private var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        recordScreenView(
            requireActivity(),
            "LoginFragment",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userType = requireArguments().getString("userType").toString()
        binding.txtBackLogin.text = "Login as $userType"
        binding.txtBackLogin.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.forgotPass.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "LoginFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_ForgotPassword_Button
            )
            navigateToForgotPass()
        }

        binding.btnLogin.setSingleClickListener {
            recordScreenView(
                requireActivity(),
                "LoginFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Button
            )
            hideKeyboard()
            handleLoginClick()
        }

        binding.editPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                //handleLoginClick()
                true
            } else false
        }


        binding.signupBtn.setSingleClickListener {
            recordScreenView(
                requireActivity(),
                "LoginFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Signup_Button
            )
            val bundle = Bundle()
            bundle.putString("userType", userType)
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment, bundle)
        }

//        with(viewModel) {
//            loginResponse.observe(requireActivity(), Observer {
//                if (it.success!!) {
//                    with(PreferenceManager) {
//
////                        status = it.data.user.status!!
//                    }
//                    onToast(it.message!!, requireContext())
//                    println("getLoginType :" + PreferenceManager.getLoginType)
//
//                    navigateToHomeScreen()
//                } else {
//                    onToast(it.message!!, requireContext())
//                }
//            })
//            showProgressbar.observe(requireActivity(), Observer { isVisible ->
//                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
//            })
//
//        }

        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE

                        if (response.data?.data != null) {
                            val responseData = response.data.data

                            Prefs.putString(SharedPrefsHelper.AuthKey, responseData.token!!)

                            responseData.user?.apply {
                                SharedPrefsHelper.setUser(responseData.user)
                                SharedPrefsHelper.setSubscribed(
                                    if (this.subscriptions_end != null) {
                                        !(this.subscriptions_end).checkIfDateGone()
                                    } else {
                                        false
                                    }
                                )
                            }
                            SharedPrefsHelper.write(SharedPrefConstant.IS_LOGIN, true)
                            SharedPrefsHelper.write(
                                SharedPrefConstant.AUTH_TOKEN,
                                responseData.token
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.MOBILE_NUMBER,
                                responseData.user?.profile_details?.mobile!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_EMAIL,
                                responseData.user.profile_details.email!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_NAME,
                                responseData.user.profile_details.name!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_EXP,
                                responseData.user.profile_details.experience!!
                            )

                            if (responseData.user.profile_details.profession?.isNotEmpty()!!) {
                                responseData.user.profile_details.profession.let {
                                    SharedPrefsHelper.write(
                                        SharedPrefConstant.USER_PROFESSION,
                                        it
                                    )
                                }
                            }
                            SharedPrefsHelper.write(
                                SharedPrefConstant.PROFILE_URL,
                                responseData.user.profile_details.profile_image!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_ID,
                                responseData.user.id!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.LOGIN_TYPE, responseData.user.type!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.EMPLOYEE_BY, responseData.user.employee_by!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_ROLE, responseData.user.role!!
                            )

                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_REGISTERED_ON,
                                responseData.user.created_at!!
                            )

                            SharedPrefsHelper.write(
                                SharedPrefConstant.USER_STATUS,
                                responseData.user.status!!
                            )

                            responseData.user.profile_complete?.toInt()?.let {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.PROFILE_COMPLETED,
                                    it
                                )
                            }

                            if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.PHOTOGRAPHER.name.lowercase(
                                    Locale.getDefault()
                                ) && SharedPrefsHelper.read(
                                    SharedPrefConstant.USER_ROLE
                                ) == Constant.USER_ROLE.CLIENT.name.lowercase(Locale.getDefault())
                            ) {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.USER_CASE_TYPE,
                                    Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name
                                )
                            } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.lowercase(
                                    Locale.getDefault()
                                ) && SharedPrefsHelper.read(
                                    SharedPrefConstant.USER_ROLE
                                ) == Constant.USER_ROLE.CLIENT.name.lowercase(Locale.getDefault())
                            ) {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.USER_CASE_TYPE,
                                    Constant.USER_CASE_TYPE.USER_CLIENT.name
                                )
                            } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.lowercase(
                                    Locale.getDefault()
                                ) && SharedPrefsHelper.read(
                                    SharedPrefConstant.USER_ROLE
                                ) == Constant.USER_ROLE.ADMIN.name.lowercase(Locale.getDefault())
                            ) {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.USER_CASE_TYPE,
                                    Constant.USER_CASE_TYPE.USER_ADMIN.name
                                )
                            } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.lowercase(
                                    Locale.getDefault()
                                ) && SharedPrefsHelper.read(
                                    SharedPrefConstant.USER_ROLE
                                ) == Constant.USER_ROLE.SUPERADMIN.name.lowercase(Locale.getDefault())
                            ) {
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.USER_CASE_TYPE,
                                    Constant.USER_CASE_TYPE.USER_SUPER_ADMIN.name
                                )
                            }

                            /* if(status.equals("accept") ){
                                                    val direction = FragmentThanksRegisterDirections.actionFragmentThanksRegisterToFragmentPhotographerProfile()
                                                    findNavController().navigate(direction)A
                                                }else if(status.equals("active") && status.equals("reject"))
                                                {
                                                    StayTunedDialog.newInstance("", requireContext())
                                                        .show(getParentFragmentManager(), StayTunedDialog.TAG)

                                                }*/

                            SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE)?.let {
                                navigateToHomeScreen()
                            }
                        }

                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        response.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.editPassword.setOnTouchListener(object :
            DrawableClickListener.RightDrawableClickListener(binding.editPassword) {
            override fun onDrawableClick(): Boolean {
                if (!passConfirmShow) {
                    passConfirmShow = true
                    binding.editPassword.transformationMethod = null
                    binding.editPassword.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            requireActivity(), R.drawable.ic_baseline_visibility_off_24
                        ), null
                    )
                } else {
                    binding.editPassword.transformationMethod = PasswordTransformationMethod()
                    passConfirmShow = false
                    binding.editPassword.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            requireActivity(), R.drawable.ic_baseline_remove_red_eye_24
                        ), null
                    )
                }
                return true
            }
        })
    }

    private fun navigateToHomeScreen() {

        if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_SUPER_ADMIN.name ||
            SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_ADMIN.name
        ) {
            val intent = Intent(requireContext(), AdminDashboardActivity::class.java)
            intent.apply {
                startActivity(this)
                requireActivity().finish()
            }
        } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name) {
            val intent = Intent(requireContext(), PhotographerDashboardActivity::class.java)
            intent.apply {
                startActivity(this)
                requireActivity().finish()
            }
        } else if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_CLIENT.name) {
            val intent = Intent(requireContext(), DashBoardScreenActivity::class.java)
            intent.apply {
                startActivity(this)
                requireActivity().finish()
            }
        }


    }

//    private fun getProfileDetails() {
//        getUserProfileViewModel.getProfileByIDUseCase(
//            PreferenceManager.saveUserID, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
//        )
//        with(getUserProfileViewModel) {
//            getUserData.observe(requireActivity(), Observer {
//                if (it.success) {
//                    var created_at = it.data.created_at!!
//                    var updated_at = it.data.updated_at!!
//                    var profile_pic = it.data.profile_details?.profile_image!!
//                    val formatter = SimpleDateFormat("yyyy-mm-dd")
//                    val formatterHH = SimpleDateFormat("hh:mm:ss aa")
//
//                    val date1: Date = formatter.parse(created_at)
//                    val date2 = formatter.parse(updated_at)
//                    println("created_at :" + date1)
//                    println("updated_at :" + date2)
//                    println("Profile pic : " + profile_pic)
//                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//                    val date11 = sdf.parse(created_at)
//                    val date22 = sdf.parse(updated_at)
//                    var msBetweenDates = Math.abs(date11.time - date22.time)
//
//                    // 👇️ convert ms to hours                  min  sec   ms
//                    var hoursBetweenDates = msBetweenDates / (60 * 60 * 1000)
//
//                    println(hoursBetweenDates)
//
//                    if (hoursBetweenDates < 24) {
//                        println(" " + "date is within 24 hours")
//                    } else {
//                        println(" " + "date is NOT within 24 hours")
//                    }
//                    val dfDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//
//                    if (date1.compareTo(date2) < 0) {
//                        println("date compare pic : +")
//                    } else if (dfDate.parse(updated_at).before(dfDate.parse(created_at))) {
//                        println("date before pic : +")
//                    } else if (dfDate.parse(created_at) == dfDate.parse(updated_at)) {
//                        println("date are equal : +")
//                        StayTunedDialog.newInstance("", requireContext())
//                            .show(parentFragmentManager, StayTunedDialog.TAG)
//
//                    } else {
//                        Log.d("Return", "getMyTime older than getCurrentDateTime ")
//                    }
//
//                } else it.message?.let { it1 ->
//                    onToast(it1, requireActivity())
//                }
//            })
//
//        }
//    }

    var userData = UserLoginRequest()

    private fun handleLoginClick() {
        val userId = binding.editPhone.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (userId.isNotEmpty()) {
            binding.errorPhone.visibility = View.GONE
            if (Character.isDigit(userId[0]) && !userId.isValidMobileNumber()) {
                binding.editPhone.requestFocus()
                binding.errorPhone.text = getString(R.string.enter_mobile_empid)
                binding.errorPhone.visibility = View.VISIBLE
            } else if (!Character.isDigit(userId[0]) && !isValidEmail(userId)) {
                binding.editPhone.requestFocus()
                binding.errorPhone.text = getString(R.string.enter_valid_email)
                binding.errorPhone.visibility = View.VISIBLE
            }
//            if (!isValidPasswordFormat(password)) {
//                binding.editPassword.requestFocus()
//                binding.errorpass.visibility = View.VISIBLE
//                return
//            }
            binding.errorpass.visibility = View.GONE
            userData.username = userId
            userData.password = password
            viewModel.userLogin(userData)
        } else {
            binding.editPhone.requestFocus()
            binding.errorPhone.text = getString(R.string.enter_email_mobile)
            binding.errorPhone.visibility = View.VISIBLE
        }
    }

    private fun navigateToForgotPass() {
        findNavController().navigate(R.id.action_loginFragment_to_fragmentMobileVerify)
    }

}