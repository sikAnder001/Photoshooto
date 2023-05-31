package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.databinding.FragmentSummaryBinding
import com.photoshooto.databinding.LayoutToolbarBackQueryBinding
import com.photoshooto.domain.model.OrderSummaryModel
import com.photoshooto.ui.job.adapters.OrderSummaryAdapter
import com.photoshooto.ui.job.utility.SpacesItemDecoration
import com.photoshooto.ui.job.utility.setSafeOnClickListener

class SummaryPageFragment : Fragment() {

    lateinit var binding: FragmentSummaryBinding
    lateinit var toolbarBinding: LayoutToolbarBackQueryBinding
    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private val navArgs: SummaryPageFragmentArgs by navArgs()
    var arraySummaryModel = ArrayList<OrderSummaryModel>()
    var totalPrice = 0
    var totalDisc = 0
    var totalTax = 0
    private var finalTotalPrice = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutToolbarBackQueryBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderSummaryAdapter = OrderSummaryAdapter(onEditClick = {
            findNavController().navigateUp()
        })

        toolbarBinding.title.text = ""
        toolbarBinding.back.setSafeOnClickListener {
            findNavController().popBackStack()
        }

        binding.labelViewCoupons.setSafeOnClickListener {
            findNavController().navigate(SummaryPageFragmentDirections.actionSummaryToCouponsFragment())
        }
        binding.btnPay.setSafeOnClickListener {
            findNavController().navigate(
                SummaryPageFragmentDirections.actionSummaryToPaymentOption(
                    ""
                )
            )
        }

/*
        binding.viewCoupons.setSafeOnClickListener {
            binding.labelViewCoupons.callOnClick()
        }*/

        arraySummaryModel.clear()
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL, false
            )

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
            itemAnimator = null
            adapter = orderSummaryAdapter
        }

        navArgs.orders.forEach {
            arraySummaryModel.add(it)
            totalPrice += it.orderRowTotal.toInt()
        }
        orderSummaryAdapter.submitList(arraySummaryModel)
        calculateTotal()
    }

    private fun calculateTotal() {

        finalTotalPrice = totalPrice - totalDisc + totalTax

        binding.ordertotalValue.text =
            requireContext().getString(R.string.rs_amount, totalPrice.toString())
        binding.orderdiscValue.text =
            requireContext().getString(R.string.rs_amount, totalDisc.toString())
        binding.ordertaxesValue.text =
            requireContext().getString(R.string.rs_amount, totalTax.toString())
        binding.orderfinalValue.text =
            requireContext().getString(R.string.rs_amount, finalTotalPrice.toString())

        binding.btnPay.text = "Pay ₹${finalTotalPrice}"
    }
}