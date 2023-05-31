package com.photoshooto.ui.admin_screen.manage_request

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.photoshooto.R
import com.photoshooto.api_call_package.network.UtilTo
import com.photoshooto.databinding.DialogNewRequestDocumentPreviewBinding
import com.photoshooto.databinding.FragmentRequestedUserDetailsBinding
import com.photoshooto.domain.usecase.manage_request.NewRequestViewModel
import com.photoshooto.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.*
import java.net.URL


class RequestedUserDetailsFragment : Fragment() {

    companion object {
        const val REQUEST_SELECT_REASON = "request_select_reason"
    }

    private lateinit var binding: FragmentRequestedUserDetailsBinding
    private val viewModel: NewRequestViewModel by viewModel()

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var isFrom: String
    private lateinit var userId: String

    private lateinit var registerForActivityResult: ActivityResultLauncher<Array<String>>
    private var fileName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_SELECT_REASON) { requestKey, bundle ->
            val result = bundle.getInt(KEY_RESULT, Activity.RESULT_CANCELED)
            if (result == Activity.RESULT_OK) {
                viewModel.getUserDetails(userId)
            }
        }
        initObserver()
        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->

            }
    }

    private fun initObserver() {
        with(viewModel) {
            getUserDetailsResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            result.data?.let { data ->
                                binding.apply {
                                    switchStatus.isChecked = data.status == viewModel.BLOCK
                                    data.profile_details?.background_image?.let { img ->
                                        Glide.with(requireContext())
                                            .load("$DOMAIN$img")
                                            .into(ivHeader)
                                    }
                                    data.profile_details?.profile_image?.let { img ->
                                        Glide.with(requireContext())
                                            .load("$DOMAIN$img")
                                            .into(ivProfileImage)
                                    }
                                    tvName.text = data.profile_details?.name
                                    tvDesignation.text = data.type ?: ""
                                    data.location?.let { location ->
                                        if (!location.city.isNullOrEmpty()) {
                                            tvLocation.text = "${location.city}, ${location.state}"
                                            tvAssignedLocation.text =
                                                "${location.city}, ${location.state}"
                                        }
                                    }

                                    tvEmailId.text = data.profile_details?.email ?: "-"
                                    tvContactNo.text = data.profile_details?.mobile ?: "-"
                                    tvAlternativeNo.text = data.profile_details?.alt_mobile ?: "-"
                                    tvGender.text = data.profile_details?.gender ?: "-"
                                    tvDob.text = data.profile_details?.birth_date ?: "-"
                                    data.profile_details?.language_know?.let { lang ->
                                        tvLanguagesKnown.text = lang.joinToString(", ")
                                    }

                                    if (data.profile_details?.profession?.isNotEmpty() == true) {
                                        tvProfession.text = data.profile_details.profession
                                    }
                                    tvStudioName.text = data.profile_details?.studio_name ?: "-"
                                    tvExpYears.text = "${data.profile_details?.experience}"
                                    updateViewFromStatus(data.status ?: "")
                                }
                            }
                        } else {
                            result.message?.let {
                                onToast(it, requireContext())
                            }
                        }
                    }
                }
            )
            updateUserStatusResponse.observe(
                requireActivity(),
                Observer { result ->
                    result.success?.let {
                        if (it) {
                            viewModel.getUserDetails(userId)
                        } else {
                            result.message?.let { onToast(it, requireContext()) }
                        }
                    }
                }
            )
            messageData.observe(
                requireActivity(),
                Observer { result ->
                    onToast(result, requireContext())
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestedUserDetailsBinding.inflate(inflater, container, false)
        isFrom = requireArguments().getString("isFrom") ?: ""
        userId = requireArguments().getString("userId") ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            registerForActivityResult.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
        initView()

        if (viewModel.getUserDetailsResponse.value == null) {
            if (requireContext().isInternetAvailable()) {
                viewModel.getUserDetails(userId)
            } else {
                onToast(getString(R.string.internet_check), requireContext())
            }
        } else initObserver()
    }

    private fun initView() {
        binding.apply {
            when (isFrom) {
                getString(R.string.label_accepted) -> {
                    groupAcceptDecline.hide()
                    cardAccepted.show()
                }
                getString(R.string.label_declined) -> {
                    groupAcceptDecline.hide()
                    cardDeclined.show()
                }
                else -> {
                    groupAcceptDecline.show()
                    cardDeclined.hide()
                    cardAccepted.hide()
                }
            }
        }
    }

    private fun updateViewFromStatus(status: String) {
        binding.apply {
            groupAcceptDecline.hide()
            cardDeclined.hide()
            cardAccepted.hide()
            when (status) {
                viewModel.ACTIVE -> {
                    groupAcceptDecline.show()
                }
                viewModel.ACCEPT -> {
                    cardAccepted.show()
                }
                viewModel.REJECT -> {
                    cardDeclined.show()
                }
                viewModel.BLOCK -> {
                    groupAcceptDecline.show()
                }
            }
        }
    }

    private fun clickListener() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            cardAccept.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    viewModel.updateUserStatus(userId, viewModel.ACCEPT)
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }
            cardDecline.setOnClickListener {
                if (requireContext().isInternetAvailable()) {
                    findNavController().navigate(
                        RequestedUserDetailsFragmentDirections.actionMoveToSelectReason(
                            userId
                        )
                    )
                } else {
                    onToast(getString(R.string.internet_check), requireContext())
                }
            }

            cardBtnCall.setOnClickListener {
                callPhoneNumber(requireActivity(), getString(R.string.support_phone_no))
            }
            cardBtnMessage.setOnClickListener {
                sendEmail(requireActivity(), getString(R.string.support_email_id), "Support")
            }

            cardPersonalDetailsTitle.setOnClickListener {
                if (viewPersonalDetails.isShown) {
                    viewPersonalDetails.hide()
                    tvPersonalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.right_arrow,
                        0
                    )
                } else {
                    viewPersonalDetails.show()
                    tvPersonalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down_arrow,
                        0
                    )
                }
            }
            cardProfessionalDetailsTitle.setOnClickListener {
                if (viewProfessionalDetails.isShown) {
                    viewProfessionalDetails.hide()
                    tvProfessionalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.right_arrow,
                        0
                    )
                } else {
                    viewProfessionalDetails.show()
                    tvProfessionalDetailsTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_down_arrow,
                        0
                    )
                }
            }

            tvAadhaarCard.setOnClickListener {
                if (viewModel.getUserDetailsResponse.value != null) {
                    val attachment = viewModel.getUserDetailsResponse.value?.data?.attachments!!
                    Log.i("attachments", Gson().toJson(attachment))
                    if (attachment.isNullOrEmpty()) {
                        Toast.makeText(context, "document is not uploaded yet!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        for (data in attachment) {
                            if (data.category == AADHAR) {
                                openDocumentPreview(data.file_path!!)
                                break
                            }
                        }
                    }
                }
            }

            tvLicence.setOnClickListener {
                if (viewModel.getUserDetailsResponse.value != null) {
                    val attachment = viewModel.getUserDetailsResponse.value?.data?.attachments!!
                    Log.i("attachments", Gson().toJson(attachment))
                    if (attachment.isNullOrEmpty()) {
                        Toast.makeText(context, "document is not uploaded yet!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        for (data in attachment) {
                            if (data.category == LICENSE) {
                                openDocumentPreview(data.file_path!!)
                                break
                            }
                        }

                    }
                }
            }

            tvVisitingCard.setOnClickListener {
                if (viewModel.getUserDetailsResponse.value != null) {
                    val attachment = viewModel.getUserDetailsResponse.value?.data?.attachments!!
                    Log.i("attachments", Gson().toJson(attachment))
                    if (attachment.isNullOrEmpty()) {
                        Toast.makeText(context, "document is not uploaded yet!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        for (data in attachment) {
                            if (data.category == VISITING_CARD) {
                                openDocumentPreview(data.file_path!!)
                                break
                            }
                        }
                    }
                }
            }
            tvAssociationCard.setOnClickListener {
                if (viewModel.getUserDetailsResponse.value != null) {
                    val attachment = viewModel.getUserDetailsResponse.value?.data?.attachments!!
                    Log.i("attachments", Gson().toJson(attachment))
                    if (attachment.isNullOrEmpty()) {
                        Toast.makeText(context, "document is not uploaded yet!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        for (data in attachment) {
                            if (data.category == ASSOCIATION_CARD) {
                                openDocumentPreview(data.file_path!!)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openDocumentPreview(imageFile: String) {
        val dialogBinding = DialogNewRequestDocumentPreviewBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())

        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.show()

        if (imageFile.isNotEmpty()) {
            Glide.with(requireActivity()).load(imageFile)
                .into(dialogBinding.ivDocument)
        }


        dialogBinding.apply {
            ivClose.setOnClickListener {
                bottomSheetDialog.hide()
            }
            cardDownload.setOnClickListener {
                if (UtilTo.checkIfUrlOK(imageFile)) {
                    downloadDocument(imageFile)
                } else {
                    showToast(context, "Url is Invalid")
                }
            }


        }
        bottomSheetDialog.show()
    }

    private fun downloadDocument(imageFile: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val flag = writeResponseBodyToDisk(imageFile)
            if (flag) {
                try {
                    val fileO = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toString() + "/Photoshooto"
                    )
                    val file = File(fileO, fileName)


                    CoroutineScope(Dispatchers.IO).launch {

                        withContext(Dispatchers.Main) {
                            if (file?.exists() == true) {
                                val path = getUriFromFile(file, requireContext())
                                try {
                                    val newIntent = Intent(Intent.ACTION_VIEW)
                                    newIntent.setDataAndType(path, "image/*")
                                    newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    startActivity(
                                        Intent.createChooser(
                                            newIntent,
                                            "Open Image"
                                        )
                                    )

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        requireContext(),
                                        e.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    "File not found",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }


//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    intent.setDataAndType(
//                        FileProvider.getUriForFile(
//                            requireContext(),
//                            context?.applicationContext!!.packageName + ".provider",File(
//                               file,"FitCru weekly diet plan.pdf"
//                            )
//                        ), "application/pdf"
//                    )
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//                    startActivity(Intent.createChooser(
//                        intent,
//                        "Open PDF"
//                    ))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No image reader found to open this file.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    private fun getUriFromFile(file: File, context: Context): Uri? =
        try {
            FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        } catch (e: Exception) {
            if (e.message?.contains("ProviderInfo.loadXmlMetaData") == true) {
                throw Error("FileProvider doesn't exist or has no permissions")
            } else {
                throw e
            }
        }

    private fun writeResponseBodyToDisk(body: String): Boolean {
        try {
            // todo change the file location/name according to your needs
            fileName = System.currentTimeMillis().toString() + "document.jpg"
            //File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            val path = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/Photoshooto"
            )

            if (!path.exists()) {
                path.mkdirs()
            }

            val futureStudioIconFile = File(path, fileName)
            val url = URL(body)
            val iN: InputStream = BufferedInputStream(url.openStream())
            var outputStream: OutputStream? = null
            val buf = ByteArray(1024)

            var n = 0
            outputStream = FileOutputStream(futureStudioIconFile)
            while (-1 != iN.read(buf).also { n = it }) {
                outputStream.write(buf, 0, n)
            }
            outputStream.flush()
            outputStream.close()
            iN.close()
            return true
        } catch (e: IOException) {
            return false
        }
    }

}
