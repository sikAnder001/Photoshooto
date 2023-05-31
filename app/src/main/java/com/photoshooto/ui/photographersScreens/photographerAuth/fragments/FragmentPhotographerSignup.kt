package com.photoshooto.ui.photographersScreens.photographerAuth.fragments

import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentPhotoSignupBinding
import com.photoshooto.domain.model.ProfileDetails
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.login.InfoPwdDialog
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


class FragmentPhotographerSignup : Fragment() {

    private lateinit var binding: FragmentPhotoSignupBinding

    private val viewModel: UserLoginViewModel by sharedViewModel<UserLoginViewModel>()

//    private val verifyOtpViewModel: VerifyOtpViewModel by viewModel()

    /*var requestRegister = RegisterVerifyOtpModel()*/
    var profileDetails = ProfileDetails()

//    private var serverVerification_key = ""
//    private var serverOtpKey = ""
//    var cTimerEmail: CountDownTimer? = null
//    private var c1 = ""
//    private var c2 = ""
//    private var c3 = ""
//    private var c4 = ""
//    private var c5 = ""
//    private var c6 = ""


    var name = ""
    var email = ""
    var contact = ""
    var newsLetterCheckbox = false
    var emailVerified = false
    var mobileVerified = false
    var editPassword = ""
    var editRePassword = ""
    var editInviteCode = ""
    var otpfrom = ""
    var passConfirmShow = false
    var passConfirmShow1 = false
    var pwdSet = false
    var smsCode = ""
    val gender = listOf("Male", "Female", "Transgender")
    var userType: String? = null

    var callCount = 0
    var navCount = 0

//    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
//    private val REQ_USER_CONSENT = 200
//    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recordScreenView(

