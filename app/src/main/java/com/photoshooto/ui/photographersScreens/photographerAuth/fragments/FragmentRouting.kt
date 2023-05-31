package com.photoshooto.ui.photographersScreens.photographerAuth.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.photoshooto.R
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentRoutingBinding
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.*
import com.truecaller.android.sdk.*


class FragmentRouting : Fragment() {

    private var _binding: FragmentRoutingBinding? = null
    private val binding get() = _binding!!
    private var userType = ""
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var RC_SIGN_IN = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recordScreenView(
            requireActivity(), "FragmentRouting", FirebaseAnalytics_Event_ScreenName.screenWelcome
        )


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.GOOGLE_AUTH_ID)).requestEmail().build()

        mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }

        val trueScope = TruecallerSdkScope.Builder(requireActivity(), sdkCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .buttonColor(ContextCompat.getColor(requireActivity(), R.color.orange_clr))
            .buttonTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
            .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
            .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
            .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
            .privacyPolicyUrl(Constant.PRIVACY_POLICY_URL)
            .termsOfServiceUrl(Constant.TERMS_N_CONDITION_URL)
            .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP).build()
        TruecallerSDK.init(trueScope)

        binding.scanQrCodeBtn.setOnClickListener {
            //val direction = FragmentRoutingDirections.actionFragmentRoutingToFragmentQrCode()
            findNavController().navigate(R.id.action_fragmentRouting_to_navigation_QrCode)
        }

        binding.loginBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", userType)
            if (userType == "User") {
                findNavController().navigate(R.id.action_fragmentRouting_to_signupFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_fragmentRouting_to_loginFragment, bundle)
            }
        }

        binding.signup.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", userType)
            if (userType == "User") {
                findNavController().navigate(R.id.action_fragmentRouting_to_loginFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_fragmentRouting_to_signupFragment, bundle)
            }
        }

        binding.btnSignup.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", userType)
            findNavController().navigate(R.id.action_fragmentRouting_to_signupFragment, bundle)
        }

        binding.userTypeRadiobtn.setOnCheckedChangeListener { group, checkedId ->
            val rad = group?.findViewById<RadioButton>(checkedId)
            if (null != rad && checkedId > -1) {
                userType = rad.text.toString()

                if (userType == "User") {
                    showUserScreenData()
                } else {
                    showPhotographerScreenData()
                }
            }
        }

        binding.truecaller.setOnClickListener {
            verify(it)
        }

        binding.googleSignIn.setOnClickListener {
            val signInIntent: Intent? = mGoogleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.userRadioButton.isChecked = true
        userType = "User"
        showUserScreenData()

        termsPrivacy()

        return root
    }

    private fun showUserScreenData() {
        binding.secondaryActionText.text = getString(R.string.signup)
        binding.signup.paintFlags = binding.signup.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.signup.text = getString(R.string.underline_login)
        binding.loginBtn.visible()
        binding.layoutSignup.visible()
        binding.scanQrCodeBtn.visible()
        binding.layoutLoginwith.gone()
        binding.btnSignup.gone()
    }

    private fun showPhotographerScreenData() {
        binding.secondaryActionText.text = getString(R.string.login)
        binding.signup.text = getString(R.string.underline_signup)
        binding.btnSignup.visible()
        binding.scanQrCodeBtn.gone()
        binding.layoutSignup.gone()
        binding.layoutLoginwith.gone()
        binding.loginBtn.visible()
        showAlertPopUpForPhotographer()
    }

    private fun showAlertPopUpForPhotographer() {
        val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_photographer_signup_alert)

        val lottieAlertView = dialog.findViewById(R.id.alert_lottie) as LottieAnimationView
        lottieAlertView.setAnimation(R.raw.alert)
        lottieAlertView.repeatCount = LottieDrawable.INFINITE
        lottieAlertView.playAnimation()

        val okayBtn = dialog.findViewById(R.id.okayBtn) as TextView
        okayBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun termsPrivacy() {
        binding.termsNCondition.setOnClickListener {
            val bundle = getTermsNConditionBundle()
            findNavController().navigate(
                R.id.action_fragmentRouting_to_navigation_Terms,
                bundle
            )
        }

        binding.privacyPolicyTv.setOnClickListener {
            val bundle = getPrivacyPolicyBundle()
            findNavController().navigate(
                R.id.action_fragmentRouting_to_navigation_Terms,
                bundle
            )
        }
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(
            title,
            args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    selectedValue: String
                ) {
                    println("praveen selectedValue " + selectedValue)
                }
            })
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }

    private val sdkCallback: ITrueCallback = object : ITrueCallback {
        override fun onSuccessProfileShared(@NonNull trueProfile: TrueProfile) {

            // This method is invoked when either the truecaller app is installed on the device and the user gives his
            // consent to share his truecaller profile OR when the user has already been verified before on the same
            // device using the same number and hence does not need OTP to verify himself again.
            Log.d("TAG", "Verified without OTP! (Truecaller User): " + trueProfile.firstName)
        }

        override fun onFailureProfileShared(@NonNull trueError: TrueError) {
            // This method is invoked when some error occurs or if an invalid request for verification is made
            Log.d("TAG", "onFailureProfileShared: " + trueError.errorType)
        }

        override fun onVerificationRequired(p0: TrueError?) {
            //TrueSDK.getInstance().requestVerification("IN", PHONE_NUMBER_STRING, apiCallback)

        }
    }

    fun verify(view: View) {
        if (TruecallerSDK.getInstance().isUsable) {
            TruecallerSDK.getInstance().getUserProfile(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TruecallerSDK.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            TruecallerSDK.getInstance()
                .onActivityResultObtained(requireActivity(), requestCode, resultCode, data)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        Log.d("TAG - Google Account", account?.givenName + " -- " + account?.email)
    }

}