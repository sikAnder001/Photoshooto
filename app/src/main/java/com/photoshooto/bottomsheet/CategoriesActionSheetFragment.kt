package com.photoshooto.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.CategoriesActionSheetBinding
import com.photoshooto.ui.job.utility.hideKeyboardFrom
import com.photoshooto.ui.usedPhotographyScreens.adapter.SelectItemListAdapter
import java.util.*

@Suppress("UNCHECKED_CAST")
class CategoriesActionSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: CategoriesActionSheetBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private lateinit var selectItemListAdapter: SelectItemListAdapter

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

        binding = CategoriesActionSheetBinding.inflate(inflater, container, false)
        selectItemListAdapter = SelectItemListAdapter(requireActivity()) { d ->
            requireContext().hideKeyboardFrom(binding.rvItem)

            onTap(d)
            dismiss()
        }

        binding.rvItem.layoutManager = LinearLayoutManager(requireContext())

        binding.rvItem.adapter = selectItemListAdapter

        binding.etSearch.doOnTextChanged { text, start, count, after ->
            selectItemListAdapter.filter.filter(text)
        }
        setLocationData()


        return binding.root
    }

    private fun setLocationData() {


        selectItemListAdapter.swapList(mainProducts)
    }


    companion object {
        private lateinit var onTap: (String) -> Unit
        var mainProducts: ArrayList<String> = ArrayList()


        fun newInstance(
            item: ArrayList<String>,
            onTap: (String) -> Unit = {},
        ): CategoriesActionSheetFragment {

            val f = CategoriesActionSheetFragment()
            Companion.onTap = onTap
            mainProducts = item

            return f
        }
    }
}
