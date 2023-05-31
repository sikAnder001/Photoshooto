package com.photoshooto.ui.qrcodegenration

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.photoshooto.R
import com.photoshooto.databinding.DialogDownloadQrConfirmationBinding
import com.photoshooto.databinding.DialogGeneratingQrBinding
import com.photoshooto.databinding.DialogQrInfoBinding
import com.photoshooto.databinding.FragmentQrCodeProceedBinding
import com.photoshooto.domain.usecase.qr_generation.QrGenerationViewModel
import com.photoshooto.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FragmentQRCodeProceed : Fragment() {

    private lateinit var qrLoaderDialog: Dialog
    private lateinit var binding: FragmentQrCodeProceedBinding
    private val viewModel: QrGenerationViewModel by viewModel()
    private var qrId: String? = null
    private var isQrDownloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initObserver() {
        viewModel.apply {
            generateQrResponse.observe(requireActivity()) { result ->
                Handler(Looper.getMainLooper()).postDelayed({
                    qrLoaderDialog.dismiss()
                    binding.viewRoot.show()

                }, 4000)

                if (result.success) {
                    result.data?.let { qrElement ->
                        Glide.with(requireContext()).load(qrElement.url).into(binding.ivQrCode)
                        qrId = qrElement.id
                    }
                } else {
                    findNavController().popBackStack()
                    onToast(result.message, requireContext())
                }
            }
            messageData.observe(requireActivity(), Observer {
                qrLoaderDialog.dismiss()
                binding.viewRoot.show()
                findNavController().popBackStack()
                onToast(it, requireContext())
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrCodeProceedBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbarQrCodeGenerated
        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        val titleTxt = toolbar.tvTitle
        titleTxt.text = activity?.getString(R.string.qr_code_generated)

        initObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()
        binding.viewRoot.hide()

        if (viewModel.generateQrResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                qrLoaderDialog.show()
                viewModel.generateQrCode()
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        } else {
            initObserver()
        }
    }

    private fun clickListener() {
        binding.apply {

            btnDownloadQr.setOnClickListener {
                if (isQrDownloaded) {
                    downloadConfirmation()
                } else {
                    GlobalScope.launch {
                        val dispatcher = this.coroutineContext
                        CoroutineScope(dispatcher).launch {
                            //val bitmap = getQrViewBitmap(binding.viewQr)
                            executeSaveFile()
                        }
                    }
                }
            }
            btnInfo.setOnClickListener {
                dialogInfo()
            }
            btnProceed.setOnClickListener {
                when {
                    qrId.isNullOrEmpty() -> onToast(
                        getString(R.string.qr_code_missing),
                        requireContext()
                    )
                    !requireContext().isInternetAvailable() -> onToast(
                        getString(R.string.internet_check),
                        requireContext()
                    )
                    else -> {
                        val bundle = Bundle()
                        bundle.putString(KEY_QR_ID, qrId)
                        findNavController().navigate(
                            R.id.action_fragmentQRCodeProceed_to_fragmentChooseStandee, bundle
                        )
                    }
                }
            }
        }
    }

    private fun executeSaveFile() {
        if (isPermissionGranted(requireActivity())) {
            val bitmap = getQrViewBitmap(binding.viewQr)
            saveFileUsingMediaStore(bitmap)
            requireActivity().runOnUiThread {
                isQrDownloaded = true
                onToast(getString(R.string.qr_code_saved), requireContext())
            }
        }
    }

    private fun getQrViewBitmap(view: ConstraintLayout): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(requireActivity(), R.color.transparent))
        view.draw(canvas)
        return bitmap
    }

    private fun saveFileUsingMediaStore(bitmap: Bitmap) {
        // val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        /*val filename = "PhotoshootoQr_$qrId.png"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        */
        val imageFileName = "PhotoshootoQr_$qrId.png"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var file: File? = null

        try {
            file = File.createTempFile(imageFileName, ".jpg", storageDir)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val bitmapData = byteArrayOutputStream.toByteArray()
            file.createNewFile()
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()

            MediaScannerConnection.scanFile(
                requireActivity(), arrayOf(file.toString()), null, null
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun scanGalleryAfterAddingImage(file: File) {

        MediaScannerConnection.scanFile(
            context, arrayOf(file.toString()), null, null
        )

        /*     val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
             val contentUri = Uri.fromFile(file)
             mediaScanIntent.data = contentUri
             requireActivity().sendBroadcast(mediaScanIntent)*/

    }

    private fun downloadConfirmation() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogDownloadQrConfirmationBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            cardNo.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            cardYes.setOnClickListener {
                bottomSheetDialog.dismiss()
                GlobalScope.launch {
                    val dispatcher = this.coroutineContext
                    CoroutineScope(dispatcher).launch {
                        //val bitmap = getQrViewBitmap(binding.viewQr)
                        executeSaveFile()
                        /*saveFileUsingMediaStore(bitmap)
                        requireActivity().runOnUiThread {
                            onToast(getString(R.string.qr_code_saved), requireContext())
                        }*/
                    }
                }
            }
        }
        bottomSheetDialog.show()
    }

    private fun initView() {
        initQrLoader()
    }

    private fun initQrLoader() {
        qrLoaderDialog = Dialog(requireContext())
        qrLoaderDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        qrLoaderDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        val dialogBinding = DialogGeneratingQrBinding.inflate(layoutInflater)
        qrLoaderDialog.setContentView(dialogBinding.root)
        qrLoaderDialog.setCancelable(false)
        qrLoaderDialog.setCanceledOnTouchOutside(false)
        // qrLoaderDialog.show()
        val window = qrLoaderDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun dialogInfo() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val dialogBinding = DialogQrInfoBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(dialogBinding.root)
        dialogBinding.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (!isPermissionGranted(requireActivity())) {
                onToast(
                    "You have to allow all the permissions to access content from camera and gallery",
                    requireActivity()
                )
            } else {
                GlobalScope.launch {
                    val dispatcher = this.coroutineContext
                    CoroutineScope(dispatcher).launch {
                        //val bitmap = getQrViewBitmap(binding.viewQr)
                        executeSaveFile()
                    }
                }
            }
        }
    }
}
