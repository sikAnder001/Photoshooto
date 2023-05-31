package com.photoshooto.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogFilterSortBySelectionBinding
import com.photoshooto.databinding.FragmentOrdersBinding
import com.photoshooto.domain.adapter.FilterAndSortSelectAdapter
import com.photoshooto.domain.adapter.OrdersAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.model.ListItem
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressWarnings("All")
class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val viewModel: OrdersViewModel by viewModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getOrderRequestList("", 10, 1)
        initView()
        initObserver()
        return root
    }

    private fun initView() {
        binding.apply {
            ivFilter.setOnClickListener {
                openFilterType()
            }
            ivShorting.setOnClickListener {
                openSortType()
            }
            imageViewBack.setOnClickListener {
                (activity as AdminDashboardActivity).binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun initObserver() {

        with(viewModel) {
            ordersLiveData.observe(requireActivity()) { ordersLiveData ->
                ordersLiveData.success.let {
                    if (it) {
                        ordersLiveData.data?.list?.let { orderList ->
                            binding.apply {
                                recyclerViewItem.layoutManager =
                                    LinearLayoutManager(requireContext())
                                recyclerViewItem.adapter =
                                    OrdersAdapter(requireActivity(), orderList)
                                (recyclerViewItem.adapter as OrdersAdapter).onItemClickListener =
                                    object : OrdersAdapter.OnItemClickListener {
                                        override fun onDetailsClick(data: ListItem) {
                                            val bundle = Bundle()
                                            bundle.putParcelable("item", data)
                                            /*val action =
                                                OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(
                                                    data
                                                )*/
                                            findNavController().navigate(
                                                R.id.action_navigation_orders_to_orderDetailsFragment,
                                                bundle
                                            )
                                        }
                                    }

                            }
                        }
                    } else {
                        onToast(ordersLiveData.message, requireContext())
                    }
                }
            }
        }
    }

    private fun openFilterType() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogFilterSortBySelectionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = getString(R.string.filter_by)
        val dataList = ArrayList<CommonMultiSelectItem>()
        dataList.add(CommonMultiSelectItem("0", "Standee"))
        dataList.add(CommonMultiSelectItem("0", "T-shirt"))

        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as FilterAndSortSelectAdapter).clearSelection()
                bottomSheetDialog.dismiss()
                viewModel.getOrderRequestList("", 10, 1)
            }
            /*btnApply.setOnClickListener {
                val dataList = (recyclerView.adapter as FilterAndSortSelectAdapter).getDataFilter()

                if (dataList[0].isSelected!! && dataList[1].isSelected!!) {
                    viewModel.getOrderRequestList("", 10, 1)
                } else {
                    val type = if (dataList[0].isSelected!!) {
                        STANDEE
                    } else if (dataList[1].isSelected!!) {
                        T_SHIRT
                    } else {
                        ""
                    }
                    viewModel.getOrderRequestList(type, 10, 1)
                }

                bottomSheetDialog.dismiss()

            }*/
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = FilterAndSortSelectAdapter(requireActivity(), dataList)
            (recyclerView.adapter as FilterAndSortSelectAdapter).onItemClickListener =
                object : FilterAndSortSelectAdapter.OnItemClickListener {
                    override fun onDetailsClick(data: CommonMultiSelectItem) {
                        val dataListSelected =
                            (recyclerView.adapter as FilterAndSortSelectAdapter).getDataFilter()

                        if (dataListSelected[0].isSelected!! && dataListSelected[1].isSelected!!) {
                            viewModel.getOrderRequestList("", 10, 1)
                        } else {
                            val type = when {
                                dataListSelected[0].isSelected!! -> {
                                    STANDEE
                                }
                                dataListSelected[1].isSelected!! -> {
                                    T_SHIRT
                                }
                                else -> {
                                    ""
                                }
                            }
                            viewModel.getOrderRequestList(type, 10, 1)
                        }

                        bottomSheetDialog.dismiss()
                    }
                }
        }
        bottomSheetDialog.show()
    }

    private fun openSortType() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogFilterSortBySelectionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = getString(R.string.label_sort_by)
        val dataList = ArrayList<CommonMultiSelectItem>()
        dataList.add(CommonMultiSelectItem("0", "Accepted Order"))
        dataList.add(CommonMultiSelectItem("0", "Rejected Orders"))
        dataList.add(CommonMultiSelectItem("0", "Pending Orders"))

        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as FilterAndSortSelectAdapter).clearSelection()
                bottomSheetDialog.dismiss()
                viewModel.getOrderRequestList("", 10, 1)
            }

            /*btnApply.setOnClickListener {
                val dataList = (recyclerView.adapter as FilterAndSortSelectAdapter).getDataFilter()

                if (dataList[0].isSelected!! && dataList[1].isSelected!! && dataList[2].isSelected!!) {
                    viewModel.getOrderRequestList("", 10, 1)
                } else {
                    val type = if (dataList[0].isSelected!!) {
                        ACCEPT
                    } else if (dataList[1].isSelected!!) {
                        DECLINE
                    } else if (dataList[2].isSelected!!) {
                        PENDING
                    } else {
                        ""
                    }
                    viewModel.getOrderRequestList(type, 10, 1)
                }

                bottomSheetDialog.dismiss()

            }*/
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = FilterAndSortSelectAdapter(requireActivity(), dataList)
            (recyclerView.adapter as FilterAndSortSelectAdapter).onItemClickListener =
                object : FilterAndSortSelectAdapter.OnItemClickListener {
                    override fun onDetailsClick(data: CommonMultiSelectItem) {
                        val dataListSelected =
                            (recyclerView.adapter as FilterAndSortSelectAdapter).getDataFilter()

                        if (dataListSelected[0].isSelected!! && dataListSelected[1].isSelected!! && dataListSelected[2].isSelected!!) {
                            viewModel.getOrderRequestList("", 10, 1)
                        } else {
                            val type = when {
                                dataListSelected[0].isSelected!! -> {
                                    ACCEPT
                                }
                                dataListSelected[1].isSelected!! -> {
                                    DECLINE
                                }
                                dataListSelected[2].isSelected!! -> {
                                    PENDING
                                }
                                else -> {
                                    ""
                                }
                            }
                            viewModel.getOrderRequestList(type, 10, 1)
                        }

                        bottomSheetDialog.dismiss()
                    }
                }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}