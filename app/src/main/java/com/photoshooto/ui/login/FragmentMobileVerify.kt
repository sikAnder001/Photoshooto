package com.photoshooto.ui.login

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.photoshooto.R
import com.photoshooto.databinding.FragmentResetPwdMobileBinding
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOTPRequestModel
import com.photoshooto.domain.usecase.verify_otp.VerifyOtpViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.*
import com.sm.app.util.GenericKeyEvent
import com.sm.app.util.GenericTextWatcher
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentMobileVerify : Fragment() {

    private lateinit var binding: FragmentResetPwdMobileBinding

    private val sendOtpViewModel: UserLoginViewModel by viewModel()
    private val resetPwdViewModel: VerifyOtpViewModel by viewModel()


    var mobile = ""
    var cTimer: CountDownTimer? = null

    private var serverVerification_key = ""
    private var serverOtpKey = ""
    private var c1 = ""
    private var c2 = ""
    private var c3 = ""
    private var c4 = ""
    private var c5 = ""
    private var c6 = ""

    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private val REQ_USER_CONSENT = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPwdMobileBinding.inflate(inflater, container, false)
        recordScreenView(
            requireActivity(),
            "FragmentMobileVerify",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Verification
        )

        setupListeners()
        startSmsUserConsent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtBackVerify.setOnClickListener {
            findNavController().popBackStack()
        }



        binding.btnNext.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentMobileVerify",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Verification
            )

            if (binding.btnNext.text.toString() == getString(R.string.next)) {
                mobileSendOtp()
            } else {
                verifyOtp()
            }
        }

        binding.loginHere.setOnClickListener {
            val direction =
                FragmentMobileVerifyDirections.actionFragmentMobileVerifyToLoginFragment()
            findNavController().navigate(direction)
        }

        binding.support.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentMobileVerify",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_LoginSupport_Button
            )

            val direction =
                FragmentMobileVerifyDirections.actionFragmentMobileVerifyToFragmentTermsPrivacy()
            findNavController().navigate(direction)
        }
        binding.resendOtpIn.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentMobileVerify",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_ResendOTP
            )
            binding.otpError.visibility = View.GONE
            if (binding.resendOtpIn.text.toString() == resources.getString(R.string.resend_otp))
                mobileSendOtp()
        }

        with(sendOtpViewModel) {
            sendMobileOtp.observe(requireActivity(), Observer { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            Log.i(
                                "Signup ViewModel OTP ",
                                "result: " + response.data?.data?.verification_key.toString()
                            )
                            binding.otpStatusMsg.visibility = View.VISIBLE
                            binding.otpStatusMsg.text = response.message
                            binding.otpLayout.visibility = View.VISIBLE

                            serverVerification_key =
                                response.data?.data?.verification_key.toString()
                            serverOtpKey = response.data?.data?.otp.toString()

                            binding.btnNext.text = getString(R.string.verify)
                            startTimer()
                        }
                        Status.ERROR -> {
                            response.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }

            })
            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

        with(resetPwdViewModel) {
            otpVerifiedData.observe(requireActivity()) {
                if (it.success) {
                    with(PreferenceManager) {
                        authTokenSave = it.data?.token
                    }
                    println(
                        "praveen authTokenSave after token vierifed  " + SharedPrefsHelper.read(
                            SharedPrefConstant.AUTH_TOKEN
                        )
                    )
                    cancelTimer()
                    /*val direction =
                        FragmentMobileVerifyDirections.actionFragmentMobileVerifyToFragmentResetPwd()
                    findNavController().navigate(direction)*/
                } else {
                    cancelTimer()
                    it.message?.let { it1 ->
                        onToast(it1, requireActivity())
                    }
                }
            }
            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

        binding.editPhone.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.btnNext.text.toString().equals(getString(R.string.next))) {
                    mobileSendOtp()
                } else {
                    verifyOtp()
                }
                true
            } else false
        })

        binding.otpEditText6.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyOtp()
                true
            } else false
        })

    }

    fun startTimer() {
        cTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(l: Long) {
                binding.resendOtpIn.text =
                    resources.getString(R.string.resend_otp) + " " + "00 : " + (l / 1000).toString()
            }

            override fun onFinish() {
                binding.otpStatusMsg.visibility = View.GONE
                binding.resendOtpIn.text = resources.getString(R.string.resend_otp)
            }
        }
        cTimer?.start()
    }

    fun cancelTimer() {
        if (cTimer != null) cTimer?.cancel()
    }


    private fun mobileSendOtp() {
        hideKeyboard()
        mobile = binding.editPhone.text.toString()
        if (mobile.isEmpty()) {
            binding.editPhone.requestFocus()
            binding.errorPhone.visibility = View.VISIBLE
            return
        }
        binding.errorPhone.visibility = View.GONE

        val sendOtpModel = SendOtpRequestModel(
            username = mobile,
            type = getString(R.string.register)
        )
        binding.otpEditText1.requestFocus()
        sendOtpViewModel.getMobileCheck(sendOtpModel)
    }

    private fun verifyOtp() {
        hideKeyboard()
        c1 = getStringFromEditText(binding.otpEditText1)
        c2 = getStringFromEditText(binding.otpEditText2)
        c3 = getStringFromEditText(binding.otpEditText3)
        c4 = getStringFromEditText(binding.otpEditText4)
        c5 = getStringFromEditText(binding.otpEditText5)
        c6 = getStringFromEditText(binding.otpEditText6)

        val smsCode = "$c1$c2$c3$c4$c5$c6"
        if (serverOtpKey == smsCode) {
            val otpVerifyModel = VerifyOTPRequestModel(
                verification_key = serverVerification_key,
                otp = smsCode
            )
            resetPwdViewModel.verifyOtp(otpVerifyModel)
        } else {
            binding.otpError.text = resources.getString(R.string.pin_valid)
            binding.otpError.visibility = View.VISIBLE
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
                        intent,
                        REQ_USER_CONSENT
                    )
                }

                override
                fun onFailure() {
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
            binding.otpEditText1.setText(number?.get(0).toString())
            binding.otpEditText2.setText(number?.get(1).toString())
            binding.otpEditText3.setText(number?.get(2).toString())
            binding.otpEditText4.setText(number?.get(3).toString())
            binding.otpEditText5.setText(number?.get(4).toString())
            binding.otpEditText6.setText(number?.get(5).toString())
            verifyOtp()
        }
    }

}