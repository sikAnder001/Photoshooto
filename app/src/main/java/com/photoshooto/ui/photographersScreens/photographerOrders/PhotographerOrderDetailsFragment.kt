package com.photoshooto.ui.photographersScreens.photographerOrders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.photoshooto.databinding.FragmentPhotographerOrderDetailsBinding
import com.photoshooto.domain.model.ListItem
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.ui.login.GetSupportDialog
import com.photoshooto.ui.photographersScreens.WriteReviewDialog
import com.photoshooto.util.STANDEE
import com.photoshooto.util.onToast
import com.photoshooto.util.recordScreenView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PhotographerOrderDetailsFragment : Fragment() {

    lateinit var binding: FragmentPhotographerOrderDetailsBinding
    private val orderViewModel: OrdersViewModel by viewModel()
    private var data: ListItem? = null
    var orderId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotographerOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordScreenView(
            requireActivity(),
            "PhotographerOrderDetailsFragment",
            FirebaseAnalytics_Event_ScreenName.screenPhotographerOrder_Details
        )
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        if (requireArguments().containsKey("OrderID")) {
            orderViewModel.getOrderDataById(requireArguments().get("item").toString())
            initLiveDataObserver()
        }

        binding.tvReview.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "PhotographerOrderDetailsFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographerOrder_Details_Review
            )
            orderId?.let { it1 ->
                WriteReviewDialog.newInstance("", it1, requireContext())
                    .show(parentFragmentManager, WriteReviewDialog.TAG)
            }
        }

        binding.tvSupport.setOnClickListener {
            recordScreenView(
                requireActivity(),
                "PhotographerOrderDetailsFragment",
                FirebaseAnalytics_Event_ScreenName.screenPhotographerOrder_Details_Support
            )
            GetSupportDialog.newInstance("", requireContext())
                .show(parentFragmentManager, GetSupportDialog.TAG)
        }

    }

    @SuppressLint("NewApi")
    private fun initLiveDataObserver() {
        with(orderViewModel) {
            orderDetailLiveData.observe(requireActivity()) { ordersLiveData ->
                ordersLiveData.success.let {
                    if (it) {
                        data = ordersLiveData?.data
                        if (data != null) {
                            with(binding) {
                                orderId = data?.id
                                tvOrderId.text = "Order ID-${orderId}"
                                if (STANDEE == data?.type) {
                                    tvProductName.text = data?.user_profile?.name
                                    tvStatus.text = data?.status
                                    if (STANDEE == data?.type) {
                                        Glide.with(requireActivity())
                                            .load(data?.standee_details?.get(0)?.images?.get(0))
                                            .into(imgProduct)
                                    } else {
                                        Glide.with(requireActivity())
                                            .load(data?.tshirt_details?.get(0)?.images?.get(0))
                                            .into(imgProduct)
                                    }

                                } else {
                                    Glide.with(requireActivity())
                                        .load(data?.tshirt_details?.get(0)?.images?.get(0))
                                        .into(imgProduct)
                                }
                                tvQuantity.text =
                                    "Quantity : ${data?.order_details?.total_units.toString()}"
                                tvDesc.text = "Rs. ${data?.deliver_address}"
                                tvOrderId.text = "Rs. ${data?.id}"

                                val odt = OffsetDateTime.parse(data?.updated_at)
                                val dtf =
                                    DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)

                                tvDate.text = "" + dtf.format(odt)

                            }
                        }
                    } else {
                        onToast(ordersLiveData.message, requireContext())
                    }
                }
            }
        }
    }
}