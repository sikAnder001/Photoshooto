package com.photoshooto.ui.admin_screen.manage_request

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogNewRequestFilterBinding
import com.photoshooto.databinding.DialogNewRequestSortByBinding
import com.photoshooto.databinding.FragmentRejectedRequestBinding
import com.photoshooto.domain.adapter.NewRequestFilterAdapter
import com.photoshooto.domain.adapter.RejectedRequestAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.model.UserElement
import com.photoshooto.domain.usecase.manage_request.NewRequestViewModel
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RejectedRequestFragment : Fragment() {

    private lateinit var binding: FragmentRejectedRequestBinding
    private val viewModel: NewRequestViewModel by viewModel()
    private val cityList = ArrayList<CommonMultiSelectItem>()

    private var page = 0
    private var noMorePages = false
    private var searchDelay = Timer()
    private var sortBy = NewRequestViewModel.SORT_BY.new_to_old
    private var sortByOrder: String? = NewRequestViewModel.SORT_BY.order_acd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            declineRequestResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.list?.let {
                                if (it.isEmpty()) {
                                    if (page == 0) {
                                        binding.tvEmptyState.show()
                                        binding.recyclerView.hide()
                                    }
                                    noMorePages = true
                                } else {
                                    binding.apply {
                                        binding.tvEmptyState.hide()
                                        binding.recyclerView.show()
                                        when (page) {
                                            0 -> {
                                                recyclerView.adapter =
                                                    RejectedRequestAdapter(it as ArrayList)
                                                (recyclerView.adapter as RejectedRequestAdapter).onItemClickListener =
                                                    object :
                                                        RejectedRequestAdapter.OnItemClickListener {
                                                        override fun onDetailsClick(
                                                            position: Int,
                                                            data: UserElement
                                                        ) {
                                                            findNavController().navigate(
                                                                RejectedRequestFragmentDirections.actionMoveToUserDetails(
                                                                    getString(R.string.label_declined),
                                                                    data.id ?: ""
                                                                )
                                                            )
                                                        }
                                                    }
                                            }
                                            else -> {
                                                (recyclerView.adapter as RejectedRequestAdapter).addMoreItems(
                                                    it as java.util.ArrayList
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            binding.tvEmptyState.show()
                            binding.recyclerView.hide()
                            result.message?.let {
                                onToast(it, requireContext())
                            }
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
        binding = FragmentRejectedRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()

        if (viewModel.declineRequestResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                callRequestApi(true)
                viewModel.getCityList()
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        } else initObserver()
    }

    private fun callRequestApi(isResetPage: Boolean = false) {
        if (isResetPage) {
            page = 0
            noMorePages = false
        }
        var filterValue: String? = null
        if (cityList.any { x -> x.isSelected == true }) {
            filterValue = "["
            cityList.filter { x -> x.isSelected == true }.forEach {
                filterValue += (if (filterValue.length != 1) "," else "") + "\"${it.value}\""
            }
            filterValue += "]"
        }
        viewModel.getRejectedRequestUser(
            page,
            binding.edtSearch.text.toString(),
            sortBy,
            sortByOrder,
            filterValue
        )
    }

    private fun initView() {
        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext())
        }
    }

    private fun clickListener() {
        binding.apply {
            ivShorting.setOnClickListener {
                openSortBy()
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    searchDelay.cancel()
                    if (p0.toString().length > 3) {
                        searchDelay = Timer()
                        searchDelay.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    requireActivity().runOnUiThread {
                                        callRequestApi(true)
                                    }
                                }
                            },
                            viewModel.SEARCH_DELAY
                        )
                    } else if (p0.toString().isEmpty()) {
                        callRequestApi(true)
                        binding.edtSearch.clearFocus()
                        hideKeyboard()
                    }
                }
            })
            nestedScrollView.viewTreeObserver.addOnScrollChangedListener(
                ViewTreeObserver.OnScrollChangedListener {
                    val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
                    val diff: Int = view.bottom - (
                            nestedScrollView.height + nestedScrollView
                                .scrollY
                            )
                    if (diff <= 0) {
                        Log.e("Pagination", "Reach")
                        if (!noMorePages && viewModel.showProgressbar.value == false) {
                            Log.e("Pagination", "Executed")
                            page++
                            callRequestApi()
                        }
                    }
                }
            )
            ivSideMenu.setOnClickListener {
                (activity as AdminDashboardActivity).drawerOpen()
            }
            ivFilter.setOnClickListener {
                openFilterBy()
            }
        }
    }

    private fun openSortBy() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogNewRequestSortByBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            tvClear.setOnClickListener {
                if (!rbNewToOld.isChecked) {
                    rbNewToOld.isChecked = true
                }
            }
            when (sortBy) {
                NewRequestViewModel.SORT_BY.new_to_old -> {
                    if (sortByOrder == NewRequestViewModel.SORT_BY.order_acd) {
                        rbNewToOld.isChecked = true
                    } else rbOldToNew.isChecked = true
                }
                NewRequestViewModel.SORT_BY.last_1_week ->
                    rbLast1Week.isChecked = true
                NewRequestViewModel.SORT_BY.last_1_month ->
                    rbLast1Month.isChecked = true
                else ->
                    rbAlphabetically.isChecked = true
            }
            radiogroup.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    R.id.rbNewToOld -> {
                        sortBy = NewRequestViewModel.SORT_BY.new_to_old
                        sortByOrder = NewRequestViewModel.SORT_BY.order_acd
                        callRequestApi(true)
                    }
                    R.id.rbOldToNew -> {
                        sortBy = NewRequestViewModel.SORT_BY.old_to_new
                        sortByOrder = NewRequestViewModel.SORT_BY.order_desc
                        callRequestApi(true)
                    }
                    R.id.rbLast1Week -> {
                        sortBy = NewRequestViewModel.SORT_BY.last_1_week
                        sortByOrder = null
                        // callRequestApi(true)
                    }
                    R.id.rbLast1Month -> {
                        sortBy = NewRequestViewModel.SORT_BY.last_1_month
                        sortByOrder = null
                        // callRequestApi(true)
                    }
                    else -> {
                        sortBy = NewRequestViewModel.SORT_BY.alphabetically
                        sortByOrder = null
                        callRequestApi(true)
                    }
                }
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    private fun openFilterBy() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogNewRequestFilterBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = NewRequestFilterAdapter(
                cityList
            )
            tvClear.setOnClickListener {
                (recyclerView.adapter as NewRequestFilterAdapter).clearSelection()
            }
            btnApply.setOnClickListener {
                bottomSheetDialog.dismiss()
                callRequestApi(true)
            }
        }
        bottomSheetDialog.show()
    }
}
