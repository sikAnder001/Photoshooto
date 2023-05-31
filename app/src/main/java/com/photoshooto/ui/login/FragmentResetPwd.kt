package com.photoshooto.ui.login

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.photoshooto.R
import com.photoshooto.databinding.FragmentResetPwdVerifyOtpBinding
import com.photoshooto.domain.usecase.reset_pwd.PwdChangeRequestModel
import com.photoshooto.domain.usecase.reset_pwd.ResetPwdViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FragmentResetPwd : Fragment() {

    private var _binding: FragmentResetPwdVerifyOtpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val resetPwdViewModel: ResetPwdViewModel by viewModel()

    var passConfirmShow = false
    var passConfirmShow1 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPwdVerifyOtpBinding.inflate(inflater, container, false)
        recordScreenView(
            requireActivity(),
            "FragmentResetPwd",
            FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_ResetPassword
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtBackReset.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnCreatePwd.setOnClickListener {
            if (binding.btnCreatePwd.text.toString().equals(getString(R.string.create_new_pwd))) {
                verifyPassword()
            }
        }

        binding.editRePassword.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                verifyPassword()
                true
            } else false
        })

        binding.infoImg.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentResetPwd",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_Login_Password_Guidelines
            )
            InfoPwdDialog.newInstance("", requireContext())
                .show(parentFragmentManager, InfoPwdDialog.TAG)
        }

        binding.getSupport.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "FragmentResetPwd",
                FirebaseAnalytics_Event_ScreenName.screenPhotographer_LoginSupport
            )
            GetSupportDialog.newInstance("", requireContext())
                .show(parentFragmentManager, GetSupportDialog.TAG)
        }

        println("authToken Resetpassword  " + PreferenceManager.saveVerification_key)

        with(resetPwdViewModel) {
            resetPwdData.observe(requireActivity(), Observer {
                onToast(it.message, requireActivity())

                if (it.success) {
                    // if (binding.btnViewProfile.text.toString().equals(getString(R.string.view_profile))) {
                    val direction =
                        FragmentResetPwdDirections.actionFragmentResetPwdToFragmentPhotographerProfile()
                    findNavController().navigate(direction)
                    // }
                    //TODO add these after adding model respons from backend
//                    with(PreferenceManager) {
//                        isLogged = true
//                        authTokenSave = it.data?.token
//                        saveMobile = it.data?.user?.profile_details?.mobile
//                        saveEmail= it.data?.user?.profile_details?.email
//                        saveUsername= it.data?.user?.profile_details?.name
//                        saveProfileUrl= it.data?.user?.profile_details?.profile_image
//                        saveUserID = it.data?.user?.id
//                    }
//                    (activity as DashBoardScreenActivity).handleSideNavigationData()
                    /* PwdChangStatusDialog.newInstance("", requireContext())
                         .show(getParentFragmentManager(), PwdChangStatusDialog.TAG)*/
                } else
                    it.message.let { it1 ->
                        onToast(it1, requireActivity())
                    }
            })
            showProgressbar.observe(requireActivity(), Observer { isVisible ->
                binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            })
        }

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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyPassword() {
        hideKeyboard()

        val editPassword = binding.editPassword.text.toString()
        val editRePassword = binding.editRePassword.text.toString()

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

        if (!editPassword.equals(editRePassword)) {
            binding.editRePassword.requestFocus()
            binding.errorRePass.text = getString(R.string.pwd_does_nt_match)
            binding.errorRePass.visibility = View.VISIBLE
            return
        }
        binding.errorRePass.visibility = View.GONE

        var pwdModel = PwdChangeRequestModel(
            password = editPassword
        )
        binding.btnViewProfile.visibility = View.VISIBLE
        binding.btnCreatePwd.visibility = View.GONE
        resetPwdViewModel.resetpassword(pwdModel, PreferenceManager.saveVerification_key)

    }

}