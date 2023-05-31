package com.photoshooto.bottomsheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.photoshooto.BuildConfig
import com.photoshooto.databinding.LayoutAddPhotoBottomSheetBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoBottomDialogFragment : BottomSheetDialogFragment(),
    EasyPermissions.PermissionCallbacks {

    val TAG = "AddPhotoBottomDialog"

    private var _binding: LayoutAddPhotoBottomSheetBinding? = null
    private val binding get() = _binding!!
    var imageFilePath: String? = null

    var photoFile: File? = null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // get the views and attach the listener
        _binding = LayoutAddPhotoBottomSheetBinding.inflate(inflater, container, false)
        return binding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewCamera.setOnClickListener {
            takePicFromCamera()
        }
        binding.textViewGallery.setOnClickListener {
            openGallery()
        }
    }

    companion object {
        const val ACTION_CAMERA_REQUEST_CODE = 1002
        const val ACTION_ALBUM_REQUEST_CODE = 2001
        const val CAMERA_PERMISSION_CODE = 1000;
        const val GALLERY_PERMISSION_CODE = 1001;
        var onImageUpLoadListener: OnImageUpLoadListener? = null
        fun newInstance(
            onImageUpLoadListener: OnImageUpLoadListener
        ): AddPhotoBottomDialogFragment {
            Companion.onImageUpLoadListener = onImageUpLoadListener
            return AddPhotoBottomDialogFragment()
        }
    }

    interface OnImageUpLoadListener {
        fun onCameraSelected(multipartBodyPart: MultipartBody.Part, imagePath: String)
        fun onGallerySelected(multipartBodyPart: MultipartBody.Part, bitmap: Bitmap)
    }

    @AfterPermissionGranted(GALLERY_PERMISSION_CODE)
    private fun openGallery() {
        context?.apply {
            val perms =
                arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (EasyPermissions.hasPermissions(this, *perms)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, ACTION_ALBUM_REQUEST_CODE)
            } else {
                activity?.apply {
                    EasyPermissions.requestPermissions(
                        this, "We need permissions so that we can upload the respective documents",
                        GALLERY_PERMISSION_CODE, *perms
                    )
                }
            }
        }
    }

    @AfterPermissionGranted(CAMERA_PERMISSION_CODE)
    private fun takePicFromCamera() {
        context?.apply {
            val perms =
                arrayOf<String>(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            if (EasyPermissions.hasPermissions(this, *perms)) {
                openCamera()
            } else {
                activity?.apply {
                    EasyPermissions.requestPermissions(
                        this, "We need permissions so that we can upload the respective documents",
                        CAMERA_PERMISSION_CODE, *perms
                    )
                }

            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context?.let {
            if (cameraIntent.resolveActivity(it.packageManager) != null) {
                //Create a file to store the image
                try {
                    photoFile = createImageFile();
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("Camera Exception", "" + ex.message)
                }
                if (photoFile != null) {
                    photoFile?.apply {
                        val photoURI: Uri = FileProvider
                            .getUriForFile(
                                it,
                                "${BuildConfig.APPLICATION_ID}.provider", this
                            )
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(
                            cameraIntent,
                            ACTION_CAMERA_REQUEST_CODE
                        )
                    }

                }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "Capture_$timeStamp"
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile: File = File.createTempFile(
            imageFileName,  /* prefix */".jpg",  /* suffix */storageDir /* directory */
        )
        imageFilePath = imageFile.absolutePath;
        return imageFile;
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ACTION_ALBUM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val resolver = activity?.contentResolver
                    val bitmap = MediaStore.Images.Media.getBitmap(resolver, data.data)
                    data.data?.apply {
                        val inputStream: InputStream? =
                            activity?.contentResolver?.openInputStream(this)
                        val requestFile: RequestBody? =
                            getBytes(inputStream!!)?.let {
                                it
                                    .toRequestBody(
                                        "image/*".toMediaTypeOrNull(),
                                        0, it.size
                                    )
                            }


                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val imageFileName =
                            "RidePlus_Partner_$timeStamp.jpg"
                        requestFile?.apply {
                            val body = MultipartBody.Part.createFormData(
                                "proof[]",
                                imageFileName,
                                this
                            )
                            onImageUpLoadListener?.onGallerySelected(body, bitmap)
                            dialog?.dismiss()
                        }

                    }


                }
            }
            ACTION_CAMERA_REQUEST_CODE -> {

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName =
                    "RidePlus_Partner_$timeStamp.jpg"
                if (resultCode == Activity.RESULT_OK) {
                    val requestFile: RequestBody? =
                        photoFile?.let {
                            it
                                .asRequestBody("image/*".toMediaTypeOrNull())
                        }
                    requestFile?.apply {
                        val body = MultipartBody.Part.createFormData(
                            "proof[]",
                            imageFileName,
                            this
                        )
                        imageFilePath?.let {
                            onImageUpLoadListener?.onCameraSelected(body, it)
                            dialog?.dismiss()
                        }

                    }

                }
            }
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> {

            }
            else -> {
                println("no handler onActivityReenter")
            }
        }
    }

    @Throws(IOException::class)
    fun getBytes(`is`: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (`is`.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

}
