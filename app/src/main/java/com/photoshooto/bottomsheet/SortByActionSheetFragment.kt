package com.photoshooto.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.SortbyActionSheetBinding
import java.util.*
import kotlin.concurrent.schedule

@Suppress("UNCHECKED_CAST")
class SortByActionSheetFragment : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    lateinit var binding: SortbyActionSheetBinding

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


        binding = SortbyActionSheetBinding.inflate(inflater, container, false)

        binding.btnApply.setOnClickListener {


            dismissSheet()

        }

        binding.tvClear.setOnClickListener {
            selectOrder = "2"

            dismissSheet()

        }

        binding.tvCancer.setOnClickListener {
            selectOrder = "3"

            dismissSheet()
        }

        if (selectOrder.equals("1", true)) {
            binding.imgHigh.setImageResource(R.drawable.ic_radio_check_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_uncheck_product)

            selectOrder = "1"
            binding.btnApply.isEnabled = true

        } else if (selectOrder.equals("-1", true)) {
            binding.imgHigh.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_check_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_uncheck_product)

            selectOrder = "-1"

            binding.btnApply.isEnabled = true

        } else if (selectOrder.equals("0", true)) {
            binding.imgHigh.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_check_product)

            selectOrder = "0"
            binding.btnApply.isEnabled = true

        }

        binding.llLowToHigh.setOnClickListener {

            binding.imgHigh.setImageResource(R.drawable.ic_radio_check_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_uncheck_product)

            selectOrder = "1"
            binding.btnApply.isEnabled = true

        }

        binding.llHighToLow.setOnClickListener {

            binding.imgHigh.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_check_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_uncheck_product)

            selectOrder = "-1"
            binding.btnApply.isEnabled = true


        }

        binding.llLatestPost.setOnClickListener {

            binding.imgHigh.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLow.setImageResource(R.drawable.ic_radio_uncheck_product)
            binding.imgLatestPOst.setImageResource(R.drawable.ic_radio_check_product)

            selectOrder = "0"
            binding.btnApply.isEnabled = true
        }

        return binding.root
    }

    private fun dismissSheet() {

        Timer().schedule(500) {
            onTap(selectOrder)
            dismiss()
        }
    }

    companion object {
        private lateinit var onTap: (String) -> Unit
        var selectOrder = ""

        fun newInstance(
            selectItem: String,
            onTap: (String) -> Unit = {},
        ): SortByActionSheetFragment {

            val f = SortByActionSheetFragment()
            Companion.onTap = onTap
            selectOrder = selectItem

            return f
        }
    }
}
