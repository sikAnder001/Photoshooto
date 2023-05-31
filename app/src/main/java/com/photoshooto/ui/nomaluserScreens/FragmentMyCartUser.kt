package com.photoshooto.ui.nomaluserScreens

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.DialogRemoveStandeeCartBinding
import com.photoshooto.databinding.FragmentMyCartBinding
import com.photoshooto.domain.adapter.StandeeCartProductAdapter
import com.photoshooto.domain.model.AddressElement

import com.photoshooto.domain.usecase.product_details.ProductDetailsViewModel
import com.photoshooto.domain.usecase.product_details.TshirtSize

import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.ui.qrcodegenration.FreeTShirtLisAdapter
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class FragmentMyCartUser : Fragment() {

    lateinit var binding: FragmentMyCartBinding
    private val viewModel: QrGenerationViewModel by viewModel()
    private var qrID: String? = null
    private var totalAmount = 0.0
    var addressElement = AddressElement("")

    var dialogBuilder: AlertDialog.Builder? = null
    var availableSize: ArrayList<TshirtSize>? = null
    var alertDialog: AlertDialog? = null
    private val productViewModel: ProductDetailsViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* setFragmentResultListener(SelectAddressFragment.REQUEST_ADDRESS_CHANGE) { requestKey, bundle ->
             try {
                 val result = bundle.getString(KEY_DATA)
                 if (!result.isNullOrEmpty()) {
                     val address = Gson().fromJson(result, AddressElement::class.java)
                     updateAddress(address)
                 }
             } catch (e: Exception) {
             }
         }*/
        // initObserver()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCartBinding.inflate(inflater, container, false)
        qrID = arguments?.getString(KEY_QR_ID)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initObserver()
        if (requireActivity().isInternetAvailable()) {
            if (viewModel.addressListResponse.value == null) {
                viewModel.getUserAddress()
                viewModel.getCartDetails()
            } else initObserver()
        } else {
            onToast(getString(R.string.internet_check), requireActivity())
        }
    }

    private fun initObserver() {
        //  binding.llEmptyCart.visibility = View.VISIBLE
        //binding.tvOrderNow.visibility  = View.GONE
        with(viewModel) {
            if (isTShirtViewShown) {
                binding.cardTShirtView.visibility = View.VISIBLE
                binding.imgRedeem.visibility = View.GONE
            }

            messageData.observe(
                requireActivity()
            ) {
                onToast(it, requireActivity())
            }
            createOrderResponse.observe(
                requireActivity()
            ) { result ->
                result.success?.let {
                    if (it) {
                        var orderID = ""
                        if (result.data != null) {
                            orderID = result.data.id.toString()
                        }
                        startPayment(orderID)

                    } else result.message?.let { msg -> onToast(msg, requireActivity()) }
                }
            }
            cartResponse.observe(
                requireActivity()
            ) { result ->
                if (result.success) {
                    result.data?.list?.let {
                        if (it.isNotEmpty()) {
                            it[0].standee_list?.let { standeeList ->
                                binding.apply {
                                    recyclerViewProductList.layoutManager =
                                        LinearLayoutManager(requireActivity())
                                    recyclerViewProductList.adapter =
                                        StandeeCartProductAdapter(standeeList as ArrayList)
                                    (recyclerViewProductList.adapter as StandeeCartProductAdapter).onItemChangedListener =
                                        object :
                                            StandeeCartProductAdapter.OnItemChangedListener {
                                            override fun onQtyChanged() {
                                                updatePriceInformation()
                                            }

                                            override fun onItemRemoveClick() {
                                                removeStandeeConfirmation()
                                            }

                                            override fun onItemEditClick() {
                                                findNavController().popBackStack()
                                            }
                                        }
                                    updatePriceInformation()
                                }
                            }
                            binding.llEmptyCart.visibility = View.GONE
                            binding.llMain.visibility = View.VISIBLE
                        } else {
                            binding.llEmptyCart.visibility = View.VISIBLE
                            binding.llMain.visibility = View.GONE
                        }
                    }

                    productViewModel.getTshirtList(10, 0)
                } else {
                    onToast(result.message, requireActivity())
                }
            }
            addressListResponse.observe(
                requireActivity()
            ) { result ->
                if (result.success) {
                    result.data?.list?.let { addressList ->
                        if (addressList.any { a -> a.is_default }) {
                            addressList[0].let {
                                addressList[0].let {
                                    updateAddress(it)
                                }
                            }
                        } else {
                            binding.cardAddressView.hide()
                            binding.btnAddAddress.show()
                        }
                    }
                } else {
                    binding.cardAddressView.hide()
                    binding.btnAddAddress.show()
                    onToast(result.message, requireActivity())
                }
            }
            showProgressbar.observe(requireActivity(), Observer {
                binding.progressbar.visibility = if (it) View.VISIBLE else View.GONE
            })
        }

        with(productViewModel) {
            productDetails.observe(requireActivity()) { productDetails ->
                productDetails.success?.let { it ->
                    if (it) {
                        var sizeJson = ""
                        productDetails?.data?.list?.let { sizeJson = Gson().toJson(it[0].sizes) }
                        availableSize = Gson().fromJson(sizeJson, Array<TshirtSize>::class.java)
                            .toList() as ArrayList

                        var tshirtList =
                            viewModel.cartResponse.value?.data?.list?.get(0)?.tshirt_list

                        binding.apply {
                            recyclerViewTShirtList.layoutManager =
                                LinearLayoutManager(requireContext())
                            recyclerViewTShirtList.adapter = FreeTShirtLisAdapter(
                                tshirtList as ArrayList,
                                availableSize ?: arrayListOf()
                            )
                            (recyclerViewTShirtList.adapter as FreeTShirtLisAdapter).onItemChangedListener =
                                object : FreeTShirtLisAdapter.OnItemChangedListener {
                                    override fun onQtyChanged() {
                                        updatePriceInformation()
                                    }

                                    override fun onRemoveItemFromTShirt() {
                                        binding.cardTShirtView.visibility = View.GONE
                                        binding.imgRedeem.visibility = View.VISIBLE
                                    }
                                }
                            updatePriceInformation()
                        }

                    } else {
                        productDetails.message?.let { msg -> onToast(msg, requireActivity()) }
                    }
                }
            }
        }
    }

    private fun startPayment(orderID: String) {

        if (activity != null) {
            (activity as PhotographerDashboardActivity).initiateRazorPay(
                totalAmount, addressElement,
                STANDEE,
                orderID
            )
        }

        /* val checkout = Checkout()
         checkout.setKeyID(Constant.RAZOR_PAY_ID)
         checkout.setImage(R.drawable.splash_logo);

         try {
             val options = JSONObject()
             options.put("name", getString(R.string.app_name))
             options.put("currency", "INR")
             //options.put("order_id", data?.id)
             options.put("amount", totalAmount.times(100))

             val jsonObject = JSONObject()
             jsonObject.put("email", SharedPrefsHelper.read(SharedPrefConstant.USER_EMAIL))
             jsonObject.put("contact", SharedPrefsHelper.read(SharedPrefConstant.MOBILE_NUMBER))
             options.put("prefill", jsonObject)

             checkout.open(requireActivity(), options)

         } catch (e: Exception) {
             onToast("Error in payment: " + e.message, requireActivity())
             e.printStackTrace()
         }*/
    }

    private fun updateAddress(address: AddressElement) {
        addressElement = address
        address.let {
            binding.btnAddAddress.hide()
            binding.cardAddressView.show()
            viewModel.address =
                "${it.flat_no}, ${it.address}, ${it.city}, ${it.state}, ${it.pincode}"
            binding.apply {
                tvDeliverTo.text =
                    "${it.user_profile?.name}\n${it.user_profile?.mobile}\n${viewModel.address}\n${it.landmark}"
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            val toolbar = binding.toolbarMyCart
            val backBtn = toolbar.backBtn
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            val titleTxt = toolbar.tvTitle
            titleTxt.text = activity?.getString(R.string.my_cart)

            btnAddAddress.setOnClickListener {
                if (requireActivity().isInternetAvailable()) {
                    //findNavController().navigate(R.id.action_move_to_manage_address)
                    findNavController().navigate(R.id.action_fragmentMyCart_to_photographerManageAddressFragment)
                } else {
                    onToast(getString(R.string.internet_check), requireActivity())
                }
            }
            btnChangeAddress.setOnClickListener {
                if (requireActivity().isInternetAvailable()) {
                    // findNavController().navigate(R.id.action_move_to_manage_address)
                    findNavController().navigate(R.id.action_fragmentMyCart_to_photographerManageAddressFragment)
                } else {
                    onToast(getString(R.string.internet_check), requireActivity())
                }
            }
            imgRedeem.setOnClickListener {
                showCustomizeShirtDialog()
            }
            btnPlaceOrder.setOnClickListener {
                if (validate()) {
                    if (requireActivity().isInternetAvailable()) {
                        viewModel.createOrder(qrID ?: "")
                    } else onToast(getString(R.string.internet_check), requireActivity())
                }
            }

            tvOrderNow.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMyCart_to_fragmentNavigation_standee_intro)
            }
        }
    }

    private fun showCustomizeShirtDialog() {

        //findNavController().navigate(R.id.action_fragmentMyCart_to_photographerCustomizeShirtFragment)
        dialogBuilder = AlertDialog.Builder(requireActivity())
        val dialogView: View = layoutInflater.inflate(R.layout.customize_shirt_dialog, null)
        dialogBuilder?.setView(dialogView)
        dialogBuilder?.setCancelable(true)

        val tvCustomize = dialogView.findViewById<TextView>(R.id.tvCustomize)
        val imgClose = dialogView.findViewById<ImageView>(R.id.imgClose)

        alertDialog = dialogBuilder?.create()
        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog?.show()

        tvCustomize.setOnClickListener {
            binding.imgRedeem.visibility = View.GONE
            binding.cardTShirtView.visibility = View.VISIBLE
            alertDialog?.dismiss()
            viewModel.isTShirtViewShown = true
            findNavController().navigate(R.id.action_fragmentMyCart_to_photographerCustomizeShirtFragment)
        }

        imgClose.setOnClickListener { v: View? -> alertDialog?.dismiss() }
    }

    private fun validate(): Boolean {
        var status = true
        if (viewModel.address.isNullOrEmpty()) {
            onToast(getString(R.string.label_select_address), requireActivity())
            status = false
        } else if (viewModel.amountDetails.total_amount == 0.0) {
            onToast(getString(R.string.label_some_thing_went_wrong), requireActivity())
            status = false
        }
        return status
    }

    private fun updatePriceInformation() {
        viewModel.cartResponse.value?.data?.list?.let {
            val tax = 18
            if (it.isNotEmpty()) {
                it[0].standee_list?.let { standeeList ->
                    viewModel.amountDetails.apply {
                        total_units = standeeList.sumOf { p -> p.quantity }
                        sub_total = standeeList.sumOf { p -> p.price }.toDouble() * total_units
                        gst_tax = (sub_total / 100 * tax)
                        total_amount = sub_total + gst_tax + shipping
                        totalAmount = total_amount
                    }
                }
                /*it[0].tshirt_list?.let { tshirtList ->
                    viewModel.amountDetails.apply {
                        total_units = tshirtList.sumOf { p ->
                            ((p.sizes ?: arrayListOf()) as ArrayList).sumOf { s -> s.quantity }
                        }
                        sub_total = tshirtList.sumOf { p -> p.price }.toDouble() * total_units
                        gst_tax = (sub_total / 100 * tax)
                        total_amount = sub_total + gst_tax + shipping
                        totalAmount = total_amount
                    }
                }*/
            }
            binding.apply {
                viewModel.amountDetails.let { amount ->
                    tvSubTotal.text = amount.sub_total.amount(requireActivity())
                    tvTax.text = amount.gst_tax.amount(requireActivity())
                    tvShipping.text = amount.shipping.amount(requireActivity())
                    tvAmountPayable.text = amount.total_amount.amount(requireActivity())
                }
            }
        }
    }

    private fun removeStandeeConfirmation() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialog)
        val dialogBinding = DialogRemoveStandeeCartBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            cardNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            cardYes.setOnClickListener {
                bottomSheetDialog.dismiss()
                viewModel.removeCart()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().popBackStack()
                }, 2000)

            }
        }
        bottomSheetDialog.show()
    }
}
