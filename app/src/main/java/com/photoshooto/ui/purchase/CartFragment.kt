package com.photoshooto.ui.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.FragmentCartBinding
import com.photoshooto.domain.adapter.CartProductAdapter
import com.photoshooto.domain.model.AddressElement
import com.photoshooto.domain.usecase.cart.CartDetailsViewModel
import com.photoshooto.domain.usecase.payment_success.PaymentSuccessViewModel
import com.photoshooto.domain.usecase.product_details.TshirtSize
import com.photoshooto.ui.manage_address.SelectAddressFragment
import com.photoshooto.ui.photographersScreens.photographerDashboard.PhotographerDashboardActivity
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartDetailsViewModel by viewModel()
    private var totalAmount = 0.0
    private val paymentSuccessViewModel: PaymentSuccessViewModel by activityViewModels()
    var addressElement = AddressElement("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
        setFragmentResultListener(ApplyCouponFragment.REQUEST_APPLY_COUPON) { requestKey, bundle ->
            viewModel.selectedCouponCode = bundle.getString(KEY_COUPON_CODE)
            viewModel.selectedCouponCode?.let {
                binding.apply {
                    groupAfterApplyCoupon.show()
                    groupPreApplyCoupon.hide()
                    tvCouponRemove.show()
                    tvCouponCode.text = it
                }
            }
        }

        setFragmentResultListener(SelectAddressFragment.REQUEST_ADDRESS_CHANGE) { requestKey, bundle ->
            val result = bundle.getString(KEY_DATA)
            if (!result.isNullOrEmpty()) {
                val address = Gson().fromJson(result, AddressElement::class.java)
                updateAddress(address)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val sizeJson = requireArguments().getString("productAvailableSize")
        viewModel.availableSize =
            Gson().fromJson(sizeJson, Array<TshirtSize>::class.java).toList() as ArrayList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()

        if (viewModel.addressListResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                viewModel.getUserAddress()
                viewModel.getCartDetails()
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        } else initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            messageData.observe(requireActivity()) {
                onToast(it, requireContext())
            }

            createOrderResponse.observe(requireActivity()) { result ->
                result.success?.let {
                    if (it) {
                        var orderId = ""
                        if (result.data != null) {
                            orderId = result.data.id.toString()
                        }
                        startPayment(orderId)
                    } else result.message?.let { msg -> onToast(msg, requireContext()) }
                }
            }
            cartResponse.observe(requireActivity()) { result ->
                if (result.success) {
                    result.data?.list?.let {
                        if (it.isNotEmpty()) {
                            it[0].tshirt_list?.let { tshirtList ->
                                binding.apply {
                                    recyclerViewProductList.layoutManager =
                                        LinearLayoutManager(requireContext())
                                    recyclerViewProductList.adapter = CartProductAdapter(
                                        tshirtList as ArrayList,
                                        availableSize ?: arrayListOf()
                                    )
                                    (recyclerViewProductList.adapter as CartProductAdapter).onItemChangedListener =
                                        object : CartProductAdapter.OnItemChangedListener {
                                            override fun onQtyChanged() {
                                                updatePriceInformation()
                                            }
                                        }
                                    updatePriceInformation()
                                }
                            }
                        }
                    }
                } else {
                    onToast(result.message, requireContext())
                }
            }

            addressListResponse.observe(requireActivity()) { result ->
                if (result.success) {
                    result.data?.list?.let { addressList ->
                        if (addressList.any { a -> a.is_default }) {
                            addressList[0].let {
                                updateAddress(it)
                            }
                        } else {
                            binding.btnAddAddress.show()
                            binding.cardAddressView.hide()
                        }
                    }
                } else {
                    binding.btnAddAddress.show()
                    binding.cardAddressView.hide()
                    onToast(result.message, requireContext())
                }
            }
        }
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

    private fun updatePriceInformation() {
        viewModel.cartResponse.value?.data?.list?.let {
            val tax = 18
            if (it.isNotEmpty()) {
                it[0].tshirt_list?.let { tshirtList ->
                    viewModel.amountDetails.apply {
                        total_units = tshirtList.sumOf { p ->
                            ((p.sizes ?: arrayListOf()) as ArrayList).sumOf { s -> s.quantity }
                        }
                        sub_total = tshirtList.sumOf { p -> p.price }.toDouble() * total_units
                        gst_tax = (sub_total / 100 * tax)
                        total_amount = sub_total + gst_tax + shipping
                        totalAmount = total_amount
                    }
                }
            }
            binding.apply {
                viewModel.amountDetails.let { amount ->
                    tvTotalUnits.text = amount.total_units.toString()
                    tvSubTotal.text = amount.sub_total.amount(requireContext())
                    tvTax.text = amount.gst_tax.amount(requireContext())
                    tvShipping.text = amount.shipping.amount(requireContext())
                    tvAmountPayable.text = amount.total_amount.amount(requireContext())
                }
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            cardCouponView.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    val bundle = bundleOf(KEY_COUPON_CODE to (viewModel.selectedCouponCode ?: ""))
                    //findNavController().navigate(CartFragmentDirections.actionApplyCoupon())
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnAddAddress.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    //findNavController().navigate(CartFragmentDirections.actionMoveToManageAddress())
                    findNavController().navigate(R.id.action_cartFragment_to_photographerManageAddressFragment)
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            btnChangeAddress.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    //findNavController().navigate(CartFragmentDirections.actionMoveToManageAddress())
                    findNavController().navigate(R.id.action_cartFragment_to_photographerManageAddressFragment)
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            tvCouponRemove.setOnClickListener {
                viewModel.selectedCouponCode = null
                groupAfterApplyCoupon.hide()
                groupPreApplyCoupon.show()
                it.hide()
            }
            btnPlaceOrder.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    if (validateOrder()) {
                        viewModel.createOrder(requireContext())
                    }
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
        }
    }

    private fun validateOrder(): Boolean {
        var status = true
        if (viewModel.address.isNullOrEmpty()) {
            status = false
            onToast(getString(R.string.label_select_address), requireContext())
        }
        return status
    }

    private fun startPayment(orderId: String) {

        if (activity != null) {
            (activity as PhotographerDashboardActivity).initiateRazorPay(
                totalAmount, addressElement,
                T_SHIRT,
                orderId
            )
        }
        /*  val checkout = Checkout()
          checkout.setKeyID(Constant.RAZOR_PAY_ID)
          checkout.setImage(R.drawable.splash_logo);

          try {
              val options = JSONObject()
              options.put("name", getString(R.string.app_name))
              options.put("currency", "INR")
  //            options.put("order_id", data?.id)
              options.put("amount", totalAmount.times(100))

              val jsonObject = JSONObject()
              jsonObject.put("email", SharedPrefsHelper.read(SharedPrefConstant.USER_EMAIL))
              jsonObject.put("contact", SharedPrefsHelper.read(SharedPrefConstant.MOBILE_NUMBER))
              options.put("prefill", jsonObject)

              checkout.open(requireActivity(), options)

          } catch (e: Exception) {
              onToast("Error in payment: " + e.message, requireContext())
              e.printStackTrace()
          }*/
    }

}
