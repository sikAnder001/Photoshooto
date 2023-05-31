package com.photoshooto.bottomsheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.photoshooto.R
import com.photoshooto.domain.model.PhotographerPlanBody
import com.photoshooto.domain.model.PhotographerServiceModel
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.dashboard.ui.home.HomeViewModel
import com.photoshooto.ui.photographer.FragmentPhotographerProfile
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlansDialog : DialogFragment() {

    private val profileViewModel: GetUserProfileViewModel by viewModel()
    var id: String? = null
    var type = ""
    var amount = ""
    var sessionHours = ""
    var plansTypeList: List<PhotographerServiceModel.Data?>? = null

    companion object {
        const val TAG = "CreatePlansDialog"
        const val ID = "id"
        const val TYPE = "type"
        const val AMOUNT = "amount"
        const val SESSION_HOUR = "sessionHours"

        fun newInstance(
            id: String?,
            type: String?,
            amount: String?,
            sessionHours: String?,
        ): CreatePlansDialog {
            val args = Bundle()
            args.putString(ID, id)
            args.putString(TYPE, type)
            args.putString(AMOUNT, amount)
            args.putString(SESSION_HOUR, sessionHours)
            val fragment = CreatePlansDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ID)!!
            type = it.getString(TYPE)!!
            amount = it.getString(AMOUNT)!!
            sessionHours = it.getString(SESSION_HOUR)!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_create_plans, container, false)
    }

    var title: AppCompatTextView? = null
    var editPlans: AppCompatEditText? = null
    var editType: AppCompatEditText? = null
    var editHours: AppCompatEditText? = null
    var okayBtn: AppCompatTextView? = null
    var ivClose: AppCompatImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getPhotographerServices()

        title = view.findViewById(R.id.tvTitle) as AppCompatTextView
        editPlans = view.findViewById(R.id.editPlans) as AppCompatEditText
        editType = view.findViewById(R.id.editType) as AppCompatEditText
        editHours = view.findViewById(R.id.editHours) as AppCompatEditText
        okayBtn = view.findViewById(R.id.btnAdd) as AppCompatTextView
        ivClose = view.findViewById(R.id.ivClose) as AppCompatImageView

        editPlans!!.setText(type)
        editType!!.setText(amount)
        editHours!!.setText(sessionHours)

        if (!id.isNullOrEmpty()) {
            title?.text = "Update Plan"
            okayBtn?.text = "Update"
        } else {
            title?.text = "Create Plan"
            okayBtn?.text = "Save"
        }

        editPlans?.setOnClickListener {
            addPlansFilters(
                getString(R.string.type_plans),
                plansTypeList?.map { it?.type ?: "" } ?: ArrayList()
            )
//            addPlansFilters(getString(R.string.type_plans), ProfessionPlans)
        }
        editHours?.setOnClickListener {
            addPlansFilters(getString(R.string.please_enter_session), sessionHourList)
        }
        okayBtn?.setOnClickListener {
            updatePlan()
        }
        ivClose?.setOnClickListener {
            dismiss()
        }

        setObservers()
    }

    fun setObservers() {

        with(profileViewModel) {
            photographerServiceResponse.observe(requireActivity()) { response ->
                if (response != null) {
                    if (response.success == true) {
                        plansTypeList = response.data ?: ArrayList()
                    }
                }
            }
        }

        with(profileViewModel) {
            updatePlan.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            (parentFragment as FragmentPhotographerProfile).loadPlans()
                            dismiss()
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 ->
                                requireContext().showToast(it1)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        with(profileViewModel) {
            savePlan.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            (parentFragment as FragmentPhotographerProfile).loadPlans()
                            dismiss()
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 ->
                                requireContext().showToast(it1)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun updatePlan() {
        val planType = editPlans!!.text.toString()
        val amount = editType!!.text.toString()
        val hours = editHours!!.text.toString()





        if (planType.isNullOrEmpty()) {
            onToast("please select plan type!", requireContext())
            return

        } else if (amount.isNullOrEmpty()) {
            onToast("please enter amount!", requireContext())
            return

        } else if (amount.toInt() > 5000000) {
            onToast("please enter amount less than 50L.", requireContext())
            return
        } else if (hours.isNullOrEmpty()) {
            onToast("please enter session!", requireContext())
            return
        }

        val icon = plansTypeList?.filter { it?.type == planType }?.get(0)?.icon

        val photographerPlanBody = PhotographerPlanBody(planType, amount, hours, icon)

        if (!id.isNullOrEmpty()) {
            profileViewModel.updatePlan(

                id, photographerPlanBody, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
            )
        } else {
            profileViewModel.savePlan(
                photographerPlanBody, SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun addPlansFilters(title: String, args: List<String>) {
        val addPhotoBottomDialogFragment = SingleSelectionBottomDialogFragment.newInstance(
            title,
            args, true,
            object : SingleSelectionBottomDialogFragment.OnRecyclerViewDataSet {
                override fun onRecyclerViewDataSetListener(
                    selectedValue: String
                ) {
                    if (title == getString(R.string.type_plans)) {
                        editPlans?.setText(selectedValue)
                    } else {
                        editHours?.setText(selectedValue)
                    }

                }
            })
        addPhotoBottomDialogFragment.show(parentFragmentManager, "bottom_sheet_fragment")
    }

    var ProfessionPlans = listOf(
        "Candid Photography",
        "Wedding Photography",
        "Pre-wedding Shoot",
        "Corporate Photography",
        "Studio Photography",
        "Cinematography"
    )

    val sessionHourList = listOf(
        "4 hours",
        "6 Hours",
        "8 Hours",
        "12 hours",
    )

}