package com.photoshooto.ui.qrcodegenration

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.photoshooto.R
import com.photoshooto.databinding.DialogUseOfStandeeBinding
import com.photoshooto.databinding.FragmentGenerateEventQrCodeBinding
import com.photoshooto.domain.adapter.ImagePagerAdapter
import com.photoshooto.domain.adapter.QrStandeePagerAdapter
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.ui.dashboard.DashBoardScreenActivity
import com.photoshooto.util.isInternetAvailable
import com.photoshooto.util.onToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.abs

class FragmentQRCodeGeneration : Fragment() {

    private lateinit var binding: FragmentGenerateEventQrCodeBinding
    private val viewModel: QrGenerationViewModel by viewModel()
    var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            standeeResponse.observe(requireActivity()) { result ->
                if (result.success) {
                    result.data?.list?.let { standeeList ->
                        binding.viewPagerTypesOfStandee.adapter = QrStandeePagerAdapter(standeeList)
                        (binding.viewPagerTypesOfStandee.adapter as QrStandeePagerAdapter).onItemClickListener =
                            object : QrStandeePagerAdapter.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    if (requireContext().isInternetAvailable()) {
                                        //findNavController().navigate(R.id.action_move_to_generate_qr)
                                        //   findNavController().navigate(R.id.action_fragmentQRCodeGenerationStandeeIntro_to_fragmentQRCodeProceed)
                                    } else {
                                        onToast(
                                            getString(R.string.internet_check), requireContext()
                                        )
                                    }
                                }
                            }
                    }
                } else {
                    onToast(result.message, requireContext())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenerateEventQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()

        if (viewModel.standeeResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                viewModel.getStandee()
            } else {
                onToast(getString(R.string.internet_check), requireContext())
                findNavController().popBackStack()
            }
        } else {
            initObserver()
        }
    }

    private fun initView() {
        binding.apply {
            viewpagerImageSlider.adapter = ImagePagerAdapter(
                arrayListOf(
                    R.drawable.standee1,
                    R.drawable.standee2,
                    R.drawable.standee3,
                    R.drawable.standee4
                ),
                object : ImagePagerAdapter.OnItemClickListener {
                    override fun onImageClick() {
                    }
                }
            )
//            viewpagerImageSlider.clipChildren = false
            viewpagerImageSlider.offscreenPageLimit = 4
            viewpagerImageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            viewpagerImageSlider.setPageTransformer(compositePageTransformer)
            TabLayoutMediator(indicator, viewpagerImageSlider) { tab, position ->
                // Some implementation
            }.attach()
            setAutoSlider(viewpagerImageSlider, 4)


            howToUseViewPager.adapter = ImagePagerAdapter(
                arrayListOf(
                    R.drawable.how_to_use_1,
                    R.drawable.how_to_use_2,
                    R.drawable.how_to_use_3,
                    R.drawable.how_to_use_4
                ),
                object : ImagePagerAdapter.OnItemClickListener {
                    override fun onImageClick() {
                    }
                }
            )
            howToUseViewPager.offscreenPageLimit = 4
            howToUseViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer1 = CompositePageTransformer()
            compositePageTransformer1.addTransformer(MarginPageTransformer(40))
            compositePageTransformer1.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            howToUseViewPager.setPageTransformer(compositePageTransformer1)
            TabLayoutMediator(howToUseIndicator, howToUseViewPager) { tab, position ->
                // Some implementation
            }.attach()
            //setAutoSlider(howToUseViewPager, 4)

        }

        binding.btnNextStandee.setOnClickListener {

        }

        binding.btnPreviousStandee.setOnClickListener {

        }
    }

    private fun setAutoSlider(viewPager2: ViewPager2, noOfPage: Int) {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == noOfPage) {
                currentPage = 0
            }
            viewPager2.setCurrentItem(currentPage++, true)
        }

        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 1000, 3000)
    }

    private fun clickListener() {
        binding.apply {
            btnGenerateQr.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    //findNavController().navigate(R.id.action_move_to_generate_qr)
                    findNavController().navigate(R.id.action_fragmentQRCodeGenerationStandeeIntro_to_fragmentQRCodeProceed)
                    // findNavController().navigate(R.id.action_move_to_generate_qr)
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            ivSideMenu.setOnClickListener {
                (activity as DashBoardScreenActivity).hanldeSideNavigationClicks(
                    resources.getString(R.string.open_drawer)
                )
            }
            cardUsesOfStandee.setOnClickListener {
                dialogUseOfStandee()
            }
        }
    }

    private fun dialogUseOfStandee() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogUseOfStandeeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
}
