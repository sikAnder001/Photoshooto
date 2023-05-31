package com.photoshooto.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.R
import com.photoshooto.databinding.LayoutValuesBottomSheetBinding

class SingleSelectionBottomDialogFragment : BottomSheetDialogFragment() {

    val TAG = "AddPhotoBottomDialog"
    private lateinit var valuesAdapter: SingleSelectionCheckAdapter

    private var _binding: LayoutValuesBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SPLASH_TIME_OUT = 200
        var title: String? = null
        var args: List<String>? = null
        var onRecyclerViewDataSetListener: OnRecyclerViewDataSet? = null
        var showSearchBar = false
        fun newInstance(
            title: String, args: List<String>,
            showSearch: Boolean,
            onRecyclerViewDataSetListener: OnRecyclerViewDataSet
        ): SingleSelectionBottomDialogFragment {
            Companion.title = title
            Companion.args = args
            Companion.onRecyclerViewDataSetListener = onRecyclerViewDataSetListener
            return SingleSelectionBottomDialogFragment()
        }
    }

    interface OnRecyclerViewDataSet {
        fun onRecyclerViewDataSetListener(imagePath: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutValuesBottomSheetBinding.inflate(inflater, container, false)
        return binding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = title

        binding.rvData.also { it1 ->
            it1.layoutManager = LinearLayoutManager(requireActivity())
            it1.setHasFixedSize(true)
        }

        binding.searchPlans.visibility = if (showSearchBar) View.VISIBLE else View.GONE

        binding.okayBtn.visibility = View.VISIBLE
        binding.okayBtn.setOnClickListener {
            dismiss()
        }

        valuesAdapter = SingleSelectionCheckAdapter(
            args!!,
            object : SingleSelectionCheckAdapter.ItemSelectionListener {
                override fun onItemClick(value: String, position: Int) {
                    onRecyclerViewDataSetListener?.onRecyclerViewDataSetListener(value)
                    binding.rvData.post { valuesAdapter.notifyDataSetChanged() }
                    dismiss()
                }
            }, requireActivity()
        )
        binding.rvData.adapter = valuesAdapter

    }


}
