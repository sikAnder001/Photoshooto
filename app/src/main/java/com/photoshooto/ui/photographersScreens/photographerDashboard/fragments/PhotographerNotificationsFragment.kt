package com.photoshooto.ui.photographersScreens.photographerDashboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.photoshooto.R
import com.photoshooto.databinding.DialogFilterSortBySelectionBinding
import com.photoshooto.databinding.FragmentPhotographerNotificationsBinding
import com.photoshooto.domain.adapter.FilterAndSortSelectAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.ui.photographersScreens.photographerDashboard.UnReadNotificationAdapter
import com.photoshooto.util.ALL
import com.photoshooto.util.PROMOTIONAL
import com.photoshooto.util.TRANSACTIONAL
import com.photoshooto.util.visible

class PhotographerNotificationsFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var binding: FragmentPhotographerNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarNotification

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.notifications)

        val filterBtn = toolbar.endIcon
        filterBtn.visible()
        filterBtn.setImageResource(R.drawable.ic_filter)
        filterBtn.setOnClickListener {
            openSortType()
        }

        binding.recyclerViewNotification.adapter = AllNotificationAdapter(requireActivity())
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.recyclerViewNotification.layoutManager = LinearLayoutManager(requireActivity())

    }

    private fun openSortType() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogFilterSortBySelectionBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = getString(R.string.label_sort_by)
        val dataList = ArrayList<CommonMultiSelectItem>()
        dataList.add(CommonMultiSelectItem("0", "All"))
        dataList.add(CommonMultiSelectItem("0", "Transactional"))
        dataList.add(CommonMultiSelectItem("0", "Promotional"))

        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as FilterAndSortSelectAdapter).clearSelection()
                bottomSheetDialog.dismiss()
                //viewModel.getOrderRequestList("", 10, 1)
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
                            //viewModel.getOrderRequestList("", 10, 1)
                        } else {
                            val type = when {
                                dataListSelected[0].isSelected!! -> {
                                    ALL
                                }
                                dataListSelected[1].isSelected!! -> {
                                    TRANSACTIONAL
                                }
                                dataListSelected[2].isSelected!! -> {
                                    PROMOTIONAL
                                }
                                else -> {
                                    ""
                                }
                            }
                            //viewModel.getOrderRequestList(type, 10, 1)
                        }

                        bottomSheetDialog.dismiss()
                    }
                }
        }
        bottomSheetDialog.show()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab?.position == 0 || tab?.position == 1) {
            binding.recyclerViewNotification.adapter = AllNotificationAdapter(requireActivity())
        } else {
            binding.recyclerViewNotification.adapter = UnReadNotificationAdapter(requireActivity())
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}