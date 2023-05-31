package com.photoshooto.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.bottomsheet.SetOfValuesBottomDialogFragment
import com.photoshooto.databinding.FragmentSignupBinding
import com.photoshooto.domain.model.ProfileDetails
import com.photoshooto.domain.model.RegisterVerifyOtpModel
import com.photoshooto.domain.usecase.login.UserLoginViewModel
import com.photoshooto.domain.usecase.verify_otp.SendOtpRequestModel
import com.photoshooto.util.*
import com.sm.app.util.GenericKeyEvent
import com.sm.app.util.GenericTextWatcher
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentSignup : Fragment() {

    private var _binding: FragmentSignupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: UserLoginViewModel by viewModel()

    private var c1 = ""
    private var c2 = ""
    private var c3 = ""
    private var c4 = ""
    private var c5 = ""
    private var c6 = ""
    private var securePin = ""

    var name = ""
    var relation = ""
    var mobile = ""
    var verifiedKeyLocal = ""
    var userType = ""

    val relationsList = listOf(
        "Family", "Blood Relatives",
        "Close Relatives", "Family Relatives",
        "Office/Business Friends", "Others"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //userType = requireArguments().getString("userType").toString()

        loginClick_underline()
        setupListeners()

//        val dataAdapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(requireActivity(), R.layout.itemview_spinner, relationsList)
//        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)

        binding.editSpinnerBtn.setOnClickListener {
            addDocuments("Relations", relationsList)
        }

//        binding.editSpinner.setAdapter(dataAdapter)
//        binding.editSpinner.setOnItemSelectedListener(this)

        binding.btnSignup1.setOnClickListener {
            name = binding.editName.text.toString()
            hideKeyboard()
            handleScreen1()
        }

        binding.btnSignup2.setOnClickListener {
            handleScreen2()
        }

        binding.btnSignup3.setOnClickListener {
            sendOtpToMobile()
        }

        binding.editName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                name = binding.editName.text.toString()
                hideKeyboard()
                handleScreen1()
                true
            } else false
        }

        binding.editPhone.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendOtpToMobile()
                true
            } else false
        }

        binding.btnSignup4.setOnClickListener {
            hideKeyboard()
            verifyClickEvent()
        }

        binding.otpEditText6.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                verifyClickEvent()
                true
            } else false
        }

        with(viewModel) {


            sendMobileOtp.observe(requireActivity(), Observer { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            response.data?.message?.let { it1 -> onToast(it1, requireActivity()) }
                            verifiedKeyLocal = response.data?.data?.verification_key.toString()
                            /* with(PreferenceManager) {
                                 clearAllPreferences()
                             }
                             */
                            SharedPrefsHelper.write(SharedPrefConstant.MOBILE_NUMBER, mobile)
                            SharedPrefsHelper.write(
                                SharedPrefConstant.VERIFICATION_KEY,
                                response.data?.data?.verification_key!!
                            )
                            SharedPrefsHelper.write(
                                SharedPrefConstant.SERVER_OTP_KEY,
                                response.data.data.otp!!
                            )

                            /*     with(PreferenceManager) {
                                     saveMobile = mobile
                                     saveVerification_key = response.data?.data?.verification_key
                                     saveOtpKey = response.data?.data?.otp
                                 }*/

                            binding.verifyPhoneTxt.text =
                                String.format(resources.getString(R.string.enter_6_digit), mobile)
                            binding.signup3.visibility = View.GONE
                            binding.signup4.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            response.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }

            })

            showProgressbar.observe(requireActivity()) { isVisible ->
                binding.postsProgressBar.visibility =
                    if (isVisible) View.VISIBLE else View.GONE
            }
        }
        return root
    }

    private fun addDocuments(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SetOfValuesBottomDialogFragment.newInstance(title, args,
            object : SetOfValuesBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    selectedValue: String
                ) {
                    relation = selectedValue
                    binding.editSpinnerBtn.setText(selectedValue)
                }
            }
        )
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }


    private fun sendOtpToMobile() {
        hideKeyboard()
        mobile = binding.editPhone.text.toString()
        handleScreen3()
    }

    private fun handleScreen1() {
        if (name.isEmpty()) {
            binding.editName.requestFocus()
            binding.errorName.visibility = View.VISIBLE
            return
        }
        binding.errorName.visibility = View.GONE
        binding.signup1.visibility = View.GONE
        binding.signup2.visibility = View.VISIBLE
    }

    private fun handleScreen2() {
        if (relation.isEmpty()) {
            binding.errorRelation.visibility = View.VISIBLE
            return
        }
        binding.errorRelation.visibility = View.GONE
        binding.signup2.visibility = View.GONE
        binding.signup3.visibility = View.VISIBLE
    }

    private fun handleScreen3() {
        if (!mobile.isValidMobileNumber()) {
            binding.editPhone.requestFocus()
            binding.errorPhone.visibility = View.VISIBLE
            return
        }
        binding.errorPhone.visibility = View.GONE

        val sendOtpModel = SendOtpRequestModel(
            username = mobile,
            type = getString(R.string.register)
        )

        viewModel.getMobileCheck(sendOtpModel)
    }

    var requestRegister = RegisterVerifyOtpModel()
    var profileDetails = ProfileDetails()

    private fun verifyClickEvent() {
        hideKeyboard()
        c1 = binding.otpEditText1.text.toString().trim()
        c2 = binding.otpEditText2.text.toString().trim()
        c3 = binding.otpEditText3.text.toString().trim()
        c4 = binding.otpEditText4.text.toString().trim()
        c5 = binding.otpEditText5.text.toString().trim()
        c6 = binding.otpEditText6.text.toString().trim()
        val smsCode = "$c1$c2$c3$c4$c5$c6"

        securePin = SharedPrefsHelper.read(SharedPrefConstant.SERVER_OTP_KEY).toString()

        if (securePin.equals(smsCode)) {

            requestRegister.otp = smsCode
            requestRegister.relation = relation
            requestRegister.username = name
            requestRegister.verification_key =
                SharedPrefsHelper.read(SharedPrefConstant.VERIFICATION_KEY).toString()
            requestRegister.type = "guest"

            profileDetails.mobile = mobile
            profileDetails.name = name
            requestRegister.profile_details = profileDetails

            viewModel.signupVerifyOtpCall(requestRegister)

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

    private fun loginClick_underline() {
        val SpanString = SpannableString(
            "Already have an Account? Login here"
        )
        val teremsAndCondition: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val bundle = Bundle()
                bundle.putString("userType", "User")
                //val direction = FragmentSignupDirections.actionFragmentSignupToLoginFragment()
                findNavController().navigate(R.id.action_FragmentSignup_to_LoginFragment, bundle)
            }
        }
        SpanString.setSpan(teremsAndCondition, 25, 35, 0)
        SpanString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.orange_clr)),
            25,
            35,
            0
        )
        SpanString.setSpan(UnderlineSpan(), 25, 33, 0)
        binding.loginClickUnderline.movementMethod = LinkMovementMethod.getInstance()
        binding.loginClickUnderline.setText(SpanString, TextView.BufferType.SPANNABLE)
        binding.loginClickUnderline.isSelected = true
        binding.loginClickUnderline.highlightColor = Color.TRANSPARENT
    }

