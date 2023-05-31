package com.photoshooto.ui.usedPhotographyScreens.fragment

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photoshooto.R
import com.photoshooto.bottomsheet.AddPhotoBottomDialogFragment
import com.photoshooto.databinding.FragmentPostProductStep02Binding
import com.photoshooto.domain.model.FileImagePRQ
import com.photoshooto.domain.model.ProductsModel
import com.photoshooto.domain.model.User
import com.photoshooto.domain.usecase.upload_file.UploadFileViewModel
import com.photoshooto.ui.job.utility.getSelectedValue
import com.photoshooto.ui.job.utility.setSafeOnClickListener
import com.photoshooto.ui.usedPhotographyScreens.adapter.BillAdapter
import com.photoshooto.ui.usedPhotographyScreens.adapter.PostProductAdapter
import com.photoshooto.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class PostProductStep02Fragment : Fragment() {

    lateinit var binding: FragmentPostProductStep02Binding

    var user: User? = null
    private lateinit var additionalAdapter: PostProductAdapter
    private lateinit var billAdapter: BillAdapter
    private lateinit var warrantyAdapter: BillAdapter

    val additionalArray: ArrayList<String> = ArrayList()

    val fileOriginalBillArray: ArrayList<FileImagePRQ> = ArrayList()
    val fileWarrantyArray: ArrayList<FileImagePRQ> = ArrayList()

    var isOriginalBill = false
    private val navArgs: PostProductStep02FragmentArgs by navArgs()

    var productsModel = ProductsModel()
    var billUploadMultipart: MultipartBody.Part? = null
    private val uploadFileViewModel: UploadFileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            return binding.root
        }
        val data = navArgs.postProductsPRQ

        binding = FragmentPostProductStep02Binding.inflate(inflater, container, false)


        Log.e("sellingLocation", "" + data.sellingLocation)


        binding.imgPostBack.setOnClickListener {
            findNavController().popBackStack()
        }

        user = SharedPrefsHelper.getUserCommon()

        binding.btnProceed.setSafeOnClickListener {

            if (binding.rgBill.getSelectedValue().equals(getString(R.string.yes_available))) {
                if (fileOriginalBillArray.size == 0) {
                    showSnackBar("Original Bill Required")
                    return@setSafeOnClickListener
                }
            }

            /* Log.e("rgBattery", "" + binding.rgBattery.getSelectedValue())
             Log.e("rgCharger", "" + binding.rgCharger.getSelectedValue())
             Log.e("rgCover", "" + binding.rgCover.getSelectedValue())
             Log.e("rgDone", "" + binding.rgDone.getSelectedValue())
             Log.e("rgBill", "" + binding.rgBill.getSelectedValue())
             Log.e("rgWarranty", "" + binding.rgWarranty.getSelectedValue())*/

            data.isOriginalBattery =
                binding.rgBattery.getSelectedValue().equals(getString(R.string.yes_available))

            data.isOriginalCharger =
                binding.rgCharger.getSelectedValue().equals(getString(R.string.yes_available))
            data.isOriginalBagCover =
                binding.rgCover.getSelectedValue().equals(getString(R.string.yes_available))
            data.serviceDone =
                binding.rgDone.getSelectedValue().equals(getString(R.string.yes_available))
            data.isBillAvailable =
                binding.rgBill.getSelectedValue().equals(getString(R.string.yes_available))
            data.isInWarranty =
                binding.rgWarranty.getSelectedValue().equals(getString(R.string.yes_available))

            data.additionalEquipment = additionalArray
            data.isBill = fileOriginalBillArray.firstOrNull()?.file
            data.iswarranty = fileWarrantyArray.firstOrNull()?.file


            Log.e("isInWarranty", "" + data.isInWarranty)
            Log.e("isBillAvailable", "" + data.isBillAvailable)

            findNavController().navigate(
                PostProductStep02FragmentDirections.actionPost2ToPost3(data)
                    .setPostProducts(productsModel)
            )
        }

        additionalAdapter = PostProductAdapter(onClose = { d ->

            additionalArray.remove(d)
            additionalAdapter.swapList(additionalArray)

        })

        billAdapter = BillAdapter(onClose = { d ->

            fileOriginalBillArray.remove(d)
            billAdapter.swapList(fileOriginalBillArray)
            binding.btnOriginalBill.isEnabled = true

        }, onView = {
            if (it.imageUrl.length != 0) {
                findNavController().navigate(
                    PostProductStep02FragmentDirections.actionPost2ToFullscreen(it.imageUrl)
                )
            } else {
                findNavController().navigate(
                    PostProductStep02FragmentDirections.actionPost2ToFullscreen(it.file?.absolutePath.toString())
                )
            }

        })

        warrantyAdapter = BillAdapter(onClose = { d ->

            fileWarrantyArray.remove(d)
            warrantyAdapter.swapList(fileWarrantyArray)
            binding.btnWarranty.isEnabled = true

        }, onView = {
            if (it.imageUrl.length != 0) {
                findNavController().navigate(
                    PostProductStep02FragmentDirections.actionPost2ToFullscreen(it.imageUrl)
                )
            } else {
                findNavController().navigate(
                    PostProductStep02FragmentDirections.actionPost2ToFullscreen(it.file?.absolutePath.toString())
                )
            }
        })

        binding.rvAdditional.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBill.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWarranty.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvAdditional.adapter = additionalAdapter
        binding.rvBill.adapter = billAdapter
        binding.rvWarranty.adapter = warrantyAdapter

        binding.llAdd.setOnClickListener {

            if (binding.etAdditional.text.length != 0) {
                additionalArray.add(binding.etAdditional.text.toString())
                additionalAdapter.swapList(additionalArray)
                binding.etAdditional.setText("")
                binding.etAdditional.setHint(getString(R.string.enter_additional_equipments))
            }
        }

        binding.rgBattery.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)
            Log.e("selectedtext-->", radio.text.toString())

        })

        binding.rgCharger.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)
            Log.e("selectedtext-->", radio.text.toString())

        })

        binding.rgCover.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)
            Log.e("selectedtext-->", radio.text.toString())

        })
        binding.rgDone.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)
            Log.e("selectedtext-->", radio.text.toString())

        })
        binding.rgBill.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)

            if (radio.text.toString().equals(getString(R.string.yes_available))) {
                binding.tvBillValue.text = getString(R.string.yes)
                binding.btnOriginalBill.isEnabled = true
            } else {
                binding.tvBillValue.text = getString(R.string.no)
                binding.btnOriginalBill.isEnabled = false

                fileOriginalBillArray.clear()
                billAdapter.swapList(fileOriginalBillArray)

            }
        })

        binding.rgWarranty.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

            val radio: RadioButton = group.findViewById(checkedId)

            if (radio.text.toString().equals(getString(R.string.yes_available))) {
                binding.tvWarrantyValue.text = getString(R.string.yes)
                binding.btnWarranty.isEnabled = true
            } else {
                binding.tvWarrantyValue.text = getString(R.string.no)
                binding.btnWarranty.isEnabled = false

                fileWarrantyArray.clear()
                warrantyAdapter.swapList(fileWarrantyArray)
            }
        })

        binding.btnOriginalBill.setOnClickListener {
            isOriginalBill = true
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

        binding.btnWarranty.setOnClickListener {
            isOriginalBill = false

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

        with(uploadFileViewModel) {
            updateImgFileStatus.observe(requireActivity()) { response ->
                if (response != null) {
                    when (response.status) {
                        Status.SUCCESS -> {

                            Log.e(
                                "Bill",
                                response.data?.data?.get(0)?.category + " " + DOMAIN + response.data?.data?.get(
                                    0
                                )?.file_path
                            )

                            if (response.data?.data?.get(0)?.category.equals("product_bill")) {
                                data.billFile = DOMAIN + response.data?.data?.get(0)?.file_path
                            } else {
                                data.warrantyCardFile =
                                    DOMAIN + response.data?.data?.get(0)?.file_path
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

        navArgs.postProducts?.let {
            if (it._id.length != 0) {
                productsModel = it
                setData()
            }
        }

        return binding.root
    }

    private fun billUpload(file: File, isCategory: String) {

        val requestFile: RequestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        requestFile.apply {
            billUploadMultipart = MultipartBody.Part.createFormData(
                "photos",
                file.name,
                this
            )
        }

        val visiting_card: RequestBody = createPartFromString(isCategory)
        val map: HashMap<String, RequestBody> = HashMap()
        map["category"] = visiting_card
        uploadFileViewModel.updateImgFile(
            billUploadMultipart,
            map,
            SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN)
        )
    }

    private fun setData() {


        if (productsModel.isOriginalBattery) {
            binding.rbBatteryYes.setChecked(true)
        } else {
            binding.rbBatteryNo.setChecked(true)
        }

        if (productsModel.isOriginalCharger) {
            binding.rbChargerYes.setChecked(true)
        } else {
            binding.rbChargerNo.setChecked(true)
        }

        if (productsModel.isOriginalBagCover) {
            binding.rbCoverYes.setChecked(true)
        } else {
            binding.rbCoverNo.setChecked(true)
        }

        if (productsModel.serviceDone) {
            binding.rbDoneYes.setChecked(true)
        } else {
            binding.rbDoneNo.setChecked(true)
        }

        if (productsModel.isBillAvailable) {
            binding.rbBillYes.setChecked(true)
        } else {
            binding.rbBillNo.setChecked(true)
        }

        if (productsModel.isInWarranty) {
            binding.rbWarrantyYes.setChecked(true)
        } else {
            binding.rbWarrantyNo.setChecked(true)
        }

        additionalArray.addAll(productsModel.additionalEquipment!!)
        additionalAdapter.swapList(additionalArray)

        if (productsModel.isBillAvailable) {

            val file = FileImagePRQ()
            file.imageUrl = productsModel.bill_file

            fileOriginalBillArray.add(file)
            billAdapter.swapList(fileOriginalBillArray)
            binding.btnOriginalBill.isEnabled = false

        }

        if (productsModel.isInWarranty) {

            val file = FileImagePRQ()
            file.imageUrl = productsModel.warrantyCardFile
            fileWarrantyArray.add(file)
            warrantyAdapter.swapList(fileWarrantyArray)
            binding.btnWarranty.isEnabled = false

        }


    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show()
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

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
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

            val selectedImageUri: Uri? = data?.data
            if (isOriginalBill) {
                val file = FileImagePRQ()
                file.file = selectedImageUri?.let {
                    createFileFromUri(
                        requireActivity(),
                        it
                    )
                }
                file.file?.let { billUpload(it, "product_bill") }

                fileOriginalBillArray.add(file)
                billAdapter.swapList(fileOriginalBillArray)
                binding.btnOriginalBill.isEnabled = false
            } else {
                val file = FileImagePRQ()
                file.file = selectedImageUri?.let {
                    createFileFromUri(
                        requireActivity(),
                        it
                    )
                }
                file.file?.let { billUpload(it, "product_warranty_card") }

                fileWarrantyArray.add(file)
                warrantyAdapter.swapList(fileWarrantyArray)
                binding.btnWarranty.isEnabled = false

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

            if (isOriginalBill) {
                val file = FileImagePRQ()
                file.file = tempUri.let { createFileFromUri(requireActivity(), it) }

                fileOriginalBillArray.add(file)

                billAdapter.swapList(fileOriginalBillArray)
                binding.btnOriginalBill.isEnabled = false
                file.file?.let { billUpload(it, "product_bill") }

            } else {
                val file = FileImagePRQ()
                file.file = tempUri.let { createFileFromUri(requireActivity(), it) }
                fileWarrantyArray.add(file)
                warrantyAdapter.swapList(fileWarrantyArray)
                binding.btnWarranty.isEnabled = false
                file.file?.let { billUpload(it, "product_warranty_card") }

            }
        }
    }

}