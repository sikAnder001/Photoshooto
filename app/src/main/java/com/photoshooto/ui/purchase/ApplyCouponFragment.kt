package com.photoshooto.ui.purchase

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
import com.photoshooto.databinding.FragmentApplyCouponBinding
import com.photoshooto.domain.adapter.CouponsAdapter
import com.photoshooto.domain.usecase.coupons.CouponsViewModel
import com.photoshooto.util.KEY_COUPON_CODE
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ApplyCouponFragment : Fragment() {

    companion object {
        const val REQUEST_APPLY_COUPON = "request_apply_coupon"
    }

    private lateinit var binding: FragmentApplyCouponBinding
    private val viewModel: CouponsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplyCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()
        viewModel.initTestData()
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun initView() {
        with(viewModel) {
            couponsDetails.observe(
                requireActivity(),
                Observer { couponsDetails ->
                    couponsDetails.success?.let { it ->
                        if (it) {
                            binding.recyclerViewCoupons.layoutManager =
                                LinearLayoutManager(requireContext())
                            binding.recyclerViewCoupons.adapter =
                                CouponsAdapter(couponsDetails.data ?: arrayListOf())
                            (binding.recyclerViewCoupons.adapter as CouponsAdapter).onItemClickListener =
                                object : CouponsAdapter.OnItemClickListener {
                                    override fun onApplyClick(code: String) {
                                        this@ApplyCouponFragment.setFragmentResult(
                                            REQUEST_APPLY_COUPON,
                                            bundleOf(KEY_COUPON_CODE to code)
                                        )
                                        findNavController().popBackStack()
                                    }
                                }
                        } else {
                            couponsDetails.message?.let { msg -> onToast(msg, requireContext()) }
                        }
                    }
                }
            )
        }
    }
}
