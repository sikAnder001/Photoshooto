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
import com.photoshooto.databinding.FragmentPaymentOptionBinding
import com.photoshooto.domain.model.PaymentListModel
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.PaymentListAdapter
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentOptionsFragment : Fragment() {

    lateinit var binding: FragmentPaymentOptionBinding
    var payList = arrayListOf<PaymentListModel>()
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var paymentListAdapter: PaymentListAdapter
    private val navArgs: PaymentOptionsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentOptionBinding.inflate(inflater, container, false)

        val toolbarBinding = binding.toolbarPayment
        toolbarBinding.tvTitle.text = getString(R.string.payment_option)
        toolbarBinding.backBtn.setSafeOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentListAdapter = PaymentListAdapter {
            var pgType = it.type
            navArgs.amount

            //todo: to send in PG params prefill.method


            // Sample Data where method can be pass
            /*val checkout = Checkout()
            checkout.setKeyID("rzp_test_OKUlMh04OnYbmc")package com.photoshooto.ui.job.fragments

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
import com.photoshooto.databinding.FragmentPaymentOptionBinding
import com.photoshooto.databinding.LayoutToolbarBinding
import com.photoshooto.domain.model.PaymentListModel
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.PaymentListAdapter
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentOptionsFragment : Fragment() {

    lateinit var binding: FragmentPaymentOptionBinding
    var payList = arrayListOf<PaymentListModel>()
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var paymentListAdapter: PaymentListAdapter
    private val navArgs: PaymentOptionsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentOptionBinding.inflate(inflater, container, false)

        val toolbarBinding = binding.toolbarPayment
        toolbarBinding.tvTitle.text = getString(R.string.payment_option)
        toolbarBinding.backBtn.setSafeOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentListAdapter = PaymentListAdapter {
            var pgType = it.type
            navArgs.amount

            //todo: to send in PG params prefill.method


            // Sample Data where method can be pass
            /*val checkout = Checkout()
            checkout.setKeyID("rzp_test_OKUlMh04OnYbmc")
            val activity: Activity = this
            try {
                val options = JSONObject()
                options.put("name", "TEST")
                options.put("description", "Charge Of Plan")
                options.put("theme.color", "#0B4CFE")
                options.put("send_sms_hash", true)
                options.put("allow_rotation", true)
                options.put("currency", "INR")
                options.put(
                    "amount", ("100").toDouble()
                ) //pass amount in currency subunits
                options.put("prefill.email", "test@example.com")
                options.put("prefill.contact", "7897897897")
                options.put("prefill.method", "card")          <<------    method   card/upi/wallet/netbanking
                checkout.open(activity, options)
            } catch (e: Exception) {
            }*/

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = paymentListAdapter
        }

        payList.clear()

        payList.add(
            PaymentListModel(
                getString(R.string.credit_debit_card), R.drawable.mdi_credit_card_outline, "card", 1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.phonepe_google_pay_bhim_upi), R.drawable.ic_upi, "upi", 1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.wallets),
                R.drawable.mdi_wallet_outline,
                "wallet",
                1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.net_banking),
                R.drawable.ic_nb,
                "netbanking",
                1
            )
        )

        paymentListAdapter.submitList(payList)

    }
}
            val activity: Activity = this
            try {
                val options = JSONObject()
                options.put("name", "TEST")
                options.put("description", "Charge Of Plan")
                options.put("theme.color", "#0B4CFE")
                options.put("send_sms_hash", true)
                options.put("allow_rotation", true)
                options.put("currency", "INR")
                options.put(
                    "amount", ("100").toDouble()
                ) //pass amount in currency subunits
                options.put("prefill.email", "test@example.com")
                options.put("prefill.contact", "7897897897")
                options.put("prefill.method", "card")          <<------    method   card/upi/wallet/netbanking
                checkout.open(activity, options)
            } catch (e: Exception) {
            }*/

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = paymentListAdapter
        }

        payList.clear()

        payList.add(
            PaymentListModel(
                getString(R.string.credit_debit_card), R.drawable.mdi_credit_card_outline, "card", 1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.phonepe_google_pay_bhim_upi), R.drawable.ic_upi, "upi", 1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.wallets),
                R.drawable.mdi_wallet_outline,
                "wallet",
                1
            )
        )
        payList.add(
            PaymentListModel(
                getString(R.string.net_banking),
                R.drawable.ic_nb,
                "netbanking",
                1
            )
        )

        paymentListAdapter.submitList(payList)

    }
}