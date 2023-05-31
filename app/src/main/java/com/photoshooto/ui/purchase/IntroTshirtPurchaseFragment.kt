package com.photoshooto.ui.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.FragmentIntroTshirtPurchaseBinding
import com.photoshooto.domain.adapter.EventTshirtTypesAdapter
import com.photoshooto.domain.adapter.ProductImagePagerAdapter
import com.photoshooto.domain.model.ProductDetailsModel
import com.photoshooto.domain.usecase.product_details.EventTshirtTypesModel
import com.photoshooto.domain.usecase.product_details.ProductDetailsViewModel
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class IntroTshirtPurchaseFragment : Fragment() {

    private lateinit var binding: FragmentIntroTshirtPurchaseBinding
    private val viewModel: ProductDetailsViewModel by viewModel()
    val productDetails = MutableLiveData<ProductDetailsModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroTshirtPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()

        if (viewModel.productDetails.value == null) {
            if (!requireActivity().isInternetAvailable()) {
                onToast(getString(R.string.internet_check), requireActivity())
                findNavController().popBackStack()
            } else {
                viewModel.getTshirtList(10, 0)
            }
        }
    }

    private fun initView() {
        binding.apply {
            with(viewModel) {
                productDetails.observe(requireActivity()) { productDetails ->
                    productDetails.success?.let { it ->
                        if (it) {
                            productDetails.data?.list?.let { productList ->
                                productList[0].let { product ->
                                    binding.apply {
                                        viewpagerImageSlider.adapter = ProductImagePagerAdapter(
                                            product.images ?: arrayListOf()
                                        )
                                        viewpagerImageSlider.clipChildren = false
                                        viewpagerImageSlider.offscreenPageLimit = 3
                                        viewpagerImageSlider.getChildAt(0).overScrollMode =
                                            RecyclerView.OVER_SCROLL_NEVER
                                        val compositePageTransformer = CompositePageTransformer()
                                        compositePageTransformer.addTransformer(
                                            MarginPageTransformer(40)
                                        )
                                        compositePageTransformer.addTransformer { page, position ->
                                            val r = 1 - abs(position)
                                            page.scaleY = 0.85f + r * 0.15f
                                        }
                                        viewpagerImageSlider.setPageTransformer(
                                            compositePageTransformer
                                        )
                                        TabLayoutMediator(indicator, viewpagerImageSlider) { _, _ ->
                                            // Some implementation
                                        }.attach()
                                    }
                                }
                            }
                        } else {
                            productDetails.message?.let { msg -> onToast(msg, requireActivity()) }
                        }
                    }
                }
            }

            recyclerViewTypeOfTshirt.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerViewTypeOfTshirt.adapter = EventTshirtTypesAdapter(
                arrayListOf(
                    EventTshirtTypesModel(R.drawable.img_temp_product_tshirt, "Standard T-Shirt"),
                    EventTshirtTypesModel(R.drawable.ic_customize_blue, "Customize")
                )
            )
            (recyclerViewTypeOfTshirt.adapter as EventTshirtTypesAdapter).onItemClickListener =
                object : EventTshirtTypesAdapter.OnItemClickListener {
                    override fun onItemClick() {
                        if (requireContext().isInternetAvailable()) {
                            //findNavController().navigate(IntroTshirtPurchaseFragmentDirections.actionStartPurchaseFlow())
                            findNavController().navigate(R.id.action_introTshirtPurchaseFragment_to_productDetailsFragment)
                        } else {
                            onToast(getString(R.string.internet_check), requireContext())
                        }
                    }
                }
        }
    }

    private fun clickListener() {
        binding.apply {
            btnCustomizeNow.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    //findNavController().navigate(IntroTshirtPurchaseFragmentDirections.actionStartPurchaseFlow())
                    findNavController().navigate(R.id.action_introTshirtPurchaseFragment_to_productDetailsFragment)
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            ivSideMenu.setOnClickListener {
                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
                    resources.getString(R.string.open_drawer)
                )
            }
        }
    }
}
