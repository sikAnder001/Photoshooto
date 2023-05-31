package com.photoshooto.ui.photographersScreens.photographerAuth.fragments

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
import com.photoshooto.ui.login.FragmentMobileVerifyDirections
import com.photoshooto.util.*
import com.sm.app.util.GenericKeyEvent
import com.sm.app.util.GenericTextWatcher
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentMobileVerify : Fragment() {

    private var _binding: FragmentResetPwdMobileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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
        _binding = FragmentResetPwdMobileBinding.inflate(inflater, container, false)
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
            requireActivity().onBackPressed()
        }


        binding.txtBackVerify.setOnClickListener {
            requireActivity().onBackPressed()
        }






        binding.btnNext.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentMobileVerify",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Verification
            )

            if (binding.btnNext.text.toString().equals(getString(R.string.next))) {
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

        /*  binding.textview2.setOnClickListener {
              recordScreenView(
                  requireActivity(),
                  "FragmentMobileVerify",
                  FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_ResendOTP
              )
              binding.otpError.visibility = View.GONE
              if (binding.resendOtpIn.text.toString() == resources.getString(R.string.resend_otp))
                  mobileSendOtp()
          }*/


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
                            //binding.otpStatusMsg.setText(response.message)
                            binding.otpLayout.visibility = View.VISIBLE

                            SharedPrefsHelper.write(
                                SharedPrefConstant.VERIFICATION_KEY,
                                response.data?.data?.verification_key.toString()
                            )

                            SharedPrefsHelper.write(
                                SharedPrefConstant.AUTH_TOKEN,
                                response.data?.data?.token.toString()
                            )

                            SharedPrefsHelper.write(
                                SharedPrefConstant.SERVER_OTP_KEY,
                                response.data?.data?.otp.toString()
                            )

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
                    SharedPrefsHelper.write(SharedPrefConstant.AUTH_TOKEN, it.data?.token!!)
                    cancelTimer()
                    findNavController().navigate(R.id.action_fragmentMobileVerify_to_fragmentResetPwd)
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
                } /*else {
                    verifyOtp()
                }*/
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

    override fun onPause() {
        super.onPause()
        cancelTimer()
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
            type = getString(R.string.reset_pwd)
        )
        binding.otpEditText1.requestFocus()
        sendOtpViewModel.getMobileCheck(sendOtpModel)
    }

    private fun verifyOtp() {
        hideKeyboard()
        c1 = binding.otpEditText1.text.toString().trim()
        c2 = binding.otpEditText2.text.toString().trim()
        c3 = binding.otpEditText3.text.toString().trim()
        c4 = binding.otpEditText4.text.toString().trim()
        c5 = binding.otpEditText5.text.toString().trim()
        c6 = binding.otpEditText6.text.toString().trim()

        val smsCode = "$c1$c2$c3$c4$c5$c6"
        if (serverOtpKey.equals(smsCode)) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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