package com.photoshooto.ui.photographersScreens.photographerAuth.fragments

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.photoshooto.R
import com.photoshooto.databinding.FragmentPhotographerMobileVerifyBinding
import com.photoshooto.domain.model.RegisterVerifyOtpModel
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOTPRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOtpViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.ui.job.utility.checkIfDateGone
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.*
import com.pixplicity.easyprefs.library.Prefs
import com.sm.app.util.GenericKeyEvent
import com.sm.app.util.GenericTextWatcher
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

import java.util.*
import java.util.regex.Pattern


class FragmentPhotographerMobileVerify : Fragment() {


    private lateinit var binding: FragmentPhotographerMobileVerifyBinding

    private val sendOtpViewModel: UserLoginViewModel by sharedViewModel()
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModel()
    var requestRegister = RegisterVerifyOtpModel()

    var mobile = ""

    private var serverVerification_key = ""
    private var serverOtpKey = ""
    private var c1 = ""
    private var c2 = ""
    private var c3 = ""
    private var c4 = ""
    private var c5 = ""
    private var c6 = ""
    var otpfrom = ""
    var name = ""
    var email = ""
    var password = ""
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private val REQ_USER_CONSENT = 200
    // var requestRegister = Data()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerMobileVerifyBinding.inflate(inflater, container, false)
        if (activity != null) {
            recordScreenView(
                requireActivity(),
                "FragmentPhotographerMobileVerify",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Verification
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            mobile = requireArguments().getString("mobile").toString()
            otpfrom = requireArguments().getString("otpKey").toString()
        }
        val toolbar = binding.mobileVerifyToolbarWithBack
        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.changeNumber.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.edit.setOnClickListener {
            requireActivity().onBackPressed()
        }


        val titleTxt = toolbar.tvTitle
        titleTxt.text = "Verification"

        binding.textView1.text = "We have sent OTP on $mobile\nIf you apply auto to the fields"

        setupListeners()

        binding.resendOtpIn.setOnClickListener {
            if (activity != null) {
                recordScreenView(
                    requireActivity(),
                    "FragmentPhotographerMobileVerify",
                    FirebaseAnalytics_Event_ScreenName.screenPhotographer_SignupResendOTP
                )
            }
            mobileSendOtp()
        }

        binding.getSupport.setOnClickListener {
            if (activity != null) {
                recordScreenView(
                    requireActivity(),
                    "FragmentPhotographerMobileVerify",
                    FirebaseAnalytics_Event_ScreenName.screenPhotographer_SignupContactus
                )
            }
            if (null != context) {
                GetSupportDialog.newInstance("", requireContext())
                    .show(parentFragmentManager, GetSupportDialog.TAG)
            }
        }

        startSmsUserConsent()

        with(sendOtpViewModel) {
            reSendMobileOtp.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            binding.progressBar.visibility = View.GONE
                            response.data?.message?.let { it1 -> onToast(it1, requireActivity()) }

                            if (response.data?.data != null) {
                                //val responseData = response.data?.data!!
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.VERIFICATION_KEY,
                                    response.data.data.verification_key!!
                                )
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.SERVER_OTP_KEY, response.data.data.otp!!
                                )
                            }


//                            SharedPrefsHelper.write(SharedPrefConstant.IS_LOGIN, true)

