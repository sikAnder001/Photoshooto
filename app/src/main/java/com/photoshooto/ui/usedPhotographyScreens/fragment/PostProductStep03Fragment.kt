package com.photoshooto.ui.usedPhotographyScreens.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.base.BaseFragment
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.databinding.FragmentPostProductStep03Binding
import com.photoshooto.domain.model.FileImagePRQ
import com.photoshooto.domain.model.PostProductsPRQ
import com.photoshooto.domain.model.ProductsModel
import com.photoshooto.domain.model.User
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.ui.dialog.ErrorDialog
import com.photoshooto.ui.job.JobViewModel
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.usedPhotographyScreens.adapter.FileUploadListAdapter
import com.photoshooto.util.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class PostProductStep03Fragment : BaseFragment() {

    lateinit var binding: FragmentPostProductStep03Binding
    private val uploadFileViewModel: UploadFileViewModel by viewModel()
    private val jobHomeViewModel: JobViewModel by viewModel()

    var user: User? = null
    private lateinit var fileUploadListAdapter: FileUploadListAdapter
    val fileArray: ArrayList<FileImagePRQ> = ArrayList()

    private val navArgs: PostProductStep03FragmentArgs by navArgs()
    var fileUploadMultipart: ArrayList<MultipartBody.Part?> = ArrayList()

    var productsModel = ProductsModel()
    var data = PostProductsPRQ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            return binding.root
        }
        binding = FragmentPostProductStep03Binding.inflate(inflater, container, false)

        binding.imgPostBack.setOnClickListener {
            findNavController().popBackStack()
        }

        user = SharedPrefsHelper.getUserCommon()

        data = navArgs.postProductsPRQ

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {

                            response.data?.data?.forEachIndexed { index, fileUpload ->
                                Log.e("file_path", "" + DOMAIN + fileUpload.file_path)

                                data.images.add(DOMAIN + fileUpload.file_path)
                            }

                            GlobalScope.launch {
                                delay(5000)     // 6 sec delay
                                withContext(Dispatchers.Main) {
                                    Log.e("Image", "Final")

                                    if (productsModel._id.length != 0) {
                                        productUpdate(data)

                                    } else {
                                        postData(data)

                                    }
                                }
                            }
                        }

                        Status.ERROR -> {
                            response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                        }
                        else -> {
                        }
                    }
                } else {
                }
            }
        }


        jobHomeViewModel.postProductData.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {

                congDailog(it.data?.id.toString())

            } else {
                ErrorDialog.showDialog(requireContext(), it.message.toString(), "Okay", true) {

                }
            }
        }

        jobHomeViewModel.postProductUpdateData.observe(viewLifecycleOwner) {
            hideProgress()
            if (it.success) {

                congDailog(it.data?.id.toString())

            } else {
                ErrorDialog.showDialog(requireContext(), it.message.toString(), "Okay", true) {

                }
            }
        }

        binding.imgClose.setOnClickListener {
            binding.rlBanner.isVisible = false
        }


        binding.btnProceed.background =
            ActivityCompat.getDrawable(requireContext(), R.drawable.button_disable)


        binding.btnProceed.setSafeOnClickListener {

            if (fileArray.size <= 4) {
                binding.rlBanner.isVisible = true
                if (binding.rlBanner.isVisible) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.rlBanner.isVisible = false
                    }, 2000)
                }
                return@setSafeOnClickListener
            }

            if (!formValidatedSuccess()) {
                return@setSafeOnClickListener
            }

            data.price = binding.etPrice.text.toString().toInt()
            data.description = binding.etDesc.text.toString()

            showProgress()


            imageUpload()
        }

        fileUploadListAdapter = FileUploadListAdapter(onDelete = { d ->
            fileArray.remove(d)
            fileUploadListAdapter.swapList(fileArray)
            binding.tvImageSize.text = "${fileArray.size}/ 15"
            binding.btnBrowseFile.isEnabled = true
            binding.tvImageSize.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey24
                )
            )
            if (fileArray.size <= 4) {
                binding.btnProceed.background =
                    ActivityCompat.getDrawable(requireContext(), R.drawable.button_disable)
            }
        }, onZoom = {

            if (it.imageUrl.length != 0) {
                findNavController().navigate(
                    PostProductStep03FragmentDirections.actionPostProductStep3ToFullscreen(it.imageUrl)
                )
            } else {
                findNavController().navigate(
                    PostProductStep03FragmentDirections.actionPostProductStep3ToFullscreen(it.file?.absolutePath.toString())
                )
            }





            findNavController().navigate(
                PostProductStep03FragmentDirections.actionHomeToPostProductStep1()
            )


        })

        binding.rvFile.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvFile.adapter = fileUploadListAdapter

        binding.btnBrowseFile.setOnClickListener {
            context?.apply {
                val perms =
                    arrayOf<String>(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                if (EasyPermissions.hasPermissions(this, *perms)) {
                    selectImage()
                } else {
                    activity?.apply {
                        EasyPermissions.requestPermissions(
                            this,
                            "We need permissions so that we can upload the respective documents",
                            AddPhotoBottomDialogFragment.CAMERA_PERMISSION_CODE,
                            *perms
                        )
                    }
                }
            }
        }

        navArgs.postProducts?.let {
            if (it._id.length != 0) {
                productsModel = it
                binding.etPrice.setText(productsModel.price.toString())
                binding.etDesc.setText(productsModel.description)

                productsModel.images.forEach {
                    val file = FileImagePRQ()
                    file.imageUrl = it

                    fileArray.add(file)

                    fileUploadListAdapter.swapList(fileArray)
                }

                binding.btnProceed.background =
                    ActivityCompat.getDrawable(requireContext(), R.drawable.button_ripple2)
            }
        }

        return binding.root
    }

    private fun postData(data: PostProductsPRQ) {

        jobHomeViewModel.postProduct(
            data
        )
    }

    private fun productUpdate(data: PostProductsPRQ) {

        jobHomeViewModel.productUpdate(
            productsModel.id,
            data
        )
    }

    private fun imageUpload() {
        Log.e("size", "" + fileArray.size)

        GlobalScope.launch {
            withContext(Dispatchers.Main) {

                val file = fileArray.filter { it.file != null }
                Log.e("file", "" + file.size)

                if (file.size != 0) {
                    val image = fileArray.filter { it.imageUrl.length != 0 }

                    if (image.size != 0) {
                        image.forEachIndexed { index, fileImagePRQ ->
                            data.images.add(fileImagePRQ.imageUrl)
                        }
                        data.billFile = productsModel.bill_file
                        data.warrantyCardFile = productsModel.warrantyCardFile
                    }

                    uploadImage(file)

                } else {
                    val image = fileArray.filter { it.imageUrl.length != 0 }

                    image.forEachIndexed { index, fileImagePRQ ->
                        data.images.add(fileImagePRQ.imageUrl)
                    }

                    productUpdate(data)
                }
            }
        }
    }

    private fun uploadImage(file: List<FileImagePRQ>) {

        requireContext().showToast("Images Uploaded")

        file.forEachIndexed { index, fileImagePRQ ->

            if (fileImagePRQ.imageUrl.length != 0) {

                data.images.add(fileImagePRQ.imageUrl)

            } else {
                val requestFile: RequestBody? =
                    fileImagePRQ.file?.asRequestBody("image/*".toMediaTypeOrNull())
                requestFile.apply {
                    fileUploadMultipart.add(this?.let {
                        MultipartBody.Part.createFormData(
                            "photos",
                            fileImagePRQ.file?.name,
                            it
                        )
                    })
                }
            }
        }

        val visiting_card: RequestBody = createPartFromString("product")
        val map: HashMap<String, RequestBody> = HashMap()
        map["category"] = visiting_card
        uploadFileViewModel.updateImgFileArrayList(
            fileUploadMultipart,
            map,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    fun selectImage() {
        val options: Array<CharSequence> = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo !!")
        builder.setCancelable(false)
        builder.setItems(options) { dialog: DialogInterface, item: Int ->
            if (options[item] == "Take Photo") {
                openCamera()
            } else if (options[item] == "Choose from Gallery") {
                openGallery()
            } else {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT);

        galleryActivityResultLauncher.launch(intent)

    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        CameraLauncher.launch(cameraIntent)

    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.getResultCode() === AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.getData()

            val mClipData: ClipData? = data?.getClipData()
            if (mClipData != null) {
                for (i in 0 until mClipData?.itemCount!!) {
                    val item = mClipData?.getItemAt(i)
                    val uri = item?.uri

                    val file = FileImagePRQ()
                    file.file = uri?.let { createFileFromUri(requireActivity(), it) }
                    file.category = "product"
                    fileArray.add(file)
                    Log.e("fileArray", "" + fileArray.toString())
                }
            } else {
                val selectedImageUri: Uri? = data?.data

                val file = FileImagePRQ()
                file.file = selectedImageUri?.let { createFileFromUri(requireActivity(), it) }
                file.category = "product"
                fileArray.add(file)
            }

            fileUploadListAdapter.swapList(fileArray)
            binding.tvImageSize.text = "${fileArray.size}/ 15"

            if (fileArray.size >= 15) {
                binding.btnBrowseFile.isEnabled = false
                binding.tvImageSize.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_error
                    )
                )
            }
            if (fileArray.size >= 5) {
                binding.btnProceed.background =
                    ActivityCompat.getDrawable(requireContext(), R.drawable.button_ripple2)
            }
        }
    }

    var CameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data
            val photo = data?.extras?.get("data") as Bitmap
            val tempUri: Uri = getImageUri(requireActivity(), photo as Bitmap)

            val file = FileImagePRQ()
            file.file = tempUri?.let { createFileFromUri(requireActivity(), it) }
            file.category = "product"

            fileArray.add(file)

            fileUploadListAdapter.swapList(fileArray)
            binding.tvImageSize.text = "${fileArray.size}/ 15"

            if (fileArray.size >= 15) {
                binding.btnBrowseFile.isEnabled = false
                binding.tvImageSize.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_error
                    )
                )
            }
            if (fileArray.size >= 5) {
                binding.btnProceed.background =
                    ActivityCompat.getDrawable(requireContext(), R.drawable.button_ripple2)

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
            if (etPrice.text.isNullOrBlank()) {
                showSnackBar("Selling Price Required")
                return false
            } else if (etDesc.text.isNullOrBlank()) {
                showSnackBar("Description Required")
                return false
            } else {
                return true
            }
        }

        return true
    }

    private fun congDailog(id: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dailog_cong)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        val btnView: Button = dialog.findViewById(R.id.btnView)
        val tvId: TextView = dialog.findViewById(R.id.tvId)

        tvId.text = "Your Product ID: ${id}"

        btnView.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(
                PostProductStep03FragmentDirections.actionPostProductStep3ToHome(id, false)
            )
        }

        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        Objects.requireNonNull(dialog.window)?.setGravity(Gravity.CENTER)

        dialog.show()
    }
}