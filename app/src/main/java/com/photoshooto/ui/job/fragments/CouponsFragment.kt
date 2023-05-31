package com.photoshooto.ui.job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.databinding.FragmentCouponsBinding
import com.photoshooto.domain.model.CouponModel
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.adapters.CouponListAdapter
import com.photoshooto.ui.job.utility.SpacesItemDecoration
import com.photoshooto.ui.job.utility.gone
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.job.utility.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

class CouponsFragment : BaseFragment() {

    lateinit var binding: FragmentCouponsBinding
    private val jobHomeViewModel: JobViewModel by viewModel()
    private lateinit var couponListAdapter: CouponListAdapter
    var dataList = arrayListOf<CouponModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCouponsBinding.inflate(inflater, container, false)
        val toolbarBinding = binding.toolbarCoupon
        toolbarBinding.tvTitle.text = "Apply Coupons"
        toolbarBinding.backBtn.setSafeOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataList.clear()

        couponListAdapter = CouponListAdapter {

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = couponListAdapter

            itemAnimator = null

            addItemDecoration(
                SpacesItemDecoration(
                    space = resources.getDimension(R.dimen.dp10).toInt(),
                    isGridLayoutManager = false,
                )
            )
        }

        couponListAdapter.submitList(dataList)

        if (dataList.isEmpty()) {
            binding.empty.visible()
            binding.textEmpty.visible()
            binding.textEmpty.text = getString(R.string.no_coupons_found)
        } else {
            binding.empty.gone()
            binding.textEmpty.gone()
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
        /*showProgress()
        jobHomeViewModel.getCoupons()*/
    }
}