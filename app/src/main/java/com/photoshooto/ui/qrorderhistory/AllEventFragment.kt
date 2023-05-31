package com.photoshooto.ui.qrorderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogNewRequestFilterBinding
import com.photoshooto.databinding.FragmentAllEventBinding
import com.photoshooto.domain.adapter.NewRequestFilterAdapter
import com.photoshooto.domain.adapter.QREventOrderPagerAdapter
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllEventFragment : Fragment() {

    companion object {
        fun newInstance() = OngoingEventFragment()
    }

    private lateinit var binding: FragmentAllEventBinding
    private val viewModel: OngoingEventViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        val className = AllEventFragment::class.java.name;
        viewModel.getEventOrderHistory(0, 0, className)
        initView()
    }

    private fun clickListener() {
        binding.apply {
            ivFilter.setOnClickListener {
                openFilterBy()
            }
        }
    }

    private fun openFilterBy() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogNewRequestFilterBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        /*with(viewModel) {
            newRequestDetails.value?.let { newRequestModel ->
                newRequestModel.success?.let { it ->
                    if (it) {
                        dialogBinding.apply {
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.adapter = NewRequestFilterAdapter(newRequestModel.data?.filterList ?: arrayListOf())
                        }
                    } else {
                        newRequestModel.message?.let { msg -> onToast(msg, requireContext()) }
                    }
                }
            }
        }*/
        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as NewRequestFilterAdapter).clearSelection()
            }
            btnApply.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()

    }

    private fun initView() {
        with(viewModel) {
            eventOrderHistoyDetails.observe(
                requireActivity(),
                Observer { eventOrderHistoyDetails ->
                    eventOrderHistoyDetails.success?.let { it ->
                        if (it) {
                            eventOrderHistoyDetails.data?.list?.let { eventorderList ->
                                eventorderList[0].let { eventorderhistory ->
                                    binding.apply {

                                        recyclerEventList.layoutManager =
                                            LinearLayoutManager(requireContext())
                                        recyclerEventList.adapter =
                                            QREventOrderPagerAdapter(eventorderList)
                                    }
                                }
                            }
                        } else {
                            eventOrderHistoyDetails.message?.let { msg ->
                                onToast(
                                    msg,
                                    requireContext()
                                )
                            }
                        }
                    }
                }
            )
        }
    }

}