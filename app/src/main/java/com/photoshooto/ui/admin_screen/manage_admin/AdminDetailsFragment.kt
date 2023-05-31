package com.photoshooto.ui.admin_screen.manage_admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.databinding.FragmentAdminDetailsBinding
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminDetailsFragment : Fragment() {

    val SWITCH_COMPAT_IGNORE_TAG = "SWITCH_COMPAT_IGNORE_TAG"
    private lateinit var binding: FragmentAdminDetailsBinding
    private val viewModel: ManageAdminViewModel by viewModel()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            getUserDetailsResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.let { data ->
                                binding.apply {
                                    switchStatus.tag = SWITCH_COMPAT_IGNORE_TAG
                                    switchStatus.isChecked = data.status == viewModel.ACTIVE
                                    switchStatus.tag = null
                                    btnBlock.text =
                                        if (data.status == viewModel.BLOCK) getString(R.string.label_unblock) else getString(
                                            R.string.label_block
                                        )
                                    tvStatus.text = data.status
                                    data.profile_details?.profile_image?.let { img ->
                                        Glide.with(requireContext())
                                            .load("$DOMAIN$img")
                                            .into(ivProfile)
                                    }
                                    tvName.text = data.profile_details?.name
                                    tvUserName.text = data.username ?: ""
                                    tvEmpType.text = "(${data.type ?: "-"})"
                                    tvEmpCode.text = "Employee Code : ${data.employee_code ?: "-"}"
                                    // tvCity.text = data.profile_details?.
                                    // tvReportingPerson.text =
                                    tvEmailId.text = data.profile_details?.email ?: "-"
                                    tvContactNo.text = data.profile_details?.mobile ?: "-"
                                    tvAlternativeNo.text = data.profile_details?.alt_mobile ?: "-"
                                    tvGender.text = data.profile_details?.gender ?: "-"
                                    data.profile_details?.language_know?.let { lang ->
                                        tvLanguagesKnown.text = lang.joinToString(", ")
                                    }
                                    data.location?.let { location ->
                                        if (!location.city.isNullOrEmpty()) {
                                            tvLocation.text = "${location.city}, ${location.state}"
                                        }
                                    }
                                    data.city_assigned?.let { cities ->
                                        if (cities.isEmpty()) {
                                            tvCities.text = "-"
                                        } else {
                                            tvCities.text = cities.joinToString(", ")
                                        }
                                    }
                                    data.module_assigned?.let { modules ->
                                        if (modules.isEmpty()) {
                                            tvModules.text = "-"
                                        } else {
                                            tvModules.text = modules.joinToString(", ")
                                        }
                                    }
                                }
                            }
                        } else {
                            result.message?.let {
                                onToast(it, requireContext())
                            }
                        }
                    }
                }
            )

            removeUserResponse.observe(requireActivity(), Observer { result ->
                result.success?.let {
                    if (it) {
                        findNavController().popBackStack()
                    } else {
                        result.message?.let { message ->
                            onToast(message, requireContext())
                        }
                    }
                }
            })


            updateUserStatusResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            viewModel.getUserDetails(userId ?: "")
                        } else {
                            result.message?.let { onToast(it, requireContext()) }
                        }
                    }
                }
            )
            messageData.observe(
                requireActivity(),
                Observer { result ->
                    onToast(result, requireContext())
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminDetailsBinding.inflate(inflater, container, false)
        userId = requireArguments().getString("userId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        if (viewModel.getUserDetailsResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                viewModel.getUserDetails(userId ?: "")
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        } else initObserver()
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            switchStatus.setOnCheckedChangeListener { compoundButton, b ->
                if (switchStatus.tag != SWITCH_COMPAT_IGNORE_TAG) {
                    if (b) {
                        viewModel.updateUserStatus(userId ?: "", viewModel.ACTIVE)
                    } else {
                        viewModel.updateUserStatus(userId ?: "", viewModel.INACTIVE)
                    }
                }
            }
            btnBlock.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    when (btnBlock.text.toString()) {
                        getString(R.string.label_block) -> {
                            viewModel.updateUserStatus(userId ?: "", viewModel.BLOCK)
                        }
                        else -> {
                            viewModel.updateUserStatus(userId ?: "", viewModel.ACTIVE)
                        }
                    }
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnRemove.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    viewModel.removeUser(userId ?: "")
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            cardPersonalDetailsTitle.setOnClickListener {
                if (viewPersonalDetails.isShown) {
                    viewPersonalDetails.hide()
                    tvPersonalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.right_arrow,
                        0
                    )
                } else {
                    viewPersonalDetails.show()
                    tvPersonalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down_arrow,
                        0
                    )
                }
            }
            cardCitiesAssignedTitle.setOnClickListener {
                if (viewCitiesAssigned.isShown) {
                    viewCitiesAssigned.hide()
                    tvCityAssignedTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.right_arrow,
                        0
                    )
                } else {
                    viewCitiesAssigned.show()
                    tvCityAssignedTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down_arrow,
                        0
                    )
                }
            }
            cardModuleAssignedTitle.setOnClickListener {
                if (viewModuleAssigned.isShown) {
                    viewModuleAssigned.hide()
                    tvModulesAssignedTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.right_arrow,
                        0
                    )
                } else {
                    viewModuleAssigned.show()
                    tvModulesAssignedTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down_arrow,
                        0
                    )
                }
            }
        }
    }
}
