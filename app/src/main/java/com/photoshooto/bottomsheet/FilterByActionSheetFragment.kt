package com.photoshooto.bottomsheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.databinding.FilterbyActionSheetBinding
import com.photoshooto.domain.model.CategoryModel
import com.photoshooto.domain.model.LocationFilter
import com.photoshooto.ui.job.utility.GetCurrentLocation
import com.photoshooto.ui.job.utility.openPermissionSettings
import com.photoshooto.ui.job.utility.toastError
import com.photoshooto.ui.usedPhotographyScreens.adapter.LocationFilterAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.ProductFilterAdapter
import com.photoshooto.util.RangeSeekbar.DoubleValueSeekBarView
import com.photoshooto.util.RangeSeekbar.OnDoubleValueSeekBarChangeListener
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.concurrent.schedule

@Suppress("UNCHECKED_CAST")
class FilterByActionSheetFragment : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    lateinit var binding: FilterbyActionSheetBinding

    private lateinit var locationListAdapter: LocationFilterAdapter
    private lateinit var productListAdapter: ProductFilterAdapter

    var selectCategory: ArrayList<String> = ArrayList()
    var selectLocation: ArrayList<String> = ArrayList()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val dialog2 = dialog as BottomSheetDialog
            dialog.setCancelable(true)

            val bottomSheet =
                dialog2.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).setState(BottomSheetBehavior.STATE_EXPANDED)
            bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        }
        return bottomSheetDialog
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilterbyActionSheetBinding.inflate(inflater, container, false)


        val typefaceRegular = requireContext().resources.getFont(R.font.poppins_regular)
        val typefaceMedium = requireContext().resources.getFont(R.font.poppins_medium)


        val formatter: NumberFormat = DecimalFormat("#,##,###")

        binding.rangeSeekbar.setOnRangeSeekBarViewChangeListener(object :
            OnDoubleValueSeekBarChangeListener {
            override fun onValueChanged(
                seekBar: DoubleValueSeekBarView?,
                min: Int,
                max: Int,
                fromUser: Boolean
            ) {

                val minPrice = if (min == 0) 500 else min

                val minPriceFormat = formatter.format(minPrice.toDouble()).toString()
                val maxPriceFormat = formatter.format(max.toDouble()).toString()

                startPrice = minPrice.toString()
                endPrice = max.toString()

                binding.tvRangePrice.text = "₹${minPriceFormat} - ₹${maxPriceFormat.toString()}"

            }

            override fun onStartTrackingTouch(
                seekBar: DoubleValueSeekBarView?,
                min: Int,
                max: Int
            ) {
            }

            override fun onStopTrackingTouch(
                seekBar: DoubleValueSeekBarView?,
                min: Int,
                max: Int
            ) {
            }
        })

        binding.tvLocation.setOnClickListener {
            binding.tvLocation.setTypeface(typefaceMedium)
            binding.tvProduct.setTypeface(typefaceRegular)
            binding.tvPrice.setTypeface(typefaceRegular)

            binding.tvLocation.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvProduct.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )
            binding.tvPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )

            binding.rvLocation.isVisible = true
            binding.imgLocation.isVisible = true
            binding.rlSearch.isVisible = true

            binding.llPrice.isVisible = false
            binding.rvProduct.isVisible = false

            binding.etSearch.setText("")
            binding.etSearch.setHint(getString(R.string.search))
        }

        binding.tvProduct.setOnClickListener {

            binding.tvLocation.setTypeface(typefaceRegular)
            binding.tvProduct.setTypeface(typefaceMedium)
            binding.tvPrice.setTypeface(typefaceRegular)

            binding.tvLocation.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )
            binding.tvProduct.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvPrice.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )

            binding.rvLocation.isVisible = false
            binding.imgLocation.isVisible = false
            binding.rlSearch.isVisible = true

            binding.rvProduct.isVisible = true

            binding.llPrice.isVisible = false

            binding.etSearch.setText("")
            binding.etSearch.setHint(getString(R.string.search))
        }

        binding.tvPrice.setOnClickListener {

            binding.tvLocation.setTypeface(typefaceRegular)
            binding.tvProduct.setTypeface(typefaceRegular)
            binding.tvPrice.setTypeface(typefaceMedium)

            binding.tvLocation.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )
            binding.tvProduct.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.neutralGrey
                )
            )
            binding.tvPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            binding.rvLocation.isVisible = false
            binding.imgLocation.isVisible = false
            binding.rvProduct.isVisible = false
            binding.rlSearch.isVisible = false

            binding.llPrice.isVisible = true

            binding.etSearch.setText("")
            binding.etSearch.setHint(getString(R.string.search))
        }


        locationListAdapter = LocationFilterAdapter(onTab = { data, index ->

            locationsList.set(index, data)


        })


        productListAdapter = ProductFilterAdapter(onTab = { data, index ->

            categoryList.set(index, data)

        })

        binding.rvLocation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProduct.layoutManager = LinearLayoutManager(requireContext())

        binding.rvLocation.adapter = locationListAdapter
        binding.rvProduct.adapter = productListAdapter



        productListAdapter.swapList(categoryList)

        locationListAdapter.swapList(locationsList)


        binding.etSearch.doOnTextChanged { text, start, count, after ->
            if (binding.rvProduct.isVisible) {
                productListAdapter.filter.filter(text)
            } else {
                locationListAdapter.filter.filter(text)
            }
        }

        binding.imgLocation.setOnClickListener {

            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()

        }

        binding.tvCancer.setOnClickListener {

            onCancer()
            dismiss()
        }

        binding.tvClear.setOnClickListener {

            onClear()
            dismiss()
        }

        binding.btnApply.setOnClickListener {

            selectCategory.clear()

            categoryList.forEachIndexed { index, categoryModel ->
                if (categoryModel.select) {
                    selectCategory.add(categoryModel.name)
                }
            }

            selectLocation.clear()

            locationsList.forEachIndexed { index, categoryModel ->
                if (categoryModel.select) {
                    selectLocation.add(categoryModel.name)
                }
            }

            dismissSheet()

        }

        return binding.root
    }

    private fun permissionRationaleMultiLocation(
        permissions: Array<PermissionBean>, token: PermissionToken
    ) {
        token.continuePermissionRequest()
    }

    private fun inCaseOfErrorLocation(e: Exception) {
        requireContext().toastError("Error for permission : " + e.message)
    }

    private fun permissionResultMultiLocation(
        permissions: Array<PermissionBean>
    ) {
        val isAllPermanentlyDenied = permissions.all {
            it.isPermanentlyDenied
        }
        if (isAllPermanentlyDenied) {

            val snackbar: Snackbar = Snackbar.make(
                binding.root,
                getString(R.string.permission_always_denied),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                activity?.openPermissionSettings()
            }
            snackbar.show()
            return
        }
        val isAllPermissionGranted = permissions.all {
            it.isGranted
        }

        if (isAllPermissionGranted) {
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                // latitude = userLatitude
                // longitude = userLongitude
                try {
                    binding.etSearch.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress =
                            (address[0].featureName) + ", " + (address[0].locality) + ", " + (address[0].adminArea)
                    }
                    binding.etSearch.setText(googleAddress)

                    binding.etSearch.setSelection(
                        binding.etSearch.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun dismissSheet() {

        Timer().schedule(500) {
            onProductTap(selectCategory, selectLocation, startPrice, endPrice)

            dismiss()
        }
    }

    companion object {
        private lateinit var onProductTap: (selectCategory: ArrayList<String>, selectLocation: ArrayList<String>, startPrice: String, endPrice: String) -> Unit
        private lateinit var onCancer: () -> Unit
        private lateinit var onClear: () -> Unit
        var categoryList: ArrayList<CategoryModel> = ArrayList()
        var locationsList: ArrayList<LocationFilter> = ArrayList()
        var startPrice: String = ""
        var endPrice: String = ""

        fun newInstance(
            categoryList: ArrayList<CategoryModel>,
            locationsList: ArrayList<LocationFilter>,

            onProductTap: (selectCategory: ArrayList<String>, selectLocation: ArrayList<String>, startPrice: String, endPrice: String) -> Unit = { selectCategory: ArrayList<String>, selectLocation: ArrayList<String>, startPrice: String, endPrice: String -> },
            onCancer: () -> Unit = {},
            onClear: () -> Unit = {},
        ): FilterByActionSheetFragment {

            val f = FilterByActionSheetFragment()
            Companion.onProductTap = onProductTap
            Companion.onCancer = onCancer
            Companion.onClear = onClear
            Companion.categoryList = categoryList
            Companion.locationsList = locationsList

            return f
        }
    }
}