//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        relation = relationsList[position]
//    }
//
//    override fun onNothingSelected(parent: AdapterView<*>?) {
//    }

}


//            val params = HashMap<String, String>()
//            params["profile_details"] = java.lang.String.valueOf(jsonInner)
//            params["verification_key"] = java.lang.String.valueOf(verifiedKeyLocal)
//            params["username"] = java.lang.String.valueOf(name)
//            params["type"] = java.lang.String.valueOf("guest")
//            params["relation"] = java.lang.String.valueOf(relation)
//            params["otp"] = java.lang.String.valueOf(smsCode)
//            println("praveen params "+params)

//private fun createPartFromString(param: String): RequestBody {
//    return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), param)
//}

//       val profileDetails: RequestBody = createPartFromString(jsonObjInner_.toString())
//            val username: RequestBody = createPartFromString(name)
//            val relation: RequestBody = createPartFromString(relation)
//            val type: RequestBody = createPartFromString("guest")
//            val otp: RequestBody = createPartFromString(smsCode)
//            val verification_key: RequestBody = createPartFromString(PreferenceManager.saveVerification_key!!)
//
//            val map: HashMap<String, RequestBody> = HashMap()
//            map["profile_details"] = profileDetails
//            map["username"] = username
//            map["type"] = type
//            map["relation"] = relation
//            map["otp"] = otp
//            map["verification_key"] = verification_key