package com.photoshooto.ui.admin_screen.manage_admin

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.photoshooto.R
import com.photoshooto.databinding.DialogAdminAdditionalDetailsBinding
import com.photoshooto.databinding.DialogAdminEmployeeTypeBinding
import com.photoshooto.databinding.DialogAdminModuleAssignedBinding
import com.photoshooto.databinding.FragmentCreateAdminBinding
import com.photoshooto.domain.adapter.CommonMultiSelectAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.usecase.manage_admin.ManageAdminViewModel
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.isValidEmail
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CreateAdminFragment : Fragment() {

    private lateinit var binding: FragmentCreateAdminBinding
    private val viewModel: ManageAdminViewModel by viewModel()
    private var empType: String? = null
    private var languageKnown: String? = null
    private var gender: String? = null
    private var location: String? = null

    private val modulesList = ArrayList<CommonMultiSelectItem>()
    private val cityList = ArrayList<CommonMultiSelectItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            createUserResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        result.message?.let { msg ->
                            onToast(msg, requireContext())
                        }
                        if (it) {
                            findNavController().popBackStack()
                        }
                    }
                }
            )
            getModulesResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.list?.let {
                                it.forEach { item ->
                                    modulesList.add(
                                        CommonMultiSelectItem(
                                            item.id ?: "",
                                            item.name ?: ""
                                        )
                                    )
                                }
                            }
                            viewModel.getCityList()
                        }
                    }
                }
            )
            getCityResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.let {
                                it.forEach { item ->
                                    cityList.add(
                                        CommonMultiSelectItem(
                                            "${item.id ?: 0}",
                                            item.name ?: ""
                                        )
                                    )
                                }
                            }
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
        binding = FragmentCreateAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        if (requireContext().isInternetAvailable()) {
            viewModel.getModulesList()
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvAdditionalDetails.setOnClickListener {
                openAdditionalDetails()
            }
            edtEmployeeType.setOnClickListener {
                openEmployeeType()
            }
            edtModuleAssigned.setOnClickListener {
                if (modulesList.isEmpty()) {
                    onToast(getString(R.string.label_some_thing_went_wrong), requireContext())
                } else openModuleAssigned()
            }
            edtCity.setOnClickListener {
                if (cityList.isEmpty()) {
                    onToast(getString(R.string.label_some_thing_went_wrong), requireContext())
                } else openCitySelect()
            }
            edtEmailId.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0?.let {
                        if (it.isEmpty()) {
                            edtEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_red_cross_close,
                                0
                            )
                        } else if (isValidEmail(it)) {
                            edtEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_green_right_mark_20dp,
                                0
                            )
                        } else edtEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_red_cross_close,
                            0
                        )
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            edtLogInEmailId.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0?.let {
                        if (it.isEmpty()) {
                            edtLogInEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_red_cross_close,
                                0
                            )
                        } else if (isValidEmail(it)) {
                            edtLogInEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_green_right_mark_20dp,
                                0
                            )
                        } else edtLogInEmailId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_red_cross_close,
                            0
                        )
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            btnAdd.setOnClickListener {
                if (validate() && requireContext().isInternetAvailable()) {
                    val assignedCities = arrayListOf<String>()
                    val assignedModules = arrayListOf<String>()
                    cityList.filter { x -> x.isSelected == true }.forEach {
                        assignedCities.add(it.value)
                    }
                    modulesList.filter { x -> x.isSelected == true }.forEach {
                        assignedModules.add(it.value)
                    }
                    viewModel.createUser(
                        edtLogInEmailId.text.toString(),
                        edtEmailId.text.toString(),
                        edtEmployeeCode.text.toString(),
                        gender ?: "",
                        edtPhoneNumber.text.toString(),
                        edtEmployeeName.text.toString(),
                        edtPassword.text.toString(),
                        empType ?: "",
                        languageKnown?.split(","),
                        assignedCities,
                        assignedModules
                    )
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
        }
    }

    private fun validate(): Boolean {
        var status = true
        binding.apply {
            if (edtEmployeeName.text.toString().isEmpty()) {
                edtEmployeeName.error = getString(R.string.is_required)
                status = false
            } else if (edtEmployeeCode.text.toString().isEmpty()) {
                edtEmployeeCode.error = getString(R.string.is_required)
                status = false
            } else if (empType.isNullOrEmpty()) {
                onToast(getString(R.string.label_select_employee_type), requireContext())
                status = false
            } else if (edtPhoneNumber.text.toString().isEmpty()) {
                edtPhoneNumber.error = getString(R.string.is_required)
                status = false
            } else if (edtEmailId.text.toString().isEmpty()) {
                edtEmailId.error = getString(R.string.is_required)
                status = false
            } else if (cityList.none { x -> x.isSelected == true }) {
                onToast(getString(R.string.label_please_assign_city), requireContext())
                status = false
            } else if (modulesList.none { x -> x.isSelected == true }) {
                onToast(getString(R.string.label_please_assign_module), requireContext())
                status = false
            } else if (edtLogInEmailId.text.toString().isEmpty()) {
                edtLogInEmailId.error = getString(R.string.is_required)
                status = false
            } else if (edtPassword.text.toString().isEmpty()) {
                edtPassword.error = getString(R.string.is_required)
                status = false
            }
        }
        return status
    }

    private fun openAdditionalDetails() {
        val additionalInfoDialog = Dialog(requireContext())
        additionalInfoDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        additionalInfoDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        val dialogBinding = DialogAdminAdditionalDetailsBinding.inflate(layoutInflater)
        additionalInfoDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            if (!languageKnown.isNullOrEmpty()) {
                edtLanguagesKnown.setText(languageKnown ?: "")
            }
            if (!gender.isNullOrEmpty()) {
                edtGender.setText(gender ?: "")
            }
            if (!location.isNullOrEmpty()) {
                edtLocation.setText(location ?: "")
            }
        }

        dialogBinding.apply {
            ivClose.setOnClickListener {
                additionalInfoDialog.dismiss()
            }
            btnAdd.setOnClickListener {
                if (edtLanguagesKnown.text.toString().isNotEmpty()) {
                    languageKnown = edtLanguagesKnown.text.toString()
                }
                if (edtGender.text.toString().isNotEmpty()) {
                    gender = edtGender.text.toString()
                }
                if (edtLocation.text.toString().isNotEmpty()) {
                    location = edtLocation.text.toString()
                }
                additionalInfoDialog.dismiss()
            }
        }
        additionalInfoDialog.show()
        val window = additionalInfoDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun openEmployeeType() {
        val employeeTypeDialog = Dialog(requireContext())
        employeeTypeDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        employeeTypeDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        val dialogBinding = DialogAdminEmployeeTypeBinding.inflate(layoutInflater)
        employeeTypeDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            empType?.let {
                if (it == "photoshooto") {
                    rdbPsEmployee.isChecked = true
                } else rdbPrintoEmployee.isChecked = true
            }
            ivClose.setOnClickListener {
                employeeTypeDialog.dismiss()
            }
            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    R.id.rdbPsEmployee ->
                        empType = "photoshooto"
                    R.id.rdbPrintoEmployee ->
                        empType = "printo"
                }
                binding.edtEmployeeType.setText(root.findViewById<MaterialRadioButton>(i).text.toString())
                employeeTypeDialog.dismiss()
            }
        }

        employeeTypeDialog.show()
        val window = employeeTypeDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun openModuleAssigned() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogAdminModuleAssignedBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as CommonMultiSelectAdapter).clearSelection()
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val searchValue = edtSearch.text.toString().toLowerCase(Locale.ROOT)
                    if (searchValue.isEmpty()) {
                        recyclerView.adapter = CommonMultiSelectAdapter(modulesList)
                    } else {
                        recyclerView.adapter = CommonMultiSelectAdapter(
                            modulesList.filter { x ->
                                x.value.toLowerCase(Locale.ROOT).contains(searchValue)
                            } as ArrayList
                        )
                    }
                }
            })
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CommonMultiSelectAdapter(modulesList)
            btnApply.setOnClickListener {
                val selectedSize = modulesList.filter { x -> x.isSelected == true }.size
                if (selectedSize != 0) {
                    val value = modulesList.filter { x -> x.isSelected == true }
                        .joinToString { x -> x.value }
                    binding.edtModuleAssigned.setText(value)
                } else binding.edtModuleAssigned.text = null
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    private fun openCitySelect() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogAdminModuleAssignedBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = getString(R.string.label_city)
        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as CommonMultiSelectAdapter).clearSelection()
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    val searchValue = edtSearch.text.toString().toLowerCase(Locale.ROOT)
                    if (searchValue.isEmpty()) {
                        recyclerView.adapter = CommonMultiSelectAdapter(cityList)
                    } else {
                        recyclerView.adapter = CommonMultiSelectAdapter(
                            cityList.filter { x ->
                                x.value.toLowerCase(Locale.ROOT).contains(searchValue)
                            } as ArrayList
                        )
                    }
                }
            })
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CommonMultiSelectAdapter(cityList)
            btnApply.setOnClickListener {
                val selectedSize = cityList.filter { x -> x.isSelected == true }.size
                if (selectedSize != 0) {
                    val value = cityList.filter { x -> x.isSelected == true }
                        .joinToString { x -> x.value }
                    binding.edtCity.setText(value)
                } else binding.edtCity.text = null
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }
}
