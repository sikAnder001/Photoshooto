package com.photoshooto.ui.usedPhotographyScreens.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.bottomsheet.FilterByActionSheetFragment
import com.photoshooto.bottomsheet.SortByActionSheetFragment
import com.photoshooto.databinding.FragmentPhotographyProductsBinding
import com.photoshooto.domain.model.CategoryModel
import com.photoshooto.domain.model.LocationFilter
import com.photoshooto.domain.model.User
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.*
import com.photoshooto.ui.usedPhotographyScreens.adapter.AllCategoryListAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.ProductsCategoryListAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.UsedProductsListAdapter
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import org.koin.androidx.viewmodel.ext.android.viewModel


class PhotographyProductsFragment : BaseFragment() {

    lateinit var binding: FragmentPhotographyProductsBinding

    private val jobHomeViewModel: JobViewModel by viewModel()

    var user: User? = null
    var isloading: Boolean = false
    var isSortByKey: String = "2"
    var hasMoreData: Boolean = true
    var currentPage = 0
    var startAmount = ""
    var endAmount = ""

    private lateinit var productsCategoryListAdapter: ProductsCategoryListAdapter
    private lateinit var usedProductsListAdapter: UsedProductsListAdapter
    private lateinit var allCategoryListAdapter: AllCategoryListAdapter
    val categoryList: ArrayList<CategoryModel> = ArrayList()
    val locationsList: ArrayList<LocationFilter> = ArrayList()
    var selectCategory: ArrayList<String> = ArrayList()
    var selectLocation: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        if (::binding.isInitialized) {
            return binding.root
        }
        // Inflate the layout for this fragment
        binding = FragmentPhotographyProductsBinding.inflate(inflater, container, false)

        binding.tvTitle.text = getString(R.string.used_photography_products)

        binding.imgBack.setOnClickListener {

            if (binding.llAllCategory.isVisible) {

                binding.llProducts.isVisible = true
                binding.llAllCategory.isVisible = false

            } else {
                findNavController().popBackStack()
            }
        }

        binding.imgFilter.setOnClickListener {

            filterByBottomSheet()
        }

        binding.imgSort.setOnClickListener {

            sortByBottomSheet()
        }

        binding.etSearch.doOnTextChanged { text, start, count, after ->

            currentPage = 0
            isloading = true

            binding.llProducts.isVisible = true
            binding.llAllCategory.isVisible = false

            val data: MutableMap<String, String> = HashMap()
            data["limit"] = AppConstant.pagination_limit.toString()
            data["page"] = currentPage.toString()
            data["query"] = text.toString()

            jobHomeViewModel.getproductsListing(url = data)
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

        currentPage = 0

        binding.btnUsedEquipments.setOnClickListener {
            findNavController().navigate(
                PhotographyProductsFragmentDirections.actionPhotographyProductsToPost1()
            )

        }

        productsCategoryListAdapter = ProductsCategoryListAdapter(onSelectCategory = { d ->
            if (d.name.equals("View All")) {
                binding.llProducts.isVisible = false
                binding.llAllCategory.isVisible = true
            } else {
                currentPage = 0
                isloading = true
                val data: MutableMap<String, String> = HashMap()
                data["limit"] = AppConstant.pagination_limit.toString()
                data["page"] = currentPage.toString()
                data["category"] = d.name

                jobHomeViewModel.getproductsListing(url = data)
            }
        })

        usedProductsListAdapter = UsedProductsListAdapter(onClick = { d ->

            findNavController().navigate(
                PhotographyProductsFragmentDirections.actionPhotographyProductsToPostDetails(
                    d.id,
                    true
                )
            )

        })

        allCategoryListAdapter = AllCategoryListAdapter(onSelectCategory = { d ->
            binding.llProducts.isVisible = true
            binding.llAllCategory.isVisible = false

            currentPage = 0
            isloading = true

            val data: MutableMap<String, String> = HashMap()
            data["limit"] = AppConstant.pagination_limit.toString()
            data["page"] = currentPage.toString()
            data["category"] = d.name

            jobHomeViewModel.getproductsListing(url = data)
        })

        binding.rvUsedProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addOnScrollListener(object : EndlessPaginationScrollListener() {
                override fun requestNewPage() {
                    super.requestNewPage()
                    if (!isloading && hasMoreData) {
                        currentPage
                        //currentPage++
                        showProgress()
                        loadData()
                    }
                }
            })
        }
        binding.rvAllCategory.layoutManager = GridLayoutManager(requireContext(), 2)


        binding.rvProductsCategory.layoutManager = GridLayoutManager(requireContext(), 5)

        binding.rvUsedProducts.adapter = usedProductsListAdapter
        binding.rvProductsCategory.adapter = productsCategoryListAdapter
        binding.rvAllCategory.adapter = allCategoryListAdapter


        attachObserver()

        loadData()

        loadCategory()

