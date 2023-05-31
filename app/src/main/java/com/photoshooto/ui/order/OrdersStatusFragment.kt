package com.photoshooto.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogFilterSortBySelectionBinding
import com.photoshooto.databinding.FragmentOrdersRequestBinding
import com.photoshooto.domain.adapter.FilterAndSortSelectAdapter
import com.photoshooto.domain.adapter.OrdersStatusAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.model.ListItem
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.ui.admin_screen.AdminDashboardActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class OrdersStatusFragment : Fragment() {

    private var _binding: FragmentOrdersRequestBinding? = null
    private val viewModel: OrdersViewModel by viewModel()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrdersRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getOrderRequestList("", 10, 1)
        initView()
        initObserver()
        return root
    }

    private fun initView() {
        binding.apply {
            tvOrderRequest.text = requireActivity().getString(R.string.order_status)

            ivFilter.setOnClickListener {
                openFilterType()
            }
            ivShorting.setOnClickListener {
                openSortType()
            }
            imageViewBack.setOnClickListener {
                (activity as AdminDashboardActivity).drawerOpen()
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
                                recyclerViewItem.adapter = OrdersStatusAdapter(orderList)
                                (recyclerViewItem.adapter as OrdersStatusAdapter).onItemClickListener =
                                    object : OrdersStatusAdapter.OnItemClickListener {
                                        override fun onDetailsClick(data: ListItem) {

                                            if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY)
                                                    ?.lowercase(Locale.getDefault()) == Constant.EmployeeBy.printo.name.lowercase(
                                                    Locale.getDefault()
                                                )
                                            ) {
                                                val action =
                                                    OrdersRequestFragmentDirections.actionOrdersRequestFragmentToOrderRequestDetailsFragment(
                                                        data
                                                    )
                                                findNavController().navigate(action)

                                            } else if (SharedPrefsHelper.read(SharedPrefConstant.EMPLOYEE_BY)
                                                    ?.lowercase(Locale.getDefault()) == Constant.EmployeeBy.photoshooto.name.lowercase(
                                                    Locale.getDefault()
                                                )
                                            ) {
                                                val bundle = Bundle()
                                                bundle.putParcelable("item", data)
                                                /*      val action =
                                                          OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(
                                                              data
                                                          )
                                                      findNavController().navigate(action)*/
                                                findNavController().navigate(
                                                    R.id.action_navigation_orders_status_to_orderDetailsFragment,
                                                    bundle
                                                )
                                                /*findNavController().navigate(

                                                    R.id.action_navigation_orders_to_orderDetailsFragment,
                                                    bundle
                                                )*/

                                            }

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