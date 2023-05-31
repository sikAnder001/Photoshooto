package com.photoshooto.ui.photographersScreens.photographerOrders

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.BottomsheetCancelOrderReasonBinding
import com.photoshooto.databinding.FragmentPhotographerTrackOrderBinding
import com.photoshooto.domain.model.ListItem
import com.photoshooto.domain.model.UpdateStatus
import com.photoshooto.domain.usecase.orders.OrdersStatusViewModel
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.util.CANCEL
import com.photoshooto.util.STANDEE
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressWarnings("All")
class PhotographerTrackOrderFragment : Fragment() {

    lateinit var binding: FragmentPhotographerTrackOrderBinding

    private var data: ListItem? = null
    private var reasonsList = mutableListOf<String>()
    private val viewModel: OrdersStatusViewModel by viewModel()
    private val orderViewModel: OrdersViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhotographerTrackOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancleOrder.setOnClickListener {
            binding.lvCancelOrder.dialogCancel.visibility = View.VISIBLE
            binding.btnCancleOrder.visibility = View.GONE
        }

        binding.lvCancelOrder.btnCancleOrder.setOnClickListener {
            binding.lvCancelOrder.dialogCancel.visibility = View.GONE
            binding.btnCancleOrder.visibility = View.VISIBLE
        }


        binding.btnCancel.setOnClickListener(View.OnClickListener {
            val cancelOrderDialog = Dialog(requireActivity())
            cancelOrderDialog.setContentView(R.layout.dialog_cancel_order)
            val btnYes: TextView =
                cancelOrderDialog.findViewById(R.id.btnYes) as TextView
            val btnNo: TextView =
                cancelOrderDialog.findViewById(R.id.btnNo) as TextView

            btnYes.setOnClickListener {
                val status = UpdateStatus(CANCEL, reasonsList)
                val id = data?.id!!
                // viewModel.updateOrderStatus("OD11HK66SU", status)
                viewModel.updateOrderStatus(id, status)
                cancelOrderDialog.dismiss()
                successfullyOrderCancel()
            }
            btnNo.setOnClickListener(View.OnClickListener {
                cancelOrderDialog.dismiss()
            })

            cancelOrderDialog.show()
        })




