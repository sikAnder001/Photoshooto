package com.photoshooto.ui.usedPhotographyScreens.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.trimmedLength
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.bottomsheet.CategoriesActionSheetFragment
import com.photoshooto.bottomsheet.ItemActionSheetFragment
import com.photoshooto.bottomsheet.ProductActionSheetFragment
import com.photoshooto.bottomsheet.SelectConditionActionSheetFragment
import com.photoshooto.databinding.FragmentPostProductStep01Binding
import com.photoshooto.domain.model.*
import com.photoshooto.ui.dialog.DateTimeDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.util.SharedPrefsHelper
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostProductStep01Fragment : BaseFragment() {


    lateinit var binding: FragmentPostProductStep01Binding
    private val jobHomeViewModel: JobViewModel by viewModel()

    var user: User? = null
    var latitude = 0.0
    var longitude = 0.0
    val mainProducts: ArrayList<BrandModel> = ArrayList()
    val categoryArrayList: ArrayList<CategoryModel> = ArrayList()
    private val navArgs: PostProductStep01FragmentArgs by navArgs()
    var productsModel = ProductsModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        if (::binding.isInitialized) {
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("address")
                ?.observe(viewLifecycleOwner) {
                    Log.e("TAG", it.toString())
                    binding.etLocation.setText(it.toString())
                }
            return binding.root
        }

        binding = FragmentPostProductStep01Binding.inflate(inflater, container, false)


        Log.e("postId", "" + navArgs.postId)

        showProgress()

        user = SharedPrefsHelper.getUserCommon()



        binding.imgPostBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvViewMap.setOnClickListener {

            findNavController().navigate(
                PostProductStep01FragmentDirections.actionPost1ToMap()
            )
        }

        jobHomeViewModel.getBrand()
        jobHomeViewModel.categoryListing()

        getMainProducts()

        getCategoryList()

        binding.btnProceed.setSafeOnClickListener {

            if (!formValidatedSuccess()) {
                return@setSafeOnClickListener
            }

            val postProductsPRQ = PostProductsPRQ(
                name = binding.tvItem.text.toString(),
                brand = binding.tvProduct.text.toString(),
                sellingLocation = binding.etLocation.text.toString(),
                condition = binding.tvCondition.text.toString(),
                purchaseDate = binding.tvDate.text.toString(),
                shutterCount = binding.etShutter.text.toString(),
                category = binding.tvCategories.text.toString(),
                sub_product_brand = binding.tvSubBrand.text.toString(),
                sub_product_name = binding.tvSubItem.text.toString()
            )

            findNavController().navigate(
                PostProductStep01FragmentDirections.actionPost1ToPost2(postProductsPRQ)
                    .setPostProducts(productsModel)
            )

        }

        binding.llBrand.setOnClickListener {

            brandListBottomSheet(mainProducts)
        }

        binding.rtSubBrand.setOnClickListener {

            subBrandListBottomSheet(mainProducts)
        }

        binding.rlItem.setOnClickListener {

            val itemData = mainProducts.find { it.name.equals(binding.tvProduct.text.toString()) }
            itemData?.products?.let { it1 -> itemBottomSheet(it1 as ArrayList<String>) }
        }

        binding.rlSubItem.setOnClickListener {

            val itemData = mainProducts.find { it.name.equals(binding.tvSubBrand.text.toString()) }
            itemData?.products?.let { it1 -> subItemBottomSheet(it1 as ArrayList<String>) }
        }

        binding.rlSelectCondition.setOnClickListener {

            selectConditionBottomSheet()
        }

        binding.rlCategories.setOnClickListener {

            val mainProducts: ArrayList<String> = ArrayList()
            mainProducts.clear()

            categoryArrayList.forEachIndexed { index, categoryModel ->

                mainProducts.add(categoryModel.name)
            }

            mainProducts.add("Others")

            categoryBottomSheet(mainProducts)

        }

        binding.imgLocation.setSafeOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )

            MayI.withActivity(requireActivity()).withPermissions(*permissions)
                .onRationale(this::permissionRationaleMultiLocation)
                .onResult(this::permissionResultMultiLocation)
                .onErrorListener(this::inCaseOfErrorLocation).check()
        }

        binding.rlDate.setOnClickListener {

            DateTimeDialog.showPostDateDialog(requireContext()) { date ->
                val tmp = date.getFormattedDatetime(
                    inputFormat = "yyyy-MM-dd", outputFormat = "dd-MM-yyyy"
                )
                binding.tvDate.text = tmp
            }
        }

        if (navArgs.postId != null) {
            jobHomeViewModel.productsDetail(navArgs.postId.toString())

            getPostData()
        }

        return binding.root
    }

    private fun getPostData() {
        jobHomeViewModel.ProductsModel.observe(viewLifecycleOwner) { data ->
            hideProgress()

            if (data.success) {
                data.data.let { data ->

                    binding.tvProduct.setText(data?.brand)
                    binding.tvItem.setText(data?.name)
                    binding.tvCategories.setText(data?.category)
                    binding.etLocation.setText(data?.sellingLocation)
                    binding.tvCondition.setText(data?.condition)
                    binding.tvDate.setText(data?.purchaseDate)
                    binding.etShutter.setText(data?.shutterCount)

                    if (data != null) {
                        productsModel = data
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }
    }

    private fun getCategoryList() {

        jobHomeViewModel.categoryList.observe(viewLifecycleOwner) { data ->


            if (data.success) {
                data.data.let { data ->

                    categoryArrayList.clear()

                    if (data?.list?.isNotEmpty() == true) {

                        categoryArrayList.addAll(data.list)
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }
    }


    private fun getMainProducts() {

        jobHomeViewModel.brandList.observe(viewLifecycleOwner) { data ->

            if (data.success) {
                data.data.let { data ->

                    data?.list?.let { mainProducts.addAll(it) }

                    val otherData = BrandModel()
                    otherData.id = "0"
                    otherData.name = "Others"
                    mainProducts.add(otherData)

                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun formValidatedSuccess(): Boolean {
        binding.apply {
            if (tvProduct.text.isNullOrBlank()) {
                showSnackBar("Select Brand")
                return false
            } else if (etLocation.text.isNullOrBlank()) {
                showSnackBar("Post Location Required")
                return false
            } else if (tvCondition.text.isNullOrBlank()) {
                showSnackBar("Product Condition Required")
                return false
            } else {
                return true
            }
        }

        return true
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
            showProgress()
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                hideProgress()
                latitude = userLatitude
                longitude = userLongitude
                try {
                    binding.etLocation.setText("")
                    var googleAddress = ""
                    if (address.isNotEmpty()) {
                        googleAddress =
                            (address[0].featureName) + ", " + (address[0].locality) + ", " + (address[0].adminArea)
                    }
                    binding.etLocation.setText(googleAddress)
                    binding.etLocation.setSelection(
                        binding.etLocation.text.toString().trimmedLength()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun brandListBottomSheet(mainProducts: ArrayList<BrandModel>) {
        val sheet = ProductActionSheetFragment.newInstance(mainProducts, onTap = {
            binding.tvProduct.setText(it)

        })

        sheet.show(childFragmentManager, "")
    }

    private fun subBrandListBottomSheet(mainProducts: ArrayList<BrandModel>) {
        val sheet = ProductActionSheetFragment.newInstance(mainProducts, onTap = {
            binding.tvSubBrand.setText(it)
            binding.tvSubBrand.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_054871
                )
            );

        })

        sheet.show(childFragmentManager, "")
    }

    private fun itemBottomSheet(mainProducts: ArrayList<String>) {
        val sheet = ItemActionSheetFragment.newInstance(mainProducts, onTap = {
            binding.tvItem.setText(it)

        })

        sheet.show(childFragmentManager, "")
    }

    private fun subItemBottomSheet(mainProducts: ArrayList<String>) {
        val sheet = ItemActionSheetFragment.newInstance(mainProducts, onTap = {
            binding.tvSubItem.setText(it)
            binding.tvSubItem.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_054871
                )
            );

        })

        sheet.show(childFragmentManager, "")
    }

    private fun categoryBottomSheet(mainProducts: ArrayList<String>) {
        val sheet = CategoriesActionSheetFragment.newInstance(mainProducts, onTap = {
            binding.tvCategories.setText(it)

        })

        sheet.show(childFragmentManager, "")
    }

    private fun selectConditionBottomSheet() {
        val sheet = SelectConditionActionSheetFragment.newInstance(onTap = {

            binding.tvCondition.text = it
        })

        sheet.show(childFragmentManager, "")
    }
}