                            /*                         SharedPrefsHelper.write(
                                                         SharedPrefConstant.AUTH_TOKEN, responseData.token!!
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
                                                     SharedPrefsHelper.write(
                                                         SharedPrefConstant.USER_PROFESSION,
                                                         responseData.user.profile_details.profession!!
                                                     )
                                                     SharedPrefsHelper.write(
                                                         SharedPrefConstant.PROFILE_URL,
                                                         responseData.user.profile_details.profile_image!!
                                                     )
                                                     SharedPrefsHelper.write(
                                                         SharedPrefConstant.USER_ID, responseData.user.id!!
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


                                                     if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.PHOTOGRAPHER.name.toLowerCase() && SharedPrefsHelper.read(
                                                             SharedPrefConstant.USER_ROLE
                                                         ) == Constant.USER_ROLE.CLIENT.name.toLowerCase()
                                                     ) {
                                                         SharedPrefsHelper.write(
                                                             SharedPrefConstant.USER_CASE_TYPE,
                                                             Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name
                                                         )
                                                     } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.toLowerCase() && SharedPrefsHelper.read(
                                                             SharedPrefConstant.USER_ROLE
                                                         ) == Constant.USER_ROLE.CLIENT.name.toLowerCase()
                                                     ) {
                                                         SharedPrefsHelper.write(
                                                             SharedPrefConstant.USER_CASE_TYPE,
                                                             Constant.USER_CASE_TYPE.USER_CLIENT.name
                                                         )
                                                     } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.toLowerCase() && SharedPrefsHelper.read(
                                                             SharedPrefConstant.USER_ROLE
                                                         ) == Constant.USER_ROLE.ADMIN.name.toLowerCase()
                                                     ) {
                                                         SharedPrefsHelper.write(
                                                             SharedPrefConstant.USER_CASE_TYPE,
                                                             Constant.USER_CASE_TYPE.USER_ADMIN.name
                                                         )
                                                     } else if (SharedPrefsHelper.read(SharedPrefConstant.LOGIN_TYPE) == Constant.USER_TYPE.USER.name.toLowerCase() && SharedPrefsHelper.read(
                                                             SharedPrefConstant.USER_ROLE
                                                         ) == Constant.USER_ROLE.SUPER_ADMIN.name.toLowerCase()
                                                     ) {
                                                         SharedPrefsHelper.write(
                                                             SharedPrefConstant.USER_CASE_TYPE,
                                                             Constant.USER_CASE_TYPE.USER_SUPER_ADMIN.name
                                                         )
                                                     }

                                                     findNavController().navigate(R.id.action_fragmentPhotographerMobileVerify_to_fragmentThanksRegister)*/

                        }
                        Status.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        Status.ERROR -> {
                            binding.progressBar.visibility = View.GONE
                            response.message?.let { it1 -> context?.showToast(it1) }
                        }
                    }
                }
            }
            registerPhotographer.observe(requireActivity()) { response ->
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
                                    responseData.token!!
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

                                navigateToHomeScreen()

                            }

                            response.data?.message?.let { it1 ->
                                onToast(it1, requireActivity())
                            }

                        }
                        Status.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            binding.progressBar.visibility = View.GONE
                            response.message?.let { it1 -> context?.showToast(it1) }
                        }
                    }
                }
            }
            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

        binding.btnNext.setOnClickListener {
            verifyOtp()

        }
        verifiedUser()
    }

    private fun mobileSendOtp() {
        hideKeyboard()
        binding.otpError.visibility = View.GONE
        binding.otpEditText1.text.clear()
        binding.otpEditText2.text.clear()
        binding.otpEditText3.text.clear()
        binding.otpEditText4.text.clear()
        binding.otpEditText5.text.clear()
        binding.otpEditText6.text.clear()
        val sendOtpModel = SendOtpRequestModel(
            username = mobile, type = "reset_password"
        )

        sendOtpViewModel.resendOTPMobileCheck(sendOtpModel)
    }

    private fun verifyOtp() {
        binding.otpEditText1.requestFocus()
        c1 = binding.otpEditText1.text.toString().trim()
        c2 = binding.otpEditText2.text.toString().trim()
        c3 = binding.otpEditText3.text.toString().trim()
        c4 = binding.otpEditText4.text.toString().trim()
        c5 = binding.otpEditText5.text.toString().trim()
        c6 = binding.otpEditText6.text.toString().trim()

        requestRegister.verification_key =
            SharedPrefsHelper.read(SharedPrefConstant.VERIFICATION_KEY).toString()
        println("serverVerify  " + requestRegister.verification_key)

        serverOtpKey = SharedPrefsHelper.read(SharedPrefConstant.SERVER_OTP_KEY)
            .toString()//PreferenceManager.saveOtpKey!!
        println("serverVerifyOTP  $serverOtpKey")

        val smsCode = "$c1$c2$c3$c4$c5$c6"
        if (serverOtpKey.equals(smsCode)) {
            val otpVerifyModel = VerifyOTPRequestModel(
                verification_key = requestRegister.verification_key, otp = smsCode
            )
            verifyOtpViewModel.verifyOtp(otpVerifyModel)
        } else {
            binding.otpError.text = resources.getString(R.string.pin_valid)
            binding.otpError.visibility = View.VISIBLE
        }


    }

    private fun verifiedUser() {
        with(verifyOtpViewModel) {
            otpVerifiedData.observe(requireActivity()) {
                if (it.success) {
                    //it.message?.let { it1 -> onToast(it1, requireActivity()) }

                    binding.btnNext.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green_color
                        )
                    )

                    binding.imgVerified.visibility = View.VISIBLE

                    binding.tvNextOrVerified.text = getString(R.string.verified)

                    sendOtpViewModel.registerPhotographerCall()


                } else it.message?.let { it1 -> onToast(it1, requireActivity()) }
            }
            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.progressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
    }

    private fun navigateToHomeScreen() {
        val intent =
            if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.USER_CLIENT.name) {
                Intent(activity, DashBoardScreenActivity::class.java)
            } else {
                Intent(activity, PhotographerDashboardActivity::class.java)
            }
        intent.apply {
            startActivity(this)
            requireActivity().finish()
        }
    }

    private fun setupListeners() {
        binding.otpEditText1.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText1, binding.otpEditText2)
        )
        binding.otpEditText2.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText2, binding.otpEditText3)
        )
        binding.otpEditText3.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText3, binding.otpEditText4)
        )
        binding.otpEditText4.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText4, binding.otpEditText5)
        )
        binding.otpEditText5.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText5, binding.otpEditText6)
        )
        binding.otpEditText6.addTextChangedListener(
            GenericTextWatcher(binding.otpEditText6, null)
        )
        binding.otpEditText1.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText1, null)
        )
        binding.otpEditText2.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText2, binding.otpEditText1)
        )
        binding.otpEditText3.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText3, binding.otpEditText2)
        )
        binding.otpEditText4.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText4, binding.otpEditText3)
        )
        binding.otpEditText5.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText5, binding.otpEditText4)
        )
        binding.otpEditText6.setOnKeyListener(
            GenericKeyEvent(binding.otpEditText6, binding.otpEditText5)
        )
    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null).addOnSuccessListener { }.addOnFailureListener { }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(
                        intent, REQ_USER_CONSENT
                    )
                }

                override fun onFailure() {
                }
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                if (message != null) {
                    getOtpFromMessage(message)
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        val pattern = Pattern.compile("(|^)\\d{6}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            val number = matcher.group(0)
            binding.otpEditText1.setText(Character.toString(number[0]))
            binding.otpEditText2.setText(Character.toString(number[1]))
            binding.otpEditText3.setText(Character.toString(number[2]))
            binding.otpEditText4.setText(Character.toString(number[3]))
            binding.otpEditText5.setText(Character.toString(number[4]))
            binding.otpEditText6.setText(Character.toString(number[5]))
            verifyOtp()
        }
    }

}