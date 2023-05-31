package com.photoshooto.ui.qrcodegenration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.FragmentChooseStandeeBinding
import com.photoshooto.domain.adapter.ChooseStandeeAdapter
import com.photoshooto.domain.adapter.ChooseStandeePagerAdapter
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentChooseStandee : Fragment() {

    private lateinit var binding: FragmentChooseStandeeBinding
    private val viewModel: QrGenerationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            standeeResponse.observe(
                requireActivity(),
                Observer { result ->
                    if (result.success) {
                        result.data?.list?.let { standeeList ->
                            binding.apply {
                                recyclerViewStandee.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                recyclerViewStandee.adapter = ChooseStandeeAdapter(standeeList)
                                (recyclerViewStandee.adapter as ChooseStandeeAdapter).onItemClickListener =
                                    object : ChooseStandeeAdapter.OnItemClickListener {
                                        override fun onItemClick() {
                                            updateStandeeInformation()
                                        }
                                    }
                                updateStandeeInformation()
                            }
                        }
                    } else {
                        onToast(result.message, requireContext())
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
        binding = FragmentChooseStandeeBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbarChooseQr
        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.choose_qr)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        if (requireContext().isInternetAvailable()) {
            viewModel.getStandee()
        } else {
            onToast(getString(R.string.internet_check), requireContext())
        }
    }

    private fun updateStandeeInformation() {
        binding.apply {
            recyclerViewStandee.adapter?.let { adapter ->
                val selectedStandee = (adapter as ChooseStandeeAdapter).getSelectedElement()
                selectedStandee?.let { standeeElement ->
                    viewpagerStandeeImages.adapter =
                        ChooseStandeePagerAdapter(standeeElement.images ?: arrayListOf())
                    TabLayoutMediator(indicator, viewpagerStandeeImages) { _, _ ->
                    }.attach()
                    tvStandeeType.text = standeeElement.type
                    tvPrice.text = standeeElement.price.toDouble().amount(requireContext())
                    tvHeight.text =
                        "${getString(R.string.standee_height)}          : ${standeeElement.height}"
                    tvWidth.text =
                        "${getString(R.string.standee_width)}           : ${standeeElement.width}"
                    tvWeight.text =
                        "${getString(R.string.standee_weight)}          : ${standeeElement.weight}"

                    tvminQuantity.text =
                        "${getString(R.string.minimum_quantity)}          : ${standeeElement.min_quantity}"
                    tvDescription.text = standeeElement.description
                }
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            btnAdd.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    binding.apply {
                        recyclerViewStandee.adapter?.let { adapter ->
                            val selectedStandee =
                                (adapter as ChooseStandeeAdapter).getSelectedElement()
                            selectedStandee?.let { standeeElement ->
                                val bundle = Bundle()
                                bundle.putString(
                                    KEY_SELECTED_STANDEE,
                                    Gson().toJson(standeeElement)
                                )
                                bundle.putString(KEY_QR_ID, arguments?.getString(KEY_QR_ID))
                                /*findNavController().navigate(
                                    R.id.action_move_to_standee_proceed,
                                    bundle
                                )*/
                                findNavController().navigate(
                                    R.id.action_fragmentChooseStandee_to_fragmentProceedToSummery,
                                    bundle
                                )
                            }
                        }
                    }
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
        }
    }
}
