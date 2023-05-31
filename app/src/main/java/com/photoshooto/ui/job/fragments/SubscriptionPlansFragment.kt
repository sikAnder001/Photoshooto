package com.photoshooto.ui.job.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentSubscriptionBinding
import com.photoshooto.databinding.LayoutToolbarBackQueryBinding
import com.photoshooto.domain.model.FeaturePlan
import com.photoshooto.domain.model.OrderList
import com.photoshooto.domain.model.OrderSummaryModel
import com.photoshooto.domain.model.SubscribePlan
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.FeatureListAdapter
import com.photoshooto.ui.job.adapters.SubscriptionListAdapter
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefsHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionPlansFragment : BaseFragment() {

    lateinit var binding: FragmentSubscriptionBinding
    lateinit var toolbarBinding: LayoutToolbarBackQueryBinding
    val page = 0
    var userPlanId = ""
    var isMonthSelected = true
    lateinit var promoObj: SubscribePlan
    var orderList = OrderList()
    var planFeatureList = arrayListOf<FeaturePlan>()
    lateinit var plan: SubscribePlan
    var selectedPlan = "Studio"
    var planList = mutableListOf<SubscribePlan>()
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var subscriptionListAdapter: SubscriptionListAdapter
    private lateinit var featureListAdapter: FeatureListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutToolbarBackQueryBinding.bind(binding.root)

        toolbarBinding.title.text = "Upgrade plans"
        toolbarBinding.query.gone()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachObserver()

        userPlanId = SharedPrefsHelper.getUserCommon()?.subscriptions_id ?: ""

        if (userPlanId.isBlank()) {
            binding.cardCurrentPlan.gone()
        } else {
            binding.cardCurrentPlan.visible()
        }

        toolbarBinding.back.setSafeOnClickListener {
            findNavController().popBackStack()
        }

        subscriptionListAdapter = SubscriptionListAdapter {
            plan = it
            selectedPlan = it.type

            setSubscription()
        }

        binding.btnSubmit.text = "Start your 3-Days Free Trial"

        binding.btnSubmit.setSafeOnClickListener {

            if (!isMonthSelected && plan.type.equals("Promo")) {
                requireContext().toastError("Please choose your plan")
                return@setSafeOnClickListener
            }

            orderList.clear()
            orderList.add(
                OrderSummaryModel(
                    orderResourceImg = R.drawable.ic_order_subs,
                    orderName = "${selectedPlan} Subscription",
                    orderType = if (isMonthSelected) "Monthly" else "Yearly",
                    orderPrice = "₹${plan.price}/M",
                    orderRowTotal = if (isMonthSelected) plan.price else plan.yearPrice,
                )
            )

            findNavController().navigate(
                SubscriptionPlansFragmentDirections.actionSubscriptionFragmentToSummaryPage(
                    orderList
                )
            )
        }

        featureListAdapter = FeatureListAdapter {

        }

        binding.recyclerPlan.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = subscriptionListAdapter
            itemAnimator = null
            addItemDecoration(
                HorizontalSpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
        }

        binding.recyclerFeatures.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = featureListAdapter
            itemAnimator = null

        }

        binding.txtMonthly.setSafeOnClickListener {
            setupPlans(0)
        }

        binding.txtYearly.setSafeOnClickListener {
            setupPlans(1)
        }

        val privacySpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Redirect to Privacy Page", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorOrange)
                ds.isUnderlineText = false
            }
        }

        val termSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Redirect to Terms Page", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorOrange)
                ds.isUnderlineText = false
            }
        }

        val spannableString = SpannableString("privacy policy")
        spannableString.setSpan(
            privacySpan, 0, "privacy policy".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val spannableString2 = SpannableString("terms of services")
        spannableString2.setSpan(
            termSpan, 0, "terms of services".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.terms.text = TextUtils.concat(
            "By joining, you agree to our ", spannableString, " and ", spannableString2, "."
        )
        binding.terms.movementMethod = LinkMovementMethod.getInstance()
        binding.terms.highlightColor =
            ContextCompat.getColor(requireContext(), R.color.colorTransparent)


    }

    private fun setupPlans(pos: Int) {

        binding.btnMonthly.visibility = View.INVISIBLE
        binding.btnYearly.visibility = View.INVISIBLE
        binding.txtMonthly.setTextColor(Color.BLACK)
        binding.txtYearly.setTextColor(Color.BLACK)

        when (pos) {
            0 -> {  //month
                isMonthSelected = true
                binding.btnMonthly.visibility = View.VISIBLE
                binding.txtMonthly.setTextColor(Color.WHITE)
                setSubscription()

                subscriptionListAdapter.apply {
                    val tmpList = currentList.toMutableList()
                    tmpList.forEach {
                        it.isYearSelected = false
                    }
                    if (!tmpList.contains(promoObj))
                        tmpList.add(0, promoObj)
                    submitList(null)
                    submitList(tmpList)
                    notifyDataSetChanged()
                }
            }

            1 -> {  //year
                isMonthSelected = false
                binding.btnYearly.visibility = View.VISIBLE
                binding.txtYearly.setTextColor(Color.WHITE)
                setSubscription()

                subscriptionListAdapter.apply {

                    val tmpList = currentList.toMutableList()
                    tmpList.forEach {
                        it.isYearSelected = true
                    }

                    if (tmpList.contains(promoObj))
                        tmpList.remove(promoObj)
                    submitList(null)
                    submitList(tmpList)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun attachObserver() {
        jobHomeViewModel.subscriptions.observe(viewLifecycleOwner) {
            hideProgress()
            binding.recyclerHeader.visible()

            if (it.success) {

                planFeatureList.clear()

                val dataList = it.data.list

                dataList.forEachIndexed { index, subscribePlan ->

                    if (subscribePlan.type.equals(selectedPlan, true)) {
                        subscribePlan.isSelected = true
                        plan = subscribePlan
                    }

                    subscribePlan.yearPrice =
                        subscribePlan.type.getHalfPlanPrice()

                    if (subscribePlan.type.equals("Promo", true)) {
                        promoObj = subscribePlan
                    }

                    if (userPlanId.isNotBlank()) {
                        if (userPlanId.equals(subscribePlan.id, true)) {
                            binding.planName.text = subscribePlan.type
                            binding.price.text = getString(R.string.rs_amount, subscribePlan.price)
                        }
                    }
                }

                if (it.data.page == 0) subscriptionListAdapter.submitList(null)
                subscriptionListAdapter.submitList(subscriptionListAdapter.currentList.plus(dataList))

                planList = (it.data.list).toMutableList()
            } else {
                requireContext().toastError(it.message.toString())
            }

            if (planList.isNotEmpty()) {
                planFeatureList.clear()
                val keySets = planList[0].featureList.keySet()
                keySets.remove("_id")

                keySets.forEach {
                    if (it.equals("total_yearly_savings")) {
                        planFeatureList.add(
                            FeaturePlan(
                                featureId = it,
                                featureName = "Total Savings",
                                plan1 = "", plan2 = "", plan3 = "", plan4 = ""
                            )
                        )
                    } else {
                        planFeatureList.add(
                            FeaturePlan(
                                featureId = it,
                                featureName = it.snakeToTitleCase(),
                                plan1 = "", plan2 = "", plan3 = "", plan4 = ""
                            )
                        )
                    }
                }
            }

            if (isMonthSelected)
                setupPlans(0)
            else
                setupPlans(1)
        }
    }

    private fun setSubscription() {
        if (planList.size == 4) {

            binding.apply {
                plan1Name.text = planList[0].type
                plan2Name.text = planList[1].type
                plan3Name.text = planList[2].type
                plan4Name.text = planList[3].type

                plan1Price.text = if (planList[0].price.equals("0")) {
                    "Free"
                } else {
                    planList[0].price
                }

                if (isMonthSelected) {
                    plan1Name.visible()
                    plan1Price.visible()

                    plan2Price.text =
                        "₹${planList[1].price}/M"
                    plan3Price.text =
                        "₹${planList[2].price}/M"
                    plan4Price.text =
                        "₹${planList[3].price}/M"
                } else {
                    plan1Name.gone()
                    plan1Price.gone()

                    plan2Price.text =
                        "₹${planList[1].type.getHalfPlanPrice()}/6M"
                    plan3Price.text =
                        "₹${planList[2].type.getHalfPlanPrice()}/6M"
                    plan4Price.text =
                        "₹${planList[3].type.getHalfPlanPrice()}/6M"
                }
            }

            if (isMonthSelected) {

                binding.bgView1.gone()
                binding.bgView2.gone()
                binding.bgView3.gone()
                binding.bgView4.gone()

                when (selectedPlan) {
                    "Promo" -> {
                        binding.bgView1.visible()
                    }
                    "Studio" -> {
                        binding.bgView3.visible()
                    }
                    "Teams" -> {
                        binding.bgView4.visible()
                    }
                    else -> {
                        binding.bgView2.visible()
                    }
                }

                planFeatureList.forEachIndexed { index, featurePlan ->
                    featurePlan.selectedPlan = selectedPlan
                    featurePlan.plan1Visible = true
                    featurePlan.plan1 =
                        planList[0].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan2 =
                        planList[1].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan3 =
                        planList[2].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan4 =
                        planList[3].featureList.get(featurePlan.featureId).toString()
                }
            } else {

                binding.bgView1.gone()
                binding.bgView2.gone()
                binding.bgView3.gone()
                binding.bgView4.gone()
                when (selectedPlan) {
                    "Studio" -> {
                        binding.bgView3.visible()
                    }
                    "Teams" -> {
                        binding.bgView4.visible()
                    }
                    "Promo" -> {
                        binding.bgView1.gone()
                    }
                    else -> {
                        binding.bgView2.visible()
                    }
                }

                planFeatureList.forEachIndexed { index, featurePlan ->
                    featurePlan.selectedPlan = selectedPlan
                    featurePlan.plan1Visible = false
                    featurePlan.plan1 =
                        planList[0].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan2 =
                        planList[1].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan3 =
                        planList[2].featureList.get(featurePlan.featureId).toString()
                    featurePlan.plan4 =
                        planList[3].featureList.get(featurePlan.featureId).toString()

                    if (!featurePlan.featureId.equals("total_yearly_savings")) {
                        if (featurePlan.plan1.isNumeric()) {
                            featurePlan.plan1 = ((featurePlan.plan1).toInt() * 6).toString()
                        }
                        if (featurePlan.plan2.isNumeric()) {
                            featurePlan.plan2 = ((featurePlan.plan2).toInt() * 6).toString()
                        }
                        if (featurePlan.plan3.isNumeric()) {
                            featurePlan.plan3 = ((featurePlan.plan3).toInt() * 6).toString()
                        }
                        if (featurePlan.plan4.isNumeric()) {
                            featurePlan.plan4 = ((featurePlan.plan4).toInt() * 6).toString()
                        }
                    }
                }
            }

            featureListAdapter.submitList(null)
            Log.e("planFeatureList", "#${planFeatureList}")
            featureListAdapter.submitList(planFeatureList)
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

    private fun initData() {
        showProgress()
        binding.recyclerHeader.invisible()
        jobHomeViewModel.getSubscriptions(page)
    }
}