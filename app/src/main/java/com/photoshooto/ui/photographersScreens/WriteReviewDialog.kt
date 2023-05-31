package com.photoshooto.ui.photographersScreens

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.photoshooto.R
import com.photoshooto.domain.model.SendFeedbackRequest
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.firebase.FirebaseAnalytics_Event_ScreenName
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WriteReviewDialog : DialogFragment(), RatingBar.OnRatingBarChangeListener {

    private var CUSTOMER_ID = ""
    private var ORDER_ID = ""
    private val viewModel: GetUserProfileViewModel by viewModel()
    var serviceType = "Ok Service"

    companion object {
        const val TAG = "WriteReviewDialog"
        private const val KEY_CUSTOMER_ID = "KEY_CUSTOMER_ID"
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun newInstance(customerId: String, orderId: String, context: Context): WriteReviewDialog {
            val args = Bundle()
            args.putString(KEY_CUSTOMER_ID, customerId)
            args.putString(KEY_ORDER_ID, orderId)
            val fragment = WriteReviewDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            CUSTOMER_ID = it.getString(KEY_CUSTOMER_ID)!!
            ORDER_ID = it.getString(KEY_ORDER_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        recordScreenView(
            requireActivity(),
            "WriteReviewDialog",
            FirebaseAnalytics_Event_ScreenName.screenPhotographerOrder_Details_Review
        )

        return inflater.inflate(R.layout.dialog_get_write_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cancelBtn = view.findViewById(R.id.btnCancel) as TextView
        val btnPost = view.findViewById(R.id.btnPost) as TextView
        val name = view.findViewById(R.id.name) as TextView
        val userId = view.findViewById(R.id.user_id) as TextView

        val profileImage = view.findViewById(R.id.profile_image) as ImageView

        val rateUser = view.findViewById(R.id.rate_user) as RatingBar
        rateUser.onRatingBarChangeListener = this

        val edtReview = view.findViewById(R.id.edtReview) as EditText

        name.text = SharedPrefsHelper.read(SharedPrefConstant.USER_NAME)
        userId.text = "ID: " + SharedPrefsHelper.read(SharedPrefConstant.USER_ID)

        Glide.with(requireContext()).load(urlAddingForPicture(SharedPrefConstant.PROFILE_URL))
            .placeholder(R.drawable.ic_temp_user_profile)
            .into(profileImage)

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        btnPost.setOnClickListener {
            if (edtReview.text.isNullOrEmpty()) {
                onToast("Please enter the review!", requireContext())
            } else {
                val attachmentList = ArrayList<String>()
                val improve_list = ArrayList<String>()

                val sendFeedbackRequest =
                    SendFeedbackRequest(
                        rateUser.rating.toInt(),
                        serviceType,
                        edtReview.text.toString(),
                        ORDER_ID,
                        attachmentList,
                        improve_list,
                        null,
                        CUSTOMER_ID,
                        false,
                        false,
                        false
                    )
                viewModel.sendFeedback(
                    SharedPrefsHelper.read(SharedPrefConstant.AUTH_TOKEN), sendFeedbackRequest
                )
            }
        }

        viewModel.sendFeedback.observe(requireActivity()) { response ->
            if (response != null) {
                when (response.status) {
                    Status.SUCCESS -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {}
                }
                dialog?.dismiss()
                viewModel.resetSendFeedback()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        dialog?.window?.setLayout(width - 128, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onRatingChanged(rateBar: RatingBar?, rating: Float, p2: Boolean) {
        rateBar?.rating = rating
        serviceType = when (rating) {
            1f -> {
                "Not so good service"
            }
            2f -> {
                "Ok service"
            }
            3f -> {
                "Good Service"
            }
            4f -> {
                "Very Good Service"
            }
            5f -> {
                "Best Service"
            }
            else -> {
                "Ok Service"
            }
        }
    }
}