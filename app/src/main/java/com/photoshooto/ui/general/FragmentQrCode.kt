package com.photoshooto.ui.general

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.photoshooto.databinding.FragmentBarcodeBinding
import com.photoshooto.util.setSingleClickListener
import java.io.IOException


class FragmentQrCode : Fragment() {

    private var _binding: FragmentBarcodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val REQUEST_CAMERA_PERMISSION = 201
    private var intentData = ""
    private var isEmail = false
    private var productid: String = ""
    private var onfirst = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        onfirst = "0"
        binding.btnAction.setSingleClickListener {
            if (intentData.isNotEmpty()) {
                if (isEmail) {
                    // startActivity(new Intent(ScannedBarcodeActivity.this, EmailActivity.class).putExtra("email_address", intentData));
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(intentData)
                        )
                    )
                }
            }
        }
    }


    private fun initialiseDetectorsAndSources() {
        onfirst = "0"
        barcodeDetector = BarcodeDetector.Builder(requireActivity())
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        cameraSource = CameraSource.Builder(requireActivity(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        //binding.surfaceView.set(true)

        //binding.surfaceView.setBackgroundColor(0Xffffffff.toInt());
        binding.surfaceView.holder.setFormat(PixelFormat.TRANSPARENT)
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(binding.surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            201
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                intentData = ""
                productid = ""
                onfirst = "0"
                binding.txtBarcodeValue.text = ""
//                Toast.makeText(
//                    applicationContext,
//                    "To prevent memory leaks barcode scanner has been stopped",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    binding.txtBarcodeValue.post(Runnable {
                        if (barcodes.valueAt(0).email != null) {
                            binding.txtBarcodeValue.removeCallbacks(null)
                            intentData = barcodes.valueAt(0).email.address
                            binding.txtBarcodeValue.text = intentData
                            isEmail = true
                            binding.btnAction.text = "ADD CONTENT TO THE MAIL"
                        } else {
                            barcodeDetector!!.release()
                            isEmail = false
                            try {
                                if (ActivityCompat.checkSelfPermission(
                                        requireActivity(),
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    cameraSource!!.stop()
                                    cameraSource!!.start(binding.surfaceView.holder)
                                } else {
                                    ActivityCompat.requestPermissions(
                                        requireActivity(),
                                        arrayOf(Manifest.permission.CAMERA),
                                        201
                                    )
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            intentData = ""
                            productid = ""
                            binding.txtBarcodeValue.text = ""
                            binding.btnAction.text = "LAUNCH URL"
                            intentData = barcodes.valueAt(0).displayValue
                            binding.txtBarcodeValue.text = intentData
                            productid = binding.txtBarcodeValue.text.toString()
                            if (productid !== "") {
                                productid = productid.replace("http://", "")
                                if (productid.contains("https://")) {
                                    productid = productid.replace("https://", "")
                                }
                                if (onfirst == "0") {
//                                    getProductData(productid.toString());
                                    Toast.makeText(
                                        requireActivity(),
                                        "productid " + productid,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    productid = ""

                                    val direction =
                                        FragmentQrCodeDirections.actionFragmentQrCodeToFragmentSignup()
                                    findNavController().navigate(direction)

                                }
                            } else {
                                try {
                                    if (ActivityCompat.checkSelfPermission(
                                            requireActivity(),
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        cameraSource!!.stop()
                                        cameraSource!!.start(binding.surfaceView.holder)
                                    } else {
                                        ActivityCompat.requestPermissions(
                                            requireActivity(),
                                            arrayOf(Manifest.permission.CAMERA),
                                            201
                                        )
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                intentData = ""
                                productid = ""
                                binding.txtBarcodeValue.text = ""
                            }
                        }
                    })
                }
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            201 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) === PackageManager.PERMISSION_GRANTED)
                    ) {
                        cameraSource!!.stop()
                        cameraSource!!.start(binding.surfaceView.holder)
                    }
                }
                return
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraSource!!.stop()
                binding.surfaceView.invalidate()
                cameraSource!!.start(binding.surfaceView.holder)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    201
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }


}

