package com.photoshooto.ui.qrcodegenration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.databinding.FragmentProceedToSummaryBinding
import com.photoshooto.domain.model.StandeeElement
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.util.*
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class FragmentProceedToSummery : Fragment() {
    private var isPickingUpJpg = false
    private lateinit var binding: FragmentProceedToSummaryBinding
    private val viewModel: QrGenerationViewModel by viewModel()
    private var selectedStandee: StandeeElement? = null
    var path = ArrayList<Uri>()
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private lateinit var titleTxt: TextView

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            viewModel.apply {
                                if (!isLogoPngUploaded && !isLogoJpgUploaded) {
                                    uploadedPngUrl = response.data?.data?.get(0)?.file_path
                                    isLogoPngUploaded = true
                                    processedAddToCart()
                                } else {
                                    uploadedJpgUrl = response.data?.data?.get(0)?.file_path
                                    isLogoJpgUploaded = true
                                    processedAddToCart()
                                }
                            }
                        }
                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {}
                    }
                }
            }

            addToCartResponse.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {
                            val bundle = Bundle()
                            bundle.putString(KEY_QR_ID, arguments?.getString(KEY_QR_ID))
                            //findNavController().navigate(R.id.action_move_to_standee_cart, bundle)
                            findNavController().navigate(
                                R.id.action_fragmentProceedToSummery_to_fragmentMyCart,
                                bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProceedToSummaryBinding.inflate(inflater, container, false)
        val standeeJson = arguments?.getString(KEY_SELECTED_STANDEE)
        selectedStandee = Gson().fromJson(standeeJson, StandeeElement::class.java)

        val toolbar = binding.toolbarQrSummary
        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.add_standee_details)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()
    }

    private fun initView() {
        binding.apply {
            selectedStandee?.let {
                Glide.with(requireContext())
                    .load(it.images?.get(0))
                    .into(includeDetails.ivStandee)

                includeDetails.tvStandeeType.text = it.type
                includeDetails.ivStandee.shapeAppearanceModel =
                    includeDetails.ivStandee.shapeAppearanceModel.toBuilder()
                        .setAllCornerSizes(20f)
                        .build()

                Glide.with(requireContext())
                    .load(it.images?.get(0))
                    .into(includeVerifyDetails.ivStandee)
                includeVerifyDetails.ivStandee.shapeAppearanceModel =
                    includeVerifyDetails.ivStandee.shapeAppearanceModel.toBuilder()
                        .setAllCornerSizes(20f)
                        .build()
                includeVerifyDetails.tvStandeeType.text = it.type
                includeVerifyDetails.tvAmount.text = it.price.toDouble().amount(requireContext())

                viewModel.filePngUri?.let { uri ->
                    includeDetails.ivPlaceHolderImage1.setImageURI(uri)
                }
                if (viewModel.filePngUri != null) {
                    binding.includeDetails.ivRemoveImage1.show()
                }
                viewModel.fileJpgUri?.let { uri ->
                    includeDetails.ivPlaceHolderImage2.setImageURI(uri)
                }
                if (viewModel.fileJpg != null) {
                    binding.includeDetails.ivRemoveImage2.show()
                }

                binding.includeDetails.edtPhotographerId.setText(
                    SharedPrefsHelper.read(
                        SharedPrefConstant.USER_ID
                    ).toString()
                )
                binding.includeDetails.edtContactNo.setText(
                    SharedPrefsHelper.read(
                        SharedPrefConstant.MOBILE_NUMBER
                    ).toString()
                )
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            includeDetails.apply {
                btnProceed.setOnClickListener {
                    // findNavController().navigate(R.id.navigation_standee_details)
                    if (validateForm()) {
                        verifyDetails()
                    }
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

                ivRemoveImage2.setOnClickListener {
                    if (viewModel.fileJpg != null)
                        removeJpgFileImage()
                }
            }
            includeDetails.imgLocation.setOnClickListener {
                getLocation()
            }
            includeVerifyDetails.apply {
                ivEdit.setOnClickListener {
                    titleTxt.text = getString(R.string.add_standee_details)
                    viewStandeeDetails.show()
                    viewVerifyStandeeDetails.hide()
                }
                cartAdd.setOnClickListener {
                    val qty = tvQty.text.toString().toInt() + 1
                    tvQty.text = (qty).toString()
                    includeVerifyDetails.tvAmount.text =
                        "${selectedStandee!!.price.toDouble() * qty}".toDouble()
                            .amount(requireContext())
                }
                cartMinus.setOnClickListener {
                    val qty = tvQty.text.toString().toInt()
                    if (qty > 1) {
                        tvQty.text = (tvQty.text.toString().toInt() - 1).toString()
                        includeVerifyDetails.tvAmount.text =
                            "${selectedStandee!!.price.toDouble() * (qty - 1)}".toDouble()
                                .amount(requireContext())
                    }
                }
                btnAddToCart.setOnClickListener {
                    processedAddToCart()
                }
                btnBuyNow.setOnClickListener {
                    processedAddToCart()
                }
            }
        }
    }

    private fun verifyDetails() {
        binding.apply {

            StandeeDetailsAddedDialog.newInstance(
                arguments?.getString(KEY_QR_ID)!!,
                requireActivity()
            ).show(parentFragmentManager, StandeeDetailsAddedDialog.TAG)

            titleTxt.text = getString(R.string.standee_details)
            viewStandeeDetails.hide()
            viewVerifyStandeeDetails.show()
            includeVerifyDetails.apply {
                txtStudioName.text = includeDetails.edtStudioName.text.toString()
                txtStudioTagline.text = includeDetails.edtStudioTagline.text.toString().let {
                    if (it.isEmpty()) {
                        ""
                    } else it
                }
                txtPhotographerId.text = includeDetails.edtPhotographerId.text.toString()
                txtContactNumber.text = includeDetails.edtContactNo.text.toString()
                txtAlternativeNumber.text = includeDetails.edtAltContactNo.text.toString()
                txtStudioAddress.text = includeDetails.edtStudioAddress.text.toString()
                imgStandee1.setImageURI(viewModel.filePng?.toUri())
                imgStandee2.setImageURI(viewModel.fileJpg?.toUri())
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProvider?.lastLocation?.addOnCompleteListener(requireActivity()) { task ->
                    val location: android.location.Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: MutableList<Address>? =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val completeAddress = list?.get(0)

                        if (completeAddress != null) {


                            val address: String =
                                completeAddress.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            val society: String =
                                checkStringReturnValue(completeAddress.subLocality) + ""
                            val city: String = checkStringReturnValue(completeAddress.locality) + ""

                            binding.includeDetails.edtStudioAddress.setText(city)

                            val state: String =
                                checkStringReturnValue(completeAddress.adminArea) + ""
                            val postalCode: String =
                                checkStringReturnValue(completeAddress.postalCode) + ""
                            val featureName: String =
                                completeAddress.featureName + ""

                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun processedAddToCart() {
        if (validateForm()) {
            if (requireActivity().isInternetAvailable()) {
                viewModel.apply {
                    /*if (uploadedPngUrl.isNullOrEmpty()) {
                        filePng?.let {
                            uploadImageFile(it)
                        }
                    } else if (uploadedJpgUrl.isNullOrEmpty()) {
                        fileJpg?.let {
                            uploadImageFile(it)
                        }
                    } else {
                        binding.includeVerifyDetails.apply {
                            selectedStandee?.let {
                                viewModel.addToCart(
                                    it,
                                    tvQty.text.toString().toInt(),
                                    txtAlternativeNumber.text.toString(),
                                    txtContactNumber.text.toString(),
                                    txtPhotographerId.text.toString(),
                                    txtStudioAddress.text.toString(),
                                    txtStudioName.text.toString(),
                                    txtStudioTagline.text.toString()
                                )
                            }
                        }
                    }*/
                    binding.includeVerifyDetails.apply {
                        selectedStandee?.let {
                            viewModel.addToCart(
                                it,
                                tvQty.text.toString().toInt(),
                                txtAlternativeNumber.text.toString(),
                                txtContactNumber.text.toString(),
                                txtPhotographerId.text.toString(),
                                txtStudioAddress.text.toString(),
                                txtStudioName.text.toString(),
                                txtStudioTagline.text.toString()
                            )
                        }
                    }
                }
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        }
    }

    private fun removePngSvgFileImage() {

        viewModel.run {
            isLogoPngUploaded = false
            filePng = null
            filePngUri = null
            uploadedPngUrl = null
        }

        binding.includeDetails.ivPlaceHolderImage1.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_image_place_holder
            )
        )
        binding.includeDetails.ivRemoveImage1.hide()
    }

    private fun removeJpgFileImage() {
        viewModel.run {
            isLogoJpgUploaded = false
            fileJpg = null
            fileJpgUri = null
            uploadedJpgUrl = null
        }

        binding.includeDetails.ivPlaceHolderImage2.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_image_place_holder
            )
        )
        binding.includeDetails.ivRemoveImage2.hide()
    }

    private fun openPngUploadView() {
        var list = ArrayList<MimeType>()
        list.add(MimeType.WEBP)
        list.add(MimeType.PNG)
        list.add(MimeType.BMP)
        list.add(MimeType.GIF)
        FishBun.with(requireActivity()).setImageAdapter(GlideAdapter())
            .setMaxCount(1).setSelectedImages(path)
            .exceptMimeType(list)
            .startAlbumWithActivityResultCallback(startResultForPNGSVG)
    }

    private fun openJpgUploadView() {
        var list = ArrayList<MimeType>()
        list.add(MimeType.WEBP)
        list.add(MimeType.JPEG)
        list.add(MimeType.BMP)
        list.add(MimeType.GIF)
        FishBun.with(requireActivity()).setImageAdapter(GlideAdapter())
            .setMaxCount(1).setSelectedImages(path)
            .exceptMimeType(list)
            .startAlbumWithActivityResultCallback(startResultForJPG)
    }

    private val startResultForPNGSVG =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                path = it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                Log.d("startForResult", path[0].path.toString())
                val file = FileUtils.getPath(requireContext(), path[0])
                if (file != null) {
                    binding.includeDetails.ivPlaceHolderImage1.setImageURI(path[0])
                    binding.includeDetails.ivRemoveImage1.show()
                    viewModel.uploadedPngUrl = null
                    viewModel.filePng = file
                    viewModel.filePngUri = path[0]
                } else {
                    onToast(getString(R.string.label_image_not_found), requireContext())
                }

            }
        }

    private val startResultForJPG =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                path = it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                Log.d("startForResult", path[0].path.toString())
                val file = FileUtils.getPath(requireContext(), path[0])
                if (file != null) {
                    binding.includeDetails.ivPlaceHolderImage2.setImageURI(path[0])
                    binding.includeDetails.ivRemoveImage2.show()
                    viewModel.uploadedJpgUrl = null
                    viewModel.fileJpg = file
                    viewModel.fileJpgUri = path[0]
                } else {
                    onToast(getString(R.string.label_image_not_found), requireContext())
                }

            }
        }


    private val pickPngSvgImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    //val file = FileUtils.getPath(requireContext(), uri)
                    val file = FileSelectionUtils.getFilePathFromUri(requireActivity(), uri)
                    if (file != null) {
                        binding.includeDetails.ivPlaceHolderImage1.setImageURI(uri)
                        binding.includeDetails.ivRemoveImage1.show()
                        viewModel.uploadedPngUrl = null
                        viewModel.filePng = file.path
                        viewModel.filePngUri = uri
                    } else {
                        onToast(getString(R.string.label_image_not_found), requireContext())
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
                    //val file = FileUtils.getPath(requireContext(), uri)
                    val file = FileSelectionUtils.getFilePathFromUri(requireActivity(), uri)
                    if (file != null) {
                        binding.includeDetails.ivPlaceHolderImage2.setImageURI(uri)
                        binding.includeDetails.ivRemoveImage2.show()
                        viewModel.uploadedJpgUrl = null
                        viewModel.fileJpg = file.path
                        viewModel.fileJpgUri = uri
                    } else {
                        onToast(getString(R.string.label_image_not_found), requireContext())
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

    private fun showDialogPermission() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
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

    private fun openImagePickerJpg() {
        val mimeTypes = arrayOf("image/jpg", "image/jpeg")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        pickJpgImage.launch(intent)
    }

    private fun openImagePickerPng() {
        val mimeTypes = arrayOf("image/svg", "image/png")
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        pickPngSvgImage.launch(intent)
        //openPngUploadView()
    }

    private fun validateForm(): Boolean {
        var status = true
        binding.includeDetails.apply {
            if (edtStudioName.text.toString().isEmpty()) {
                edtStudioName.error = getString(R.string.is_required)
                status = false
            } else if (edtPhotographerId.text.toString().isEmpty()) {
                edtPhotographerId.error = getString(R.string.is_required)
                status = false
            } else if (edtContactNo.text.toString().isEmpty()) {
                edtContactNo.error = getString(R.string.is_required)
                status = false
            } /*else if (edtAltContactNo.text.toString().isEmpty()) {
                edtAltContactNo.error = getString(R.string.is_required)
                status = false
            }*/ else if (edtStudioAddress.text.toString().isEmpty()) {
                edtStudioAddress.error = getString(R.string.is_required)
                status = false
            } else if (viewModel.filePng == null) {
                onToast(getString(R.string.upload_logo), requireContext())
                status = false
            } else if (viewModel.fileJpg == null) {
                onToast(getString(R.string.upload_logo), requireContext())
                status = false
            }
        }
        return status
    }
}
