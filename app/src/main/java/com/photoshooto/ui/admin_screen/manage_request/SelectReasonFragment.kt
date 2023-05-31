package com.photoshooto.ui.admin_screen.manage_request

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSelectReasonBinding
import com.photoshooto.domain.adapter.SelectReasonAdapter
import com.photoshooto.domain.model.ReasonElement
import com.photoshooto.domain.usecase.manage_request.NewRequestViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectReasonFragment : Fragment() {

    private lateinit var binding: FragmentSelectReasonBinding
    private val viewModel: NewRequestViewModel by viewModel()
    private val detailsReasonList = arrayListOf<ReasonElement>()
    private val documentReasonList = arrayListOf<ReasonElement>()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            getReasonsResponse.observe(
                requireActivity(),
                Observer { result ->
                    result?.success?.let {
                        if (it) {
                            result.data?.list?.let {
                                detailsReasonList.addAll(it.filter { x -> x.type == "general" } as ArrayList)
                                documentReasonList.addAll(it.filter { x -> x.type == "documents" } as ArrayList)
                                binding.apply {
                                    recyclerViewReasonsDetails.layoutManager =
                                        LinearLayoutManager(requireContext())
                                    recyclerViewReasonsDocuments.layoutManager =
                                        LinearLayoutManager(requireContext())
                                    recyclerViewReasonsDetails.adapter =
                                        SelectReasonAdapter(detailsReasonList)
                                    recyclerViewReasonsDocuments.adapter =
                                        SelectReasonAdapter(documentReasonList)
                                }
                            }
                        } else {
                            result.message?.let { onToast(it, requireContext()) }
                        }
                    }
                }
            )
            updateUserStatusResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            this@SelectReasonFragment.setFragmentResult(
                                RequestedUserDetailsFragment.REQUEST_SELECT_REASON,
                                bundleOf(
                                    KEY_RESULT to Activity.RESULT_OK
                                )
                            )
                            findNavController().popBackStack()
                        } else {
                            result.message?.let { onToast(it, requireContext()) }
                        }
                    }
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectReasonBinding.inflate(inflater, container, false)
        userId = requireArguments().getString("userId") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListener()
        initView()

        if (requireContext().isInternetAvailable()) {
            viewModel.getReasons()
        } else {
            onToast(getString(R.string.internet_check), requireContext())
        }
    }

    private fun initView() {
        binding.apply {
            tablayoutTypes.addTab(
                tablayoutTypes.newTab().setText(getString(R.string.label_details))
            )
            tablayoutTypes.addTab(
                tablayoutTypes.newTab().setText(getString(R.string.label_documents))
            )
        }
    }

    private fun clickListener() {
        binding.apply {
            btnDone.setOnClickListener {
                if (detailsReasonList.none { x -> x.is_selected } && documentReasonList.none { x -> x.is_selected }
                ) {
                    onToast(getString(R.string.label_select_reasons), requireContext())
                } else {
                    val selectedReasons = arrayListOf<String>()
                    detailsReasonList.filter { x -> x.is_selected }.forEach {
                        it.reason_name?.let { reason ->
                            selectedReasons.add(reason)
                        }
                    }
                    documentReasonList.filter { x -> x.is_selected }.forEach {
                        it.reason_name?.let { reason ->
                            selectedReasons.add(reason)
                        }
                    }
                    viewModel.updateUserStatus(userId, viewModel.REJECT, selectedReasons)
                }
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tablayoutTypes.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            recyclerViewReasonsDetails.show()
                            recyclerViewReasonsDocuments.hide()
                        }
                        1 -> {
                            recyclerViewReasonsDetails.hide()
                            recyclerViewReasonsDocuments.show()
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }
}
