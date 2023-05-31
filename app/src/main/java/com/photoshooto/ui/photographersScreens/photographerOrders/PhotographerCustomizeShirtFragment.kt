package com.photoshooto.ui.photographersScreens.photographerOrders

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.FragmentPhotographerCustomizeShirtBinding
import com.photoshooto.domain.adapter.ProductImagePagerAdapter
import com.photoshooto.domain.adapter.ProductSizeAdapter
import com.photoshooto.domain.usecase.product_details.ProductDetailsViewModel
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotographerCustomizeShirtFragment : Fragment() {

    private var isPickingUpJpg = false
    private lateinit var binding: FragmentPhotographerCustomizeShirtBinding
    private val viewModel: ProductDetailsViewModel by viewModel()
    private val qrViewModel: QrGenerationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotographerCustomizeShirtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListener()
        initView()

        qrViewModel.getCartDetails()

        if (viewModel.productDetails.value == null) {
            if (!requireActivity().isInternetAvailable()) {
                onToast(getString(R.string.internet_check), requireActivity())
                findNavController().popBackStack()
            } else {
                viewModel.getTshirtList(10, 0)
            }
        }
    }

    private fun initObserver() {
        with(qrViewModel) {
            messageData.observe(
                requireActivity()
            ) {
                onToast(it, requireActivity())
            }

            cartResponse.observe(
                requireActivity()
            ) { result ->
                if (result.success) {
                    result.data?.list?.let {
                        if (it.isNotEmpty()) {
                            it[0].standee_list?.let { standeeList ->
                                binding.apply {
                                    edtStudioName.setText(standeeList[0].studio_name + " ")
                                    if (standeeList[0].studio_tagline != null) {
                                        edtStudioTagline.setText(standeeList[0].studio_tagline + " ")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    onToast(result.message, requireActivity())
                }
            }
        }
        with(viewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            viewModel.apply {
                                if (!isLogoPngUploaded && !isLogoJpgUploaded) {
                                    uploadedPngUrl = response.data?.data?.get(0)?.file_path!!
                                    isLogoPngUploaded = true
                                    processedBuyNow()
                                } else {
                                    uploadedJpgUrl = response.data?.data?.get(0)?.file_path
                                    isLogoJpgUploaded = true
                                    processedBuyNow()
                                }
                            }
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireActivity().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }
            addToCartResponse.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            var sizeJson = ""
                            productDetails.value?.data?.list?.let {
                                sizeJson = Gson().toJson(it[0].sizes)
                            }

                            val bundle = Bundle()
                            bundle.putString("productAvailableSize", sizeJson)
                            findNavController().navigate(
                                R.id.action_productDetailsFragment_to_cartFragment, bundle, null
                            )
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireActivity().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun initView() {
        with(viewModel) {
            productDetails.observe(requireActivity()) { productDetails ->
                productDetails.success?.let { it ->
                    if (it) {
                        productDetails.data?.list?.let { productList ->
                            productList[0].let { product ->
                                binding.apply {
                                    viewPagerProductImages.adapter = ProductImagePagerAdapter(
                                        product.images ?: arrayListOf()
                                    )
                                    TabLayoutMediator(
                                        indicator,
                                        viewPagerProductImages
                                    ) { tab, position ->
                                        // Some implementation
                                    }.attach()
                                    recyclerViewSizeSelector.layoutManager =
                                        LinearLayoutManager(
                                            requireActivity(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )
                                    recyclerViewSizeSelector.adapter =
                                        ProductSizeAdapter(product.sizes ?: arrayListOf())

                                    var productDescription = product.description + "\n"
                                    product.properties?.forEach {
                                        productDescription += ("\n  ● $it")
                                    }
                                    tvDetailsContent.text = productDescription.trim()

                                    if (viewModel.filePng != null) {
                                        ivRemoveImage1.show()
                                        Glide.with(requireActivity()).load(viewModel.filePng)
                                            .into(ivPlaceHolderImage1)
                                    }

                                    if (viewModel.fileJpg != null) {
                                        ivRemoveImage2.show()
                                        Glide.with(requireActivity()).load(viewModel.fileJpg)
                                            .into(ivPlaceHolderImage2)
                                    }
                                }
                            }
                        }
                    } else {
                        productDetails.message?.let { msg -> onToast(msg, requireActivity()) }
                    }
                }
            }
            showProgressbar.observe(requireActivity()) {
                if (it) {
                    binding.progressBarCommon.visibility = View.VISIBLE
                } else {
                    binding.progressBarCommon.visibility = View.GONE
                }
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddToCart.setOnClickListener {
                processedBuyNow()
            }

            ivPlaceHolderImage1.setOnClickListener {
                context?.let {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        isPickingUpJpg = false
                        permissionReqLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        openImagePickerPng()
                    }
                }
            }

            ivRemoveImage1.setOnClickListener {
                if (viewModel.filePng != null)
                    removePngSvgFileImage()
            }


            ivPlaceHolderImage2.setOnClickListener {
                context?.let {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        isPickingUpJpg = true
                        permissionReqLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        openImagePickerJpg()
                    }
                }
            }

            tvNeedTShirt.setOnClickListener {
                findNavController().navigate(R.id.action_photographerCustomizeShirtFragment_to_productDetailsFragment)
            }

            ivRemoveImage2.setOnClickListener {
                if (viewModel.fileJpg != null)
                    removeJpgFileImage()
            }
        }
    }

    private fun processedBuyNow() {
        /*if (validateForm()) {
            if (requireActivity().isInternetAvailable()) {
                viewModel.apply {
                    *//* when {
                         uploadedPngUrl.isNullOrEmpty() -> {
                             filePng?.let {
                                 uploadImageFile(it)
                             }
                         }
                         uploadedJpgUrl.isNullOrEmpty() -> {
                             fileJpg?.let {
                                 uploadImageFile(it)
                             }
                         }
                         else -> {
                             binding.apply {
                                 (recyclerViewSizeSelector.adapter as ProductSizeAdapter).getSelectedElement()
                                     ?.let { selectedSize ->
                                         addTshirtToCart(
                                             edtStudioName.text.toString(),
                                             edtStudioTagline.text.toString(),
                                             TshirtSize(
                                                 selectedSize.is_avilable,
                                                 1,
                                                 selectedSize.size
                                             )
                                         )
                                     }
                             }
                         }
                     }*//*
                    *//*binding.apply {
                        (recyclerViewSizeSelector.adapter as ProductSizeAdapter).getSelectedElement()
                            ?.let { selectedSize ->
                                addTshirtToCart(
                                    edtStudioName.text.toString(),
                                    edtStudioTagline.text.toString(),
                                    TshirtSize(
                                        selectedSize.is_avilable,
                                        1,
                                        selectedSize.size
                                    )
                                )
                            }
                    }*//*

                    findNavController().popBackStack()
                }
            } else onToast(getString(R.string.internet_check), requireActivity())
        }*/
        findNavController().popBackStack()
    }

    private val pickPngSvgImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    //val file = FileUtils.getPath(requireActivity(), uri)
                    val file = FileSelectionUtils.getFilePathFromUri(requireActivity(), uri)
                    if (file != null) {
                        binding.ivPlaceHolderImage1.setImageURI(it.data?.data)
                        binding.ivRemoveImage1.show()
                        viewModel.uploadedPngUrl = null
                        viewModel.filePng = file.path
                    } else {
                        onToast(getString(R.string.label_image_not_found), requireActivity())
                    }
                }
            }
        }

    private val pickJpgImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    //val file = FileUtils.getPath(requireActivity(), uri)
                    val file = FileSelectionUtils.getFilePathFromUri(requireActivity(), uri)
                    if (file != null) {
                        binding.ivPlaceHolderImage2.setImageURI(it.data?.data)
                        binding.ivRemoveImage2.show()
                        viewModel.uploadedJpgUrl = null
                        viewModel.fileJpg = file.path
                    } else {
                        onToast(getString(R.string.label_image_not_found), requireActivity())
                    }
                }
            }
        }

    private val permissionReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> {
                    if (isPickingUpJpg) {
                        openImagePickerJpg()
                    } else {
                        openImagePickerPng()
                    }
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                }
                else -> {
                    showDialogPermission()
                }
            }
        }

    private fun removePngSvgFileImage() {

        viewModel.run {
            isLogoPngUploaded = false
            filePng = null
            uploadedPngUrl = null
        }

        binding.ivPlaceHolderImage1.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_image_place_holder
            )
        )
        binding.ivRemoveImage1.hide()
    }

    private fun removeJpgFileImage() {
        viewModel.run {
            isLogoJpgUploaded = false
            fileJpg = null
            uploadedJpgUrl = null
        }

        binding.ivPlaceHolderImage2.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_image_place_holder
            )
        )
        binding.ivRemoveImage2.hide()
    }

    private fun showDialogPermission() {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setMessage(getString(R.string.label_storage_permission_required))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.label_ok)
            ) { dialog, _ ->
                dialog.cancel()
                openAppPermissionSetting()
            }
            .setNegativeButton(
                getString(R.string.label_cancel)
            ) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.app_name))
        alert.show()
    }

    private fun openAppPermissionSetting() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context?.packageName, null)
            intent.data = uri
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private fun openImagePickerPng() {
        val mimeTypes = arrayOf("image/svg", "image/png")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        pickPngSvgImage.launch(intent)
    }

    private fun openImagePickerJpg() {
        val mimeTypes = arrayOf("image/jpg", "image/jpeg")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        pickJpgImage.launch(intent)
    }

    private fun validateForm(): Boolean {
        var status = true
        binding.apply {
            if (!requireActivity().isInternetAvailable()) {
                onToast(getString(R.string.internet_check), requireActivity())
            } else if ((recyclerViewSizeSelector.adapter as ProductSizeAdapter).getSelectedElement() == null) {
                onToast(getString(R.string.label_select_size), requireActivity())
            } else if (edtStudioName.text.toString().isEmpty()) {
                edtStudioName.error = getString(R.string.is_required)
                status = false
            } else if (viewModel.filePng == null) {
                onToast(getString(R.string.upload_logo), requireActivity())
                status = false
            } else if (viewModel.fileJpg == null) {
                onToast(getString(R.string.upload_logo), requireActivity())
                status = false
            }
        }
        return status
    }
}