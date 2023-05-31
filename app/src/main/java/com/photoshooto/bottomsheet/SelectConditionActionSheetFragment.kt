package com.photoshooto.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.SelectConditionActionSheetBinding
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.ui.usedPhotographyScreens.adapter.SelectItemListAdapter
import java.util.*
import kotlin.concurrent.schedule

@Suppress("UNCHECKED_CAST")
class SelectConditionActionSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: SelectConditionActionSheetBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private lateinit var selectItemListAdapter: SelectItemListAdapter

    var selectCondition = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val dialog2 = dialog as BottomSheetDialog
            dialog.setCancelable(true)

            val bottomSheet =
                dialog2.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).setState(BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SelectConditionActionSheetBinding.inflate(inflater, container, false)

        selectItemListAdapter = SelectItemListAdapter(requireActivity()) { item ->

            selectCondition = item
        }

        binding.tvClear.setOnClickListener {
            dismiss()
        }

        binding.btnAppy.setOnClickListener {

            onTap(selectCondition)
            requireContext().hideKeyboardFrom(binding.btnAppy)

            dismissSheet()

        }

        binding.rvSelect.layoutManager = LinearLayoutManager(requireContext())

        binding.rvSelect.adapter = selectItemListAdapter


        setLocationData()


        return binding.root
    }

    private fun setLocationData() {
        val tempDataArray: ArrayList<String> = ArrayList()

        tempDataArray.add("Excellent, New Condition")
        tempDataArray.add("Good, Minor Defects")
        tempDataArray.add("Average, Slight Wear & Tear")
        tempDataArray.add("Bad, Rough use")
        tempDataArray.add("Others")


        selectItemListAdapter.swapList(tempDataArray, true)

    }


    private fun dismissSheet() {

        Timer().schedule(800) {
            dismiss()
        }
    }

    companion object {
        private lateinit var onTap: (String) -> Unit


        fun newInstance(
            onTap: (String) -> Unit = {},
        ): SelectConditionActionSheetFragment {

            val f = SelectConditionActionSheetFragment()
            Companion.onTap = onTap

            return f
        }
    }
}
