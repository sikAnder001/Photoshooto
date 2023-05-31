package com.photoshooto.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hsalf.smileyrating.SmileyRating
import com.photoshooto.R
import com.photoshooto.databinding.FeedbackFragmentBinding
import com.photoshooto.domain.model.SendFeedbackRequest
import com.photoshooto.domain.usecase.profile.GetUserProfileViewModel
import com.photoshooto.ui.purchase.PaymentSuccessDialog
import com.photoshooto.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedbackFragment : Fragment(), SuccessDialog.Listener {

    private lateinit var binding: FeedbackFragmentBinding
    private var response_bool: Boolean = false
    private var quality_bool: Boolean = false
    private var delivery_bool: Boolean = false
    private var rating: Int? = 0
    private val viewModel: GetUserProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedbackFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.feedbackToolbar

        val backBtn = toolbar.backBtn
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val titleTxt = toolbar.tvTitle
        titleTxt.text = "Feedback"

        binding.smileRating.setTitle(SmileyRating.Type.TERRIBLE, "Very Bad")
        binding.smileRating.setSmileySelectedListener { type ->
            // You can compare it with rating Type
            rating = type.rating
        }

        if (SharedPrefsHelper.read(SharedPrefConstant.USER_CASE_TYPE) == Constant.USER_CASE_TYPE.PHOTOGRAPHER_CLIENT.name) {
            binding.photographerView.visibility = View.VISIBLE
        } else {
            binding.photographerView.visibility = View.GONE
        }

        binding.responseLike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.responseLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.responseUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            response_bool = true
        }

        binding.responseUnlike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.responseLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.responseUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            response_bool = false
        }

        binding.qualityLike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.qualityLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.qualityUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            quality_bool = true
        }

        binding.qualityUnlike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.qualityLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.qualityUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            quality_bool = false
        }

        binding.deliveryLike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.deliveryLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.deliveryUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            delivery_bool = true
        }

        binding.deliveryUnlike.setOnClickListener {
            context?.let { it1 -> ContextCompat.getColor(it1, R.color.grey_clr_light) }
                ?.let { it2 ->
                    binding.deliveryLike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }

            context?.let { it1 -> ContextCompat.getColor(it1, R.color.orange_clr) }
                ?.let { it2 ->
                    binding.deliveryUnlike.setColorFilter(
                        it2,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            delivery_bool = false
        }

        binding.btnSubmitFeedback.setOnClickListener {
            if (rating == 0) {
                onToast("Please select the review!", requireContext())
            } else {
                val attachmentList = ArrayList<String>()
                val improve_list = ArrayList<String>()

                val sendFeedbackRequest =
                    SendFeedbackRequest(
                        rating,
                        binding.subject.text.toString(),
                        binding.feedbackMessage.text.toString(),
                        null,
                        attachmentList,
                        improve_list,
                        null,
                        null,
                        response_bool,
                        quality_bool,
                        delivery_bool
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
                        SuccessDialog.newInstance(
                            getString(R.string.thanks_for_sending_your_valuable_feedback),
                            this
                        )
                            .show(
                                requireActivity().supportFragmentManager,
                                PaymentSuccessDialog.TAG
                            )
                    }
                    Status.ERROR -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                    else -> {
                        response.data?.message?.let { it1 -> requireContext().showToast(it1) }
                    }
                }
                viewModel.resetSendFeedback()
            }
        }
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }

    override fun goToHome() {
        onBackPressed()
    }
}