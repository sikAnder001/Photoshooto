package com.photoshooto.ui.order

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogOrderDeliveredBinding
import com.photoshooto.databinding.DialogOrderStatusBinding
import com.photoshooto.databinding.DialogStatusTypeBinding
import com.photoshooto.databinding.FragmentOrderRequestDetailsBinding
import com.photoshooto.domain.adapter.CommonSingleSelectAdapter
import com.photoshooto.domain.adapter.OrderSizeAdapter
import com.photoshooto.domain.adapter.OrderUploadsAdapter
import com.photoshooto.domain.model.CommonMultiSelectItem
import com.photoshooto.domain.model.ListItem
import com.photoshooto.domain.model.UpdateStatus
import com.photoshooto.domain.usecase.orders.OrdersStatusViewModel
import com.photoshooto.domain.usecase.orders.OrdersViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderRequestDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderRequestDetailsBinding
    var billId = ""
    var status = ""
    private val args: OrderDetailsFragmentArgs by navArgs()
    private lateinit var data: ListItem
    private val viewModel: OrdersViewModel by viewModel()
    private val viewModelStatus: OrdersStatusViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderRequestDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = args.item
        initView()
    }

    private fun initView() {

        binding.apply {
            recyclerViewSizeSelector.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerViewUpload.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            tvPhotographerId.text = data.user_id
            tvOrderId.text = data.id
            tvQty.text = data.order_details?.total_units.toString()

            if ("tshirt" == data.type) {
                recyclerViewSizeSelector.adapter =
                    OrderSizeAdapter(data.tshirt_details?.get(0)?.sizes ?: arrayListOf())
                llSize.visibility = View.VISIBLE
                tvStudioName.text = data.tshirt_details?.get(0)?.studio_name
                tvStudioTaglineLabel.text = data.tshirt_details?.get(0)?.studio_tagline
                tvContactNo.text = data.tshirt_details?.get(0)?.contact_number
                tvAlternativeNo.text = data.tshirt_details?.get(0)?.alternate_number

                recyclerViewUpload.adapter =
                    OrderUploadsAdapter(data.tshirt_details?.get(0)?.studio_logo ?: arrayListOf())
                Glide.with(requireContext()).load(data.tshirt_details?.get(0)?.images?.get(0))
                    .into(appCompatImageView2)

            } else {
                llSize.visibility = View.GONE
                tvStudioName.text = data.standee_details?.get(0)?.studio_name
                tvStudioTaglineLabel.text = data.standee_details?.get(0)?.studio_tagline
                tvContactNo.text = data.standee_details?.get(0)?.contact_number
                tvAlternativeNo.text = data.standee_details?.get(0)?.alternate_contact_number
                recyclerViewUpload.adapter =
                    OrderUploadsAdapter(data.standee_details?.get(0)?.studio_logo ?: arrayListOf())
                Glide.with(requireContext()).load(data.standee_details?.get(0)?.images?.get(0))
                    .into(appCompatImageView2)
            }

            etStatus.setOnClickListener {
                //openStatusSelect()
                openStatus()
            }

            tvAccept.setOnClickListener {
                val updateStatus = UpdateStatus(status = ACCEPT)
                viewModelStatus.updateOrderStatus(
                    "" + data.id,
                    updateStatus
                )
            }
            tvHoldOrder.setOnClickListener {
                val updateStatus = UpdateStatus(status = HOLD)
                viewModelStatus.updateOrderStatus(
                    "" + data.id,
                    updateStatus
                )
            }
            tvProceed.setOnClickListener {
                handleProceedClick()
            }
            tvDecline.setOnClickListener {
                val updateStatus = UpdateStatus(status = DECLINE)
                viewModelStatus.updateOrderStatus(
                    "" + data.id,
                    updateStatus
                )
            }

            imageViewBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        if (data.status == HOLD || data.status == ACCEPT || data.status == REJECT || data.status == DECLINE || data.status == BLOCK) {
            binding.llButton.visibility = View.GONE
        } else {
            binding.llButton.visibility = View.VISIBLE
        }
        if (data.status == HOLD || data.status == ACCEPT) {
            binding.llOrderStatus.visibility = View.VISIBLE
        } else {
            binding.llOrderStatus.visibility = View.GONE
        }

    }


    private fun initObserver() {
        with(viewModel) {
            orderDetailLiveData.observe(requireActivity()) { ordersLiveData ->
                if (ordersLiveData.data != null) {
                    data = ordersLiveData.data
                    initView()
                }
            }
        }
        with(viewModelStatus) {
            commonResponseLiveData.observe(requireActivity()) { ordersLiveData ->
                ordersLiveData.success?.let { it ->
                    if (it) {
                        Toast.makeText(context, "" + ordersLiveData.message, Toast.LENGTH_SHORT)
                            .show()
                        viewModel.getOrderDataById(data.id!!)
                    } else {
                        ordersLiveData.message?.let { msg -> onToast(msg, requireContext()) }
                    }
                }
            }
        }
    }

    private fun handleProceedClick() {
        billId = binding.editBillId.editableText.toString().trim()
        status = binding.etStatus.editableText.toString().trim()

        binding.erroreEditBillId.visibility = View.GONE
        binding.errorStatus.visibility = View.GONE

        if (billId.isEmpty()) {
            binding.editBillId.requestFocus()
            binding.erroreEditBillId.text = getString(R.string.enter_bill_id)
            binding.erroreEditBillId.visibility = View.VISIBLE
            return
        }

        if (status.isEmpty()) {
            binding.etStatus.requestFocus()
            binding.errorStatus.visibility = View.VISIBLE
            return
        }

        val value = binding.etStatus.text.toString()
        var updateStatus = UpdateStatus(status = DESIGNED)
        if (value == "Designed") {
            updateStatus = UpdateStatus(status = DESIGNED)
        } else if (value == "Printed") {
            updateStatus = UpdateStatus(status = PRINTED)
        } else if (value == "Dispatched") {
            updateStatus = UpdateStatus(status = DISPATCHED)
        } else if (value == "Delivered") {
            updateStatus = UpdateStatus(status = DELIVERED)
        }

        viewModelStatus.updateOrderStatus(
            data.id!!,
            updateStatus
        )

        openSuccessDialog()
    }

    private fun openSuccessDialog() {
        val dialogOrderRequestSuccess = Dialog(requireContext())
        dialogOrderRequestSuccess.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogOrderRequestSuccess.window?.setBackgroundDrawable(ColorDrawable(0))
        val dialogBinding = DialogOrderDeliveredBinding.inflate(layoutInflater)
        dialogOrderRequestSuccess.setContentView(dialogBinding.root)
        dialogBinding.apply {
            tvDialogOrderSuccessNotNow.setOnClickListener {
                dialogOrderRequestSuccess.dismiss()
            }
            tvDialogOrderSuccessRateUs.setOnClickListener {
                dialogOrderRequestSuccess.dismiss()
            }
        }
        dialogOrderRequestSuccess.show()
        val window = dialogOrderRequestSuccess.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun openStatusSelect() {
        val additionalInfoDialog = Dialog(requireContext())
        additionalInfoDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        additionalInfoDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        val dialogBinding = DialogStatusTypeBinding.inflate(layoutInflater)
        additionalInfoDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            ivClose.setOnClickListener {
                additionalInfoDialog.dismiss()
            }
        }
        additionalInfoDialog.show()
        val window = additionalInfoDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    private fun openStatus() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        //val bottomSheetDialog = Dialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogOrderStatusBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)

        dialogBinding.tvTitle.text = getString(R.string.order_status)
        val dataList = ArrayList<CommonMultiSelectItem>()
        dataList.add(CommonMultiSelectItem("0", "Designed"))
        dataList.add(CommonMultiSelectItem("0", "Printed"))
        dataList.add(CommonMultiSelectItem("0", "Dispatched"))
        dataList.add(CommonMultiSelectItem("0", "Delivered"))

        dialogBinding.apply {
            tvClear.setOnClickListener {
                (recyclerView.adapter as CommonSingleSelectAdapter).clearSelection()
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = CommonSingleSelectAdapter(dataList)
            (recyclerView.adapter as CommonSingleSelectAdapter).onItemClickListener =
                object : CommonSingleSelectAdapter.OnItemClickListener {
                    override fun onDetailsClick(value: String) {
                        binding.etStatus.setText(value)
                        bottomSheetDialog.dismiss()
                    }
                }
            bottomSheetDialog.show()
        }

    }

}