        loadLocation()

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
            showProgress()
            GetCurrentLocation.getLastLocation(requireContext()) { userLatitude, userLongitude, address ->
                hideProgress()
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

    private fun attachObserver() {
        jobHomeViewModel.productsList.observe(viewLifecycleOwner) { data ->
            hideProgress()

            isloading = false
            if (data.success) {
                data.data.let { data ->
                    if (data?.list?.isNotEmpty() == true) {
                        if (data.page == 0) {
                            usedProductsListAdapter.swapList(null, true)
                        }
                        usedProductsListAdapter.swapList(data.list, true)
                    } else {
                        usedProductsListAdapter.swapList(data?.list, true)
                        requireContext().toastError("No records found")

                        hasMoreData = false
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.categoryList.observe(viewLifecycleOwner) { data ->

            val list: ArrayList<CategoryModel> = ArrayList()

            if (data.success) {
                data.data.let { data ->

                    list.clear()
                    categoryList.clear()

                    if (data?.list?.isNotEmpty() == true) {
                        if (data.page == 0) {
                            productsCategoryListAdapter.swapList(data.list)
                            allCategoryListAdapter.swapList(data.list)
                        }
                        allCategoryListAdapter.swapList(data.list)

                        list.addAll(data.list)
                        categoryList.addAll(data.list)

                        val categoryModel = CategoryModel()
                        categoryModel.id = "0"
                        categoryModel.name = requireContext().getString(R.string.view_all)

                        if (list.size >= 4) {

                            val item = list.subList(0, 4)
                            item.add(categoryModel)

                            productsCategoryListAdapter.swapList(item)

                        } else {
                            list.add(categoryModel)
                            productsCategoryListAdapter.swapList(list)
                        }
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

        jobHomeViewModel.locationList.observe(viewLifecycleOwner) { data ->

            if (data.success) {
                data.data.let { data ->

                    locationsList.clear()

                    data.forEachIndexed { index, s ->

                        val locationFilter = LocationFilter()
                        locationFilter.name = s
                        locationsList.add(locationFilter)
                    }
                }
            } else {
                requireContext().toastError(data.message.toString())
            }
        }

    }

    /*override fun onResume() {
        super.onResume()
        loadData()
    }*/


    private fun loadData() {
        isloading = true
        if (currentPage == 0) showProgress()

        val data: MutableMap<String, String> = HashMap()
        data["limit"] = AppConstant.pagination_limit.toString()
        data["page"] = currentPage.toString()

        jobHomeViewModel.getproductsListing(url = data)
    }

    private fun loadCategory() {

        jobHomeViewModel.categoryListing()
    }

    private fun loadLocation() {

        jobHomeViewModel.locationListing()
    }


    private fun sortByBottomSheet() {
        val sheet = SortByActionSheetFragment.newInstance(isSortByKey, onTap = {

            currentPage = 0
            isloading = true
            isSortByKey = it

            val data: MutableMap<String, String> = HashMap()
            data["limit"] = AppConstant.pagination_limit.toString()
            data["page"] = currentPage.toString()


            if (it.equals("0", true)) {
                data["sort_by"] = "created_at"
                data["order"] = it

            } else if (it.equals("1", true) || it.equals("-1", true)) {
                data["sort_by"] = "price"
                data["order"] = it
            } else {

            }

            jobHomeViewModel.getproductsListing(url = data)

        })

        sheet.show(childFragmentManager, "")
    }

    private fun filterByBottomSheet() {

        categoryList.forEachIndexed { index, categoryModel ->

            if (selectCategory.contains(categoryModel.name)) {
                categoryModel.select = true
                categoryList.set(index, categoryModel)
            } else {
                categoryModel.select = false
                categoryList.set(index, categoryModel)
            }
        }

        locationsList.forEachIndexed { index, locationFilter ->

            if (selectLocation.contains(locationFilter.name)) {
                locationFilter.select = true
                locationsList.set(index, locationFilter)
            } else {
                locationFilter.select = false
                locationsList.set(index, locationFilter)
            }
        }

        val sheet = FilterByActionSheetFragment.newInstance(
            categoryList,
            locationsList,
            onProductTap = { selectCategoryList, selectLocationList, startPrice, endPrice ->

                startAmount = startPrice
                endAmount = endPrice

                selectLocation.clear()
                selectCategory.clear()
                selectCategory.addAll(selectCategoryList)
                selectLocation.addAll(selectLocationList)


                val selectCategoryData = StringBuilder()
                selectCategory.forEach {
                    if (selectCategoryData.length > 0) {
                        selectCategoryData.append("|")
                    }
                    selectCategoryData.append(it)
                }

                val selectLocationData = StringBuilder()
                selectLocation.forEach {
                    if (selectLocationData.length > 0) {
                        selectLocationData.append("|")
                    }
                    selectLocationData.append(it)
                }

                Log.e("selectCategoryData", "" + selectCategoryData.toString())
                Log.e("selectLocationData", "" + selectLocationData.toString())

                currentPage = 0
                isloading = true

                val data: MutableMap<String, String> = HashMap()
                data["limit"] = AppConstant.pagination_limit.toString()
                data["page"] = currentPage.toString()

                if (selectLocationData.length != 0) {
                    data["seller_location"] = selectLocationData.toString()
                }
                if (selectCategoryData.length != 0) {
                    data["category"] = selectCategoryData.toString()
                }
                if (startAmount.length != 0) {
                    data["start_price"] = startAmount
                }
                if (endAmount.length != 0) {
                    data["end_price"] = endAmount
                }
                Log.e("params", "" + data.toString())

                jobHomeViewModel.getproductsListing(url = data)

            },
            onCancer = {


            },
            onClear = {
                selectCategory.clear()
                selectLocation.clear()

                val data: MutableMap<String, String> = HashMap()
                data["limit"] = AppConstant.pagination_limit.toString()
                data["page"] = currentPage.toString()

                jobHomeViewModel.getproductsListing(url = data)
            })

        sheet.show(childFragmentManager, "")
    }

}