        binding.lvCancelOrder.btnCancleReason.setOnClickListener {
            val bottomsheetDialog = this.let { it1 ->
                BottomSheetDialog(
                    requireActivity(),
                    R.style.BottomSheetDialog
                )
            }
            layoutInflater.inflate(R.layout.bottomsheet_cancel_order_reason, null)

            val cancelOrderReasonBinding =
                BottomsheetCancelOrderReasonBinding.inflate(layoutInflater)

            val chkArrivalText = cancelOrderReasonBinding.chkArrivalTime.text.toString()
            val chkMyMindText = cancelOrderReasonBinding.chkChangeMyMind.text.toString()
            val chkChangeShippingAddressText =
                cancelOrderReasonBinding.chkChangeShippingAddress.text.toString()
            val chkDoNotNeedProductText =
                cancelOrderReasonBinding.chkDoNotNeedProduct.text.toString()
            val chkFoundCheaperText = cancelOrderReasonBinding.chkFoundCheaper.text.toString()
            val chkOrderMistakeText = cancelOrderReasonBinding.chkOrderMistake.text.toString()


            if (cancelOrderReasonBinding.chkArrivalTime.isChecked && !reasonsList.contains(
                    chkArrivalText
                )
            ) {
                reasonsList.add(chkArrivalText)
            } else {
                reasonsList.remove(chkArrivalText)
            }

            if (reasonsList.contains(chkMyMindText)) {
                cancelOrderReasonBinding.chkChangeMyMind.isChecked = true
            }

            if (reasonsList.contains(chkChangeShippingAddressText)
            ) {
                cancelOrderReasonBinding.chkChangeShippingAddress.isChecked = true
            }

            if (reasonsList.contains(chkDoNotNeedProductText)
            ) {
                cancelOrderReasonBinding.chkDoNotNeedProduct.isChecked = true
            }

            if (reasonsList.contains(chkFoundCheaperText)) {
                cancelOrderReasonBinding.chkFoundCheaper.isChecked = true
            }

            if (reasonsList.contains(chkOrderMistakeText)) {
                cancelOrderReasonBinding.chkOrderMistake.isChecked = true
            }



            cancelOrderReasonBinding.btnApply.setOnClickListener {

                if (cancelOrderReasonBinding.chkArrivalTime.isChecked && !reasonsList.contains(
                        chkArrivalText
                    )
                ) {
                    reasonsList.add(chkArrivalText)
                } else {
                    reasonsList.remove(chkArrivalText)
                }

                if (cancelOrderReasonBinding.chkChangeMyMind.isChecked && !reasonsList.contains(
                        chkMyMindText
                    )
                ) {
                    reasonsList.add(chkMyMindText)
                } else {
                    reasonsList.remove(chkMyMindText)
                }

                if (cancelOrderReasonBinding.chkChangeShippingAddress.isChecked && !reasonsList.contains(
                        chkChangeShippingAddressText
                    )
                ) {
                    reasonsList.add(chkChangeShippingAddressText)
                } else {
                    reasonsList.remove(chkChangeShippingAddressText)
                }

                if (cancelOrderReasonBinding.chkDoNotNeedProduct.isChecked && !reasonsList.contains(
                        chkDoNotNeedProductText
                    )
                ) {
                    reasonsList.add(chkDoNotNeedProductText)
                } else {
                    reasonsList.remove(chkDoNotNeedProductText)
                }
                if (cancelOrderReasonBinding.chkFoundCheaper.isChecked && !reasonsList.contains(
                        chkFoundCheaperText
                    )
                ) {
                    reasonsList.add(chkFoundCheaperText)
                } else {
                    reasonsList.remove(chkFoundCheaperText)
                }

                if (cancelOrderReasonBinding.chkOrderMistake.isChecked && !reasonsList.contains(
                        chkOrderMistakeText
                    )
                ) {
                    reasonsList.add(chkOrderMistakeText)
                } else {
                    reasonsList.remove(chkOrderMistakeText)
                }

                if (reasonsList.isEmpty()) {
                    binding.btnCancel.supportBackgroundTintList =
                        ColorStateList.valueOf(R.color.grey3)
                } else {
                    binding.btnCancel.supportBackgroundTintList =
                        ColorStateList.valueOf(R.color.red_color)
                }




                bottomsheetDialog.dismiss()
            }

            cancelOrderReasonBinding.tvClear.setOnClickListener {
                reasonsList.clear()
                bottomsheetDialog.dismiss()
            }

            bottomsheetDialog.setCancelable(true)
            bottomsheetDialog.setContentView(cancelOrderReasonBinding.root)
            bottomsheetDialog.show()
        }


        if (requireArguments().containsKey("item")) {
            orderViewModel.getOrderDataById(requireArguments().get("item").toString())
        }

        initLiveDataObserver()

    }

    private fun initLiveDataObserver() {
        with(orderViewModel) {
            orderDetailLiveData.observe(requireActivity()) { ordersLiveData ->
                ordersLiveData.success.let {
                    if (it) {
                        data = ordersLiveData?.data
                        if (data != null) {
                            with(binding) {
                                tvOrderId.text = "Order ID-${data?.id}"
                                if (STANDEE == data?.type) {
                                    tvProductName.text = data?.user_profile?.name
                                    Glide.with(requireActivity())
                                        .load(data?.standee_details?.get(0)?.images?.get(0))
                                        .into(imgProduct)

                                } else {
                                    Glide.with(requireActivity())
                                        .load(data?.tshirt_details?.get(0)?.images?.get(0))
                                        .into(imgProduct)
                                }
                                tvQuantity.text =
                                    "Quantity : ${data?.order_details?.total_units.toString()}"
                                tvPrice.text = "Rs. ${data?.order_details?.total_amount.toString()}"

                                tvDelivered.text = "Delivered To : ${data?.deliver_address}"
                                // tvPaymentStatus.text= listItem.status

                                val odt = OffsetDateTime.parse(data?.updated_at)
                                val dtf =
                                    DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)

                                tvConfirmationDate.text = "" + dtf.format(odt)

                            }
                        }
                    } else {
                        onToast(ordersLiveData.message, requireContext())
                    }
                }
            }
        }
    }

    private fun successfullyOrderCancel() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_order_cancelled_successfully)
        val dialogButton = dialog.findViewById<View>(R.id.btnDone) as TextView
        dialogButton.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }
        dialog.show()

    }
}