            requireActivity(),
            "FragmentPhotographerSignup",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup
        )
        userType = requireArguments().getString("userType").toString()

        termsPrivacy()
        binding.txtBackSignup.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                findNavController().navigateUp()
            }
        }
        binding.btnNextSignup.setOnClickListener {
            hideKeyboard()
            validateBasicData()
            callCount++
        }
        binding.newsLetterCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            (!isChecked).also { this.newsLetterCheckbox = it }
        }


        binding.editPassword.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            val isSoftImeEvent =
                (event == null && (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEND))
            if (isSoftImeEvent) {
                val pwd: String = binding.editPassword.text.toString().trim()
                if (TextUtils.isEmpty(pwd)) {
                    binding.editPassword.background =
                        requireView().resources.getDrawable(R.drawable.ic_edittext_error_bg)
                    binding.errorpassword.visibility = View.VISIBLE
                } else {
                    if (pwd.length < 8) {
                        binding.editPassword.background =
                            requireView().resources.getDrawable(
                                R.drawable.ic_edittext_error_bg,
                                null
                            )
                        binding.editPassword.requestFocus()
                        binding.errorpassword.visibility = View.VISIBLE
                        binding.errorpassword.text =
                            requireActivity().resources.getString(R.string.password_8_chars_text)
                    } else {
                        if (pwd.isEmpty() || !isValidPassword(pwd)) {
                            binding.editPassword.background =
                                requireView().resources.getDrawable(
                                    R.drawable.ic_edittext_error_bg,
                                    null
                                )
                            binding.editPassword.requestFocus()
                            binding.errorpassword.visibility = View.VISIBLE
                            binding.errorpassword.text =
                                requireActivity().resources.getString(R.string.pwd_validation)
                        } else {
                            binding.editPassword.background =
                                requireView().resources.getDrawable(R.drawable.ic_edittext_bg, null)
                            binding.errorpassword.visibility = View.GONE
                            binding.editRePassword.requestFocus()
                        }
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.editRePassword.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            val isSoftImeEvent =
                (event == null && (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEND))
            if (isSoftImeEvent) {
                val pwd: String = binding.editRePassword.text.toString().trim()
                if (TextUtils.isEmpty(pwd)) {
                    binding.editRePassword.background =
                        requireView().resources.getDrawable(
                            R.drawable.ic_edittext_error_bg,
                            null
                        ) //binding.editLName.requestFocus()
                    binding.errorRePass.visibility = View.VISIBLE
                } else {
                    if (pwd.length < 8) {
                        binding.editRePassword.background =
                            requireView().resources.getDrawable(
                                R.drawable.ic_edittext_error_bg,
                                null
                            )
                        binding.editRePassword.requestFocus()
                        binding.errorRePass.visibility = View.VISIBLE
                        binding.errorRePass.text =
                            requireActivity().resources.getString(R.string.password_8_chars_text)
                    } else {
                        if (pwd.isEmpty() || !isValidPassword(pwd)) {
                            binding.editRePassword.background =
                                requireView().resources.getDrawable(
                                    R.drawable.ic_edittext_error_bg,
                                    null
                                )
                            binding.editRePassword.requestFocus()
                            binding.errorRePass.visibility = View.VISIBLE
                            binding.errorRePass.text =
                                requireActivity().resources.getString(R.string.pwd_validation)
                        } else {
                            binding.editRePassword.background =
                                requireView().resources.getDrawable(R.drawable.ic_edittext_bg, null)
                            binding.errorRePass.visibility = View.GONE

                            //validateBasicData()
                            hideKeyboard()
                        }
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.editPassword.setOnTouchListener(object :
            DrawableClickListener.RightDrawableClickListener(binding.editPassword) {
            override fun onDrawableClick(): Boolean {
                if (!passConfirmShow) {
                    passConfirmShow = true
                    binding.editPassword.transformationMethod = null
                    binding.editPassword.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_visibility_off_24
                        ),
                        null
                    )
                } else {
                    binding.editPassword.transformationMethod = PasswordTransformationMethod()
                    passConfirmShow = false
                    binding.editPassword.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_remove_red_eye_24
                        ),
                        null
                    )
                }
                return true
            }
        })

        binding.editRePassword.setOnTouchListener(object :
            DrawableClickListener.RightDrawableClickListener(binding.editRePassword) {
            override fun onDrawableClick(): Boolean {
                if (!passConfirmShow1) {
                    passConfirmShow1 = true
                    binding.editRePassword.transformationMethod = null
                    binding.editRePassword.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_visibility_off_24
                        ),
                        null
                    )
                } else {
                    binding.editRePassword.transformationMethod = PasswordTransformationMethod()
                    passConfirmShow1 = false
                    binding.editRePassword.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_baseline_remove_red_eye_24
                        ),
                        null
                    )
                }
                return true
            }
        })

        binding.infoImg.setOnClickListener {
            InfoPwdDialog.newInstance("", requireContext())
                .show(parentFragmentManager, InfoPwdDialog.TAG)
        }

        binding.editGender.isFocusable = false
        binding.editGender.setOnClickListener {
            // addDocuments("Gender", gender)
        }

        val fragmentActivity = activity
        if (fragmentActivity != null) {
            with(viewModel) {
                sendMobileOtp.observe(fragmentActivity) { response ->
                    if (response != null) {
                        when (response.status) {
                            Status.SUCCESS -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnNextSignup.visibility = View.VISIBLE

                                response.data?.message?.let { it1 ->
                                    onToast(it1, fragmentActivity)
                                }

                                if (response.data?.data != null) {

                                    SharedPrefsHelper.write(
                                        SharedPrefConstant.VERIFICATION_KEY,
                                        response.data.data.verification_key!!
                                    )
                                    SharedPrefsHelper.write(
                                        SharedPrefConstant.SERVER_OTP_KEY, response.data.data.otp!!
                                    )

                                    val bundle = Bundle()
                                    bundle.putString("mobile", contact)
                                    bundle.putString("otpKey", otpfrom)
                                    if (callCount != navCount) {
                                        navCount = callCount
                                        lifecycleScope.launchWhenResumed {
                                            findNavController().safeNavigate(
                                                R.id.action_signupFragment_to_PhotographerMobileVerify,
                                                bundle
                                            )
                                        }
                                    }
                                }

                            }
                            Status.LOADING -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.btnNextSignup.visibility = View.GONE
                            }
                            Status.ERROR -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnNextSignup.visibility = View.VISIBLE
                                response.message?.let { it1 -> context?.showToast(it1) }
                            }
                        }
                    }
                }
            }
            /*registerPhotographer.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            binding.progressBar.visibility = View.GONE

                            if (response.data?.data != null) {
                                val responseData = response.data.data
//                            SharedPrefsHelper.write(SharedPrefConstant.IS_LOGIN, true)
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
                                SharedPrefsHelper.write(
                                    SharedPrefConstant.USER_PROFESSION,
                                    responseData.user.profile_details.profession!!
                                )
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


                                 val bundle = Bundle()
                                 bundle.putString("mobile", contact)
                                 bundle.putString("otpKey", otpfrom)
                                 findNavController().navigate(
                                     R.id.action_signupFragment_to_fragmentPhotographerMobileVerify,
                                     bundle
                                 )

                                val sendOtpModel = SendOtpRequestModel(
                                    username = contact,
                                    type = getString(R.string.register)
                                )
                                otpfrom = "mobile"
                                viewModel.getMobileCheck(sendOtpModel)
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
                            response.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                    }
                }
            }*/

        }
    }

    private fun validateBasicData() {

        if (binding.progressBar.visibility != View.VISIBLE) {

            name = binding.editName.text.toString()
            email = binding.editEmail.text.toString()
            contact = binding.editPhone.text.toString()

            if (name.isEmpty()) {
                binding.editName.requestFocus()
                binding.errorName.visibility = View.VISIBLE
                return
            }
            binding.errorName.visibility = View.GONE



            if (email.isEmpty()) {
                binding.editEmail.requestFocus()
                binding.errorEmail.visibility = View.VISIBLE
                return

            }
            binding.errorEmail.visibility = View.GONE

            if (contact.isEmpty()) {
                binding.editPhone.requestFocus()
                binding.errorPhone.visibility = View.VISIBLE
                return
            }
            binding.errorPhone.visibility = View.GONE

            editPassword = binding.editPassword.text.toString()
            editRePassword = binding.editRePassword.text.toString()

            if (editPassword.isEmpty()) {
                binding.editPassword.requestFocus()
                binding.errorpassword.visibility = View.VISIBLE
                return
            }
            binding.errorpassword.visibility = View.GONE

            if (editRePassword.isEmpty()) {
                binding.editRePassword.requestFocus()
                binding.errorRePass.visibility = View.VISIBLE
                return
            }
            binding.errorRePass.visibility = View.GONE

            if (editPassword != editRePassword) {
                binding.editRePassword.requestFocus()
                binding.errorRePass.text = getString(R.string.pwd_does_nt_match)
                binding.errorRePass.visibility = View.VISIBLE
                return
            }
            binding.errorRePass.visibility = View.GONE

            editInviteCode = binding.editInvitecode.text.toString()
            verifyPassword()
        }
    }

    private fun verifyPassword() {
        hideKeyboard()
        viewModel.requestRegister.type = userType?.lowercase(Locale.getDefault())
        viewModel.requestRegister.password = getStringFromEditText(binding.editPassword)
        viewModel.requestRegister.invite_code = getStringFromEditText(binding.editInvitecode)
        profileDetails.mobile = contact
        profileDetails.name = name
        profileDetails.email = email
        profileDetails.first_name = name
        viewModel.requestRegister.profile_details = profileDetails

        val sendOtpModel = SendOtpRequestModel(
            username = contact,
            type = getString(R.string.register)
        )
        otpfrom = "mobile"
        viewModel.getMobileCheck(sendOtpModel)
        //viewModel.registerPhotographerCall(requestRegister)
    }

    override fun onResume() {
        super.onResume()
        recordScreenView(
            requireActivity(),
            "FragmentPhotographerSignup",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Signup
        )
    }

    private fun termsPrivacy() {
        binding.termsNCondition.setOnClickListener {
            val bundle = getTermsNConditionBundle()
            lifecycleScope.launchWhenResumed {
                findNavController().safeNavigate(
                    R.id.action_signupFragment_to_navigation_Terms,
                    bundle
                )
            }
        }

        binding.privacyPolicyTv.setOnClickListener {
            val bundle = getPrivacyPolicyBundle()
            lifecycleScope.launchWhenResumed {
                findNavController().safeNavigate(
                    R.id.action_signupFragment_to_navigation_Terms,
                    bundle
                )
            }
        }
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(title, args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    imagePath: String
                ) {
                    binding.editGender.setText(imagePath)
                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }
}


